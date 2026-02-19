import java.net.*;
import java.io.*;

public class DateClient
{
    public static void main(String[] args) {
        try {
            Socket sock = new Socket("172.16.41.197", 6013);

            PrintWriter pout = new PrintWriter(sock.getOutputStream(), true);
            BufferedReader bin = new BufferedReader(
                    new InputStreamReader(sock.getInputStream()));
            BufferedReader userInput = new BufferedReader(
                    new InputStreamReader(System.in));

            System.out.println("Server: " + bin.readLine());

            String userMessage;
            String serverMessage;

            while (true) {

                userMessage = userInput.readLine();

                if (userMessage == null ||
                    userMessage.equalsIgnoreCase("exit")) {
                    pout.println("exit");
                    break;
                }

                pout.println(userMessage);

                serverMessage = bin.readLine();

                if (serverMessage == null ||
                    serverMessage.equalsIgnoreCase("exit")) {
                    break;
                }

                System.out.println("Server: " + serverMessage);
            }

            sock.close();

        }
        catch (IOException ioe) {
            System.err.println(ioe);
        }
    }
}
