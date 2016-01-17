package client

import (
	"crypto/rand"
	"encoding/hex"
)

func (c *client) HandleTarget() error {
	id, err := findOpenId()
	if err != nil {
		return err
	}

	targetPool.Lock()
	target[id] = c
	targetPool.Unlock()

	return nil
}

func findOpenId() (string, error) {
	targetPool.RLock()
	defer targetPool.RUnlock()

	var id string
	for ok := false; !ok; _, ok = targetPool[id] {
		i, err := generateId()

		if err != nil {
			return "", err
		}

		id = i
	}

	return id, nil
}

func generateId() (string, error) {
	const Len = 16
	idBuff := make([]byte, Len / 2)
	if _, err := rand.Read(idBuff); err != nil {
		return "", err
	}
	return hex.EncodeToString(idBuff), nil
}
