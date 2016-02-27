package me.mazeika.transhift.puncher;

/**
 * Represents a pool of unique 16 byte tags.
 */
public interface TagPool
{
    int LENGTH = 16;

    /**
     * Generates a new, random, and unique tag, automatically adding it to the
     * pool. Thread-safe.
     *
     * @return the generated tag
     */
    Tag generate();

    /**
     * Removes the given tag from the pool. Thread-safe.
     *
     * @param tag the tag to remove
     */
    void remove(Tag tag);
}
