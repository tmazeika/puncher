package me.mazeika.transhift.puncher.pipeline;

import java.util.Collection;
import java.util.HashSet;

public class PipelineImpl implements Pipeline
{
    private final Collection<Runnable> workers = new HashSet<>();

    @Override
    public void register(Runnable handler)
    {
        workers.add(handler);
    }

    @Override
    public void fire()
    {
        workers.parallelStream().forEach(Runnable::run);
    }
}
