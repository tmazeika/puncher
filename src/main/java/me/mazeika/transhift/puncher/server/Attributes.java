package me.mazeika.transhift.puncher.server;

import java.net.Socket;
import java.util.Optional;

interface Attributes
{
    <T> Optional<T> remove(Socket socket, int id);

    <T> Optional<T> get(Socket socket, int id);

    <T> Optional<T> set(Socket socket, int id, T obj);
}
