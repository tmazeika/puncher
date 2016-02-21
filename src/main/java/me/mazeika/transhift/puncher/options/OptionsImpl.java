package me.mazeika.transhift.puncher.options;

import com.google.inject.assistedinject.Assisted;

import javax.inject.Inject;

public class OptionsImpl implements Options
{
    private final String host;
    private final int port;

    @Inject
    public OptionsImpl(@Assisted String host, @Assisted int port)
    {
        this.host = host;
        this.port = port;
    }

    @Override
    public String getHost()
    {
        return host;
    }

    @Override
    public int getPort()
    {
        return port;
    }
}
