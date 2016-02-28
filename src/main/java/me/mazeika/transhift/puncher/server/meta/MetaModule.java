package me.mazeika.transhift.puncher.server.meta;

import com.google.inject.AbstractModule;

public class MetaModule extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind(MetaMap.class).to(MetaMapImpl.class);
    }
}
