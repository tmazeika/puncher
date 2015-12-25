package main

import (
    "net"
    "fmt"
    "os"
    "math/rand"
    "time"
    "sync"
)

// protocol information
const (
    ProtoPort uint16 =  50977
    ProtoPortStr     = "50977"
    ProtoPeerUIDLen  = 16
)

type ProtoMsg byte

// protocol messages
const (
    ProtoMsgClientTypeDL ProtoMsg = 0x00
    ProtoMsgClientTypeUL ProtoMsg = 0x01
)

var awaitingConns = map[string]net.Conn{}
var awaitingConnsMutex = &sync.Mutex{}

func main() {
    listener, err := net.Listen("tcp", net.JoinHostPort("", ProtoPortStr))

    if err != nil {
        fmt.Fprintln(os.Stderr, err)
        os.Exit(1)
    }

    for {
        conn, err := listener.Accept()

        go func() {
            if err != nil {
                fmt.Fprintln(os.Stderr, err)
                return
            }

            clientTypeBuffer := make([]byte, 1)
            conn.Read(clientTypeBuffer)

            switch ProtoMsg(clientTypeBuffer[0]) {
            case ProtoMsgClientTypeDL:
                go handleDownloader(conn)
            case ProtoMsgClientTypeUL:
                go handleUploader(conn)
            default:
                fmt.Fprintln(os.Stderr, "Protocol error")
            }
        }()
    }
}

func handleDownloader(conn net.Conn) {
    var uid string
    awaitingConnsMutex.Lock()

    for uid == "" || awaitingConns[uid] != nil {
        uid = randSeq(ProtoPeerUIDLen)
    }

    awaitingConns[uid] = conn
    awaitingConnsMutex.Unlock()
    conn.Write([]byte(uid))
    conn.Close()
}

func handleUploader(conn net.Conn) {
    uidBuffer := make([]byte, ProtoPeerUIDLen)
    conn.Read(uidBuffer)
    uid := string(uidBuffer)
    awaitingConnsMutex.Lock()

    for awaitingConns[uid] == nil {
        awaitingConnsMutex.Unlock()
        time.Sleep(time.Millisecond * 100)
        awaitingConnsMutex.Lock()
    }

    awaitingConn := awaitingConns[uid]
    awaitingConnsMutex.Unlock()
    conn.Write([]byte(awaitingConn.RemoteAddr().String() + "\n"))
    conn.Close()
    delete(awaitingConns, uid)
}

func randSeq(n int) string {
    letters := []rune("abcdefghjklmnpqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ23456789")

    b := make([]rune, n)

    for i := range b {
        b[i] = letters[rand.Intn(len(letters))]
    }

    return string(b)
}
