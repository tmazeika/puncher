package me.mazeika.transhift.puncher.server;

import com.google.inject.assistedinject.Assisted;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Optional;

class RemoteImpl implements Remote
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
        final InputStream in = socket.getInputStream();

        int tempB;

        for (int i = 0; i < n; i++) {
            if ((tempB = in.read()) == -1) {
                throw new IllegalStateException("eof");
            }

            b[i] = (byte) tempB;
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

    @Override
    public String toString()
    {
        return socket.getRemoteSocketAddress().toString();
    }
}
