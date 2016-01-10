package server

import (
	"crypto/rand"
	"encoding/hex"
)

func (c client) handleTarget() (err error) {
	var id string

	for c.server.targetPool.contains(id) {
		id, err = generateId(c.server.idLen)

		if err != nil {
			return
		}
	}

	c.server.targetPool.put(id, c)
	defer c.server.targetPool.del(id)
}

func generateId(len int) (string, error) {
	idBuff := make([]byte, len / 2 + 1) // 2 hex chars per byte, +1 to ceil

	if _, err := rand.Read(idBuff); err != nil {
		return "", err
	}

	return hex.EncodeToString(idBuff[:len]), nil
}
