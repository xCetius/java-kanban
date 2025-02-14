package main.java.controllers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import main.java.domain.Subtask;
import main.java.exceptions.ManagerSaveException;
import main.java.exceptions.TaskNotFoundException;
import main.java.managers.TaskManager;


import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SubTaskHandler extends BaseHttpHandler implements HttpHandler {

    public SubTaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String[] pathParts = path.split("/");

        int subId = pathParts.length == 3 && !pathParts[2].isEmpty() ? Integer.parseInt(pathParts[2]) : 0;
        boolean hasId = subId != 0;

        switch (method) {
            case "GET":
                if (!hasId) {
                    List<Subtask> subtasks = taskManager.getSubTasks();
                    sendText(exchange, gson.toJson(subtasks));
                } else {
                    try {
                        Subtask subtask = taskManager.getSubTaskById(subId);
                        sendText(exchange, gson.toJson(subtask));
                    } catch (Exception e) {
                        sendNotFound(exchange);
                        System.out.println(e.getMessage());
                    }
                }
                break;

            case "POST":
                InputStream inputStream = exchange.getRequestBody();
                String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                inputStream.close();
                Subtask subtask = gson.fromJson(body, Subtask.class);

                if (subtask.getId() == 0) {
                    try {
                        taskManager.add(subtask);
                        sendText(exchange, gson.toJson(subtask));
                    } catch (Exception e) {
                        sendHasInteractions(exchange);
                    }
                } else {
                    try {
                        taskManager.update(subtask);
                        sendText(exchange, "");
                    } catch (ManagerSaveException e) {
                        sendHasInteractions(exchange);
                    } catch (TaskNotFoundException e) {
                        sendNotFound(exchange);
                        System.out.println(e.getMessage());
                    }
                }
                break;

            case "DELETE":
                try {
                    taskManager.deleteSubTaskById(subId);
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
