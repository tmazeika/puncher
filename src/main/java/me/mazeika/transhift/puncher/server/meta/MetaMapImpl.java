package me.mazeika.transhift.puncher.server.meta;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

class MetaMapImpl implements MetaMap
{
    private final Map<MetaKey, Object> map = new HashMap<>();

    @Override
    public <T> Optional<T> get(final MetaKey<T> key)
    {
        // noinspection unchecked
        return (Optional<T>) map.entrySet().stream()
                .filter(entry -> entry.getKey().id == key.id)
                .map(Map.Entry::getValue)
                .findAny();
    }

    @Override
    public <T> void set(final MetaKey<T> key, final T obj)
    {
        map.put(key, obj);
    }

    @Override
    public void remove(final MetaKey key)
    {
        map.entrySet().removeIf(entry -> entry.getKey().id == key.id);
    }
}
