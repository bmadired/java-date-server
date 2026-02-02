import java.io.*;
import java.net.*;

public class DateClient {
    public static void main(String[] args) {
        try {
            String serverIP = "192.168.137.1"; // localhost
            int port = 8080;

            Socket socket = new Socket(serverIP, port);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));

            String date = in.readLine();
            System.out.println("Date from server: " + date);

            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
