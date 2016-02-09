package me.mazeika.transhift.puncher.server;

public interface IdPool
{
    int ID_LEN = 16;

    /**
     * Generates a new, random, unique, and valid ID, automatically adding it to
     * the pool. Thread-safe.
     *
     * @return the generated ID
     */
    byte[] generate();

    /**
     * Removes the given ID from the pool. Thread-safe.
     *
     * @param id the ID to remove
     */
    void remove(byte[] id);
}
