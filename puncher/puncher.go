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
    KeyFileName  = "puncher_cert.key"
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

    logInfo(conn, "incoming connection")

    in, out := common.MessageChannel(conn)
    msg, ok := <- in

    if ! ok {
        handleError(conn, out, true, "closing connection")
        return
    }

    // Expect ClientType message.
    if msg.Packet != common.ClientType {
        handleError(conn, out, false, "expected client type, got 0x%x", msg)
        return
    }

    switch common.ClientType(msg.Body[0]) {
    case common.DownloaderClientType:
        handleDownloader(conn, in, out)
    case common.UploaderClientType:
        handleUploader(conn, in, out)
    default:
        handleError(conn, out, true, "expected client type body to be uploader or downloader, got 0x%x", msg)
        return
    }
}

type Downloader struct {
    uid       string
    conn      net.Conn
    peerReady chan int
}

type DownloaderPool struct {
    sync.RWMutex

    downloaders []Downloader
}

func (p *DownloaderPool) Add(dl *Downloader) {
    dl.peerReady = make(chan int)

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

var (
    dlPool = DownloaderPool{}
)

func handleDownloader(conn net.Conn, in chan common.Message, out chan common.Message) {
    var uid string
    var err error

    logInfo(conn, "identified as downloader")

    dlPool.RLock()

    // Generate Uid.
    for exists := true; exists; _, exists = dlPool.Find(uid) {
        uid, err = generateUid()

        if err != nil {
            dlPool.RUnlock()
            handleError(conn, out, true, "error generating UID: %s", err)
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

    logInfo(conn, "sent UID")

    select {
    case time.After(time.Hour):
        out <- common.Message{
            Packet: common.Halt,
            Body:   []string("timeout"),
        }
    case msg := <- in:
        if msg.Packet == common.Halt {
            logMessage(conn, "halt:", string(msg.Body))
        } else {
            handleError(conn, out, false, "expected halt, got 0x%x", msg)
        }
    case <- dl.peerReady:
        out <- common.Message{ common.UploaderReady }
    }
}

func handleUploader(conn net.Conn, in chan common.Message, out chan common.Message) {
    logInfo(conn, "identified as uploader")
    logInfo(conn, "awaiting UID")

    msg, ok := <- in

    if ! ok {
        handleError(conn, out, true, "closing connection")
        return
    }

    // Expect Uid.
    if msg.Packet != common.UidRequest {
        handleError(conn, out, false, "expected UID, got 0x%x", msg)
        return
    }

    uid := string(msg.Body)

    logInfo("got UID")

    // Validate Uid.
    if len(uid) != common.UidLength {
        handleError(conn, out, false, "invalid UID length (not %d), got '%s'", common.UidLength, uid)
        return
    }

    // See if downloader is waiting.
    dl, waiting := dlPool.Find(uid)

    if ! waiting {
        out <- common.Message{ common.PeerNotFound }
        return
    }

    // Indicate to the downloader that the uploader is ready.
    dl.peerReady <- 0
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
