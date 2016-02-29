package me.mazeika.transhift.puncher.server;

import me.mazeika.transhift.puncher.server.meta.MetaKeys;
import me.mazeika.transhift.puncher.tags.Tag;

import javax.inject.Singleton;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
class SourceConnectNotifierImpl implements SourceConnectNotifier
{
    private final Map<Remote, Listener> listeners = new ConcurrentHashMap<>();

    @Override
    public void register(final Remote remote, final Listener listener)
    {
        listeners.put(remote, listener);
    }

    @Override
    public Optional<Remote> fire(final Remote source, final Tag tag)
    {
        final Optional<Remote> peer = listeners.entrySet().stream()
                .filter(entry -> entry.getKey().meta().get(MetaKeys.TAG).get()
                        .equals(tag))
                .filter(entry -> entry.getValue().onConnect(source, tag))
                .map(Map.Entry::getKey)
                .findAny();

        // unregister listener after it handled the source
        peer.ifPresent(listeners::remove);

        return peer;
    }
}
