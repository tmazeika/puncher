package me.mazeika.transhift.puncher.server;

/**
 * Represents a pool of unique 16 byte ID's.
 */
public interface IdPool
{
    int ID_LENGTH = 16;

    /**
     * Generates a new, random, and unique ID, automatically adding it to the
     * pool. Thread-safe.
     */
    byte[] generate();

    /**
     * Removes the given ID from the pool. Thread-safe.
     *
     * @param id the ID to remove
     */
    void remove(byte[] id);
}
