import java.net.*;
import java.io.*;

public class DateClient {
    public static void main(String[] args) {
        try {
            Socket sock = new Socket("192.168.1.169", 6013);

            PrintWriter pout = new PrintWriter(sock.getOutputStream(), true);
            BufferedReader bin  = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));

            // Handle name registration before starting receiver thread
            String line;
            while ((line = bin.readLine()) != null) {
                System.out.println(line);
                if (line.startsWith("Your ID is:")) break;
                pout.println(userInput.readLine());
            }

            // Only start receiver thread after registration is complete
            Thread receiver = new Thread(() -> {
                try {
                    String incoming;
                    while ((incoming = bin.readLine()) != null) {
                        System.out.println(incoming);
                    }
                } catch (IOException e) {}
            });
            receiver.setDaemon(true);
            receiver.start();

            String userMessage;
            while ((userMessage = userInput.readLine()) != null) {
                pout.println(userMessage);
                if (userMessage.equalsIgnoreCase("exit")) break;
            }

            sock.close();

        } catch (IOException ioe) {
            System.err.println(ioe);
        }
    }
}
