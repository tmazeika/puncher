package me.mazeika.transhift.puncher.server;

import io.netty.util.AttributeKey;

import java.security.SecureRandom;
import java.util.Optional;

public class Client
{
    public static int ID_LEN = 16;

    /**
     * The attribute key, used when attaching the Client object to its context.
     */
    public static AttributeKey<Client> ATTR =
            AttributeKey.newInstance(Client.class.getSimpleName());

    private static final SecureRandom random = new SecureRandom();

    private boolean dialing;
    private byte[] id;

    /**
     * Validates the given ID. The ID is valid if and only if its length is
     * equal to {@link Client#ID_LEN}.
     *
     * @param id the ID to validate
     *
     * @return {@code true} if the ID is valid, {@code false} otherwise
     */
    public static boolean validateId(byte[] id)
    {
        return id.length == ID_LEN;
    }

    /**
     * Generates a new, random, valid ID such that {@link #validateId(byte[])}
     * would return {@code true} on the resulting ID.
     *
     * @return a new ID
     */
    public static byte[] generateId()
    {
        final byte[] id = new byte[ID_LEN];

        random.nextBytes(id);

        return id;
    }

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
