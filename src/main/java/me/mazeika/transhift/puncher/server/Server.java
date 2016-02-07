package me.mazeika.transhift.puncher.server;

import com.google.inject.Inject;
import me.mazeika.transhift.puncher.cli.CliModel;

public class Server implements IServer
{
    @Inject
    private CliModel cli;

    @Override
    public void start()
    {
        System.out.println("Server#start");
        System.out.println(cli.getHost() + ":" + cli.getPort());
    }
}
