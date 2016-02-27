package me.mazeika.transhift.puncher.server.handlers;

import me.mazeika.transhift.puncher.server.Remote;
import me.mazeika.transhift.puncher.tags.Tag;
import me.mazeika.transhift.puncher.tags.TagPool;

import javax.inject.Inject;

public class TagSearchHandler implements Handler
{
    private final TagPool tagPool;

    @Inject
    public TagSearchHandler(final TagPool tagPool)
    {
        this.tagPool = tagPool;
    }

    @Override
    public void handle(final Remote remote) throws Exception
    {
        // if implemented correctly, cannot throw NPE
        final Tag tag = remote.getTag().get();

        // TODO: search
    }
}
