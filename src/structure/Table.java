package structure;

import sqlengine.Query;
import exceptions.SqlException;
import sqlparser.DBOps;

import java.util.*;
import java.util.stream.IntStream;

public class Table
{
    int id;
    ArrayList<String> attributes;
    ArrayList<Query> queries;

    public Table()
    {
        id = 1; /* Start the id from 1 so that there isn't a 0 in the table */
        attributes = new ArrayList<>();
        attributes.add("id");
        queries = new ArrayList<>();
    }

    public void enterQuery(Query query)
    {
        queries.add(query);
        query.addData("id", Integer.toString(id++)); /* Increment id with each added value */
    }

    public void addAttribute(String attrName)
    {
        if (attributes.contains(attrName)) { /* if attribute already exists, exit the method */
            return;
        }
        attributes.add(attrName);
        queries.forEach(query -> query.addData(attrName, null));
    }

    public void removeAttribute(String attrName)
    {
        attributes.remove(attrName);
        queries.forEach(query -> query.removeData(attrName));
    }

    public void addVal(String[] values) throws SqlException
    {
        if (attributes.size() - 1 == values.length) {
            Query query = new Query();
            query.addData("id", Integer.toString(id++));
            IntStream.range(0, values.length).forEach(i -> query.addData(attributes.get(i + 1), values[i]));
            queries.add(query);
        } else {
            throw new SqlException("[ERROR]: Id mismatch");
        }
    }

    public void addValId(String[] values) throws SqlException
    {
        if (attributes.size() == values.length) {
            Query query = new Query();
            id++;
            IntStream.range(0, values.length).forEach(i -> {
                query.addData(attributes.get(i), values[i]);
            });
            queries.add(query);
        } else {
            throw new SqlException("[ERROR]: Id mismatch");
        }
    }

    /* Returns the value of the specified attribute */
    public String getVal(String[] values, String op) throws SqlException
    {
        StringBuilder sqlReply = new StringBuilder();

        if(values[0].equals("*")) values = attributes.toArray(new String[0]);
        {
            getValAddTab(values, sqlReply);
        }
        sqlReply.append(values[values.length - 1]).append("\n");
        int j = 0;

        while (j < queries.size()) {
            getValIterate(values, op, sqlReply, j);
            j++;
        }
        return sqlReply.toString();
    }

    private void getValAddTab(String[] values, StringBuilder sqlReply) {
        int i = 0;
        while (i < values.length - 1) {
            sqlReply.append(values[i]).append("\t");
            i++;
        }
    }

    private void getValIterate(String[] values, String op, StringBuilder sqlReply, int j) throws SqlException {
        Query query = queries.get(j);
        if (DBOps.conCheck(query, op)) {
            int i = 0;
            while (i < values.length - 1) {
                sqlReply.append(query.attributeData(values[i])).append("\t");
                i++;
            }
            sqlReply.append(query.attributeData(values[values.length - 1])).append("\n");
        }
    }

    public void updateValCheck(String[] values, String op) throws SqlException
    {
        for (int j = 0, queriesSize = queries.size(); j < queriesSize; j++) {
            Query query = queries.get(j);
            if (!DBOps.conCheck(query, op)) {
                continue;
            }
            updateVal(values, query);
        }
    }

    private void updateVal(String[] values, Query query) {
        IntStream.iterate(0, i -> i < values.length, i -> i + 2).forEach(i -> query.updateData(values[i], values[i + 1]));
    }

    public void removeVal(String op) throws SqlException
    {
        ListIterator<Query> queryIterator = queries.listIterator();

        if (queryIterator.hasNext()) {
            do {
                if (DBOps.conCheck(queryIterator.next(), op)) {
                    queryIterator.remove();
                }
            } while (queryIterator.hasNext());
        }
    }

    public ArrayList<String> getAttributes()
    {
        return attributes;
    }

    public ArrayList<Query> getData()
    {
        return queries;
    }

}
