package me.mazeika.transhift.puncher;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class TagPoolTest
{
    private TagPool tagPool;

    @Before
    public void setUp()
    {
        tagPool = new TagPoolImpl();
    }

    @Test
    public void testGenerate()
    {
        final byte[] tag0 = tagPool.generate();
        final byte[] tag1 = tagPool.generate();

        assertThat(Arrays.equals(tag0, tag1), is(false));
    }

    @Test
    public void testRemove()
    {
        final byte[] tag0 = tagPool.generate();

        tagPool.remove(tag0);
    }
}
