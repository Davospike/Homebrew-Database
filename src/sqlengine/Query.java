package sqlengine;

import exceptions.SqlException;

import java.util.*;

public class Query
{
    LinkedHashMap<String, String> queries;

    public Query()
    {
        queries = new LinkedHashMap<>();
    }

    /* Adds data to the table */
    public void addData(String col, String data)
    {
        queries.put(col, data == null ? "" : data);
    }

    /* Updates table data */
    public void updateData(String col, String data)
    {
        queries.replace(col, data);
    }

    /* Returns the data contained within an attribute */
    public String attributeData(String col) throws SqlException
    {
        if (queries.containsKey(col)) {
            return queries.get(col);
        }
        throw new SqlException("[ERROR]: Attribute does not exist");
    }
    /* Joins tables and helps display results */
    public Query joinQuery(Query query, String origTable, String joinTable, String con)
    {
        Query q = new Query();
        var joinQueries = query.getQueries();

        queries.keySet().stream().filter(col -> !col.equals(con)).forEach(col -> {
            String data = queries.get(col);
            q.addData(origTable + "." + col, data);
        });
        joinQueries.keySet().stream().filter(col -> !col.equals(con)).forEach(col -> {
            String data = joinQueries.get(col);
            q.addData(joinTable + "." + col, data);
        });
        return q;
    }

    public LinkedHashMap<String, String> getQueries()
    {
        return queries;
    }

    public void removeData(String col)
    {
        queries.remove(col);
    }

}
