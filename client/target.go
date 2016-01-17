package client

import (
	"crypto/rand"
	"encoding/hex"
)

func (c *client) HandleTarget() error {


	return nil
}

func generateId() (string, error) {
	const Len = 16
	idBuff := make([]byte, Len / 2)
	if _, err := rand.Read(idBuff); err != nil {
		return "", err
	}
	return hex.EncodeToString(idBuff), nil
}
