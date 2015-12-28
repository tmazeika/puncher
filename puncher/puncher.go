package puncher

import (
    "github.com/transhift/common/common"
    "net"
    "fmt"
    "os"
    "sync"
    "math/rand"
    "bufio"
    "time"
    "github.com/codegangsta/cli"
    "crypto/tls"
)

const (
    CertFileName = "puncher_cert.pem"
    KeyFileName = "puncher_cert.key"
)

type uidConnMap map[string]net.Conn

type Args struct {
    port   string
    appDir string
}

func (a Args) PortOrDef(def string) string {
    if len(a.port) == 0 {
        return def
    }

    return a.port
}

func Start(c *cli.Context) {
    args := Args{
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
        MinVersion: tls.VersionTLS12,
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

        go func() {
            clientTypeBuffer := make([]byte, 1)
            conn.Read(clientTypeBuffer)

            switch ProtocolMessage(clientTypeBuffer[0]) {
            case DownloadClientType:
                handleDownloader(conn, downloaders, downloadersMutex)
            case UploadClientType:
                handleUploader(conn, downloaders, downloadersMutex)
            default:
                fmt.Fprintln(os.Stderr, "Protocol error")
            }
        }()
    }
}

func handleDownloader(conn net.Conn, downloaders uidConnMap, downloadersMutex *sync.Mutex) {
    defer conn.Close()

    var uid string

    downloadersMutex.Lock()

    for len(uid) == 0 || downloaders[uid] != nil {
        uid = randSeq(UidLength)
    }

    downloaders[uid] = conn

    downloadersMutex.Unlock()

    if _, err := conn.Write([]byte(uid)); err != nil {
        fmt.Fprintln(os.Stderr, err)
    }
}

func handleUploader(conn net.Conn, downloaders uidConnMap, downloadersMutex *sync.Mutex) {
    defer conn.Close()

    uidBuffer := make([]byte, UidLength)

    if _, err := conn.Read(uidBuffer); err != nil {
        fmt.Fprintln(os.Stderr, err)
    }

    uid := string(uidBuffer)

    downloadersMutex.Lock()

    for downloaders[uid] == nil {
        downloadersMutex.Unlock()
        time.Sleep(time.Second)
        downloadersMutex.Lock()
    }

    dlConn := downloaders[uid]

    downloadersMutex.Unlock()

    out := bufio.NewWriter(conn)

    out.WriteString(dlConn.RemoteAddr().String())
    out.WriteRune('\n')
    out.Flush()

    delete(downloaders, uid)
}

func randSeq(n int) string {
    letters := []rune("abcdefghjklmnpqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ23456789")
    seq := make([]byte, n)

    for i := range seq {
        seq[i] = byte(letters[rand.Intn(len(letters))])
    }

    return string(seq)
}
