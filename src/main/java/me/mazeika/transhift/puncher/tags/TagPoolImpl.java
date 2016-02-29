package me.mazeika.transhift.puncher.tags;

import com.google.inject.Singleton;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
class TagPoolImpl implements TagPool
{
    private static final Logger logger = LogManager.getLogger();
    private static final SecureRandom random = new SecureRandom();

    private final Collection<Tag> pool = ConcurrentHashMap.newKeySet();
    private final Tag.Factory tagInternFactory;

    @Inject
    public TagPoolImpl(final Tag.Factory tagInternFactory)
    {
        this.tagInternFactory = tagInternFactory;
    }

    @Override
    public Tag generate()
    {
        final byte[] b = new byte[Tag.LENGTH];
        final Tag tag;

        synchronized (pool) {
            do {
                random.nextBytes(b);
                logger.trace("generated tag {}", Arrays.toString(b));
            }
            while (pool.stream().anyMatch(e -> e.equalsArray(b)));

            pool.add(tag = tagInternFactory.create(b));
        }

        return tag;
    }

    @Override
    public void remove(final Tag tag)
    {
        synchronized (pool) {
            pool.removeIf(e -> e.equals(tag));
        }
    }
}
