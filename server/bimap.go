package server

type biMap struct {
	m map[string]*target
	r map[*target]string
}

func (b *biMap) put(k string, v *target) {
	b.m[k] = v
	b.r[v] = k
}

func (b *biMap) del(k string) {
	v := b.m[k]
	delete(b.m, k)
	delete(b.r, v)
}

func (b *biMap) contains(k string) bool {
	_, found := b.m[k]
	return found
}

func (b *biMap) containsRev(v *target) bool {
	_, found := b.r[v]
	return found
}

func (b *biMap) get(k string) *target {
	return b.m[k]
}

func (b *biMap) getRev(v *target) string {
	return b.r[v]
}
