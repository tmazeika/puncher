package me.mazeika.transhift.puncher.cli;

import com.beust.jcommander.Parameter;
import com.google.inject.Singleton;

@Singleton
public class CliModel
{
    @Parameter(names = "--help", description = "Display help.", help = true)
    private boolean help;

    @Parameter(names = { "--host", "-h" }, description = "The host to bind to.")
    private String host = "127.0.0.1";

    @Parameter(names = { "--port", "-p" }, description = "The port to bind to.",
            validateWith = PortValidator.class)
    private int port = 50977;

    public boolean isHelp()
    {
        return help;
    }

    public String getHost()
    {
        return host;
    }

    public int getPort()
    {
        return port;
    }
}
