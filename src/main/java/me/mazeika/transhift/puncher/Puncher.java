package me.mazeika.transhift.puncher;

import com.google.inject.Guice;
import me.mazeika.transhift.puncher.modules.OptionsModule;
import me.mazeika.transhift.puncher.modules.ServerModule;
import me.mazeika.transhift.puncher.server.Server;

import java.io.IOException;

public class Puncher
{
    public static void main(String[] args) throws IOException
    {
        Guice.createInjector(
                new OptionsModule(args), new ServerModule())
                .getInstance(Server.class).start();
    }
}
