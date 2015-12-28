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
            Name: "port, p",
            Value: "50977",
            Usage: "port to bind to",
        },
    }

    app.Action = puncher.Start

    app.Run(os.Args)
}

