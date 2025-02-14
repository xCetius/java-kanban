package main.java.controllers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import main.java.domain.Epic;
import main.java.domain.Subtask;
import main.java.exceptions.ManagerSaveException;
import main.java.exceptions.TaskNotFoundException;
import main.java.managers.TaskManager;


import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {

    public EpicHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String[] pathParts = path.split("/");
        boolean needSubTasks = pathParts.length == 4;

        int epicId = pathParts.length >= 3 && !pathParts[2].isEmpty() ? Integer.parseInt(pathParts[2]) : 0;
        boolean hasId = epicId != 0;

        switch (method) {
            case "GET":
                if (!hasId) {
                    List<Epic> epics = taskManager.getEpics();
                    sendText(exchange, gson.toJson(epics));
                } else {
                    if (needSubTasks) {
                        try {
                            List<Subtask> subtasks = taskManager.getEpicSubs(epicId);
                            sendText(exchange, gson.toJson(subtasks));
                        } catch (TaskNotFoundException e) {
                            sendNotFound(exchange);
                        }

                    } else {
                        try {
                            Epic epic = taskManager.getEpicById(epicId);
                            sendText(exchange, gson.toJson(epic));
                        } catch (Exception e) {
                            sendNotFound(exchange);
                            System.out.println(e.getMessage());
                        }
                    }
                }
                break;

            case "POST":
                InputStream inputStream = exchange.getRequestBody();
                String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                inputStream.close();
                Epic epic = gson.fromJson(body, Epic.class);


                if (epic.getId() == 0) {
                    try {
                        taskManager.add(epic);
                        sendText(exchange, gson.toJson(epic));
                    } catch (Exception e) {
                        sendHasInteractions(exchange);
                    }
                } else {
                    try {
                        taskManager.update(epic);
                        sendText(exchange, "");
                    } catch (ManagerSaveException e) {
                        sendHasInteractions(exchange);
                    } catch (TaskNotFoundException e) {
                        sendNotFound(exchange);
                    }
                }
                break;

            case "DELETE":
                try {
                    taskManager.deleteEpicById(epicId);
                    sendText(exchange, "");
                } catch (Exception e) {
                    sendNotFound(exchange);
                    System.out.println(e.getMessage());
                }
                break;

            default:
                sendNotFound(exchange);
        }

    }
}

