package me.mazeika.transhift.puncher.cli;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;

public class CliPortValidator implements IParameterValidator
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
