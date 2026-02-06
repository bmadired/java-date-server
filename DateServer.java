import java.net.*;
import java.io.*;

public class DateServer
{
    public static void main (String[] args) {
        try {
            ServerSocket sock = new ServerSocket(6013);

            /* now listen for connection */
            while (true) {
                Socket client = sock.accept();

                System.out.println("Client connected");

                PrintWriter pout = new PrintWriter(client.getOutputStream(), true);
                BufferedReader bin = new BufferedReader(
                        new InputStreamReader(client.getInputStream()));
                BufferedReader serverInput = new BufferedReader(
                        new InputStreamReader(System.in));

                /* write the Date to the socket */
                pout.println(new java.util.Date().toString());

                String clientMessage;
                String serverMessage;

                while (true) {

                    clientMessage = bin.readLine();

                    if (clientMessage == null ||
                        clientMessage.equalsIgnoreCase("exit")) {
                        break;
                    }

                    System.out.println("Client: " + clientMessage);

                    serverMessage = serverInput.readLine();

                    if (serverMessage.equalsIgnoreCase("exit")) {
                        pout.println("exit");
                        break;
                    }

                    pout.println(serverMessage);
                }

                /* close the socket and resume */
                /* listening for connection */
                client.close();
            }
        }
        catch (IOException ioe) {
            System.err.println(ioe);
        }
    }
}
