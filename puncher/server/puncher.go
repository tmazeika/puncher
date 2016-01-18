package server

import (
	"strconv"
	"github.com/codegangsta/cli"
	"log"
	"os"
	"github.com/transhift/appdir"
	"github.com/transhift/common/security"
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

	const DefAppDir = "$HOME/.transhift"
	dir, err := appdir.NewPreferNonEmpty(a.appDir, DefAppDir)
	if err != nil {
		log.Fatalln("error:", err)
	}

	const KeyName = "pcert.key"
	const CertName = "pcert.pem"
	cert, err := security.Certificate(KeyName, CertName, dir)
	if err != nil {
		log.Fatalln("error:", err)
	}

	log.Println("Starting server...")

	if err := NewServer(a.host, strconv.Itoa(a.port)).Start(cert); err != nil {
		log.Fatalln("error:", err)
	}
}
