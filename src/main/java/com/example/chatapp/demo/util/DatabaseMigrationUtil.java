package com.example.chatapp.demo.util;

import jakarta.persistence.*;

/**
 * Utilitaire pour exécuter les migrations de base de données.
 * Cette classe permet de créer les tables dans la base de données
 * en se basant sur les entités JPA et la configuration dans persistence.xml.
 */
public class DatabaseMigrationUtil {

    /**
     * Exécute les migrations pour créer ou mettre à jour les tables dans la base de données.
     * Cette méthode utilise la configuration hibernate.hbm2ddl.auto dans persistence.xml
     * pour déterminer comment les tables doivent être créées ou mises à jour.
     */
    public static void runMigrations() {
        System.out.println("Démarrage des migrations de la base de données...");
        
        try {
            // Initialiser l'EntityManagerFactory, ce qui déclenche la création/mise à jour du schéma
            EntityManagerFactory emf = JpaManager.getEntityManagerFactory();
            
            System.out.println("Migrations terminées avec succès.");
            System.out.println("Les tables ont été créées/mises à jour dans la base de données.");
        } catch (Exception e) {
            System.err.println("Erreur lors de l'exécution des migrations: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Point d'entrée principal pour exécuter les migrations depuis la ligne de commande.
     */
    public static void main(String[] args) {
        runMigrations();
        
        // Fermer l'EntityManagerFactory après avoir terminé
        JpaManager.close();
    }
}