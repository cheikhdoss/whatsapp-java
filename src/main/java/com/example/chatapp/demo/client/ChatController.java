package com.example.chatapp.demo.client;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.util.Arrays;

public class ChatController {

    @FXML
    private ListView<String> messageArea;
    @FXML
    private TextField messageField;
    @FXML
    private ListView<String> userListView;
    @FXML
    private TextField newMemberField;

    private ChatClient client;

    public void setClient(ChatClient client) {
        this.client = client;
        client.onMessageReceived = this::handleServerMessage;
    }

    @FXML
    private void sendMessage() {
        String message = messageField.getText().trim();
        if (!message.isEmpty()) {
            messageArea.getItems().add("Me: " + message);
            messageArea.scrollTo(messageArea.getItems().size() - 1);
            client.sendMessage(message);
            messageField.clear();
        }
    }

    @FXML
    private void signOut() {
        client.close();
        Platform.exit();
    }

    private void handleServerMessage(String message) {
        Platform.runLater(() -> {
            if (message.startsWith("USERLIST:")) {
                updateUserList(message.substring(9));
            } else if (message.equals("BANNED")) {
                handleBan();
            } else if (message.startsWith("ERROR:")) {
                showError(message);
            } else {
                messageArea.getItems().add(message);
                messageArea.scrollTo(messageArea.getItems().size() - 1);
                playNotificationSound();
            }
        });
    }

    private void updateUserList(String csvUsers) {
        if (csvUsers.isEmpty()) {
            userListView.setItems(FXCollections.observableArrayList("Aucun ami connecté"));
        } else {
            userListView.setItems(FXCollections.observableArrayList(Arrays.asList(csvUsers.split(","))));
        }
    }

    private void handleBan() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Banni");
        alert.setHeaderText(null);
        alert.setContentText("Vous avez été banni du chat pour avoir utilisé des mots injurieux.");
        alert.showAndWait();
        signOut();
    }

    @FXML
    private void addMember() {
        String newMember = newMemberField.getText().trim();
        if (!newMember.isEmpty()) {
            client.sendMessage("ADD_USER:" + newMember);
            newMemberField.clear();
        }
    }

    private void playNotificationSound() {
        try {
            String soundFile = "src/main/resources/com/example/chatapp/demo/client/notification.wav";
            Media sound = new Media(new File(soundFile).toURI().toString());
            MediaPlayer mediaPlayer = new MediaPlayer(sound);
            mediaPlayer.play();
        } catch (Exception e) {
            System.err.println("Erreur lors de la lecture du son de notification: " + e.getMessage());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur de Connexion");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
        signOut();
    }
}
