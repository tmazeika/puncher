package me.mazeika.transhift.puncher.server;

import io.netty.util.AttributeKey;

import java.util.Optional;

public class Client
{
    /**
     * The attribute key, used when attaching the Client object to its context.
     */
    public static AttributeKey<Client> ATTR =
            AttributeKey.newInstance(Client.class.getSimpleName());

    private boolean dialing;
    private byte[] id;

    /**
     * Gets if this client is trying to dial its peer.
     *
     * @return whether this client is trying to dial its peer
     */
    public boolean isDialing()
    {
        return dialing;
    }

    /**
     * Gets the ID.
     *
     * @return the ID
     */
    public Optional<byte[]> getId()
    {
        return Optional.ofNullable(id);
    }

    /**
     * Sets if this client is trying to dial its peer.
     *
     * @param dialing whether this client is trying to dial its peer
     */
    public void setDialing(boolean dialing)
    {
        this.dialing = dialing;
    }

    /**
     * Sets the ID.
     *
     * @param id the ID
     */
    public void setId(byte[] id)
    {
        this.id = id;
    }
}
