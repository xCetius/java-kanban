package main.java.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import main.java.adapters.DurationTypeAdapter;
import main.java.adapters.LocalDateTimeTypeAdapter;
import main.java.managers.HistoryManager;
import main.java.managers.InMemoryTaskManager;
import main.java.managers.TaskManager;


import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

public class BaseHttpHandler {
    protected TaskManager taskManager;
    protected HistoryManager historyManager = InMemoryTaskManager.historyManager;
    protected GsonBuilder gsonBuilder = new GsonBuilder();

    protected Gson gson = gsonBuilder
            .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
            .create();

    public BaseHttpHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    protected void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        if (h.getRequestMethod().equals("POST")) {
            h.sendResponseHeaders(201, resp.length);
        } else {
            h.sendResponseHeaders(200, resp.length);
        }
        try (OutputStream os = h.getResponseBody()) {
            os.write(resp);
        }
    }

    protected void sendNotFound(HttpExchange h) throws IOException {
        byte[] resp = "Not Found".getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(404, resp.length);
        try (OutputStream os = h.getResponseBody()) {
            os.write(resp);
        }
    }

    protected void sendHasInteractions(HttpExchange h) throws IOException {
        byte[] resp = "Not Acceptable".getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(406, resp.length);
        try (OutputStream os = h.getResponseBody()) {
            os.write(resp);
        }
    }
}