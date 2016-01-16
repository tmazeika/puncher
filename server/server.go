package server

import (
	"crypto/tls"
	"encoding/gob"
	"errors"
	"log"
	"net"
	"os"
	"time"
)

type server struct {
	host       string
	port       string
	idLen      uint
	targetPool biMap
}

type client struct {
	net.Conn

	server *server
	logger *log.Logger
	enc    *gob.Encoder
	dec    *gob.Decoder
}

type target struct {
	*client

	ready  chan<- *source
	connAt <-chan time.Time
}

type source struct {
	*client

	latency time.Duration
}

func New(host, port string, idLen uint) *server {
	return &server{
		host:  host,
		port:  port,
		idLen: idLen,
	}
}

func (s server) Start(cert *tls.Certificate) error {
	const KeepAlivePeriod = time.Second * 30

	tlsConfig := tls.Config{
		Certificates: []tls.Certificate{*cert},
		MinVersion:   tls.VersionTLS12,
	}
	laddr, err := net.ResolveTCPAddr("tcp", net.JoinHostPort(s.host, s.port))

	if err != nil {
		return err
	}

	listener, err := net.ListenTCP("tcp", laddr)

	if err != nil {
		return err
	}

	go func() {
		for {
			tcpConn, err := listener.AcceptTCP()

			if err != nil {
				Logger.Println("error:", err)
				continue
			}

			tlsConn := tls.Server(tcpConn, &tlsConfig)
			c := client{
				Conn:   tlsConn,
				server: &s,
				logger: log.New(os.Stdout, tcpConn.RemoteAddr().String(), LogFlags),
			}

			if err := tcpConn.SetKeepAlive(true); err != nil {
				c.logger.Println("error:", err)
				continue
			}

			if err := tcpConn.SetKeepAlivePeriod(KeepAlivePeriod); err != nil {
				c.logger.Println("error:", err)
				continue
			}

			go c.handle()
		}
	}()

	return nil
}

func (c client) handle() {
	defer c.Conn.Close()

	c.enc = gob.NewEncoder(c.Conn)
	c.dec = gob.NewDecoder(c.Conn)

	// Expect client type.
	var clientType protocol.ClientType
	err := c.dec.Decode(&clientType)

	if err != nil {
		c.logger.Println("error:", err)
		return
	}

	switch clientType {
	case protocol.TargetClient:
		err = c.handleTarget()
	case protocol.SourceClient:
		err = c.handleSource()
	default:
		c.logger.Printf("error: unknown client type 0x%x\n", clientType)
	}

	if err != nil {
		c.logger.Println("error:", err)
	}
}

func (c client) measureLatency() (time.Duration, error) {
	// Send ping signal.
	err := c.enc.Encode(protocol.PingSignal)

	if err != nil {
		return 0, err
	}

	var sig protocol.Signal
	startTime := time.Now()

	// Expect pong signal.
	if err = c.dec.Decode(&sig); err != nil {
		return 0, err
	}

	stopTime := time.Now()

	if sig != protocol.PongSignal {
		handleBadSig(c.logger, sig)
		return 0, errors.New("couldn't measure latency")
	}

	return stopTime.Sub(startTime), nil
}

func handleBadSig(logger *log.Logger, sig protocol.Signal) {
	if sig == -1 {
		logger.Println("error receiving signal")
	} else {
		logger.Println("got unexpected signal")
	}
}
