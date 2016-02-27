package me.mazeika.transhift.puncher.tags;

import me.mazeika.transhift.puncher.server.Remote;

import java.util.Optional;

/**
 * Represents a pool of unique 16 byte tags.
 */
public interface TagPool
{
    /**
     * Generates a new, random, and unique tag, automatically adding it to the
     * pool, for the given remote. Thread-safe.
     *
     * @param remote the remote to map the tag to
     */
    void generateFor(Remote remote);

    /**
     * Finds and removes the given tag from the pool, returning an Optional
     * describing its associated {@link Remote}. Thread-safe.
     *
     * @param tag the tag to remove
     *
     * @return an Optional describing the associated Remote
     */
    Optional<Remote> findAndRemove(Tag tag);
}
