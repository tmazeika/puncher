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

    for {
        conn, err := listener.Accept()

        if err != nil {
            fmt.Println(os.Stderr, "Error accepting connection:", err)
            continue
        }

        go handleConn(conn)
    }
}

func handleConn(conn net.Conn) {
    defer conn.Close()

    logInfo(conn, "Incoming connection")

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
        handleDownloader(conn, in, out)
    case common.UploaderClientType:
        handleUploader(conn, in, out)
    default:
        handleError(conn, out, true, "Expected client type body to be uploader or downloader, got 0x%x", msg)
        return
    }
}

type Downloader struct {
    uid       string
    conn      net.Conn
    peerReady chan bool
}

type DownloaderPool struct {
    sync.RWMutex

    downloaders []Downloader
}

func (p *DownloaderPool) Add(dl *Downloader) {
    dl.peerReady = make(chan bool)

    p.Lock()
    defer p.Unlock()

    p.downloaders = append(p.downloaders, dl)
}

func (p *DownloaderPool) Find(uid string) (dl *Downloader, exists bool) {
    p.RLock()
    defer p.RUnlock()

    for _, d := range p.downloaders {
        if d.uid == uid {
            return d, true
        }
    }

    return nil, false
}

func (p *DownloaderPool) Remove(dl *Downloader) {
    p.Lock()
    defer p.Unlock()

    for i, d := range p.downloaders {
        if d.uid == dl.uid {
            p.downloaders = append(p.downloaders[:i], p.downloaders[i + 1:]...)
            break
        }
    }
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
    dlPool = DownloaderPool{}
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

func (b *Broadcaster) Broadcast(dl *Downloader) {
    for _, l := range b.listeners {
        l.incoming <- dl

        if <- l.claimed {
            l.cancel <- 0
            return
        }
    }

    // If no uploaders were waiting for the downloader, add the downloader to
    // the pool to be claimed in the future.
    dlPool.Add(dl)
}

func (b *Broadcaster) handleListenerCancel(listener BroadcastListener) {
    <- listener.cancel

    b.Lock()
    delete(b.listeners, listener)
    b.Unlock()
}

func handleDownloader(conn net.Conn, in chan common.Message, out chan common.Message) {
    var uid string
    var err error

    logInfo(conn, "Identified as downloader")

    dlPool.RLock()

    // Generate Uid.
    for exists := true; exists; _, exists = dlPool.Find(uid) {
        uid, err = generateUid()

        if err != nil {
            dlPool.RUnlock()
            handleError(conn, out, true, "Error generating UID: %s", err)
            return
        }
    }

    dlPool.RUnlock()

    dl := Downloader{ uid, conn }

    dlPool.Add(dl)
    defer dlPool.Remove(dl)

    // Send Uid.
    out <- common.Message{
        Packet: common.UidAssignment,
        Body:   []byte(uid),
    }

    logInfo(conn, "Sent UID")

    // Notify uploader(s), if any, of new downloader connection.
    incomingBroadcaster.Broadcast(dl)

    var peerReady bool

    for ! peerReady {
        peerReady = <- dl.peerReady
    }
}

func handleUploader(conn net.Conn, in chan common.Message, out chan common.Message) {
    logInfo(conn, "Identified as uploader")
    logInfo(conn, "Awaiting UID")

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

    logInfo("Got UID")

    // Validate Uid.
    if len(uid) != common.UidLength {
        handleError(conn, out, false, "Invalid UID length (not %d), got '%s'", common.UidLength, uid)
        return
    }

    // See if downloader is waiting.
    dl, exists := dlPool.Find(uid)

    if ! exists {
        out <- common.Message{ common.PeerNotFound }
        return
    }

    // Otherwise, wait for a downloader.
    /*if ! exists {
        incoming, claimed, cancel := incomingBroadcaster.Listen()

        ListeningLoop:
        for {
            select {
            case msg := <- in:
                cancel <- 0

                // Indicate to the downloader that the uploader is no longer
                // ready.
                dl.peerReady <- false

                if msg.Packet != common.Halt {
                    handleError(conn, out, false, "Only allowed halt, got 0x%x", msg)
                } else {
                    logMessage(conn, "Halt:", string(msg.Body))
                }

                return
            case incomingDl := <- incoming:
                if incomingDl.uid == uid {
                    claimed <- true
                    dl = incomingDl
                    break ListeningLoop
                } else {
                    claimed <- false
                }
            }
        }
    }*/

    // Indicate to the downloader that the uploader is ready.
    dl.peerReady <- true
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

func logInfo(conn net.Conn, msg ...string) {
    fmt.Println(conn.RemoteAddr(), "--", msg)
}

func logMessage(conn net.Conn, msg ...string) {
    fmt.Println(conn.RemoteAddr(), "->", msg)
}

func generateUid() (string, error) {
    uidBuff := make([]byte, common.UidLength / 2) // 2 hex chars per byte

    if _, err := rand.Read(uidBuff); err != nil {
        return "", err
    }

    return fmt.Sprintf("%x", uidBuff)
}
