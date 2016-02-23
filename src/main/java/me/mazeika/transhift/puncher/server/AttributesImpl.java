package me.mazeika.transhift.puncher.server;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import javax.inject.Singleton;
import java.net.Socket;
import java.util.Optional;

@Singleton
class AttributesImpl implements Attributes
{
    private final ThreadLocal<Table<Socket, Integer, Object>> attributes =
            ThreadLocal.withInitial(HashBasedTable::create);

    // as long as #set is implemented correctly, these methods can never throw a
    // ClassCastException

    @Override
    public <T> Optional<T> remove(final Socket socket, final int id)
    {
        // noinspection unchecked
        return Optional.ofNullable((T) attributes.get().remove(socket, id));
    }

    @Override
    public <T> Optional<T> get(final Socket socket, final int id)
    {
        // noinspection unchecked
        return Optional.ofNullable((T) attributes.get().get(socket, id));
    }

    @Override
    public <T> Optional<T> set(final Socket socket, final int id, final T obj)
    {
        // noinspection unchecked
        return Optional.ofNullable((T) attributes.get().put(socket, id, obj));
    }
}
