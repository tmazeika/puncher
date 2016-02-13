package me.mazeika.transhift.puncher.cli;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.google.inject.Singleton;

@Singleton
public class CliModel
{
    @Parameter(names = "--help", description = "Display help.", help = true)
    private boolean help;

    @Parameter(names = { "--host", "-h" }, description = "The host to bind to.")
    private String host = "127.0.0.1";

    @Parameter(names = { "--port", "-p" }, description = "The port to bind to.",
            validateWith = PortValidator.class)
    private int port = 50977;

    public boolean isHelp()
    {
        return help;
    }

    public String getHost()
    {
        return host;
    }

    public int getPort()
    {
        return port;
    }

    private class PortValidator implements IParameterValidator
    {
        private static final int MIN_PORT = 0x0000;
        private static final int MAX_PORT = 0xffff;

        @Override
        public void validate(String name, String value)
        {
            final int port;

            try {
                port = Integer.parseInt(value);
            }
            catch (NumberFormatException e) {
                throwInvalid(value);
                return;
            }

            if (port < MIN_PORT || port > MAX_PORT) {
                throwInvalid(value);
            }
        }

        private void throwInvalid(String value)
        {
            throw new ParameterException(String.format(
                    "'%s' is not a valid port number", value));
        }
    }
}
