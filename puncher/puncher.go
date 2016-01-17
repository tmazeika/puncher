package puncher

import (
	"strconv"
	"github.com/codegangsta/cli"
	"github.com/transhift/common/storage"
	"github.com/transhift/puncher/server"
	"crypto/tls"
	"github.com/transhift/common/logging"
)

const (
	CertName = "pcert.pem"
	KeyName  = "pcert.key"
)

type args struct {
	host   string
	port   int
	appDir string
}

func Start(c *cli.Context) {
	a := args{
		host:   c.GlobalString("host"),
		port:   c.GlobalInt("port"),
		appDir: c.GlobalString("app-dir"),
	}

	cert, err := Cert(&a)

	if err != nil {
		logging.Logger.Fatalln("error:", err)
	}

	err = Server(&a, cert)

	if err != nil {
		logging.Logger.Fatalln("error:", err)
	}
}

func Cert(a *args) (*tls.Certificate, error) {
	s, err := storage.New(a.appDir, nil)

	if err != nil {
		return nil, err
	}

	return s.Certificate(CertName, KeyName)
}

func Server(a *args, cert *tls.Certificate) error {
	s := server.New(a.host, strconv.Itoa(a.port))

	if err := s.Start(cert); err != nil {
		return err
	}

	return nil
}
