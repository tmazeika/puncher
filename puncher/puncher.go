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

type downloader struct {
    conn       net.Conn
    ready      bool
    readyCh    chan int
    responseCh chan bool
}

type uidConnMap map[string]downloader

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

    downloaders := uidConnMap{}
    downloadersMutex := &sync.Mutex{}

    for {
        conn, err := listener.Accept()

        if err != nil {
            fmt.Println(os.Stderr, err)
            continue
        }

        go handleConn(conn)

        /*go func() {
            clientTypeBuffer := make([]byte, 1)

            conn.Read(clientTypeBuffer)

            clientType := common.ProtocolMessage(clientTypeBuffer[0])

            switch clientType {
            case common.DownloadClientType:
                handleDownloader(conn, downloaders, downloadersMutex)
            case common.UploadClientType:
                handleUploader(conn, downloaders, downloadersMutex)
            default:
                fmt.Fprintf(os.Stderr, "Protocol error from '%s': expected client type, got 0x%X\n", conn.RemoteAddr().String(), clientType)
            }
        }()*/
    }
}

func handleConn(conn net.Conn) {
    defer conn.Close()

    msgCh := common.MessageChannel(conn)

    msg, ok := <- msgCh

    if ! ok {
        fmt.Printf("Closing connection with '%s'\n", conn.RemoteAddr())
        return
    }

    // Expect ClientType message.
    if msg.Packet != common.ClientType {
        fmt.Fprintf(os.Stderr, "Expected ClientType message from '%s', got 0x%x\n", conn.RemoteAddr(), msg)
        return
    }

    switch common.ClientType(msg.Body[0]) {
    case common.DownloaderClientType:
        // TODO: handleDownloader(...)
    case common.UploaderClientType:
        handleUploader(conn, msgCh)
    default:
        fmt.Fprintf(os.Stderr, "Expected ClientType body from '%s', got ox%x\n", conn.RemoteAddr(), msg)
        return
    }
}

type DownloaderPool struct {
    downloaders []
}

func handleUploader(conn net.Conn, msgCh chan common.Message) {
    msg, ok := <- msgCh

    if ! ok {
        fmt.Printf("Closing connection with '%s'\n", conn.RemoteAddr())
        return
    }

    // Expect Uid message.
    if msg.Packet != common.UidRequest {
        fmt.Fprintf(os.Stderr, "Expected Uid message from '%s', got 0x%x\n", conn.RemoteAddr(), msg)
        return
    }

    uid := string(msg.Body)

    // Validate Uid.
    if len(uid) != common.UidLength {
        fmt.Fprintf(os.Stderr, "Received invalid Uid from '%s', got '%s'\n", conn.RemoteAddr(), uid)
        return
    }
}

/*func uidExists(downloaders uidConnMap, uid string) bool {
    _, exists := downloaders[uid]

    return exists
}

func _handleDownloader(conn net.Conn, downloaders uidConnMap, downloadersMutex *sync.Mutex) {
    defer conn.Close()

    dlAddrStr := conn.RemoteAddr().String()
    var uid string

    downloadersMutex.Lock()

    for len(uid) == 0 || uidExists(downloaders, uid) {
        uid = randSeq(common.UidLength)
    }

    downloader := downloader{
        conn:    conn,
        readyCh: make(chan int),
    }

    downloaders[uid] = downloader

    downloadersMutex.Unlock()

    // OUT: UID
    if _, err := conn.Write([]byte(uid)); err != nil {
        fmt.Fprintln(os.Stderr, err)
    }

    fmt.Printf("Gave downloader '%s' UID: %s\n", dlAddrStr, uid)

    go func() {
        <- downloader.readyCh

        downloader.ready = true
        // OUT: PuncherReady
        _, err := conn.Write(common.Mtob(common.PuncherReady))

        if err != nil {
            fmt.Fprintf(os.Stderr, "Error for downloader '%': %s\n", dlAddrStr, err)
        }

        downloader.responseCh <- err == nil
    }()

    time.Sleep(time.Second)

    for ! downloader.ready {
        // OUT: **ping**
        if active := ping(conn); ! active {
            fmt.Printf("Downloader '%s' timed out\n", dlAddrStr)
            return
        }

        time.Sleep(time.Second)
    }

    delete(downloaders, uid)
}

func handleUploader(conn net.Conn, downloaders uidConnMap, downloadersMutex *sync.Mutex) {
    defer conn.Close()

    ulAddrStr := conn.RemoteAddr().String()
    uidBuffer := make([]byte, common.UidLength)

    if _, err := conn.Read(uidBuffer); err != nil {
        fmt.Fprintln(os.Stderr, err)
    }

    uid := string(uidBuffer)

    downloadersMutex.Lock()

    for ! uidExists(downloaders, uid) {
        downloadersMutex.Unlock()
        time.Sleep(time.Second)

        // OUT: **ping**
        if active := ping(conn); ! active {
            fmt.Printf("Uploader '%s' timed out\n", ulAddrStr)
            return
        }

        downloadersMutex.Lock()
    }

    downloader := downloaders[uid]

    downloadersMutex.Unlock()

    // OUT: **ping**
    if active := ping(conn); ! active {
        fmt.Printf("Uploader '%s' timed out\n", ulAddrStr)
        return
    }

    out := bufio.NewWriter(conn)
    dlAddrStr := downloader.conn.RemoteAddr().String()

    // TODO: error check
    // OUT: EndPing
    out.Write(common.Mtob(common.PuncherEndPing))
    // OUT: downloader address + NL
    out.WriteString(dlAddrStr)
    out.WriteRune('\n')
    out.Flush()
    fmt.Printf("Gave uploader '%s' downloader's address: '%s'\n", ulAddrStr, dlAddrStr)

    downloader.readyCh <- 0

    downloaderReady := <- downloader.responseCh

    if downloaderReady {
        // OUT: PuncherReady
        conn.Write(common.Mtob(common.PuncherReady))
        fmt.Printf("Told uploader '%s' that downloader was ready\n", ulAddrStr)
    } else {
        // OUT: PuncherNotReady
        conn.Write(common.Mtob(common.PuncherNotReady))
        fmt.Printf("Told uploader '%s' that downloader was **NOT** ready\n", ulAddrStr)
    }
}*/

//func ping(conn net.Conn) bool {
//    connAddrStr := conn.RemoteAddr().String()
//
//    // OUT: PuncherPing
//    if _, err := conn.Write(common.Mtob(common.PuncherPing)); err != nil {
//        fmt.Fprintf(os.Stderr, "Error for '%': %s\n", connAddrStr, err)
//        return false
//    }
//
//    pongBuffer := make([]byte, 1)
//
//    conn.SetReadDeadline(time.Now().Add(time.Second * 30))
//
//    // IN: PuncherPong
//    if _, err := conn.Read(pongBuffer); err != nil {
//        return false
//    }
//
//    conn.SetReadDeadline(time.Time{})
//
//    pong := pongBuffer[0]
//
//    switch common.ProtocolMessage(pong) {
//    case common.PuncherPong:
//        return true
//    default:
//        fmt.Fprintf(os.Stderr, "Protocol error from '%s': expected pong, got 0x%X\n", connAddrStr, pong)
//        return false
//    }
//}

func generateUid() (string, error) {
    uidBuff := make([]byte, common.UidLength / 2) // 2 hex chars per byte

    if _, err := rand.Read(uidBuff); err != nil {
        return "", err
    }

    return fmt.Sprintf("%x", uidBuff)
}
