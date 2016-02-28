package me.mazeika.transhift.puncher.server;

import me.mazeika.transhift.puncher.protocol.Protocol;

import java.util.Optional;
import java.util.stream.Stream;

public enum RemoteType
{
    /**
     * The remote that wants <em>to</em> dial its peer.
     */
    SOURCE(Protocol.REMOTE_TYPE_SOURCE),

    /**
     * The remote that wants to <em>be</em> dialed by its peer.
     */
    TARGET(Protocol.REMOTE_TYPE_TARGET);

    private final byte b;

    RemoteType(final int b)
    {
        this.b = (byte) b;
    }

    public static Optional<RemoteType> fromByte(byte b)
    {
        return Stream.of(values()).filter(type -> type.b == b).findAny();
    }
}
