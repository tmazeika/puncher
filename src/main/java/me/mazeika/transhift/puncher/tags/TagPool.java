package me.mazeika.transhift.puncher.tags;

/**
 * Represents a pool of unique 16 byte tags.
 */
public interface TagPool
{
    /**
     * Generates a new, random, and unique tag, adding it to the pool.
     * Thread-safe.
     *
     * @return the created {@link Tag}
     */
    Tag generate();

    /**
     * Removes the given tag from the pool. Thread-safe.
     *
     * @param tag the tag to remove
     */
    void remove(Tag tag);
}
