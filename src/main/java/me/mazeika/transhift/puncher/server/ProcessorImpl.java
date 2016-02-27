package me.mazeika.transhift.puncher.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

class ProcessorImpl implements Processor
{
    private static final Logger logger = LogManager.getLogger();

    @Override
    public void process(final Socket socket) throws IOException
    {
        final InputStream in = socket.getInputStream();

        // temporary echo
        while (true) {
            final int b = in.read();

            logger.trace("{}: read byte 0x{}", socket.getRemoteSocketAddress(),
                    Integer.toHexString(b));
            socket.getOutputStream().write(b);
        }
    }
}
