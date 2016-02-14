package me.mazeika.transhift.puncher.server;

import com.google.inject.throwingproviders.CheckedProvider;

import java.io.IOException;

public interface SelectorProvider<T> extends CheckedProvider<T>
{
    T get() throws IOException;
}
