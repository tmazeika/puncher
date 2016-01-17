package client

import (
	"crypto/rand"
	"encoding/hex"
)

func findOpenId() (string, error) {
	targets.RLock()
	defer targets.RUnlock()

	var id string
	for ok := false; !ok; _, ok = targets.pool[id] {
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
