package com.example.chatapp.demo.client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField pseudoField;
    @FXML
    private Label errorLabel;

    private ChatClient client;

    @FXML
    private void joinChat() {
        String pseudo = pseudoField.getText().trim();
        if (pseudo.isEmpty()) {
            errorLabel.setText("Le pseudo ne peut pas être vide.");
            return;
        }

        try {
            // Se connecte au serveur et démarre l'écoute des messages
            client = new ChatClient("localhost", 1232, this::handleServerMessage);
            client.startClient();
            // Envoie le pseudo pour validation
            client.sendMessage(pseudo);
        } catch (IOException e) {
            errorLabel.setText("Erreur: Impossible de se connecter au serveur.");
            e.printStackTrace();
        }
    }

    private void handleServerMessage(String message) {
        Platform.runLater(() -> {
            switch (message) {
                case "SUBMIT_PSEUDO":
                    // Server is ready, pseudo already sent.
                    break;
                case "PSEUDO_ACCEPTED":
                    switchToChatView();
                    break;
                case "PSEUDO_REJECTED":
                    showAlert(Alert.AlertType.ERROR, "Login Failed", "Pseudo already in use.");
                    client.close();
                    break;
                case "GROUP_FULL":
                    showAlert(Alert.AlertType.ERROR, "Login Failed", "The chat group is full.");
                    client.close();
                    break;
                case "LOGIN_FAILED_BANNED":
                    showAlert(Alert.AlertType.ERROR, "Login Failed", "This account has been banned.");
                    client.close();
                    break;
                default:
                    // Other messages are ignored at this stage
                    break;
            }
        });
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void switchToChatView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("chat-view.fxml"));
            Parent root = loader.load();

            ChatController chatController = loader.getController();
            chatController.setClient(client); // Passe le client au contrôleur de chat

            Stage stage = (Stage) pseudoField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Chat - " + pseudoField.getText());

            // Assure la déconnexion propre à la fermeture de la fenêtre
            stage.setOnCloseRequest(event -> {
                client.close();
                Platform.exit();
            });

        } catch (IOException e) {
            e.printStackTrace();
            errorLabel.setText("Erreur lors du chargement de la vue de chat.");
        }
    }
}
