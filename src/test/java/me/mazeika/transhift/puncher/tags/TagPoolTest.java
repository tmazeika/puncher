package me.mazeika.transhift.puncher.tags;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class TagPoolTest
{
    private TagPool tagPool;

    @Before
    public void setUp()
    {
        tagPool = new TagPoolImpl(TagImpl::new);
    }

    @Test
    public void testGenerate()
    {
        final Tag tag0 = tagPool.generate();
        final Tag tag1 = tagPool.generate();

        assertThat(tag0.equals(tag1), is(false));
    }

    @Test
    public void testRemove()
    {
        final Tag tag0 = tagPool.generate();

        assertThat(tagPool.remove(tag0), is(true));
    }
}
