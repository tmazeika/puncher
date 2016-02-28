package me.mazeika.transhift.puncher.protocol;

public final class Protocol
{
    private Protocol() { }

    public static final byte REMOTE_TYPE_SOURCE = 0x00;
    public static final byte REMOTE_TYPE_TARGET = 0x01;

    public static final byte PEER_NOT_FOUND     = 0x02;
    public static final byte PEER_FOUND         = 0x03;
}
