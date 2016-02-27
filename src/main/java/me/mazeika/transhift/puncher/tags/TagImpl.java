package me.mazeika.transhift.puncher.tags;

import com.google.inject.assistedinject.Assisted;

import javax.inject.Inject;
import java.util.Arrays;

public class TagImpl implements TagIntern
{
    private final byte[] b;

    @Inject
    public TagImpl(@Assisted final byte[] b)
    {
        this.b = b;
    }

    @Override
    public byte[] get()
    {
        return b.clone();
    }

    @Override
    public byte[] intern()
    {
        return b;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final TagImpl oTag = (TagImpl) o;

        return Arrays.equals(b, oTag.b);
    }

    @Override
    public int hashCode()
    {
        return Arrays.hashCode(b);
    }
}
