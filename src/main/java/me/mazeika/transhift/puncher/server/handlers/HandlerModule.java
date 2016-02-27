package me.mazeika.transhift.puncher.server.handlers;

import com.google.inject.AbstractModule;

public class HandlerModule extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind(Handler.class).annotatedWith(Handler.Type.class)
                .to(TypeHandler.class);
        bind(Handler.class).annotatedWith(Handler.TagConsumption.class)
                .to(TagConsumptionHandler.class);
        bind(Handler.class).annotatedWith(Handler.TagProduction.class)
                .to(TagProductionHandler.class);
    }
}
