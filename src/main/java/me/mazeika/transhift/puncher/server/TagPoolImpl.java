package me.mazeika.transhift.puncher.server;

import com.google.inject.Singleton;

import java.security.SecureRandom;
import java.util.*;

@Singleton
public class TagPoolImpl implements TagPool
{
    private static final SecureRandom random = new SecureRandom();

    private final Collection<byte[]> pool = new HashSet<>();

    @Override
    public byte[] generate()
    {
        final byte[] tag = new byte[ID_LENGTH];

        synchronized (pool) {
            do {
                random.nextBytes(tag);
            }
            while (pool.stream().anyMatch(o -> Arrays.equals(o, tag)));

            pool.add(tag);
        }

        return tag.clone();
    }

    @Override
    public void remove(byte[] id)
    {
        synchronized (pool) {
            pool.removeIf(o -> Arrays.equals(o, id));
        }
    }
}
