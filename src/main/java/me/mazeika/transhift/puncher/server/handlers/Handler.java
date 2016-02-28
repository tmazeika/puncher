package me.mazeika.transhift.puncher.server.handlers;

import com.google.inject.BindingAnnotation;
import me.mazeika.transhift.puncher.server.Remote;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

public interface Handler
{
    /**
     * Handles a {@link Remote}.
     *
     * @param remote the remote
     *
     * @throws Exception
     */
    void handle(Remote remote) throws Exception;

    // handler types

    @BindingAnnotation @Target({ FIELD, PARAMETER, METHOD }) @Retention(RUNTIME)
    @interface Type { }

    @BindingAnnotation @Target({ FIELD, PARAMETER, METHOD }) @Retention(RUNTIME)
    @interface TagConsumption { }

    @BindingAnnotation @Target({ FIELD, PARAMETER, METHOD }) @Retention(RUNTIME)
    @interface TagProduction { }

    @BindingAnnotation @Target({ FIELD, PARAMETER, METHOD }) @Retention(RUNTIME)
    @interface TagSearch { }

    @BindingAnnotation @Target({ FIELD, PARAMETER, METHOD }) @Retention(RUNTIME)
    @interface AddressExchange { }

    @BindingAnnotation @Target({ FIELD, PARAMETER, METHOD }) @Retention(RUNTIME)
    @interface Block { }
}
