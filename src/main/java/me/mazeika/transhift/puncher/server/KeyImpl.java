package me.mazeika.transhift.puncher.server;

import javax.inject.Inject;
import java.net.Socket;
import java.util.Optional;

public class KeyImpl<T> implements Key<T>
{
    private static int nextId;

    private final Attributes attributes;
    private final int id = nextId++;

    @Inject
    public KeyImpl(final Attributes attributes)
    {
        this.attributes = attributes;
    }

    @Override
    public Optional<T> remove(final Socket socket)
    {
        return attributes.remove(socket, id);
    }

    @Override
    public Optional<T> get(final Socket socket)
    {
        return attributes.get(socket, id);
    }

    @Override
    public Optional<T> set(final Socket socket, final T obj)
    {
        return attributes.set(socket, id, obj);
    }
}
