package me.mazeika.transhift.puncher.server;

import com.google.inject.BindingAnnotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.net.Socket;
import java.util.Optional;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

public interface Key<T>
{
    Optional<T> remove(Socket socket);

    Optional<T> get(Socket socket);

    Optional<T> set(Socket socket, T obj);

    // key types

    @BindingAnnotation @Target({ FIELD, PARAMETER, METHOD }) @Retention(RUNTIME)
    @interface Shutdown { }
}
