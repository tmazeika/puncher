package me.mazeika.transhift.puncher.tags;

import com.google.inject.assistedinject.Assisted;

import javax.inject.Inject;
import java.util.Arrays;

class TagImpl implements Tag
{
    private final byte[] b;

    @Inject
    public TagImpl(@Assisted final byte[] b)
    {
        if (b.length != Tag.LENGTH) {
            throw new IllegalArgumentException("Tag length must be " +
                    Tag.LENGTH + ", but got " + b.length);
        }

        this.b = b;
    }

    @Override
    public byte[] get()
    {
        return b.clone();
    }

    @Override
    public boolean equalsArray(final byte[] b)
    {
        return Arrays.equals(this.b, b);
    }

    @Override
    public String toString()
    {
        final StringBuilder builder = new StringBuilder(b.length * 2);

        builder.append("0x");

        for (byte e : b) {
            if (e < 0x10) {
                builder.append("0");
            }

            builder.append(Integer.toHexString(e));
        }

        return builder.toString();
    }

    @Override
    public boolean equals(final Object o)
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
