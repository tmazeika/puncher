package puncher

/*
import (
    "net"
    "github.com/transhift/common/common"
    "time"
    "fmt"
    "sync"
    "os"
    "errors"
    "encoding/binary"
)

var (
    ErrTimeOut = errors.New("peer timed out")
    ErrInvalidUid = errors.New("peer sent invalid UID")
    ErrUnexpectedResponse = errors.New("peer sent unexpected response")
)

type DownloaderPool struct {
    downloaders map[string]*Downloader
}

func (p *DownloaderPool) Take(uid string) *Downloader {
    if d, ok := p.downloaders[uid]; ok {
        delete(p.downloaders, uid)
        return &d
    }

    return nil
}

type Downloader struct {
    sync.Mutex

    conn    net.Conn
    uid     string
    claimed bool
}

func (d *Downloader) Claim() {
    d.claimed = true
}

type Uploader struct {
    sync.Mutex

    conn         net.Conn
    stopPinging  bool
    awaitingPing bool
}

func (u *Uploader) ExpectUid() (string, error) {
    uidBuff := make([]byte, common.UidLength)

    select {
    case read, err := u.conn.Read(uidBuff):
        if err != nil {
            return "", err
        }

        if read != common.UidLength {
            return "", ErrInvalidUid
        }
    case time.After(time.Second * 30):
        return "", ErrTimeOut
    }

    return string(uidBuff), nil
}

func (u *Uploader) Ping() error {
    if _, err := u.conn.Write(common.Mtob(common.Ping)); err != nil {
        return err
    }

    resBuff := make([]byte, 1)

    select {
    case _, err := u.conn.Read(resBuff):
        if err != nil {
            return err
        }
    case time.After(time.Second * 30):
        return ErrTimeOut
    }

    if common.ProtocolMessage(resBuff[0]) != common.Pong {
        return ErrUnexpectedResponse
    }

    return nil
}

type Broadcaster struct {
    eventChs []chan Downloader
}

func (b *Broadcaster) Broadcast(e Downloader) {
    for _, ch := range b.eventChs {
        ch <- e
    }
}

func (b *Broadcaster) Subscribe() (eventCh chan Downloader) {
    eventCh = make(chan Downloader)
    append(b.eventChs, eventCh)
    return
}

func handleUploader(u *Uploader, p *DownloaderPool, b *Broadcaster) {
    uid, err := u.ExpectUid()

    if err != nil {
        fmt.Fprintln(os.Stderr, "error while expecting UID: ", err)
        return
    }

    downloader := p.Take(uid)

    if downloader == nil {
        go func() {
            time.Sleep(time.Second * 30)

            for ! u.stopPinging {
                u.Lock()
                u.Ping()
                u.Unlock()
                time.Sleep(time.Second * 30)
            }
        }()

        select {
        case downloader = <- b.Subscribe():
            u.Lock()
            u.stopPinging = true
            u.Unlock()
        case time.After(time.Hour):
            fmt.Fprintln(os.Stderr, "uploader didn't receive downloader after 1 hour")
            return
        }
    }
}
*/
