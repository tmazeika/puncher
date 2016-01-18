package client

import (
	"encoding/gob"
	"log"
	"net"
	"os"
	"github.com/transhift/puncher/common/protocol"
	"fmt"
	"sync"
)

var (
	targets = targetPool{}
)

type targetPool struct {
	sync.RWMutex

	pool map[string]*target
}

type client struct {
	net.Conn

	Logger *log.Logger
	enc    *gob.Encoder
	dec    *gob.Decoder
}

type target struct {
	*client

	ready chan *client
}

func Initialize() {
	targets.pool = map[string]*target{}
}

func New(conn net.Conn) *client {
	return &client{
		Conn:   conn,
		Logger: logger(conn),
		enc:    gob.NewEncoder(conn),
		dec:    gob.NewDecoder(conn),
	}
}

func logger(conn net.Conn) *log.Logger {
	const Flags = log.Ldate | log.Ltime | log.LUTC | log.Lshortfile
	prefix := "[" + conn.RemoteAddr().String() + "] "
	return log.New(os.Stdout, prefix, Flags)
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
