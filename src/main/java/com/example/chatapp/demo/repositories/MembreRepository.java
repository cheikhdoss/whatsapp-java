package com.example.chatapp.demo.repositories;

import com.example.chatapp.demo.entities.Membre;
import com.example.chatapp.demo.util.JpaManager;

import jakarta.persistence.*;

import java.util.List;
import java.util.Optional;

public class MembreRepository {

    public void save(Membre membre) {
        EntityManager em = JpaManager.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            if (membre.getId() == null) {
                em.persist(membre); // New entity
            } else {
                em.merge(membre);   // Existing entity
            }
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

    public Optional<Membre> findByPseudo(String pseudo) {
        EntityManager em = JpaManager.getEntityManager();
        try {
            TypedQuery<Membre> query = em.createQuery("SELECT m FROM Membre m WHERE m.pseudo = :pseudo", Membre.class);
            query.setParameter("pseudo", pseudo);
            return Optional.ofNullable(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    public List<Membre> findAll() {
        EntityManager em = JpaManager.getEntityManager();
        try {
            return em.createQuery("SELECT m FROM Membre m", Membre.class).getResultList();
        } finally {
            em.close();
        }
    }

    public long count() {
        EntityManager em = JpaManager.getEntityManager();
        try {
            return em.createQuery("SELECT COUNT(m) FROM Membre m WHERE m.banni = false", Long.class).getSingleResult();
        } finally {
            em.close();
        }
    }
}
