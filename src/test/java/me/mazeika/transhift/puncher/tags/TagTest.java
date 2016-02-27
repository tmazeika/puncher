package me.mazeika.transhift.puncher.tags;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

public class TagTest
{
    private static final byte TEST_VALUE = (byte) 0xcd;

    private byte[] b;
    private TagIntern tag;

    @Before
    public void before()
    {
        b = new byte[Tag.LENGTH];

        Arrays.fill(b, TEST_VALUE);

        tag = new TagImpl(b);
    }

    @Test
    public void testGet()
    {
        assertThat(tag.get(), is(b.clone()));
        assertThat(tag.get(), is(not(sameInstance(b))));
    }

    @Test
    public void testIntern()
    {
        assertThat(tag.intern(), is(b.clone()));
        assertThat(tag.intern(), is(sameInstance(b)));
    }

    @Test
    public void testEquals()
    {
        final Tag otherTag = new TagImpl(b.clone());
        final Tag otherBadTag = new TagImpl(new byte[Tag.LENGTH]);

        assertThat(tag, is(otherTag));
        assertThat(tag, is(not(otherBadTag)));
    }
}
