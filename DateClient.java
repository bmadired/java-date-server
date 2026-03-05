import java.net.*;
import java.io.*;

public class DateClient {
    public static void main(String[] args) {
        try {
            Socket sock = new Socket("192.168.1.169", 6013);

            PrintWriter pout = new PrintWriter(sock.getOutputStream(), true);
            BufferedReader bin  = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));

            Thread receiver = new Thread(() -> {
                try {
                    String incoming;
                    while ((incoming = bin.readLine()) != null) {
                        System.out.println("Server: " + incoming);
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
