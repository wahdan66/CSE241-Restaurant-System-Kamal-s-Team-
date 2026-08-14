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
    private volatile PrintWriter out;

    public void startListening(Consumer<String> onMessageReceived) {
        startListening(onMessageReceived, null);
    }

    public void startListening(Consumer<String> onMessageReceived, Runnable onConnected) {
        Thread clientThread = new Thread(() -> {
            PrintWriter writer = null;
            try (Socket socket = new Socket(HOST, PORT);
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
            ) {
                writer = new PrintWriter(socket.getOutputStream(), true);
                out = writer;
                if (onConnected != null) {
                    Platform.runLater(onConnected);
                }
                String message;

                while ((message = in.readLine()) != null) {
                    final String msg = message;
                    // Safely execute on JavaFX Application Thread
                    Platform.runLater(() -> onMessageReceived.accept(msg));
                }
            } catch (Exception e) {
                System.out.println("[SocketClient] Not connected to live sync server.");
            } finally {
                if (out == writer) {
                    out = null;
                }
            }
        });
        clientThread.setDaemon(true);
        clientThread.start();
    }

    public void sendMessage(String message) {
        PrintWriter writer = out;
        if (writer != null) {
            writer.println(message);
            return;
        }

        Thread senderThread = new Thread(() -> {
            try (Socket socket = new Socket(HOST, PORT);
                 PrintWriter temporaryWriter = new PrintWriter(socket.getOutputStream(), true)) {
                temporaryWriter.println(message);
            } catch (Exception e) {
                System.out.println("[SocketClient] Could not send live sync message.");
            }
        }, "OrderSocketMessageSender");
        senderThread.setDaemon(true);
        senderThread.start();
    }
}
