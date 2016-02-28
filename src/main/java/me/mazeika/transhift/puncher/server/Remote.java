package me.mazeika.transhift.puncher.server;

import me.mazeika.transhift.puncher.server.meta.MetaMap;
import me.mazeika.transhift.puncher.tags.Tag;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Optional;

public interface Remote
{
    /**
     * Gets the socket.
     *
     * @return the socket
     */
    Socket socket();

    InputStream in() throws IOException;

    OutputStream out() throws IOException;

    /**
     * Gets the meta map associated with this Remote.
     *
     * @return the meta map
     */
    MetaMap meta();

    interface Factory
    {
        /**
         * Creates a {@link Remote} instance.
         *
         * @param socket the socket of the remote
         *
         * @return a new Remote instance
         */
        Remote create(Socket socket);
    }
}
