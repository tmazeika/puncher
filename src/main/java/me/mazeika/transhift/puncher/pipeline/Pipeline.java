package me.mazeika.transhift.puncher.pipeline;

import com.google.inject.BindingAnnotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

public interface Pipeline
{
    void register(Runnable runnable);

    void fire();

    // pipeline types

    @BindingAnnotation @Target({ FIELD, PARAMETER, METHOD }) @Retention(RUNTIME)
    @interface Shutdown { }
}
