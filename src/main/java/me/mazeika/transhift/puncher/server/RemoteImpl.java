package me.mazeika.transhift.puncher.server;

import com.google.inject.assistedinject.Assisted;
import me.mazeika.transhift.puncher.server.meta.MetaMap;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Collection;

class RemoteImpl implements Remote
{
    private final Collection<Remote> globalPool;
    private final MetaMap metaMap;
    private final Socket socket;

    private boolean destroyed;

    @Inject
    public RemoteImpl(@Pool final Collection<Remote> globalPool,
                      final MetaMap metaMap, @Assisted final Socket socket)
    {
        this.globalPool = globalPool;
        this.metaMap = metaMap;
        this.socket = socket;

        synchronized (this.globalPool) {
            globalPool.add(this);
        }
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
    public void destroy()
    {
        destroyed = true;

        synchronized (globalPool) {
            globalPool.remove(this);
        }

        synchronized (this) {
            notify();
        }
    }

    @Override
    public boolean isDestroyed()
    {
        return destroyed;
    }

    @Override
    public String toString()
    {
        return socket.getRemoteSocketAddress().toString();
    }
}
