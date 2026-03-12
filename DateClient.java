import java.net.*;
import java.io.*;
 
public class DateClient {
    public static void main(String[] args) {
        try {
            Socket sock = new Socket("172.16.41.197", 6013);
            PrintWriter pout = new PrintWriter(sock.getOutputStream(), true);
            BufferedReader bin = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
            new Thread(new Runnable() {
                public void run() {
                    try {
                        String incoming;
                        while ((incoming = bin.readLine()) != null) {
                            System.out.println(incoming);
                            if (incoming.equalsIgnoreCase("Connection was ended.")) {
                                System.exit(0);
                            }
                        }
                    } catch (IOException e) {}
                }
            }).start();
            String userMessage;
            while ((userMessage = userInput.readLine()) != null) {
                if (userMessage.equalsIgnoreCase("exit")) {
                    pout.println("exit");
                    System.out.println("Connection was ended.");
                    break;
                } else if (userMessage.equalsIgnoreCase("list")) {
                    pout.println("list");
                } else if (userMessage.equalsIgnoreCase("all")) {
                    String broadMsg = userInput.readLine();
                    pout.println("all");
                    pout.println(broadMsg);
                } else if (userMessage.matches("\\d+")) {
                    int targetId = Integer.parseInt(userMessage);
                    String privateMsg = userInput.readLine();
                    pout.println("@" + targetId);
                    pout.println(privateMsg);
                } else {
                    pout.println(userMessage);
                }
            }
            sock.close();
        } catch (IOException ioe) {
            System.err.println(ioe);
        }
    }
}

