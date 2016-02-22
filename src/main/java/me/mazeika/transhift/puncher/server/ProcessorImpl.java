package me.mazeika.transhift.puncher.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class ProcessorImpl implements Processor
{
    @Override
    public void process(final Socket socket) throws IOException
    {
        final InputStream in = socket.getInputStream();

        while (true) {
            socket.getOutputStream().write(in.read());
        }
    }
}
