package exceptions;

public class MiscErrors
{
    /* Collates multiple error methods into one, returning the appropriate message.
    *  Decided to have this "misc error" class as a catch all instead of splitting them up */
    public static String mError(String query) throws SqlException
    {
        if (query.contains(";")) {
            if (!quoteCheck(query) && !parenCheck(query)) {
                return query;
            }
            throw new SqlException("[ERROR]: Invalid query");
        } else {
            throw new SqlException("[ERROR]: Semi colon missing at end of line");
        }
    }

    /* Ensures there are an equal number of quotes ' in a query */
    private static boolean quoteCheck(String query)
    {
        int cnt = 0;

        char[] charArray = query.toCharArray();
        for (int i = 0, charArrayLength = charArray.length; i < charArrayLength; i++) {
            char ch = charArray[i];
            if (ch == '\'') cnt++;
        }
        return cnt == 0;
    }

    /* Ensures there are an equal number of parentheses ( ) in a query */
    private static boolean parenCheck(String query)
    {
        int cnt = 0;

        char[] charArray = query.toCharArray();
        for (int i = 0, charArrayLength = charArray.length; i < charArrayLength; i++) {
            char ch = charArray[i];
            if (ch == '(') cnt++;
            if (ch == '(') cnt--;
        }
        return cnt == 0;
    }
}

