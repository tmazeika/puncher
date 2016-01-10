package server

import (
	"net"
	"crypto/tls"
	"time"
	"log"
	"os"
)

const LogFlags = log.Ldate | log.Ltime | log.LUTC | log.Lshortfile

var logger = log.New(os.Stdout, "", LogFlags)

type server struct {
	host   string
	port   string
	idLen  uint
	cert   *tls.Certificate
}

type client struct {
	net.Conn

	logger log.Logger
}

func New(host, port string, idLen uint, cert *tls.Certificate) *server {
	return &server{host, port, idLen, cert}
}

func (s server) Listen() error {
	const KeepAlivePeriod = time.Second * 30

	tlsConfig := tls.Config{
		Certificates: []tls.Certificate{s.cert},
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

	for {
		conn, err := listener.AcceptTCP()

		if err != nil {
			logger.Println("error:", err)
			continue
		}

		tlsConn := tls.Server(conn, &tlsConfig)
		c := client{tlsConn, log.New(os.Stdout, conn.RemoteAddr().String(), LogFlags)}

		if err := conn.SetKeepAlive(true); err != nil {
			c.logger.Println("error:", err)
			continue
		}

		if err := conn.SetKeepAlivePeriod(KeepAlivePeriod); err != nil {
			c.logger.Println("error:", err)
			continue
		}

		s.handleConn(c)
	}
}

func (s server) handleConn(c client) {

}
