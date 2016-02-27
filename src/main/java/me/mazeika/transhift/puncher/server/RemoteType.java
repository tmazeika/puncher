package me.mazeika.transhift.puncher.server;

import java.util.Optional;
import java.util.stream.Stream;

public enum RemoteType
{
    /**
     * The remote that wants <em>to</em> dial its peer.
     */
    SOURCE(0x00),

    /**
     * The remote that wants to <em>be</em> dialed by its peer.
     */
    TARGET(0x01);

    private final byte b;

    RemoteType(int b)
    {
        this.b = (byte) b;
    }

    public static Optional<RemoteType> fromByte(byte b)
    {
        return Stream.of(values()).filter(type -> type.b == b).findAny();
    }
}
