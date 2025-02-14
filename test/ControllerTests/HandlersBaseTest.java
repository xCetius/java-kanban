package ControllerTests;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import main.java.controllers.HttpTaskServer;
import main.java.adapters.DurationTypeAdapter;
import main.java.adapters.LocalDateTimeTypeAdapter;
import main.java.managers.HistoryManager;
import main.java.managers.InMemoryTaskManager;
import main.java.managers.Managers;
import main.java.managers.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.time.Duration;
import java.time.LocalDateTime;

public abstract class HandlersBaseTest {
    protected HttpTaskServer httpServer;
    protected TaskManager taskManager;
    protected HistoryManager historyManager;
    protected HttpClient client = HttpClient.newHttpClient();
    protected GsonBuilder gsonBuilder = new GsonBuilder();
    protected Gson gson = gsonBuilder
            .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
            .create();
    URI url;

    @BeforeEach
    void beforeEach() throws IOException {
        taskManager = Managers.getDefault();
        historyManager = InMemoryTaskManager.historyManager;
        httpServer = new HttpTaskServer(taskManager);
        httpServer.start();

    }

    @AfterEach
    void afterEach() {
        taskManager.clearTasks();
        taskManager.clearEpics();
        taskManager.clearSubTasks();
        httpServer.stop();
    }

    @Test
    void shouldBeNotNull() {
        Assertions.assertNotNull(taskManager);
        Assertions.assertNotNull(httpServer);
        Assertions.assertNotNull(historyManager);
        Assertions.assertNotNull(client);
        Assertions.assertNotNull(gson);

    }
}
