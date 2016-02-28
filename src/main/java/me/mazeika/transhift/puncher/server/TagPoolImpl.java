package me.mazeika.transhift.puncher.server;

import com.google.inject.Singleton;

import javax.inject.Inject;
import java.security.SecureRandom;
import java.util.*;
import java.util.function.Predicate;

@Singleton
class TagPoolImpl implements TagPool
{
    private static final SecureRandom random = new SecureRandom();

    private final Map<Tag, Remote> pool = new HashMap<>();
    private final Tag.Factory tagInternFactory;

    @Inject
    public TagPoolImpl(final Tag.Factory tagInternFactory)
    {
        this.tagInternFactory = tagInternFactory;
    }

    @Override
    public void generateFor(final Remote remote)
    {
        final byte[] b = new byte[Tag.LENGTH];
        final Tag tag;

        synchronized (pool) {
            do {
                random.nextBytes(b);
            }
            while (pool.keySet().stream().anyMatch(o -> o.equalsArray(b)));

            tag = tagInternFactory.create(b);

            pool.put(tag, remote);
        }

        remote.setTag(tag);
    }

    @Override
    public Optional<Remote> findAndRemove(final Tag tag)
    {
        final Predicate<? super Map.Entry<Tag, Remote>> matchPredicate =
                entry -> entry.getKey().equals(tag);
        final Optional<Remote> result;

        synchronized (pool) {
            // search for Tag and get Optional of Remote
            result = pool.entrySet().stream()
                    .filter(matchPredicate)
                    .map(Map.Entry::getValue)
                    .findAny();

            // remove entry if found
            result.ifPresent(e -> pool.entrySet().removeIf(matchPredicate));
        }

        return result;
    }
}
