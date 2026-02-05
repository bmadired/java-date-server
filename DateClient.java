import java.net.*;
import java.io.*;

public class DateClient {
    public static void main(String[] args) {
        try {
            Socket sock = new Socket("172.16.41.197", 6013);

            PrintWriter pout = new PrintWriter(sock.getOutputStream(), true);
            BufferedReader bin = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));

            // Thread to read messages from server
            new Thread(() -> {
                String serverMessage;
                try {
                    while ((serverMessage = bin.readLine()) != null) {
                        if (serverMessage.equalsIgnoreCase("exit")) {
                            System.out.println("Server closed the connection.");
                            System.exit(0); // terminate client
                        }
                        System.out.println("Server: " + serverMessage);
                    }
                } catch (IOException e) {
                    System.err.println("Server connection closed.");
                }
            }).start();

            // Main thread handles sending messages to server
            System.out.println("Type messages to send to the server (type 'exit' to quit):");
            String message;
            while ((message = userInput.readLine()) != null) {
                pout.println(message);
                if (message.equalsIgnoreCase("exit")) break;
            }

            sock.close();

        } catch (IOException ioe) {
            System.err.println(ioe);
        }
    }
}