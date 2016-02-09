package me.mazeika.transhift.puncher.server;

import io.netty.channel.ChannelHandlerContext;

import java.util.Optional;

/**
 * Maps a unique 16 byte ID to a {@link ChannelHandlerContext}.
 */
public interface IdMapper
{
    int ID_LENGTH = 16;

    /**
     * Generates a new, random, unique, and valid ID for the given context,
     * adding the mapping between the ID and context to the map. Thread-safe.
     *
     * @param ctx the context to generate an ID for
     */
    byte[] generateFor(ChannelHandlerContext ctx);

    /**
     * Finds the context mapped from the given ID.
     *
     * @param id the ID of the context to find
     *
     * @return an Optional of the found context; empty if not found
     */
    Optional<ChannelHandlerContext> find(byte[] id);

    /**
     * Removes the mapping between the given context and its ID. Thread-safe.
     *
     * @param ctx the context to remove the mapping for
     */
    void remove(ChannelHandlerContext ctx);
}
