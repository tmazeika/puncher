package me.mazeika.transhift.puncher.server;

import com.google.inject.assistedinject.Assisted;
import me.mazeika.transhift.puncher.server.meta.MetaMap;
import me.mazeika.transhift.puncher.tags.Tag;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

class RemoteImpl implements Remote
{
    private final MetaMap metaMap;
    private final Socket socket;

    @Inject
    public RemoteImpl(final MetaMap metaMap, @Assisted final Socket socket)
    {
        this.metaMap = metaMap;
        this.socket = socket;
    }

    @Override
    public Socket socket()
    {
        return socket;
    }

    @Override
    public InputStream in() throws IOException
    {
        return socket.getInputStream();
    }

    @Override
    public OutputStream out() throws IOException
    {
        return socket.getOutputStream();
    }

    @Override
    public MetaMap meta()
    {
        return metaMap;
    }

    @Override
    public String toString()
    {
        return socket.getRemoteSocketAddress().toString();
    }
}
