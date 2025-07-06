package com.example.chatapp.demo.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.function.Consumer;

public class ChatClient {

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    public Consumer<String> onMessageReceived;

    public ChatClient(String host, int port, Consumer<String> onMessageReceived) throws IOException {
        this.socket = new Socket(host, port);
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.onMessageReceived = onMessageReceived;
    }

    public void startClient() {
        new Thread(() -> {
            try {
                String serverMessage;
                while ((serverMessage = in.readLine()) != null) {
                    onMessageReceived.accept(serverMessage);
                }
            } catch (IOException e) {
                onMessageReceived.accept("ERROR: Connection lost with the server.");
            } finally {
                close();
            }
        }).start();
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    public void close() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
