package main.java.controllers;


import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import main.java.domain.Task;
import main.java.exceptions.ManagerSaveException;
import main.java.exceptions.TaskNotFoundException;
import main.java.managers.TaskManager;


import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {

    public TaskHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String[] pathParts = path.split("/");

        int taskId = pathParts.length == 3 && !pathParts[2].isEmpty() ? Integer.parseInt(pathParts[2]) : 0;
        boolean hasId = taskId != 0;

        switch (method) {
            case "GET":
                if (!hasId) {
                    List<Task> tasks = taskManager.getTasks();
                    sendText(exchange, gson.toJson(tasks));
                } else {
                    try {
                        Task task = taskManager.getTaskById(taskId);
                        sendText(exchange, gson.toJson(task));
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
                Task task = gson.fromJson(body, Task.class);

                if (task.getId() == 0) {
                    try {
                        taskManager.add(task);
                        sendText(exchange, gson.toJson(task));
                    } catch (Exception e) {
                        sendHasInteractions(exchange);
                    }
                } else {
                    try {
                        taskManager.update(task);
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
                    taskManager.deleteTaskById(taskId);
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


