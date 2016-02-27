package me.mazeika.transhift.puncher;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import me.mazeika.transhift.puncher.options.OptionsModule;
import me.mazeika.transhift.puncher.pipeline.PipelineModule;
import me.mazeika.transhift.puncher.server.ServerModule;
import me.mazeika.transhift.puncher.pipeline.Pipeline;
import me.mazeika.transhift.puncher.server.Server;
import me.mazeika.transhift.puncher.server.handlers.HandlerModule;
import me.mazeika.transhift.puncher.tags.TagModule;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Puncher
{
    public static void main(String[] args) throws IOException
    {
        final Injector injector = Guice.createInjector(
                new OptionsModule(args),
                new PipelineModule(),
                new ServerModule(),
                new TagModule(),
                new HandlerModule());

        // add shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            injector.getInstance(Key.get(
                    Pipeline.class, Pipeline.Shutdown.class)).fire();
        }));

        // start
        injector.getInstance(Server.class).start();
    }
}
