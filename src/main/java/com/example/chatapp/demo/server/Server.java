package com.example.chatapp.demo.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class Server {

    private static final int PORT = 1232;
    private static final List<ClientHandler> clients = new ArrayList<>();
    private static final ExecutorService pool = Executors.newCachedThreadPool();

    public static void main(String[] args) {
        System.out.println("Serveur de chat démarré...");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nouveau client connecté: " + clientSocket.getInetAddress());
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                pool.execute(clientHandler);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addClient(ClientHandler client) {
        synchronized (clients) {
            clients.add(client);
            System.out.println("[DEBUG] Clients actifs après ajout: " + clients.stream().map(ClientHandler::getPseudo).toList());
        }
        System.out.println("Client authentifié: " + client.getPseudo());
        broadcastSystemMessage("SERVER: " + client.getPseudo() + " a rejoint le chat.");
        broadcastUserList();
    }

    public static void broadcastMessage(String message, ClientHandler sender) {
        List<ClientHandler> clientsToBroadcast;
        synchronized (clients) {
            clientsToBroadcast = new ArrayList<>(clients);
        }
        System.out.println("[DEBUG] Broadcasting message to " + (clientsToBroadcast.size() - 1) + " other clients.");
        for (ClientHandler client : clientsToBroadcast) {
            if (client != sender) {
                client.sendMessage(message);
            }
        }
    }

    public static void removeClient(ClientHandler client) {
        synchronized (clients) {
            clients.remove(client);
            System.out.println("[DEBUG] Clients actifs après suppression: " + clients.stream().map(ClientHandler::getPseudo).toList());
        }
        System.out.println("Client déconnecté: " + client.getPseudo());
        broadcastSystemMessage("SERVER: " + client.getPseudo() + " a quitté le chat.");
        broadcastUserList();
    }

    public static void broadcastSystemMessage(String message) {
        List<ClientHandler> clientsToBroadcast;
        synchronized (clients) {
            clientsToBroadcast = new ArrayList<>(clients);
        }
        for (ClientHandler client : clientsToBroadcast) {
            client.sendMessage(message);
        }
    }

    public static boolean isPseudoTaken(String pseudo) {
        if (pseudo == null || pseudo.isEmpty()) {
            return true; // Ne pas autoriser les pseudos vides
        }
        synchronized (clients) {
            return clients.stream().anyMatch(c -> pseudo.equals(c.getPseudo()));
        }
    }

    public static void broadcastUserList() {
        String userList;
        synchronized (clients) {
            userList = clients.stream()
                    .map(ClientHandler::getPseudo)
                    .filter(p -> p != null && !p.isEmpty())
                    .collect(Collectors.joining(","));
        }
        broadcastSystemMessage("USERLIST:" + userList);
    }
}
