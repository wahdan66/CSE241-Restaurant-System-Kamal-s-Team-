package com.restaurant.network;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class OrderSocketServer implements Runnable {

    private static final int PORT = 8888;
    private static final Set<PrintWriter> clientWriters = Collections.synchronizedSet(new HashSet<>());
    private static OrderSocketServer instance;
    private boolean running = true;

    private OrderSocketServer() {}

    public static synchronized OrderSocketServer getInstance() {
        if (instance == null) {
            instance = new OrderSocketServer();
        }
        return instance;
    }

    public void startServer() {
        Thread serverThread = new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(PORT)) {
                System.out.println("[SocketServer] Server running on port " + PORT);
                while (running) {
                    Socket clientSocket = serverSocket.accept();
                    new Thread(new ClientHandler(clientSocket)).start();
                }
            } catch (java.net.BindException e) {
                System.out.println("[SocketServer] Port " + PORT + " already in use. Joining as a client instance.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "SocketServerThread");

        serverThread.setDaemon(true);
        serverThread.start();
    }
    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("[SocketServer] Live sync server started on port " + PORT);

            while (running) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (Exception e) {
            System.err.println("[SocketServer] Error: " + e.getMessage());
        }
    }

    public static void broadcast(String message) {
        synchronized (clientWriters) {
            for (PrintWriter writer : clientWriters) {
                writer.println(message);
            }
        }
    }

    private static class ClientHandler implements Runnable {
        private final Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            PrintWriter out = null;
            try (Socket clientSocket = socket;
                 BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                clientWriters.add(out);
                String message;
                while ((message = in.readLine()) != null) {
                    if ("REQUEST_MENU".equals(message)) {
                        out.println(MenuUpdateMessage.snapshot());
                    } else {
                        MenuUpdateMessage.apply(message);
                        OrderUpdateMessage.apply(message);
                        broadcast(message);
                    }
                }
            } catch (Exception e) {
                // Client disconnected
            } finally {
                if (out != null) {
                    clientWriters.remove(out);
                    out.close();
                }
            }
        }
    }
}
