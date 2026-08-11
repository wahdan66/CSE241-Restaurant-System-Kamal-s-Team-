package com.restaurant.network;

import javafx.application.Platform;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.function.Consumer;

public class OrderSocketClient {

    private static final String HOST = "localhost";
    private static final int PORT = 8888;
    private PrintWriter out;

    public void startListening(Consumer<String> onMessageReceived) {
        Thread clientThread = new Thread(() -> {
            try (
                    Socket socket = new Socket(HOST, PORT);
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
            ) {
                out = new PrintWriter(socket.getOutputStream(), true);
                String message;

                while ((message = in.readLine()) != null) {
                    final String msg = message;
                    // Safely execute on JavaFX Application Thread
                    Platform.runLater(() -> onMessageReceived.accept(msg));
                }
            } catch (Exception e) {
                System.out.println("[SocketClient] Not connected to live sync server.");
            }
        });
        clientThread.setDaemon(true);
        clientThread.start();
    }

    public void sendMessage(String message) {
        if (out != null) {
            out.println(message);
        }
    }
}