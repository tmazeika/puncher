package me.mazeika.transhift.puncher.server;

import java.util.Optional;

public enum RemoteType
{
    SOURCE (0x30),
    TARGET (0x31),
    ;

    private final byte b;

    RemoteType(int i)
    {
        b = (byte) i;
    }

    public static Optional<RemoteType> fromByte(byte b)
    {
        for (RemoteType t : values()) {
            if (t.b == b) {
                return Optional.of(t);
            }
        }

        return Optional.empty();
    }
}
