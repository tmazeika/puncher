package me.mazeika.transhift.puncher.server;

import me.mazeika.transhift.puncher.server.handlers.Handler;

import javax.inject.Inject;
import java.net.Socket;

class ProcessorImpl implements Processor
{
    private final Remote.Factory remoteFactory;
    private final Handler firstHandler;

    @Inject
    public ProcessorImpl(final Remote.Factory remoteFactory,
                         @Handler.Type final Handler firstHandler)
    {
        this.remoteFactory = remoteFactory;
        this.firstHandler = firstHandler;
    }

    @Override
    public void process(final Socket socket) throws Exception
    {
        firstHandler.handle(remoteFactory.create(socket));
    }
}
