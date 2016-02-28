package me.mazeika.transhift.puncher.tags;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

public class TagsModule extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind(TagPool.class).to(TagPoolImpl.class);

        // install Tag.Factory
        install(new FactoryModuleBuilder()
                .implement(Tag.class, TagImpl.class)
                .build(Tag.Factory.class));
    }
}
