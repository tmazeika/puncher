package me.mazeika.transhift.puncher.server.meta;

@SuppressWarnings("unused")
final class MetaKey<T>
{
    private static int nextId;

    protected final int id = nextId++;
}
