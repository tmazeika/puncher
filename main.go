package main

import (
    "net"
    "fmt"
    "os"
    "math/rand"
    "time"
    "sync"
    "bufio"
)

const (
    // TODO: these should be in a config
    Port uint16 =  50977
    PortStr     = "50977"

    // UidLength is the length of the UID that the puncher server issues.
    UidLength = 16
)

type ProtocolMessage byte

const (
    DownloadClientType ProtocolMessage = 0x00
    UploadClientType   ProtocolMessage = 0x01
)

type uidConnMap map[string]net.Conn

func main() {
    // TODO: uncomment in release
    //rand.Seed(int64(time.Now().Nanosecond()))

    listener, err := net.Listen("tcp", net.JoinHostPort("", PortStr))

    if err != nil {
        fmt.Fprintln(os.Stderr, err)
        os.Exit(1)
    }

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
                fmt.Printf("DEBUG: received download client -> %s\n", conn.RemoteAddr().String())
                handleDownloader(conn, downloaders, downloadersMutex)
            case UploadClientType:
                fmt.Printf("DEBUG: received upload client -> %s\n", conn.RemoteAddr().String())
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

    fmt.Printf("DEBUG: wrote to downloader: '%s'\n", uid)
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

    fmt.Printf("DEBUG: wrote to uploader: '%s'\n", dlConn.RemoteAddr().String())

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
