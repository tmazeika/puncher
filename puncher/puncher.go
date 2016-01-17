package puncher

import (
	"strconv"
	"github.com/codegangsta/cli"
	"github.com/transhift/common/storage"
	"github.com/transhift/puncher/server"
	"crypto/tls"
	"log"
	"os"
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
	log.SetOutput(os.Stdout)
	log.SetFlags(log.Ldate | log.Ltime | log.LUTC | log.Lshortfile)

	a := args{
		host:   c.GlobalString("host"),
		port:   c.GlobalInt("port"),
		appDir: c.GlobalString("app-dir"),
	}

	cert, err := Cert(&a)

	if err != nil {
		log.Fatalln("Error:", err)
	}

	log.Println("Starting server...")

	if err := server.New(a.host, strconv.Itoa(a.port)).Start(cert); err != nil {
		log.Fatalln("Error:", err)
	}
}

func Cert(a *args) (*tls.Certificate, error) {
	s, err := storage.New(a.appDir, nil)

	if err != nil {
		return nil, err
	}

	return s.Certificate(CertName, KeyName)
}
