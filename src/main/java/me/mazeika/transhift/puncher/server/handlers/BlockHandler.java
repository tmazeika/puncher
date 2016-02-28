package me.mazeika.transhift.puncher.server.handlers;

import me.mazeika.transhift.puncher.server.Remote;

class BlockHandler implements Handler
{
    @Override
    public void handle(final Remote remote) throws Exception
    {
        // noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (remote) {
            while (! remote.isDestroyed()) {
                remote.wait();
            }
        }
    }
}
