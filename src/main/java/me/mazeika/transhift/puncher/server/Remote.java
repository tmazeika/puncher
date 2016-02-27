package me.mazeika.transhift.puncher.server;

import me.mazeika.transhift.puncher.tags.Tag;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Optional;

public interface Remote
{
    /**
     * Gets the socket.
     *
     * @return the socket
     */
    Socket getSocket();

    /**
     * Reads {@code n} bytes from the socket's {@link InputStream}, throwing an
     * error if it couldn't be fully read.
     *
     * @param n the number of bytes to read
     *
     * @return the read bytes
     *
     * @throws IOException when an error occurs with the socket or {@code n}
     * bytes couldn't be read
     */
    byte[] waitAndRead(int n) throws IOException;

    /**
     * Gets an optional describing the tag.
     *
     * @return an optional describing the tag
     */
    Optional<Tag> getTag();

    /**
     * Sets the tag.
     *
     * @param tag the tag
     */
    void setTag(Tag tag);

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
