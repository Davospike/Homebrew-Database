package sqlparser;

import sqlengine.Query;
import exceptions.SqlException;
import java.util.*;
import java.util.regex.*;

public class DBOps
{
    public static boolean conCheck(Query query, String op) throws SqlException
    {
        if (!op.isBlank()) {
            Stack<Boolean> result = new Stack<>();
            Stack<String> ops = new Stack<>();
            StringBuilder argument = new StringBuilder();

            char[] charArray = op.toCharArray();
            for (int i = 0, charArrayLength = charArray.length; i < charArrayLength; i++) {
                char ch = charArray[i];
                if (ch == '(') {
                    argument = openParen(ops, argument);
                } else if (ch == ')') {
                    argument = closeParen(query, result, ops, argument);
                } else if (ch == ';') {
                    if (semiColon(result, ops)) {
                        return entryCalc(query, argument.toString());
                    }
                } else {
                    argument.append(ch);
                }
            }
            if (result.size() <= 1) {
                return result.pop();
            }
            throw new SqlException("[ERROR]: Mismatched brackets");
        } else {
            return true;
        }
    }
    /* Runs if there are still arguments to be evaluated */
    private static boolean semiColon(Stack<Boolean> result, Stack<String> ops) throws SqlException {
        if (result.size() <= 1) {
            return true;
        } else {
            result.push(andOr(result.pop(), result.pop(), ops.pop()));
        }
        return false;
    }
    /* If the argument is not empty then it can only be WHERE
     * Search for '(' then reset the argument when one is met */
    private static StringBuilder closeParen(Query query, Stack<Boolean> result, Stack<String> ops, StringBuilder argument) throws SqlException {
        if (!argument.toString().isBlank()) {
            result.push(entryCalc(query, argument.toString()));
        }
        if (!ops.peek().equals("(")) {
            do {
                result.push(andOr(result.pop(), result.pop(), ops.pop()));
            } while (!ops.peek().equals("("));
        }
        ops.pop();
        argument = new StringBuilder();
        return argument;
    }
    /* If the argument is not empty then it can only be AND or OR
    *  A '(' is added to the stack and the argument is emptied and returned to allow for another run */
    private static StringBuilder openParen(Stack<String> ops, StringBuilder argument) {
        if (!argument.toString().isBlank()) {
            ops.push(argument.toString());
        }
        ops.push("(");
        argument = new StringBuilder();
        return argument;
    }
    /* Returns either a & b or a | b depending on the operator specified */
    public static boolean andOr(Boolean query, Boolean query2, String op) throws SqlException
    {
        switch (op.replace(" ", "")) {
            case "AND":
                return query && query2;
            case "OR":
                return query || query2;
        }
        throw new SqlException("[ERROR]: Operator not recognised");
    }

    /* Searches for the specified patterns and either passes the arguments to conCalc or returns an exception */
    public static boolean entryCalc(Query query, String argument) throws SqlException
    {
        Pattern pattern = Pattern.compile("([a-zA-Z0-9]+)\\s*(==|>|<|>=|<=|!=|LIKE)\\s*([a-zA-Z0-9'. ]+)");
        Matcher matcher = pattern.matcher(argument);
        boolean matches = matcher.find();

        if (!matches) {
            throw new SqlException("[ERROR]: String expected");
        } else {
            return conCalc(query.attributeData(matcher.group(1)), matcher.group(2), matcher.group(3));
        }
    }

    /* Depending upon the matched groups, returns the appropriate calculation */
    public static boolean conCalc(String value, String op, String condition) throws SqlException
    {
        if (Pattern.matches("'[a-zA-Z0-9 ]*'", condition)) {
            return strConCalc(value, op, condition);
        } else if (Pattern.matches("true|false", condition)) {
            return boolConCalc(value, op, condition);
        } else if (Pattern.matches("[0-9]+", condition)) {
            return intConCalc(value, op, condition);
        } else if (Pattern.matches("[0-9]+.[0-9]+", condition)) {
            return floatConCalc(value, op, condition);
        } else return false;
    }
    /* For floats */
    private static boolean floatConCalc(String value, String op, String condition) throws SqlException
    {
        if (Pattern.matches("[0-9]+.[0-9]+", value)) {
            return intCalc(value, op, condition, true, false);
        }
        throw new SqlException("[ERROR]: Data types must match");
    }
    /* For ints */
    private static boolean intConCalc(String value, String op, String condition) throws SqlException
    {
        if (Pattern.matches("[0-9]+", value)) {
            return intCalc(value, op, condition, false, true);
        }
        throw new SqlException("[ERROR]: Data types must match");
    }
    /* For booleans */
    private static boolean boolConCalc(String value, String op, String condition) throws SqlException
    {
        if (Pattern.matches("true|false", value)) {
            return boolCalc(value, op, condition);
        }
        throw new SqlException("[ERROR]: Data types must match");
    }
    /* For strings */
    private static boolean strConCalc(String value, String op, String condition) throws SqlException
    {
        if (Pattern.matches("'[a-zA-Z0-9 ]*'", value)) {
            return strCalc(value, op, condition);
        }
        throw new SqlException("[ERROR]: Data types must match");
    }
    /* For strings */
    public static boolean strCalc(String value, String op, String condition) throws SqlException
    {
        if ("==".equals(op)) {
            return (condition.compareTo(value) == 0);
        } else if ("LIKE".equals(op)) {
            return value.contains(condition.replace("'", ""));
        } else if ("!=".equals(op)) {
            return (condition.compareTo(value) != 0);
        } else if ("<=".equals(op)) {
            return (condition.compareTo(value) <= 0);
        } else if (">".equals(op)) {
            return (condition.compareTo(value) > 0);
        } else if ("<".equals(op)) {
            return (condition.compareTo(value) < 0);
        } else if (">=".equals(op)) {
            return (condition.compareTo(value) >= 0);
        }
        throw new SqlException("[ERROR]: Unrecognised operator");
    }
    /* For booleans */
    public static boolean boolCalc(String value, String op, String condition) throws SqlException
    {
        if (!"==".equals(op)) {
            if ("!=".equals(op)) {
                return !condition.equals(value);
            }
        } else {
            return condition.equals(value);
        }
        throw new SqlException("[ERROR]: You cannot use the entered operator with booleans");
    }
    /* For ints or floats, depending on the booleans passed to the method. Returns the appropriate function
    *  depending on the entered argument */
    public static boolean intCalc(String value, String op, String condition, Boolean floatCheck, Boolean intCheck) throws SqlException
    {
        float numVal;
        float numCon;

        if (!floatCheck) {
            if (intCheck) {
                numVal = Integer.parseInt(value);
                numCon = Integer.parseInt(condition);
            } else {
                throw new SqlException("[ERROR]: Attribute cannot be converted to a number");
            }
        } else {
            numVal = Float.parseFloat(value);
            numCon = Float.parseFloat(condition);
        }

        if ("==".equals(op)) {
            return numVal == numCon;
        } else if (">".equals(op)) {
            return numVal > numCon;
        } else if ("<=".equals(op)) {
            return numVal <= numCon;
        } else if ("<".equals(op)) {
            return numVal < numCon;
        } else if (">=".equals(op)) {
            return numVal >= numCon;
        } else if ("!=".equals(op)) {
            return numVal != numCon;
        }
        throw new SqlException("[ERROR]: Unrecognised operator");
    }
}
