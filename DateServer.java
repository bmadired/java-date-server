import java.net.*;
import java.io.*;
import java.util.*;

public class DateServer {
    static List<ClientHandler> clients = Collections.synchronizedList(new ArrayList<>());
    static int idCounter = 1;

    public static void main(String[] args) {
        try {
            ServerSocket sock = new ServerSocket(6013);

            new Thread(() -> {
                BufferedReader serverInput = new BufferedReader(new InputStreamReader(System.in));
                String input;
                try {
                    while ((input = serverInput.readLine()) != null) {
                        if (input.equalsIgnoreCase("all")) {
                            synchronized (clients) {
                                for (ClientHandler c : clients) {
                                    System.out.println(c.id + ". " + c.name);
                                }
                            }
                        } else {
                            try {
                                int clientId = Integer.parseInt(input);
                                ClientHandler target = findClientById(clientId);
                                if (target != null) {
                                    String msg = serverInput.readLine();
                                    target.sendMessage("Server: " + msg);
                                }
                            } catch (NumberFormatException e) {}
                        }
                    }
                } catch (IOException e) {}
            }).start();

            while (true) {
                Socket client = sock.accept();
                ClientHandler handler = new ClientHandler(client);
                new Thread(handler).start();
            }

        } catch (IOException ioe) {
            System.err.println(ioe);
        }
    }

    static synchronized ClientHandler findClientById(int id) {
        for (ClientHandler c : clients) {
            if (c.id == id) return c;
        }
        return null;
    }

    static synchronized boolean nameExists(String name) {
        for (ClientHandler c : clients) {
            if (c.name.equalsIgnoreCase(name)) return true;
        }
        return false;
    }

    static void broadcast(String msg, ClientHandler sender) {
        synchronized (clients) {
            for (ClientHandler c : clients) {
                if (c != sender) {
                    c.sendMessage(msg);
                }
            }
        }
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
                bin  = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                pout.println("Enter your name:");
                name = bin.readLine();
                while (nameExists(name)) {
                    pout.println("Name already taken. Enter another name:");
                    name = bin.readLine();
                }

                synchronized (DateServer.class) {
                    id = idCounter++;
                    clients.add(this);
                }

                pout.println("Your ID is: " + id);

                String message;
                while ((message = bin.readLine()) != null) {
                    if (message.equalsIgnoreCase("exit")) break;
                    broadcast(name + ": " + message, this);
                }

            } catch (IOException e) {
            } finally {
                clients.remove(this);
                try { socket.close(); } catch (IOException e) {}
            }
        }

        void sendMessage(String msg) {
            pout.println(msg);
        }
    }
}
