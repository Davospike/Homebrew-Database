package sqlengine;

import java.util.*;
import java.util.stream.IntStream;

/* Formats strings to display the results in the command line */
public class Display
{
    public static String results(String result)
    {
        String outcome;

        if (!result.equals("id\n")) {
            StringBuilder sb = new StringBuilder();
            ArrayList<String> list = new ArrayList<String>();
            String[] x = result.split("\n"), y = x[0].split("\t");
            int[] size = new int[y.length];

            int i = 0, xLength = x.length;
            while (i < xLength) {
                i = format(list, x[i], size, i);
            }

            int listSize = list.size();
            strFormat(sb, list, y, size, listSize);
            return sb.toString();
        } else {
            outcome = "[ERROR]: Table is empty";
            return outcome;
        }
    }

    private static void strFormat(StringBuilder sb, ArrayList<String> list, String[] y, int[] size, int listSize) {
        int i = 0, j = 0;
        while (i < listSize) {
            sb.append(String.format("%-" + (size[j++] + 2) + "s", list.get(i)));

            if (j % y.length == 0) {
                sb.append("\n");
                j = 0;
            }
            i++;
        }
    }

    private static int format(ArrayList<String> list, String x, int[] size, int i) {
        String[] var = x.split("\t", -1);
        IntStream.range(0, var.length).forEach(j -> {
            size[j] = Math.max(var[j].replace("'", "").length(), size[j]);
            list.add(var[j].replace("'", ""));
        });
        i++;
        return i;
    }
}
