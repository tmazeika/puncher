package main

import (
    "net"
    "fmt"
    "os"
    /*"time"*/
    "math/rand"
)

// protocol information
const (
    ProtoPort uint16 =  50977
    ProtoPortStr     = "50977"
    ProtoPeerStrLen  = 16
)

type ProtoMsg byte

// protocol messages
const (
    /*ProtoMsgPing ProtoMsg = 0x00
    ProtoMsgPong ProtoMsg = 0x01*/
)

func main() {
    laddr, err := net.ResolveTCPAddr("tcp", net.JoinHostPort("", ProtoPortStr))

    if err != nil {
        fmt.Fprintln(os.Stderr, err)
        os.Exit(1)
    }

    listener, err := net.ListenTCP("tcp", laddr)

    if err != nil {
        fmt.Fprintln(os.Stderr, err)
        os.Exit(1)
    }

    ch := make(chan *net.TCPConn)

    awaitingConns := map[string]net.TCPConn{}

    go func() {
        for {
            conn := <- ch

            if err != nil {
                fmt.Fprintln(os.Stderr, err)
                continue
            }

            var uid string
            /*var connClosed bool*/

            for {
                uid = randSeq(ProtoPeerStrLen)

                if awaitingConns[uid] == nil {
                    awaitingConns[uid] = conn
                    break
                }
            }



            // pinging
            /*go func() {
                pongBuffer := make([]byte, 1)

                go func() {
                    for ! connClosed {
                        time.Sleep(time.Second * 30)

                        if pongBuffer[0] != byte(ProtoMsgPong) {
                            conn.Close()
                            connClosed = true
                        }
                    }
                }()

                for ! connClosed {
                    conn.Write([]byte{byte(ProtoMsgPing)})
                    conn.Read(pongBuffer)
                    time.Sleep(time.Second * 30)
                }
            }()*/
        }
    }()

    for {
        conn, err := listener.AcceptTCP()

        if err != nil {
            fmt.Fprintln(os.Stderr, err)
            continue
        }

        fmt.Println("Connection from", conn.RemoteAddr().String())
        ch <- conn
    }
}

func randSeq(n int) string {
    const Letters = []rune("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789")

    b := make([]rune, n)

    for i := range b {
        b[i] = Letters[rand.Intn(len(Letters))]
    }

    return string(b)
}
