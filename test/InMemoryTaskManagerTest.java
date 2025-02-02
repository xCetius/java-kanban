import main.java.managers.HistoryManager;
import main.java.managers.InMemoryTaskManager;
import main.java.managers.Managers;
import main.java.domain.Task;
import main.java.enums.Status;
import main.java.domain.Epic;
import main.java.domain.Subtask;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    HistoryManager historyManager = InMemoryTaskManager.historyManager;


    private boolean isOverlapping(Task task1, Task task2) {
        LocalDateTime start1 = task1.getStartTime();
        LocalDateTime end1 = task1.getEndTime();
        LocalDateTime start2 = task2.getStartTime();
        LocalDateTime end2 = task2.getEndTime();

        return start1.isBefore(end2) && start2.isBefore(end1);
    }

    @Override
    protected InMemoryTaskManager createTaskManager() {
        return Managers.getDefault();
    }

    @AfterEach
    public void afterEach() {
        taskManager.clearTasks();
        taskManager.clearEpics();

    }

    @Test
    public void shouldReturnTrueOnIdComparison() {
        Task task1 = new Task("Task 1", "Description 1", Status.NEW);
        Task task2 = new Task("Task 2", "Description 2", Status.IN_PROGRESS);
        taskManager.add(task1);
        taskManager.add(task2);
        task2.setId(task1.getId());
        Assertions.assertEquals(task1, task2);
    }

    @Test
    public void shouldReturnTrueOnIdComparisonInheritors() {
        Epic epic1 = new Epic("Epic 1", "Epic description 1");
        Epic epic2 = new Epic("Epic 2", "Epic description 2");
        taskManager.add(epic1);
        taskManager.add(epic2);
        epic2.setId(epic1.getId());
        Assertions.assertEquals(epic1, epic2);
    }

    @Test
    public void shouldBeNotNull() {
        Assertions.assertNotNull(taskManager);
        Assertions.assertNotNull(historyManager);
    }

    @Test
    public void shouldReturnNotNullOnGettingAddedTasks() {
        Task task1 = new Task("Task 1", "Description 1", Status.NEW);
        taskManager.add(task1);
        Epic epic1 = new Epic("Epic 1", "Epic description 1");
        taskManager.add(epic1);
        Subtask subtask1 = new Subtask("Subtask 1", "Description of subtask 1", Status.NEW, taskManager.getEpics().getFirst().getId());
        taskManager.add(subtask1);

        Assertions.assertNotNull(taskManager.getTaskById(taskManager.getTasks().getFirst().getId()));
        Assertions.assertNotNull(taskManager.getEpicById(taskManager.getEpics().getFirst().getId()));
        Assertions.assertNotNull(taskManager.getSubTaskById(taskManager.getSubTasks().getFirst().getId()));
    }

    @Test
    public void shouldOverrideManualId() {
        Task task1 = new Task("Task 1", "Description 1", Status.NEW);
        task1.setId(25);
        taskManager.add(task1);
        int newId = taskManager.getTasks().getFirst().getId();
        Assertions.assertNotEquals(newId, 25);
    }

    @Test
    public void testEditedTaskShouldNotBeEqualToAdded() {
        Task addedTask;
        Task task1 = new Task("Task 1", "Description 1", Status.NEW);
        taskManager.add(task1);
        addedTask = taskManager.getTaskById(1);
        task1.setId(15);
        task1.setDescription("Description 2");
        task1.setStatus(Status.IN_PROGRESS);
        task1.setName("Task 2");
        Assertions.assertNotEquals(addedTask.getId(), task1.getId());
        Assertions.assertNotEquals(addedTask.getDescription(), task1.getDescription());
        Assertions.assertNotEquals(addedTask.getName(), task1.getName());
        Assertions.assertNotEquals(addedTask.getStatus(), task1.getStatus());

    }


    @Test
    public void shouldIncreaseNextId() {
        Task task1 = new Task("main.java.domain.Task 1", "Description 1", Status.NEW);
        taskManager.add(task1);
        taskManager.add(task1);
        Assertions.assertEquals(2, taskManager.getTasks().getLast().getId());
    }

    @Test
    public void testEpicShouldChangeStatus() {
        Epic addedEpic1;
        Epic addedEpic2;

        Epic epic1 = new Epic("Epic 1", "Epic description 1");
        Epic epic2 = new Epic("Epic 2", "Epic description 2");

        taskManager.add(epic1);
        taskManager.add(epic2);

        // Создаём подзадачи
        Subtask subtask1 = new Subtask("Subtask 1", "Description of subtask 1", Status.NEW, epic1.getId());
        Subtask subtask2 = new Subtask("Subtask 2", "Description of subtask 2", Status.NEW, epic2.getId());
        Subtask subtask3 = new Subtask("Subtask 3", "Description of subtask 3", Status.DONE, epic2.getId());

        taskManager.add(subtask1);
        taskManager.add(subtask2);
        taskManager.add(subtask3);

        subtask1.setStatus(Status.IN_PROGRESS);
        subtask2.setStatus(Status.DONE);

        taskManager.update(subtask1);
        taskManager.update(subtask2);

        addedEpic1 = taskManager.getEpics().getFirst();
        addedEpic2 = taskManager.getEpics().getLast();

        //Смена статусов после апдейта статусов подзадач
        Assertions.assertEquals(Status.IN_PROGRESS, addedEpic1.getStatus());
        Assertions.assertEquals(Status.DONE, addedEpic2.getStatus());

        taskManager.clearSubTasks();

        //Смена статусов на NEW после очистки всех подзадач
        Assertions.assertEquals(Status.NEW, addedEpic1.getStatus());
        Assertions.assertEquals(Status.NEW, addedEpic2.getStatus());

        //Проверка, что в самих эпиках привязка удалена
        Assertions.assertEquals(0, addedEpic1.getSubTasksIds().size());
        Assertions.assertEquals(0, addedEpic2.getSubTasksIds().size());
    }

    @Test
    public void shouldRemoveEpicAndItsSubtasks() {
        Epic epic1 = new Epic("Epic 1", "Epic description 1");
        taskManager.add(epic1);

        Subtask subtask1 = new Subtask("Subtask 1", "Description of subtask 1", Status.NEW, epic1.getId());
        Subtask subtask2 = new Subtask("Subtask 2", "Description of subtask 2", Status.NEW, epic1.getId());
        Subtask subtask3 = new Subtask("Subtask 3", "Description of subtask 3", Status.DONE, epic1.getId());

        taskManager.add(subtask1);
        taskManager.add(subtask2);
        taskManager.add(subtask3);

        Assertions.assertEquals(1, taskManager.getEpics().size());
        Assertions.assertEquals(3, taskManager.getSubTasks().size());

        taskManager.deleteEpicById(epic1.getId());

        Assertions.assertEquals(0, taskManager.getEpics().size());
        Assertions.assertEquals(0, taskManager.getSubTasks().size());
    }


    @Test
    void shouldReturnEmptyHistoryWhenNoTasks() {
        List<Task> history = historyManager.getHistory();

        assertTrue(history.isEmpty());
    }

    @Test
    void shouldUseCorrectClassToString() {

        Epic epic1 = new Epic("Epic 1", "Epic description");
        taskManager.add(epic1);

        Subtask subTask1 = new Subtask("Subtask 1", "Subtask description", Status.NEW, epic1.getId(), super.subtask1StartTime, super.subtask1Duration);
        taskManager.add(subTask1);

        taskManager.getEpicById(epic1.getId());
        taskManager.getSubTaskById(subTask1.getId());

        Task epicInHistory = historyManager.getHistory().getFirst();
        Task subtaskInHistory = historyManager.getHistory().getLast();

        //Добавляем старому эпику недостающие данные
        epic1.addSubTasksId(2);
        epic1.setStartTime(taskManager.getEpicById(1).getStartTime());
        epic1.setDuration(taskManager.getEpicById(1).getDuration());


        Assertions.assertEquals(epic1.toString(), epicInHistory.toString());
        Assertions.assertEquals(subTask1.toString(), subtaskInHistory.toString());

    }

    @Test
    void shouldDetectOverlappingTasks() {
        Task task1 = new Task(1, "Task 1", Status.NEW, "Desc", LocalDateTime.of(2024, 2, 1, 10, 0), Duration.ofMinutes(60));
        Task task2 = new Task(2, "Task 2", Status.NEW, "Desc", LocalDateTime.of(2024, 2, 1, 10, 30), Duration.ofMinutes(60));

        assertTrue(isOverlapping(task1, task2));
    }

    @Test
    void shouldNotDetectOverlapForNonOverlappingTasks() {
        Task task1 = new Task(1, "Task 1", Status.NEW, "Desc", LocalDateTime.of(2024, 2, 1, 10, 0), Duration.ofMinutes(59));
        Task task2 = new Task(2, "Task 2", Status.NEW, "Desc", LocalDateTime.of(2024, 2, 1, 11, 0), Duration.ofMinutes(60));

        assertFalse(isOverlapping(task1, task2));
    }

    @Test
    void shouldDetectOverlapWhenTasksTouch() {
        Task task1 = new Task(1, "Task 1", Status.NEW, "Desc", LocalDateTime.of(2024, 2, 1, 10, 0), Duration.ofMinutes(60));
        Task task2 = new Task(2, "Task 2", Status.NEW, "Desc", LocalDateTime.of(2024, 2, 1, 11, 0), Duration.ofMinutes(60));

        assertFalse(isOverlapping(task1, task2));
    }


}




