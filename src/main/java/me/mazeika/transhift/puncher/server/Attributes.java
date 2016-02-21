package me.mazeika.transhift.puncher.server;

import org.glassfish.grizzly.Grizzly;
import org.glassfish.grizzly.attributes.Attribute;

public final class Attributes
{
    public static final Attribute<RemoteType> REMOTE_TYPE =
            Grizzly.DEFAULT_ATTRIBUTE_BUILDER.createAttribute("remote_type");
    public static final Attribute<byte[]> TAG =
            Grizzly.DEFAULT_ATTRIBUTE_BUILDER.createAttribute("tag");

    private Attributes() { }
}
