import java.net.*;
import java.io.*;

public class DateServer {

    // Max number of clients
    private static final int MAX_CLIENTS = 50;

    // Store client names and sockets
    private static String[] clientNames = new String[MAX_CLIENTS];
    private static Socket[] clientSockets = new Socket[MAX_CLIENTS];

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(6013);
            System.out.println("Server started on port 6013...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ClientHandler(clientSocket)).start();
            }

        } catch (IOException ioe) {
            System.err.println(ioe);
        }
    }

    static class ClientHandler implements Runnable {
        private Socket clientSocket;
        private String clientName;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        public void run() {
            try {
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                // Ask for client name
                while (true) {
                    out.println("Enter your name:");
                    clientName = in.readLine();

                    if (clientName == null || clientName.isEmpty()) {
                        clientSocket.close();
                        return;
                    }

                    synchronized (clientNames) {
                        if (isNameTaken(clientName)) {
                            out.println("Name already taken, try another.");
                        } else {
                            addClient(clientName, clientSocket);
                            out.println("Welcome, " + clientName + "!");
                            broadcast(clientName + " has joined the chat.", clientName);
                            break;
                        }
                    }
                }

                // Display current date
                out.println("Server date: " + new java.util.Date().toString());

                String message;
                while ((message = in.readLine()) != null) {
                    if (message.equalsIgnoreCase("exit")) {
                        break;
                    }

                    // Display on server console
                    System.out.println(clientName + ": " + message);

                    // Send to other clients
                    broadcast(clientName + ": " + message, clientName);
                }

                // Client disconnects
                synchronized (clientNames) {
                    removeClient(clientName);
                }
                broadcast(clientName + " has left the chat.", clientName);
                clientSocket.close();

            } catch (IOException ioe) {
                System.err.println("Connection error with client " + clientName + ": " + ioe.getMessage());
            }
        }

        private boolean isNameTaken(String name) {
            for (int i = 0; i < MAX_CLIENTS; i++) {
                if (clientNames[i] != null && clientNames[i].equals(name)) {
                    return true;
                }
            }
            return false;
        }

        private void addClient(String name, Socket socket) {
            for (int i = 0; i < MAX_CLIENTS; i++) {
                if (clientNames[i] == null) {
                    clientNames[i] = name;
                    clientSockets[i] = socket;
                    break;
                }
            }
        }

        private void removeClient(String name) {
            for (int i = 0; i < MAX_CLIENTS; i++) {
                if (name.equals(clientNames[i])) {
                    clientNames[i] = null;
                    clientSockets[i] = null;
                    break;
                }
            }
        }

        private void broadcast(String message, String sender) {
            for (int i = 0; i < MAX_CLIENTS; i++) {
                try {
                    if (clientSockets[i] != null && !clientNames[i].equals(sender)) {
                        PrintWriter out = new PrintWriter(clientSockets[i].getOutputStream(), true);
                        out.println(message);
                    }
                } catch (IOException e) {
                    System.err.println("Failed to send message to " + clientNames[i]);
                }
            }
        }
    }
}

