package client

import "github.com/transhift/puncher/common/protocol"

func (c *client) HandleSource() error {
	// Expect ID.
	var id string
	if err := c.dec.Decode(&id); err != nil {
		return err
	}

	t, ok := FindTarget(id)
	if !ok {
		return c.enc.Encode(protocol.TargetNotFoundSignal)
	}
	if err := c.enc.Encode(protocol.OkaySignal); err != nil {
		return err
	}

	// Tell the target that the source is ready.
	t.ready <- c

	// Send source remote address.
	return c.enc.Encode(t.Conn.RemoteAddr().String())
}

func FindTarget(id string) (*target, bool) {
	targets.RLock()
	defer targets.RUnlock()

	return targets.pool[id]
}
