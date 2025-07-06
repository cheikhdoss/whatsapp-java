package com.example.chatapp.demo.repositories;

import com.example.chatapp.demo.entities.Message;
import com.example.chatapp.demo.util.JpaManager;

import jakarta.persistence.*;

import java.util.Collections;
import java.util.List;

public class MessageRepository {

    public void save(Message message) {
        EntityManager em = JpaManager.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(message);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    public List<Message> findLast15Messages() {
        EntityManager em = JpaManager.getEntityManager();
        try {
            List<Message> messages = em.createQuery("SELECT m FROM Message m JOIN FETCH m.expediteur ORDER BY m.dateHeure DESC", Message.class)
                    .setMaxResults(15)
                    .getResultList();
            Collections.reverse(messages); // Pour afficher du plus ancien au plus récent
            return messages;
        } finally {
            em.close();
        }
    }
}
