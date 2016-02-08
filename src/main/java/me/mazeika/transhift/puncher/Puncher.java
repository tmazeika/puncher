package me.mazeika.transhift.puncher;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.google.inject.Guice;
import com.google.inject.Injector;
import me.mazeika.transhift.puncher.cli.CliModel;
import me.mazeika.transhift.puncher.modules.ServerModule;
import me.mazeika.transhift.puncher.server.Server;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Puncher
{
    private static final Logger logger = LogManager.getLogger();

    public static void main(String[] args) throws Exception
    {
        final Injector injector = Guice.createInjector(new ServerModule());

        if (parseCli(injector.getInstance(CliModel.class), args)) {
            injector.getInstance(Server.class).start();
        }
    }

    /**
     * Parses CLI arguments with JCommander. Takes a model to load options into
     * and the program arguments to parse.
     *
     * @param cli the model to load options into
     * @param args the arguments to parse
     *
     * @return {@code true} for success <em>and</em> the '--help' flag was not
     * passed
     */
    private static boolean parseCli(CliModel cli, String[] args)
    {
        final JCommander commands;

        try {
            commands = new JCommander(cli, args);
        }
        catch (ParameterException e) {
            logger.error(e.getMessage());
            return false;
        }

        if (cli.isHelp()) {
            commands.setProgramName("puncher");
            commands.usage();
        }

        return ! cli.isHelp();
    }
}
