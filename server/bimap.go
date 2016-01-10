package server
import "sync"

type biMap struct {
	sync.RWMutex

	m map[string]*target
	r map[*target]string
}

func (b *biMap) put(k string, v *target) {
	b.Lock()
	defer b.Unlock()

	b.m[k] = v
	b.r[v] = k
}

func (b *biMap) del(k string) {
	b.RLock()
	v := b.m[k]
	b.RUnlock()

	b.Lock()
	defer b.Unlock()

	delete(b.m, k)
	delete(b.r, v)
}

func (b *biMap) contains(k string) bool {
	b.RLock()
	defer b.RUnlock()

	_, found := b.m[k]
	return found
}

func (b *biMap) containsRev(v *target) bool {
	b.RLock()
	defer b.RUnlock()

	_, found := b.r[v]
	return found
}

func (b *biMap) get(k string) (*target, bool) {
	b.RLock()
	defer b.RUnlock()

	return b.m[k]
}

func (b *biMap) getRev(v *target) (string, bool) {
	b.RLock()
	defer b.RUnlock()

	return b.r[v]
}
