package me.mazeika.transhift.puncher.server;

import me.mazeika.transhift.puncher.tags.Tag;

import java.io.IOException;
import java.net.Socket;
import java.util.Optional;

public interface Remote
{
    Socket getSocket();

    byte[] waitAndRead(int n) throws IOException;

    Optional<Tag> getTag();

    void setTag(Tag tag);

    interface Factory
    {
        Remote create(Socket socket);
    }
}
