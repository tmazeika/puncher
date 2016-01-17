package server

import (
	"crypto/tls"
	"log"
	"net"
	"time"
	"github.com/transhift/puncher/client"
)

type server struct {
	host string
	port string
}

func New(host, port string) *server {
	return &server{
		host: host,
		port: port,
	}
}

func (s server) listen() (<-chan *net.TCPConn, error) {
	const Net = "tcp"

	laddr, err := net.ResolveTCPAddr(Net, net.JoinHostPort(s.host, s.port))

	if err != nil {
		return nil, err
	}

	l, err := net.ListenTCP(Net, laddr)

	if err != nil {
		return nil, err
	}

	ch := make(chan *net.TCPConn)

	go func() {
		for {
			conn, err := l.AcceptTCP()

			if err != nil {
				log.Println("error:", err)
			} else {
				ch <- conn
			}
		}
	}()

	return ch, nil
}

func (s server) Start(cert *tls.Certificate) error {
	ch, err := s.listen()

	if err != nil {
		return err
	}

	tlsConfig := tlsConfig(*cert)

	go func() {
		for {
			tcpConn := <- ch
			tlsConn := tls.Server(tcpConn, tlsConfig)
			c := client.New(tlsConn)

			if err := useKeepAlive(tcpConn); err != nil {
				log.Println("error:", err)
			}

			go c.Handle()
		}
	}()

	return nil
}

func useKeepAlive(conn *net.TCPConn) error {
	const Period = time.Second * 30

	if err := conn.SetKeepAlive(true); err != nil {
		return err
	}

	return conn.SetKeepAlivePeriod(Period)
}

func tlsConfig(cert tls.Certificate) *tls.Config {
	return &tls.Config{
		Certificates: []tls.Certificate{cert},
		MinVersion:   tls.VersionTLS12,
	}
}
