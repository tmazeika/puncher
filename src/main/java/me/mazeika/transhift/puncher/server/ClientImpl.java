package me.mazeika.transhift.puncher.server;

import java.util.Optional;

public class ClientImpl implements Client
{
    private boolean dialing;
    private byte[] id;

    @Override
    public boolean isDialing()
    {
        return dialing;
    }

    @Override
    public Optional<byte[]> getId()
    {
        return Optional.ofNullable(id);
    }

    @Override
    public void setDialing(boolean dialing)
    {
        this.dialing = dialing;
    }

    @Override
    public void setId(byte[] id)
    {
        if (id.length != IdPool.ID_LEN) {
            throw new IllegalArgumentException(String.format(
                    "ID length not %d (got %d)", IdPool.ID_LEN, id.length));
        }

        this.id = id;
    }
}
