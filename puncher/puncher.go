package puncher

import (
    "github.com/transhift/common/common"
    "net"
    "fmt"
    "os"
    "sync"
    "github.com/codegangsta/cli"
    "crypto/tls"
    "crypto/rand"
    "time"
)

const (
    CertFileName = "puncher_cert.pem"
    KeyFileName = "puncher_cert.key"
)

type args struct {
    port   string
    appDir string
}

func (a args) portOrDef(def string) string {
    if len(a.port) == 0 {
        return def
    }

    return a.port
}

func Start(c *cli.Context) {
    args := args{
        port:   c.GlobalString("port"),
        appDir: c.GlobalString("app-dir"),
    }

    storage := &common.Storage{
        CustomDir: args.appDir,
    }

    cert, err := storage.Certificate(CertFileName, KeyFileName)

    if err != nil {
        fmt.Fprintln(os.Stderr, err)
        os.Exit(1)
    }

    listener, err := tls.Listen("tcp", net.JoinHostPort("", args.port), &tls.Config{
        Certificates: []tls.Certificate{cert},
        MinVersion:   tls.VersionTLS12,
    })

    if err != nil {
        fmt.Fprintln(os.Stderr, err)
        os.Exit(1)
    }

    fmt.Printf("Listening on port %s\n", args.port)
    // TODO: uncomment for release
    //rand.Seed(int64(time.Now().Nanosecond()))

    dlPool := DownloaderPool{}

    for {
        conn, err := listener.Accept()

        if err != nil {
            fmt.Println(os.Stderr, "Error accepting connection:", err)
            continue
        }

        go handleConn(conn, dlPool)
    }
}

func handleConn(conn net.Conn, dlPool DownloaderPool) {
    defer conn.Close()

    log(conn, "Incoming connection")

    in, out := common.MessageChannel(conn)
    msg, ok := <- in

    if ! ok {
        handleError(conn, out, true, "Closing connection")
        return
    }

    // Expect ClientType message.
    if msg.Packet != common.ClientType {
        handleError(conn, out, false, "Expected client type, got 0x%x", msg)
        return
    }

    switch common.ClientType(msg.Body[0]) {
    case common.DownloaderClientType:
        handleDownloader(conn, dlPool, in, out)
    case common.UploaderClientType:
        handleUploader(conn, dlPool, in, out)
    default:
        handleError(conn, out, true, "Expected client type body to be uploader or downloader, got 0x%x", msg)
        return
    }
}

type Downloader struct {
    sync.RWMutex

    uid     string
    conn    net.Conn
    claimed bool
}

type DownloaderPool struct {
    sync.RWMutex

    uidConnMap      map[string]net.Conn
}

type BroadcastListener struct {
    incoming chan Downloader
    claimed  chan bool
    cancel   chan int
}

type Broadcaster struct {
    sync.RWMutex

    listeners []BroadcastListener
}

var (
    incomingBroadcaster = Broadcaster{}
)

func (b *Broadcaster) Listen() (incoming chan Downloader, claimed chan bool, cancel chan int) {
    incoming = make(chan Downloader)
    claimed = make(chan bool)
    cancel = make(chan int)

    listener := BroadcastListener{
        incoming,
        claimed,
        cancel,
    }

    b.Lock()
    b.listeners = append(b.listeners, listener)
    b.Unlock()

    go b.handleListenerCancel(listener)

    return
}

func (b *Broadcaster) Broadcast(d Downloader) {
    for _, l := range b.listeners {
        l.incoming <- d

        if <- l.claimed {
            l.cancel <- 0
            break
        }
    }
}

func (b *Broadcaster) handleListenerCancel(listener BroadcastListener) {
    <- listener.cancel

    b.Lock()
    delete(b.listeners, listener)
    b.Unlock()
}

func handleDownloader(conn net.Conn, dlPool DownloaderPool, in chan common.Message, out chan common.Message) {
    var uid string
    var err error

    log(conn, "Identified as downloader")

    dlPool.RLock()

    // Generate Uid.
    for exists := true; exists; _, exists = dlPool.uidConnMap[uid] {
        uid, err = generateUid()

        if err != nil {
            dlPool.RUnlock()
            handleError(conn, out, true, "Error generating UID: %s", err)
            return
        }
    }

    dlPool.RUnlock()

    dlPool.Lock()
    dlPool.uidConnMap[uid] = conn
    dlPool.Unlock()

    // Send Uid.
    out <- common.Message{
        Packet: common.UidAssignment,
        Body:   []byte(uid),
    }

    log(conn, "Sent UID")

    // Notify uploader(s), if any, of new downloader connection.
    incomingBroadcaster.Broadcast(Downloader{
        uid,
        conn,
    })
}

func handleUploader(conn net.Conn, dlPool DownloaderPool, in chan common.Message, out chan common.Message) {
    log(conn, "Identified as uploader")
    log(conn, "Awaiting UID")

    msg, ok := <- in

    if ! ok {
        handleError(conn, out, true, "Closing connection")
        return
    }

    // Expect Uid.
    if msg.Packet != common.UidRequest {
        handleError(conn, out, false, "Expected UID, got 0x%x", msg)
        return
    }

    uid := string(msg.Body)

    // Validate Uid.
    if len(uid) != common.UidLength {
        handleError(conn, out, false, "Invalid UID length (not %d), got '%s'", common.UidLength, uid)
        return
    }

    // See if downloader is waiting.
    dlPool.RLock()
    dlConn, exists := dlPool.uidConnMap[uid]
    dlPool.RUnlock()

    // Otherwise, wait for a downloader.
    if ! exists {
        incoming, claimed, cancel := incomingBroadcaster.Listen()

        ListeningLoop:
        for {
            select {
            case msg := <- in:
                if msg.Packet != common.Halt {
                    handleError(conn, out, false, "Only allowed halt, got 0x%x", msg)
                }

                cancel <- 0
            case dl := <- incoming:
                if dl.uid == uid {
                    claimed <- true
                    dlConn = dl.conn
                    break ListeningLoop
                } else {
                    claimed <- false
                }
            }
        }
    }
}

func handleError(conn net.Conn, out chan common.Message, internal bool, format string, a ...interface{}) {
    var packet common.Packet
    msg := fmt.Sprintf(format, a)

    if internal {
        packet = common.InternalError
    } else {
        packet = common.ProtocolError
    }

    fmt.Fprintln(os.Stderr, conn.RemoteAddr(), "<-", msg)

    out <- common.Message{
        Packet: packet,
        Body:   []byte(msg),
    }
}

func log(conn net.Conn, msg string) {
    fmt.Fprintln(conn.RemoteAddr(), "--", msg)
}

func generateUid() (string, error) {
    uidBuff := make([]byte, common.UidLength / 2) // 2 hex chars per byte

    if _, err := rand.Read(uidBuff); err != nil {
        return "", err
    }

    return fmt.Sprintf("%x", uidBuff)
}
