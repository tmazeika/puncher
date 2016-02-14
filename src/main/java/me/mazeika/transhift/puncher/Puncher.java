package me.mazeika.transhift.puncher;

import com.google.inject.Guice;
import com.google.inject.Injector;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import me.mazeika.transhift.puncher.modules.ArgsModule;
import me.mazeika.transhift.puncher.modules.ServerModule;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class Puncher
{
    private static final Logger logger = LogManager.getLogger();

    public static void main(String[] args) throws IOException
    {
        // parse program arguments
        final OptionParser parser = new OptionParser();

        parser.printHelpOn(System.out);
        final OptionSpec<Void> help =
                parser.accepts("help", "Shows this help.")
                        .forHelp();
        final OptionSpec<String> host =
                parser.accepts("host", "The host to bind to.")
                        .withRequiredArg()
                        .defaultsTo("127.0.0.1");
        final OptionSpec<Integer> port =
                parser.accepts("port", "The port to bind to.")
                        .withRequiredArg()
                        .ofType(int.class)
                        .defaultsTo(50977);
        final OptionSet options = parser.parse(args);

        if (! options.has(help)) {
            // start
            final Injector injector = Guice.createInjector(
                    new ArgsModule(options, host, port),
                    new ServerModule());
        }
    }
}
