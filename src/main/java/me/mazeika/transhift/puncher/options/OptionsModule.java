package me.mazeika.transhift.puncher.options;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.assistedinject.FactoryModuleBuilder;

import javax.inject.Singleton;

public class OptionsModule extends AbstractModule
{
    private final String[] args;

    public OptionsModule(final String[] args)
    {
        this.args = args;
    }

    @Override
    protected void configure()
    {
        // install Options.Factory
        install(new FactoryModuleBuilder()
                .implement(Options.class, OptionsImpl.class)
                .build(Options.Factory.class));
    }

    @Provides @Singleton
    Options provideOptions(final Options.Factory factory)
    {
        final String host = args[0];
        final int port = Integer.parseInt(args[1]);

        return factory.create(host, port);
    }
}
