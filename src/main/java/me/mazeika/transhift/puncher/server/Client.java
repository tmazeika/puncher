package me.mazeika.transhift.puncher.server;

import io.netty.util.AttributeKey;

import java.util.Optional;

public class Client
{
    public static AttributeKey<Client> ATTR =
            AttributeKey.newInstance("client");

    private boolean dialing;
    private byte[] id;

    public boolean isDialing()
    {
        return dialing;
    }

    public Optional<byte[]> getId()
    {
        return Optional.ofNullable(id);
    }

    public void setDialing(boolean dialing)
    {
        this.dialing = dialing;
    }

    public void setId(byte[] id)
    {
        this.id = id;
    }
}
