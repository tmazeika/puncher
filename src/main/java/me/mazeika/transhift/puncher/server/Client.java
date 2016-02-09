package me.mazeika.transhift.puncher.server;

import io.netty.util.AttributeKey;

import java.util.Optional;

public interface Client
{
    /**
     * The attribute key, used when attaching the Client object to its context.
     */
    AttributeKey<Client> ATTR = AttributeKey.newInstance("client");

    /**
     * Gets if this client is trying to dial its peer.
     *
     * @return whether this client is trying to dial its peer
     */
    boolean isDialing();

    /**
     * Gets the ID.
     *
     * @return the ID
     */
    Optional<byte[]> getId();

    /**
     * Sets if this client is trying to dial its peer.
     *
     * @param dialing whether this client is trying to dial its peer
     */
    void setDialing(boolean dialing);

    /**
     * Sets the ID.
     *
     * @param id the ID
     */
    void setId(byte[] id);
}
