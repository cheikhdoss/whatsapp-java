package com.example.chatapp.demo.services;

import com.example.chatapp.demo.entities.Membre;
import com.example.chatapp.demo.entities.Message;
import com.example.chatapp.demo.repositories.MembreRepository;
import com.example.chatapp.demo.repositories.MessageRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ChatService {

    private final MembreRepository membreRepository = new MembreRepository();
    private final MessageRepository messageRepository = new MessageRepository();
    private static final List<String> BLACKLIST = Arrays.asList("GENOCID", "TERRORISM", "ATTACK", "CHELSEA", "JAVA NEKHOUL");

    public synchronized Optional<Membre> login(String pseudo) {
        // Find if the member already exists in the database
        Optional<Membre> membreOpt = membreRepository.findByPseudo(pseudo);

        if (membreOpt.isPresent()) {
            // The member exists, return them. The ClientHandler will check the ban status.
            return membreOpt;
        } else {
            // The member does not exist, create them (sign-up)
            if (isGroupFull()) {
                return Optional.empty(); // Group is full, cannot create a new member
            }

            Membre newMembre = new Membre();
            newMembre.setPseudo(pseudo);
            newMembre.setDateInscription(LocalDateTime.now());
            newMembre.setBanni(false);
            membreRepository.save(newMembre);
            return Optional.of(newMembre);
        }
    }

    public Message saveMessage(Membre expediteur, String contenu) {
        Message message = new Message();
        message.setContenu(contenu);
        message.setExpediteur(expediteur);
        message.setDateHeure(LocalDateTime.now());
        messageRepository.save(message);
        return message;
    }

    public boolean isBanned(String messageContent) {
        return BLACKLIST.stream().anyMatch(word -> messageContent.toUpperCase().contains(word));
    }

    public void banMember(Membre membre) {
        membre.setBanni(true);
        membreRepository.save(membre);
    }

    public boolean isGroupFull() {
        return membreRepository.count() >= 7;
    }

    public List<Message> getMessageHistory() {
        return messageRepository.findLast15Messages();
    }
}
