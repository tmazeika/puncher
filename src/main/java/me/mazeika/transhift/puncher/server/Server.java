package me.mazeika.transhift.puncher.server;

import com.google.common.util.concurrent.AbstractExecutionThreadService;
import com.google.common.util.concurrent.ServiceManager;

import javax.inject.Inject;

public class Server extends AbstractExecutionThreadService
{
    @Inject
    public Server(ServiceManager serviceManager)
    {
    }

    @Override
    protected void run() throws Exception
    {

    }
}
