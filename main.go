package main

import (
    "github.com/codegangsta/cli"
    "github.com/transhift/puncher/puncher"
    "os"
)

func main() {
    app := cli.NewApp()
    app.Name = "Puncher"
    app.Usage = "TCP hole puncher for Transhift, a peer-to-peer file transferer"
    app.Version = "0.1.0"
    app.Flags = []cli.Flag{
        cli.StringFlag{
            Name: "host, h",
            Value: "127.0.0.1",
            Usage: "host to bind to",
        },
        cli.IntFlag{
            Name: "port, p",
            Value: 50977,
            Usage: "port to bind to",
        },
        cli.IntFlag{
            Name: "id-len",
            Value: 16,
            Usage: "length of issued identifiers",
        },
        cli.StringFlag{
            Name: "app-dir",
            Usage: "application directory",
        },
    }
    app.Action = puncher.Start

    app.Run(os.Args)
}

