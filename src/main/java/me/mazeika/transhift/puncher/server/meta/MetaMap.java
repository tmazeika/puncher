package me.mazeika.transhift.puncher.server.meta;

import java.util.Optional;

public interface MetaMap
{
    /**
     * Gets an Optional describing the object associated with the given key.
     *
     * @param key the key
     * @param <T> the type of the associated object
     *
     * @return an Optional of the object
     */
    <T> Optional<T> get(MetaKey<T> key);

    /**
     * Sets the object to be associated with the given key.
     *
     * @param key the key
     * @param obj the associated object
     * @param <T> the type of the associated object
     */
    <T> void set(MetaKey<T> key, T obj);

    /**
     * Removes the entry with the given key.
     *
     * @param key the key
     */
    void remove(MetaKey key);
}
