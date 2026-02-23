import java.net.*;
import java.io.*;

public class DateServer
{
    static ClientHandler[] clients = new ClientHandler[100];
    static String[] clientNames = new String[100];
    static int clientCount = 0;
    static int idCounter = 1;

    public static void main (String[] args) {
        try {
            ServerSocket sock = new ServerSocket(6013);

            new Thread(new Runnable() {
                public void run() {
                    BufferedReader serverInput =
                            new BufferedReader(new InputStreamReader(System.in));
                    String input;

                    try {
                        while ((input = serverInput.readLine()) != null) {

                            if (input.equalsIgnoreCase("all")) {
                                for (int i = 0; i < clientCount; i++) {
                                    System.out.println(clients[i].id + ". " + clients[i].name);
                                }
                            }
                            else {
                                try {
                                    int clientId = Integer.parseInt(input);
                                    ClientHandler target = findClientById(clientId);

                                    if (target != null) {
                                        String msg = serverInput.readLine();
                                        target.sendMessage(msg);
                                    }

                                } catch (NumberFormatException e) {
                                }
                            }
                        }
                    } catch (IOException e) {
                    }
                }
            }).start();

            while (true) {
                Socket client = sock.accept();
                System.out.println("Client connected");

                ClientHandler handler = new ClientHandler(client);
                new Thread(handler).start();
            }
        }
        catch (IOException ioe) {
            System.err.println(ioe);
        }
    }

    static synchronized boolean nameExists(String name) {
        for (int i = 0; i < clientCount; i++) {
            if (clientNames[i].equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    static synchronized void addClient(ClientHandler handler) {
        clients[clientCount] = handler;
        clientNames[clientCount] = handler.name;
        clientCount++;
    }

    static synchronized void removeClient(ClientHandler handler) {
        for (int i = 0; i < clientCount; i++) {
            if (clients[i] == handler) {
                for (int j = i; j < clientCount - 1; j++) {
                    clients[j] = clients[j + 1];
                    clientNames[j] = clientNames[j + 1];
                }
                clientCount--;
                break;
            }
        }
    }

    static synchronized ClientHandler findClientById(int id) {
        for (int i = 0; i < clientCount; i++) {
            if (clients[i].id == id) {
                return clients[i];
            }
        }
        return null;
    }

    static class ClientHandler implements Runnable {

        Socket socket;
        PrintWriter pout;
        BufferedReader bin;
        int id;
        String name;

        ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                pout = new PrintWriter(socket.getOutputStream(), true);
                bin = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));

                pout.println("Enter your name:");
                name = bin.readLine();

                while (nameExists(name)) {
                    pout.println("Name already taken. Enter another name:");
                    name = bin.readLine();
                }

                id = idCounter++;
                addClient(this);

                pout.println("Your ID is: " + id);

                String message;

                while ((message = bin.readLine()) != null) {

                    if (message.equalsIgnoreCase("exit"))
                        break;

                    System.out.println(name + ": " + message);
                }

                removeClient(this);
                socket.close();

            } catch (IOException e) {
            }
        }

        void sendMessage(String msg) {
            pout.println(msg);
        }
    }
}
