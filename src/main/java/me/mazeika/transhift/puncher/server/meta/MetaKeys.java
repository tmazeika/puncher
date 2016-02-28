package me.mazeika.transhift.puncher.server.meta;

import me.mazeika.transhift.puncher.server.Remote;
import me.mazeika.transhift.puncher.tags.Tag;

public final class MetaKeys
{
    private MetaKeys() { }

    public static final MetaKey<Tag>    TAG  = new MetaKey<>();
    public static final MetaKey<Remote> PEER = new MetaKey<>();
}
