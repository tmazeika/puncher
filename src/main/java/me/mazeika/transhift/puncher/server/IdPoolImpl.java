package me.mazeika.transhift.puncher.server;

import com.google.inject.Singleton;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Singleton
public class IdPoolImpl implements IdPool
{
    private static final SecureRandom random = new SecureRandom();

    private final Set<byte[]> pool = new HashSet<>();

    @Override
    public byte[] generate()
    {
        byte[] id = new byte[ID_LEN];

        synchronized (pool) {
            while (pool.stream().anyMatch(otherId ->
                    Arrays.equals(id, otherId))) {
                random.nextBytes(id);
            }

            pool.add(id);
        }

        return id;
    }

    @Override
    public void remove(byte[] id)
    {
        synchronized (pool) {
            pool.removeIf(otherId -> Arrays.equals(id, otherId));
        }
    }
}
