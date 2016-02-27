package me.mazeika.transhift.puncher.server;

import com.google.inject.assistedinject.Assisted;
import me.mazeika.transhift.puncher.tags.Tag;

import javax.inject.Inject;
import java.io.IOException;
import java.net.Socket;
import java.util.Optional;

public class RemoteImpl implements Remote
{
    private final Socket socket;

    private Tag tag;

    @Inject
    public RemoteImpl(@Assisted final Socket socket)
    {
        this.socket = socket;
    }

    @Override
    public Socket getSocket()
    {
        return socket;
    }

    @Override
    public byte[] waitAndRead(int n) throws IOException
    {
        final byte[] b = new byte[n];

        if (socket.getInputStream().read(b) != n) {
            throw new IOException(n + " byte(s) not read");
        }

        return b;
    }

    @Override
    public Optional<Tag> getTag()
    {
        return Optional.ofNullable(tag);
    }

    @Override
    public void setTag(Tag tag)
    {
        this.tag = tag;
    }
}
