package client

import "errors"

func (c *client) HandleTarget() error {
	id, err := findOpenId()
	if err != nil {
		return err
	}

	t := target{
		client: c,
		ready:  make(chan *client),
	}

	targets.Lock()
	targets.pool[id] = t
	targets.Unlock()

	// Send ID.
	if err := c.enc.Encode(id); err != nil {
		return err
	}

	// Wait for source ready.
	s, ok := <-t.ready
	if !ok {
		return errors.New("read ready channel error")
	}

	// Send source address.
	return c.enc.Encode(s.RemoteAddr().String())
}
