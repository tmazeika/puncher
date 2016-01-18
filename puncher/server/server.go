package server

import (
	"crypto/tls"
	"log"
	"net"
	"time"
	"github.com/transhift/puncher/puncher/client"
)

type server struct {
	host string
	port string
}

func NewServer(host, port string) *server {
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
		log.Println("Listening @", l.Addr().String())

		for {
			conn, err := l.AcceptTCP()

			if err != nil {
				log.Println("Error:", err)
			} else {
				log.Println("Accepted client:", conn.RemoteAddr().String())
				ch <- conn
			}
		}
	}()

	return ch, nil
}

func (s server) Start(cert tls.Certificate) error {
	ch, err := s.listen()

	if err != nil {
		return err
	}

	tlsConfig := tlsConfig(cert)

	for {
		tcpConn := <- ch
		tlsConn := tls.Server(tcpConn, tlsConfig)
		c := client.New(tlsConn)

		if err := useKeepAlive(tcpConn); err != nil {
			c.Logger.Println("error:", err)
			continue
		}

		go func() {
			defer tlsConn.Close()

			if err := c.Handle(); err != nil {
				c.Logger.Println("error:", err)
			}
		}()
	}

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
