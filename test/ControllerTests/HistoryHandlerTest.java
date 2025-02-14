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

public class HistoryHandlerTest extends HandlersBaseTest {
    Task task1 = new Task("Task 1", "Description 1", Status.NEW, LocalDateTime.of(2024, 2, 1, 10, 0), Duration.ofMinutes(30));
    Task task2 = new Task("Task 2", "Description 2", Status.IN_PROGRESS, LocalDateTime.of(2024, 2, 2, 10, 0), Duration.ofMinutes(60));

    @BeforeEach
    void setUp() {
        taskManager.add(task1);
        taskManager.add(task2);
        url = URI.create("http://localhost:8080/history");

    }

    @Test
    void shouldReturnHistory() throws Exception {
        for (int i = 0; i < 3; i++) {
            taskManager.getTaskById(1);
            taskManager.getTaskById(2);
        }
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(response.body(), gson.toJson(historyManager.getHistory()));
        assertEquals(2, historyManager.getHistory().size());
    }
}
