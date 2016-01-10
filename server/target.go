package server

import (
	"crypto/rand"
	"encoding/hex"
	"github.com/transhift/common/protocol"
	"time"
)

func (c client) handleTarget() (err error) {
	c.logger.Println("identified as target")

	var id string

	// Generate identifier.
	for c.server.targetPool.contains(id) {
		id, err = generateId(c.server.idLen)

		if err != nil {
			return
		}
	}

	readyCh := make(chan *source)
	connAtCh := make(chan time.Time)
	target := target{&c, readyCh, connAtCh}

	c.server.targetPool.put(id, &target)
	defer c.server.targetPool.del(id)

	// Send identifier.
	err = c.enc.Encode(id)

	if err != nil {
		return
	}

	select {
	// Receive remote signal.
	case sig := <-protocol.SignalChannel(c.dec):
		if sig == protocol.ExitSignal {
			c.logger.Println("got exit signal")
		} else {
			handleBadSig(c.logger, sig)
		}
	// Receive source ready.
	case source := <-readyCh:
		// Send source address.
		err = c.enc.Encode(source.RemoteAddr().String())

		if err != nil {
			return
		}

		// Expect okay signal.
		var sig protocol.Signal
		err = c.dec.Decode(&sig)

		if err != nil {
			return
		}

		if sig == protocol.OkaySignal {
			c.logger.Println("got okay signal")
		} else {
			handleBadSig(c.logger, sig)
			return
		}

		latency, err := c.measureLatency()

		if err != nil {
			return
		}

		connAt := time.Now()

		// Use maximum latency.
		if latency.Nanoseconds() > source.latency.Nanoseconds() {
			connAt = connAt.Add(latency)
		} else {
			connAt = connAt.Add(source.latency)
		}

		connAtCh <- connAt.Add(time.Second)
	}

	return
}

func generateId(len uint) (string, error) {
	idBuff := make([]byte, len/2+1) // 2 hex chars per byte, +1 to ceil

	if _, err := rand.Read(idBuff); err != nil {
		return "", err
	}

	return hex.EncodeToString(idBuff[:len]), nil
}
