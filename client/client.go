package client

import (
	"encoding/gob"
	"log"
	"net"
	"os"
	"github.com/transhift/puncher/common/protocol"
	"fmt"
)

type client struct {
	net.Conn

	version string
	logger  *log.Logger
	enc     *gob.Encoder
	dec     *gob.Decoder
}

func New(conn net.Conn) *client {
	return &client{
		Conn:   conn,
		logger: logger(conn),
		enc:    gob.NewEncoder(conn),
		dec:    gob.NewDecoder(conn),
	}
}

func logger(conn net.Conn) *log.Logger {
	const Flags = log.Ldate | log.Ltime | log.LUTC | log.Lshortfile
	return log.New(os.Stdout, conn.RemoteAddr().String(), Flags)
}

func (c *client) Handle() error {
	// Expect NodeType.
	var nodeType protocol.NodeType
	if err := c.dec.Decode(&nodeType); err != nil {
		return err
	}

	switch nodeType {
	case protocol.TargetNode:
		return c.HandleTarget()
	case protocol.SourceNode:
		return c.HandleSource()
	default:
		return fmt.Errorf("invalid NodeType 0x%x", nodeType)
	}
}

func (c *client) HandleTarget() error {
	return nil
}

func (c *client) HandleSource() error {
	return nil
}
