package me.mazeika.transhift.puncher.tags;

import com.google.inject.Singleton;

import javax.inject.Inject;
import java.security.SecureRandom;
import java.util.*;

@Singleton
public class TagPoolImpl implements TagPool
{
    private static final SecureRandom random = new SecureRandom();

    private final Collection<TagIntern> pool = new HashSet<>();
    private final TagIntern.Factory tagInternFactory;

    @Inject
    public TagPoolImpl(final TagIntern.Factory tagInternFactory)
    {
        this.tagInternFactory = tagInternFactory;
    }

    @Override
    public Tag generate()
    {
        final byte[] b = new byte[LENGTH];
        final TagIntern tag;

        synchronized (pool) {
            do {
                random.nextBytes(b);
            }
            while (pool.stream().anyMatch(o ->
                    Arrays.equals(o.intern(), b)));

            tag = tagInternFactory.create(b);

            pool.add(tag);
        }

        return tag;
    }

    @Override
    public void remove(final Tag tag)
    {
        synchronized (pool) {
            // noinspection SuspiciousMethodCalls
            pool.remove(tag);
        }
    }
}
