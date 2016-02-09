package me.mazeika.transhift.puncher.server;

import com.google.inject.Singleton;
import io.netty.channel.ChannelHandlerContext;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Singleton
public class IdPoolImpl implements IdPool
{
    private static final SecureRandom random = new SecureRandom();

    private final Map<byte[], ChannelHandlerContext> map = new HashMap<>();

    @Override
    public byte[] generateFor(ChannelHandlerContext ctx)
    {
        byte[] id = new byte[ID_LENGTH];

        synchronized (map) {
            do {
                random.nextBytes(id);
            }
            while (map.keySet().stream().anyMatch(key ->
                    Arrays.equals(key, id)));

            map.put(id, ctx);
        }

        return id.clone();
    }

    @Override
    public Optional<ChannelHandlerContext> find(byte[] id)
    {
        synchronized (map) {
            return map.entrySet().stream()
                    .filter(entry -> Arrays.equals(entry.getKey(), id))
                    .map(Map.Entry::getValue)
                    .findAny();
        }
    }

    @Override
    public void remove(ChannelHandlerContext ctx)
    {
        synchronized (map) {
            map.values().removeIf(value -> value == ctx);
        }
    }
}
