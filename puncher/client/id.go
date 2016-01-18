package client

import (
	"crypto/rand"
	"encoding/hex"
)

func findOpenId() (id string, err error) {
	targets.RLock()
	defer targets.RUnlock()

	for ok := true; ok; _, ok = targets.pool[id] {
		id, err = generateId()
		if err != nil {
			break
		}
	}
	return
}

func generateId() (string, error) {
	const Len = 16
	idBuff := make([]byte, Len / 2)
	if _, err := rand.Read(idBuff); err != nil {
		return "", err
	}
	return hex.EncodeToString(idBuff), nil
}
