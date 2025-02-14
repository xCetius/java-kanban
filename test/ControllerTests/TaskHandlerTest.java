package ControllerTests;

import main.java.domain.Task;
import main.java.enums.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskHandlerTest extends HandlersBaseTest {

    Task task1 = new Task("Task 1", "Description 1", Status.NEW, LocalDateTime.of(2024, 2, 1, 10, 0), Duration.ofMinutes(30));
    Task task2 = new Task("Task 2", "Description 2", Status.IN_PROGRESS, LocalDateTime.of(2024, 2, 2, 10, 0), Duration.ofMinutes(60));


    @BeforeEach
    void setUp() {
        taskManager.add(task1);
        taskManager.add(task2);
        url = URI.create("http://localhost:8080/tasks");
    }


    @Test
    void shouldReturnTasks() throws Exception {
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(response.body(), gson.toJson(taskManager.getTasks()));
    }

    @Test
    void shouldReturnTaskById() throws Exception {
        URI newUrl = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(newUrl).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(response.body(), gson.toJson(taskManager.getTaskById(1)));
    }

    @Test
    void shouldPostTask() throws Exception {
        Task task3 = new Task("Task 3", "Description 3", Status.NEW, LocalDateTime.of(2024, 2, 3, 10, 0), Duration.ofMinutes(30));
        String json = gson.toJson(task3);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(json)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals(response.body(), gson.toJson(taskManager.getTaskById(3)));
    }

    @Test
    void shouldUpdateTask() throws Exception {
        task1.setDescription("Updated description");
        String json = gson.toJson(task1);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(json)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals(taskManager.getTaskById(1).getDescription(), "Updated description");

    }

    @Test
    void shouldDeleteTask() throws Exception {
        URI newUrl = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(newUrl).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(1, taskManager.getTasks().size());
    }
}

