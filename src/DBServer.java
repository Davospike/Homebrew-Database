import sqlengine.DBCommand;
import sqlengine.DBCommandInterface;
import exceptions.*;
import sqlparser.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class DBServer {
    final public DBCommandInterface handler;

    public DBServer(int portNumber) throws SqlException {
        handler = new DBCommand();
        try {
            ServerSocket serverSocket = new ServerSocket(portNumber);
            System.out.println("Server Listening");
            while(true) processNextConnection(serverSocket);
        } catch(IOException ioe) {
            System.err.println(ioe);
        }
    }

    private void processNextConnection(ServerSocket serverSocket)
    {
        try {
            Socket socket = serverSocket.accept();
            BufferedReader socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter socketWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            System.out.println("Connection Established");
            while(true) processNextCommand(socketReader, socketWriter);
        } catch(IOException ioe) {
            System.err.println(ioe);
        } catch(NullPointerException npe) {
            System.out.println("Connection Lost");
        } catch (SqlException e) {
            e.printStackTrace();
        }
    }

    private void processNextCommand(BufferedReader socketReader, BufferedWriter socketWriter) throws IOException, NullPointerException, SqlException
    {
        try {
            String incomingCommand = socketReader.readLine();
            /* Everything happens in the below 3 lines: the command is passed into the Parser which feeds it into
            * the different statement classes depending on the entered command, the result is then flushed
            * back to the command line */
            socketWriter.write(Parser.parse(incomingCommand).runStatement(handler));
            socketWriter.write("\n" + ((char)4) + "\n");
            socketWriter.flush();
        } catch(IOException | SqlException e) {
            socketWriter.write("" + e);
            socketWriter.write("\n" + ((char)4) + "\n");
            socketWriter.flush();
        }
    }

    public static void main(String args[]) throws SqlException {
        DBServer server = new DBServer(8888);
    }
}
