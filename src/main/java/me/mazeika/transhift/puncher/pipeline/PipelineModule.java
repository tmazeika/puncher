package me.mazeika.transhift.puncher.pipeline;

import com.google.inject.AbstractModule;

import javax.inject.Singleton;

public class PipelineModule extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind(Pipeline.class)
                .annotatedWith(Pipeline.Shutdown.class)
                .to(PipelineImpl.class)
                .in(Singleton.class);
    }
}
