package ControllerTests;

import main.java.domain.Epic;
import main.java.domain.Subtask;
import main.java.enums.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicHandlerTest extends HandlersBaseTest {

    Epic epic1 = new Epic("Epic 1", "Epic description 1");
    Epic epic2 = new Epic("Epic 2", "Epic description 2");

    Subtask subtask1 = new Subtask("Subtask 1", "Description of subtask 1", Status.NEW, 1, LocalDateTime.of(2024, 2, 3, 10, 0), Duration.ofMinutes(60));
    Subtask subtask2 = new Subtask("Subtask 2", "Description of subtask 2", Status.IN_PROGRESS, 1, LocalDateTime.of(2024, 2, 4, 10, 0), Duration.ofMinutes(60));


    @BeforeEach
    void setUp() {
        taskManager.add(epic1);
        taskManager.add(epic2);
        taskManager.add(subtask1);
        taskManager.add(subtask2);
        url = URI.create("http://localhost:8080/epics");

    }


    @Test
    void shouldReturnEpics() throws Exception {
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(response.body(), gson.toJson(taskManager.getEpics()));
    }

    @Test
    void shouldReturnEpicById() throws Exception {
        URI newUrl = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder().uri(newUrl).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(response.body(), gson.toJson(taskManager.getEpicById(1)));
    }

    @Test
    void shouldPostEpic() throws Exception {
        Epic epic3 = new Epic("Epic 3", "Epic description 3");
        String json = gson.toJson(epic3);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(json)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        epic3.setStatus(Status.NEW);
        epic3.setId(5);
        assertEquals(201, response.statusCode());
        assertEquals(response.body(), gson.toJson(epic3));
    }

    @Test
    void shouldUpdateEpic() throws Exception {
        epic1.setDescription("Updated description");
        String json = gson.toJson(epic1);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(json)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals(taskManager.getEpicById(1).getDescription(), "Updated description");

    }

    @Test
    void shouldDeleteEpic() throws Exception {
        URI newUrl = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder().uri(newUrl).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(1, taskManager.getEpics().size());
    }

    @Test
    void shouldReturnSubtasks() throws Exception {
        URI newUrl = URI.create("http://localhost:8080/epics/1/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(newUrl).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(response.body(), gson.toJson(taskManager.getEpicSubs(1)));
    }
}

