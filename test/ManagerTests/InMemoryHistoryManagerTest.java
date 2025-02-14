package ManagerTests;

import main.java.domain.Epic;
import main.java.managers.HistoryManager;
import main.java.managers.InMemoryTaskManager;
import main.java.managers.Managers;
import main.java.domain.Subtask;
import main.java.domain.Task;
import main.java.managers.TaskManager;
import main.java.enums.Status;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


class InMemoryHistoryManagerTest {
    HistoryManager historyManager = InMemoryTaskManager.historyManager;
    TaskManager taskManager;
    private Task task1;
    private Task task2;
    private Task task3;

    @BeforeEach
    void beforeEach() {
        task1 = new Task(1, "Task 1", Status.NEW, "Description 1", LocalDateTime.now(), Duration.ofMinutes(30));
        task2 = new Task(2, "Task 2", Status.IN_PROGRESS, "Description 2", LocalDateTime.now().plusMinutes(60), Duration.ofMinutes(30));
        task3 = new Task(3, "Task 3", Status.DONE, "Description 3", LocalDateTime.now().plusMinutes(120), Duration.ofMinutes(30));
        taskManager = Managers.getDefault();
    }

    @AfterEach
    public void afterEach() {
        taskManager.clearTasks();
        taskManager.clearEpics();

    }

    @Test
    void shouldReturnEmptyHistoryInitially() {
        assertTrue(historyManager.getHistory().isEmpty());
    }

    @Test
    void shouldNotAllowDuplicateEntries() {

        taskManager.add(task1);
        taskManager.add(task2);

        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task1.getId());
        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
    }

    @Test
    void shouldRemoveTaskFromHistory() {
        taskManager.add(task1);
        taskManager.add(task2);
        taskManager.add(task3);


        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(task3.getId());

        taskManager.deleteTaskById(task2.getId());

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertFalse(history.contains(task2));
    }

    @Test
    void shouldHandleDeletionFromBeginningMiddleAndEnd() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(task1.getId()); // Начало
        historyManager.remove(task2.getId()); // Середина
        historyManager.remove(task3.getId()); // Конец

        assertTrue(historyManager.getHistory().isEmpty());
    }

    @Test
    public void testEditedTaskShouldNotBeEqualToAddedToHistory() {
        Task task1 = new Task("Task 1", "Description 1", Status.NEW);
        taskManager.add(task1);
        taskManager.getTaskById(task1.getId());

        task1.setId(15);
        task1.setDescription("Description 2");
        task1.setStatus(Status.IN_PROGRESS);
        task1.setName("Task 2");

        Task taskFromHistory = historyManager.getHistory().getFirst();

        Assertions.assertNotEquals(task1.getId(), taskFromHistory.getId());
        Assertions.assertNotEquals(task1.getDescription(), taskFromHistory.getDescription());
        Assertions.assertNotEquals(task1.getName(), taskFromHistory.getName());
        Assertions.assertNotEquals(task1.getStatus(), taskFromHistory.getStatus());

    }

    @Test
    void shouldAddTasksToHistory() {
        Task task1 = new Task("Task 1", "Description 1", Status.NEW);
        Task task2 = new Task("Task 2", "Description 2", Status.NEW);

        taskManager.add(task1);
        taskManager.add(task2);

        taskManager.getTaskById(1);
        taskManager.getTaskById(2);


        List<Task> history = historyManager.getHistory();

        Assertions.assertEquals(2, history.size());
        Assertions.assertEquals(task1, history.get(0));
        Assertions.assertEquals(task2, history.get(1));
    }

    @Test
    void shouldUpdateTaskPositionWhenReAdded() {
        Task task1 = new Task("Task 1", "Description 1", Status.NEW);
        Task task2 = new Task("Task 2", "Description 2", Status.NEW);

        taskManager.add(task1);
        taskManager.add(task2);

        taskManager.getTaskById(1);
        taskManager.getTaskById(2);
        taskManager.getTaskById(1);// Добавляем task1 повторно

        List<Task> history = historyManager.getHistory();

        Assertions.assertEquals(2, history.size());
        Assertions.assertEquals(task2, history.get(0));
        Assertions.assertEquals(task1, history.get(1));
    }

    @Test
    void shouldDoNothingWhenRemovingNonexistentTask() {


        Task task1 = new Task("Task 1", "Description 1", Status.NEW);

        taskManager.add(task1);
        taskManager.getTaskById(1);


        historyManager.remove(2);

        List<Task> history = historyManager.getHistory();

        Assertions.assertEquals(1, history.size());
        Assertions.assertEquals(task1, history.getFirst());
    }

    @Test
    void shouldClearTasksAndHistory() {

        taskManager.add(task1);
        taskManager.add(task2);

        Epic epic1 = new Epic("Epic 1", "Epic description");

        taskManager.add(epic1);

        Subtask subTask1 = new Subtask("Subtask 1", "Subtask description", Status.NEW, epic1.getId());
        taskManager.add(subTask1);

        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getEpicById(epic1.getId());
        taskManager.getSubTaskById(subTask1.getId());

        Assertions.assertNotEquals(historyManager.getHistory().size(), 0);

        taskManager.clearTasks();
        taskManager.clearEpics();

        Assertions.assertEquals(historyManager.getHistory().size(), 0);


    }
}
