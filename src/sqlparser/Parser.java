package sqlparser;

import exceptions.SqlException;
import statements.*;

/* Handles all incoming commands and sends them off to the classes in the "statements" package   */
public class Parser
{
    public static Statement parse(String query) throws SqlException
    {
        /* Ensures a query has actually been entered. Also triggers when exiting DBClient with ctrl c, unfortunately */
        if (query.split(" ").length != 1) {
            int i = 0;

            if (query.split(" ")[i].equals("")) {
                do {
                    i++;
                } while (query.split(" ")[i].equals(""));
            }

            return getStatement(query, i);
        } else {
            throw new SqlException("[ERROR]: No query entered");
        }
    }

    private static Statement getStatement(String query, int i) throws SqlException {
        if ("CREATE".equalsIgnoreCase(query.split(" ")[i])) {
            return new Create(query);
        } else if ("USE".equalsIgnoreCase(query.split(" ")[i])) {
            return new Use(query);
        } else if ("DROP".equalsIgnoreCase(query.split(" ")[i])) {
            return new Drop(query);
        } else if ("ALTER".equalsIgnoreCase(query.split(" ")[i])) {
            return new Alter(query);
        } else if ("INSERT".equalsIgnoreCase(query.split(" ")[i])) {
            return new Insert(query);
        } else if ("SELECT".equalsIgnoreCase(query.split(" ")[i])) {
            return new Select(query);
        } else if ("UPDATE".equalsIgnoreCase(query.split(" ")[i])) {
            return new Update(query);
        } else if ("DELETE".equalsIgnoreCase(query.split(" ")[i])) {
            return new Delete(query);
        } else if ("JOIN".equalsIgnoreCase(query.split(" ")[i])) {
            return new Join(query);
        }
        throw new SqlException("[ERROR]: Invalid query");
    }
}
