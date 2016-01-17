package main

import (
	"github.com/codegangsta/cli"
	"github.com/transhift/puncher/puncher/server"
	"os"
)

func main() {
	app := cli.NewApp()
	app.Name = "Puncher"
	app.Usage = "TCP hole puncher for Transhift, a peer-to-peer file transferer"
	app.Version = "0.1.0"
	app.Flags = []cli.Flag{
		cli.StringFlag{
			Name:  "host",
			Value: "127.0.0.1",
			Usage: "host to bind to",
		},
		cli.IntFlag{
			Name:  "port",
			Value: 50977,
			Usage: "port to bind to",
		},
		cli.StringFlag{
			Name:  "app-dir",
			Usage: "application directory",
		},
	}
	app.Action = server.Start
	app.Run(os.Args)
}
