package me.mazeika.transhift.puncher.cli;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;

public class CliPortValidator implements IParameterValidator
{
    @Override
    public void validate(String name, String value) throws ParameterException
    {
        final int port;

        try {
            port = Integer.parseInt(value);
        }
        catch (NumberFormatException e) {
            throwInvalid(value);
            return;
        }

        if (port < 0x0000 || port > 0xffff) {
            throwInvalid(value);
        }
    }

    private void throwInvalid(String value)
    {
        throw new ParameterException(String.format(
                "'%s' is not a valid port number", value));
    }
}
