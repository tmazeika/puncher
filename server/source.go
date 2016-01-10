package server

func (c client) handleSource() error {
	// Expect identifier.
	var id string
	err := c.dec.Decode(&id)

	if err != nil {
		return err
	}

	return nil
}
