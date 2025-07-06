package com.example.chatapp.demo.server;

import com.example.chatapp.demo.entities.Membre;
import com.example.chatapp.demo.entities.Message;
import com.example.chatapp.demo.services.ChatService;
import com.example.chatapp.demo.services.ChatService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class ClientHandler implements Runnable {

    private final Socket clientSocket;
    private final ChatService chatService;
    private final com.example.chatapp.demo.repositories.MembreRepository membreRepository;
    private PrintWriter out;
    private BufferedReader in;
    private Membre membre;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
        this.chatService = new ChatService();
        this.membreRepository = new com.example.chatapp.demo.repositories.MembreRepository();
    }

    @Override
    public void run() {
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            // --- Processus de connexion ---
            while (true) {
                out.println("SUBMIT_PSEUDO");
                String pseudo = in.readLine();
                if (pseudo == null) return; // Client déconnecté

                if (Server.isPseudoTaken(pseudo)) {
                    out.println("PSEUDO_REJECTED"); // Déjà connecté
                    continue;
                }

                Optional<Membre> membreOpt = chatService.login(pseudo);

                if (membreOpt.isPresent()) {
                    this.membre = membreOpt.get();

                    if (membre.isBanni()) {
                        out.println("LOGIN_FAILED_BANNED");
                        return; // The 'finally' block will handle closing the socket.
                    }

                    out.println("PSEUDO_ACCEPTED");
                    // Envoi de l'historique des messages
                    chatService.getMessageHistory().forEach(msg -> {
                        String formatted = String.format("%s [%s]: %s",
                                msg.getExpediteur().getPseudo(),
                                msg.getDateHeure().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                                msg.getContenu());
                        out.println(formatted);
                    });
                    break;
                } else {
                    // La connexion a échoué. Vérifions pourquoi.
                    if (chatService.isGroupFull() && membreRepository.findByPseudo(pseudo).isEmpty()) {
                        out.println("GROUP_FULL");
                    } else {
                        out.println("PSEUDO_REJECTED"); // Banni ou autre erreur
                    }
                }
            }

            // --- Finalisation de la connexion ---
            Server.addClient(this); // Ajoute le client à la liste et notifie les autres
            sendMessage("SERVER: Bienvenue sur le chat, " + membre.getPseudo() + "!");

            // --- Envoi de l'historique ---
            chatService.getMessageHistory().forEach(msg -> {
                String formattedMsg = String.format("[%s] %s: %s",
                        msg.getDateHeure().format(DateTimeFormatter.ofPattern("HH:mm")),
                        msg.getExpediteur().getPseudo(),
                        msg.getContenu());
                sendMessage(formattedMsg);
            });

            // --- Boucle de lecture des messages ---
            String clientMessage;
            while ((clientMessage = in.readLine()) != null) {
                if (clientMessage.startsWith("ADD_USER:")) {
                    // TODO: Implement "Add Friend" functionality on the server
                    System.out.println("Received ADD_USER command (feature not implemented): " + clientMessage);
                    continue; // Skip processing as a regular message
                }

                if (chatService.isBanned(clientMessage)) {
                    chatService.banMember(membre);
                    Server.broadcastSystemMessage("SERVER: " + membre.getPseudo() + " a été banni pour avoir tenu des propos injurieux.");
                    out.println("BANNED");
                    break; // Termine la boucle et la connexion
                }

                Message savedMessage = chatService.saveMessage(membre, clientMessage);
                String formattedMsg = String.format("[%s] %s: %s",
                        savedMessage.getDateHeure().format(DateTimeFormatter.ofPattern("HH:mm")),
                        savedMessage.getExpediteur().getPseudo(), // Use sender from saved message
                        savedMessage.getContenu());
                Server.broadcastMessage(formattedMsg, this);
            }

        } catch (IOException e) {
            System.out.println("Erreur avec le client " + (membre != null ? membre.getPseudo() : "") + ": " + e.getMessage());
        } finally {
            if (membre != null) {
                Server.removeClient(this);
            }
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    public String getPseudo() {
        return membre != null ? membre.getPseudo() : "";
    }
}
