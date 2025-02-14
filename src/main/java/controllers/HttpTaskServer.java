package main.java.controllers;

import com.sun.net.httpserver.HttpServer;
import main.java.managers.TaskManager;


import java.io.IOException;
import java.net.InetSocketAddress;


public class HttpTaskServer {
    private final TaskManager taskManager;
    HttpServer httpServer;
    private static final int PORT = 8080;

    public HttpTaskServer(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    public void start() throws IOException {
        this.httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TaskHandler(taskManager));
        httpServer.createContext("/epics", new EpicHandler(taskManager));
        httpServer.createContext("/subtasks", new SubTaskHandler(taskManager));
        httpServer.createContext("/history", new HistoryHandler(taskManager));
        httpServer.createContext("/prioritized", new PrioritizedHandler(taskManager));
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(0);
    }

}
