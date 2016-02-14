package me.mazeika.transhift.puncher.server;

import com.google.inject.Singleton;

import java.security.SecureRandom;
import java.util.*;

@Singleton
public class IdPoolImpl implements IdPool
{
    private static final SecureRandom random = new SecureRandom();

    private final Collection<byte[]> pool = new HashSet<>();

    @Override
    public byte[] generate()
    {
        final byte[] id = new byte[ID_LENGTH];

        synchronized (pool) {
            do {
                random.nextBytes(id);
            }
            while (pool.stream().anyMatch(o -> Arrays.equals(o, id)));

            pool.add(id);
        }

        return id.clone();
    }

    @Override
    public void remove(byte[] id)
    {
        synchronized (pool) {
            pool.removeIf(o -> Arrays.equals(o, id));
        }
    }
}
