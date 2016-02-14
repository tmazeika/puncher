package me.mazeika.transhift.puncher.modules;

import com.google.inject.AbstractModule;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import me.mazeika.transhift.puncher.binding_annotations.Args;

public class ArgsModule extends AbstractModule
{
    private final OptionSet options;
    private final OptionSpec<String> host;
    private final OptionSpec<Integer> port;

    public ArgsModule(OptionSet options, OptionSpec<String> host,
                      OptionSpec<Integer> port)
    {
        this.options = options;
        this.host = host;
        this.port = port;
    }

    @Override
    protected void configure()
    {
        bind(String.class)
                .annotatedWith(Args.Host.class)
                .toInstance(host.value(options));
        bind(int.class)
                .annotatedWith(Args.Port.class)
                .toInstance(port.value(options));
    }
}
