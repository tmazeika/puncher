package me.mazeika.transhift.puncher.server;

import me.mazeika.transhift.puncher.tags.Tag;

import java.util.Optional;

public interface SourceConnectNotifier
{
    /**
     * Registers a {@link Listener} associated with a {@link Remote} to be
     * called every time a source remote makes a dial. It is the job of the
     * listener to return {@code true} if its {@link Tag} matches that of the
     * source remote, or {@code false} otherwise. The listener will
     * automatically unregister itself if it returns {@code true}.
     *
     * @param remote the source remote
     * @param listener the listener to call
     */
    void register(Remote remote, Listener listener);

    /**
     * Fires the connect event, notifying all listeners. If one of the listeners
     * was able to handle the event (due to a {@link Tag} match), the
     * {@link Remote} associated with the successful listener will be described
     * in a returned Optional. If no listener was able to handle it (therefore
     * no target remotes had the given tag), an empty Optional is returned.
     *
     * @param source the source remote
     * @param tag the Tag to try to match with
     *
     * @return an Optional describing the matching Remote
     */
    Optional<Remote> fire(Remote source, Tag tag);

    interface Listener
    {
        /**
         * @see #register(Remote, Listener)
         */
        boolean onConnect(Remote source, Tag tag);
    }
}
