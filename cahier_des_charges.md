# Cahier des charges – Simulation d’un groupe WhatsApp

## 1. Objectif général
Développer une application Java simulant un groupe WhatsApp avec une interface graphique, gestion réseau, base de données, et filtrage de messages.

---

## 2. Technologies et outils à utiliser
- **Communication réseau :** Sockets Java (TCP)
- **Interface graphique :** JavaFX + Scene Builder
- **Base de données :** MySQL ou PostgreSQL
- **Accès aux données :** JPA / Hibernate
- **Utilitaires :** Lombok (pour réduire le code boilerplate)
- **Gestionnaire de dépendances :** Maven

---

## 3. Fonctionnalités attendues

### 3.1 Gestion des membres
- Groupe limité à 7 membres maximum.
- Enregistrement d’un pseudo unique à la connexion (vérification en base).
- Possibilité de quitter le groupe volontairement.
- Message d’erreur clair si le groupe est plein (client et serveur).

### 3.2 Messagerie
- Envoi de messages texte à tous les membres sauf à l’expéditeur.
- Ajout de la date, l’heure et le pseudo de l’expéditeur à chaque message.
- Notification (son ou pop-up) à la réception d’un nouveau message.
- Chargement de l’historique des 15 derniers messages à la connexion.

### 3.3 Persistance des données
- Sauvegarde des membres et messages en base de données.
- Utilisation d’entités JPA avec relations (ex : OneToMany Membres/Messages).

### 3.4 Filtrage et sécurité
- Liste noire de mots injurieux : GENOCID, TERRORISM, ATTACK, CHELSEA, JAVA NEKHOUL.
- Bannissement immédiat de l’utilisateur si un message contient un mot interdit.
- Notification à tous les membres lors d’un bannissement.

---

## 4. Contraintes techniques
- Respect du modèle client/serveur (Sockets TCP).
- Respect des bonnes pratiques Java (POO, séparation des couches, etc.).
- Gestion des erreurs et des cas limites (pseudo déjà pris, groupe plein, etc.).
- Interface utilisateur ergonomique et réactive (JavaFX).

---

## 5. Modélisation (JPA)
- Entité `Membre` : id, pseudo, date d’inscription, statut (banni ou non), etc.
- Entité `Message` : id, contenu, date/heure, expéditeur (relation ManyToOne), etc.
- Relation OneToMany entre Membre et Message.

---

## 6. Livrables
- Code source complet (Maven)
- Script SQL de création de la base de données
- Documentation utilisateur et technique
- Fichier de configuration (application.properties ou équivalent)
- Fichier README

---

## 7. Bonus (facultatif)
- Gestion des avatars
- Messages privés
- Statistiques (nombre de messages, membres actifs, etc.)
