package me.mazeika.transhift.puncher.options;

public interface Options
{
    /**
     * Gets the host. May be IPv4, IPv6, or simply malformed.
     *
     * @return the host
     */
    String getHost();

    /**
     * Gets the port. Ranges from 0 to 65535.
     *
     * @return the port
     */
    int getPort();

    interface Factory
    {
        /**
         * Creates an Options instance.
         *
         * @param host the host, see {@link #getHost()} for valid input
         * @param port the port, see {@link #getPort()} for valid input
         *
         * @return a new Options instance
         */
        Options create(String host, int port);
    }
}
