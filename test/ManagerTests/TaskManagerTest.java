package ManagerTests;

import main.java.domain.Epic;
import main.java.domain.Subtask;
import main.java.domain.Task;
import main.java.enums.Status;
import main.java.managers.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;
    Duration task1Duration = Duration.ofHours(1);
    LocalDateTime task1StartTime = LocalDateTime.now().plusDays(1);
    Duration task2Duration = Duration.ofHours(2);
    LocalDateTime task2StartTime = LocalDateTime.now().plusDays(2);
    Duration subtask1Duration = Duration.ofHours(4);
    LocalDateTime subtask1StartTime = LocalDateTime.now().plusDays(4);
    Duration subtask2Duration = Duration.ofHours(4);
    LocalDateTime subtask2StartTime = LocalDateTime.now().plusDays(5);

    protected abstract T createTaskManager();

    @BeforeEach
    void setUp() {
        taskManager = createTaskManager();

    }

    @AfterEach
    public void afterEach() {
        taskManager.clearTasks();
        taskManager.clearEpics();

    }

    @Test
    void shouldCalculateEpicStatusWhenAllSubtasksAreNew() {
        Epic epic = new Epic("Epic 1", "Description");
        taskManager.add(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description", Status.NEW, epic.getId(), subtask1StartTime, subtask1Duration);
        Subtask subtask2 = new Subtask("Subtask 2", "Description", Status.NEW, epic.getId(), subtask2StartTime, subtask2Duration);

        taskManager.add(subtask1);
        taskManager.add(subtask2);

        assertEquals(Status.NEW, taskManager.getEpicById(epic.getId()).getStatus());
    }

    @Test
    void shouldCalculateEpicStatusWhenAllSubtasksAreDone() {
        Epic epic = new Epic("Epic 2", "Description");
        taskManager.add(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description", Status.DONE, epic.getId(), subtask1StartTime, subtask1Duration);
        Subtask subtask2 = new Subtask("Subtask 2", "Description", Status.DONE, epic.getId(), subtask2StartTime, subtask2Duration);

        taskManager.add(subtask1);
        taskManager.add(subtask2);

        assertEquals(Status.DONE, taskManager.getEpicById(epic.getId()).getStatus());
    }

    @Test
    void shouldCalculateEpicStatusWhenSubtasksAreNewAndDone() {
        Epic epic = new Epic("Epic 3", "Description");
        taskManager.add(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description", Status.NEW, epic.getId(), subtask1StartTime, subtask1Duration);
        Subtask subtask2 = new Subtask("Subtask 2", "Description", Status.DONE, epic.getId(), subtask2StartTime, subtask2Duration);

        taskManager.add(subtask1);
        taskManager.add(subtask2);

        assertEquals(Status.IN_PROGRESS, taskManager.getEpicById(epic.getId()).getStatus());
    }

    @Test
    void shouldCalculateEpicStatusWhenAllSubtasksAreInProgress() {
        Epic epic = new Epic("Epic 4", "Description");
        taskManager.add(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description", Status.IN_PROGRESS, epic.getId(), subtask1StartTime, subtask1Duration);
        Subtask subtask2 = new Subtask("Subtask 2", "Description", Status.IN_PROGRESS, epic.getId(), subtask2StartTime, subtask2Duration);

        taskManager.add(subtask1);
        taskManager.add(subtask2);

        assertEquals(Status.IN_PROGRESS, taskManager.getEpicById(epic.getId()).getStatus());
    }

    @Test
    void shouldReturnPrioritizedTasksOrderedByStartTime() {
        Task task1 = new Task("Task1", "Desc", Status.NEW,
                task1StartTime.plusHours(2), Duration.ofHours(1));
        Task task2 = new Task("Task2", "Desc", Status.NEW,
                task2StartTime.plusHours(1), Duration.ofHours(1));

        taskManager.add(task1);
        taskManager.add(task2);

        List<Task> prioritized = taskManager.getPrioritizedTasks();
        assertEquals(List.of(task1, task2), prioritized);
    }


    @Test
    void shouldLinkSubtaskToEpic() {
        Epic epic = new Epic("Epic", "Desc");
        taskManager.add(epic);

        Subtask subtask = new Subtask("Sub", "Desc", Status.NEW, epic.getId(), subtask1StartTime, subtask1Duration);
        taskManager.add(subtask);

        Epic loadedEpic = taskManager.getEpicById(epic.getId());

        assertEquals(epic.getId(), subtask.getEpicId());
        assertTrue(loadedEpic.getSubTasksIds().contains(subtask.getId()));
    }
}
