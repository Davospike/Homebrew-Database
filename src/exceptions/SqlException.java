package exceptions;

public class SqlException extends Exception
{
    /* The one exception that I use throughout the program */
    public SqlException(String message)
    {
        super(message);
    }
}
