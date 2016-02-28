package me.mazeika.transhift.puncher.server;

import com.google.inject.BindingAnnotation;
import me.mazeika.transhift.puncher.server.meta.MetaMap;
import me.mazeika.transhift.puncher.tags.Tag;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.net.Socket;
import java.util.Optional;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

public interface Remote
{
    /**
     * Gets the socket.
     *
     * @return the socket
     */
    Socket socket();

    /**
     * Gets the {@link InputStream} of the socket.
     *
     * @return the InputStream
     *
     * @throws IOException
     */
    InputStream in() throws IOException;

    /**
     * Gets the {@link OutputStream} of the socket.
     *
     * @return the OutputStream
     *
     * @throws IOException
     */
    OutputStream out() throws IOException;

    /**
     * Gets the meta map associated with this Remote.
     *
     * @return the meta map
     */
    MetaMap meta();

    /**
     * Destroys this Remote, removing it from the global Remote pool.
     */
    void destroy();

    /**
     * Gets if this Remote is destroyed.
     *
     * @return {@code true} if this is destroyed
     */
    boolean isDestroyed();

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

    @BindingAnnotation @Target({ FIELD, PARAMETER, METHOD }) @Retention(RUNTIME)
    @interface Pool { }
}
