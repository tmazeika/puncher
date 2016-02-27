package me.mazeika.transhift.puncher.server;

import me.mazeika.transhift.puncher.Tag;

import java.net.Socket;

public interface Remote
{
    Socket getSocket();

    Tag getTag();

    void setTag(Tag tag);

    interface Factory
    {
        Remote create(Socket socket);
    }
}
