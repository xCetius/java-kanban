package test.java;


import main.java.HistoryManager;
import main.java.Managers;
import main.java.Task;
import main.java.TaskManager;
import main.java.Status;
import main.java.Epic;
import main.java.Subtask;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class InMemoryTaskManagerTest {
    TaskManager taskManager;
    HistoryManager historyManager;
    Managers managers;

    @BeforeEach
    public void beforeEach() {

        managers = new Managers();
        taskManager = managers.getDefault();
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    public void shouldReturnTrueOnIdComparison() {
        Task task1 = new Task("main.java.Task 1", "Description 1", Status.NEW);
        Task task2 = new Task("main.java.Task 2", "Description 2", Status.IN_PROGRESS);
        taskManager.add(task1);
        taskManager.add(task2);
        task2.setId(task1.getId());
        Assertions.assertEquals(task1, task2);

    }

    @Test
    public void shouldReturnTrueOnIdComparisonInheritors() {
        Epic epic1 = new Epic("main.java.Epic 1", "main.java.Epic description 1");
        Epic epic2 = new Epic("main.java.Epic 2", "main.java.Epic description 2");
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
        Task task1 = new Task("main.java.Task 1", "Description 1", Status.NEW);
        taskManager.add(task1);
        Epic epic1 = new Epic("main.java.Epic 1", "main.java.Epic description 1");
        taskManager.add(epic1);
        Subtask subtask1 = new Subtask("main.java.Subtask 1", "Description of subtask 1", Status.NEW, taskManager.getEpics().getFirst().getId());
        taskManager.add(subtask1);

        Assertions.assertNotNull(taskManager.getTaskById(taskManager.getTasks().getFirst().getId()));
        Assertions.assertNotNull(taskManager.getEpicById(taskManager.getEpics().getFirst().getId()));
        Assertions.assertNotNull(taskManager.getSubTaskById(taskManager.getSubTasks().getFirst().getId()));

    }

    @Test
    public void shouldOverrideManualId() {
        Task task1 = new Task("main.java.Task 1", "Description 1", Status.NEW);
        task1.setId(25);
        taskManager.add(task1);
        Assertions.assertNotEquals(taskManager.getTaskById(taskManager.getTasks().getFirst().getId()), 25);

    }

    @Test
    public void EditedTaskShouldNotBeEqualToAdded() {
        Task addedTask;
        Task task1 = new Task("main.java.Task 1", "Description 1", Status.NEW);
        taskManager.add(task1);
        addedTask = taskManager.getTaskById(1);
        task1.setId(15);
        task1.setDescription("Description 2");
        task1.setStatus(Status.IN_PROGRESS);
        task1.setName("main.java.Task 2");
        Assertions.assertNotEquals(addedTask.getId(), task1.getId());
        Assertions.assertNotEquals(addedTask.getDescription(), task1.getDescription());
        Assertions.assertNotEquals(addedTask.getName(), task1.getName());
        Assertions.assertNotEquals(addedTask.getStatus(), task1.getStatus());

    }

    @Test
    public void EditedTaskShouldNotBeEqualToAddedToHistory() {
        Task task1 = new Task("main.java.Task 1", "Description 1", Status.NEW);
        taskManager.add(task1);

        task1.setId(15);
        task1.setDescription("Description 2");
        task1.setStatus(Status.IN_PROGRESS);
        task1.setName("main.java.Task 2");

        Task taskFromHistory = historyManager.getHistory().getFirst();

        Assertions.assertNotEquals(task1.getId(), taskFromHistory.getId());
        Assertions.assertNotEquals(task1.getDescription(), taskFromHistory.getDescription());
        Assertions.assertNotEquals(task1.getName(), taskFromHistory.getName());
        Assertions.assertNotEquals(task1.getStatus(), taskFromHistory.getStatus());

    }

    @Test
    public void NextIdShouldIncrease() {
        Task task1 = new Task("main.java.Task 1", "Description 1", Status.NEW);
        taskManager.add(task1);
        taskManager.add(task1);
        Assertions.assertEquals(2, taskManager.getTasks().getLast().getId());

    }

    @Test
    public void EpicShouldChangeStatus() {
        Epic addedEpic1;
        Epic addedEpic2;

        Epic epic1 = new Epic("main.java.Epic 1", "main.java.Epic description 1");
        Epic epic2 = new Epic("main.java.Epic 2", "main.java.Epic description 2");

        taskManager.add(epic1);
        taskManager.add(epic2);


        // Создаём подзадачи
        Subtask subtask1 = new Subtask("main.java.Subtask 1", "Description of subtask 1", Status.NEW, epic1.getId());
        Subtask subtask2 = new Subtask("main.java.Subtask 2", "Description of subtask 2", Status.NEW, epic2.getId());
        Subtask subtask3 = new Subtask("main.java.Subtask 3", "Description of subtask 3", Status.DONE, epic2.getId());

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
        Assertions.assertEquals(0,addedEpic1.getSubTasksIds().size());
        Assertions.assertEquals(0,addedEpic2.getSubTasksIds().size());
    }

    @Test
    public void ShouldRemoveEpicAndItsSubtasks() {
        Epic epic1 = new Epic("main.java.Epic 1", "main.java.Epic description 1");
        taskManager.add(epic1);

        Subtask subtask1 = new Subtask("main.java.Subtask 1", "Description of subtask 1", Status.NEW, epic1.getId());
        Subtask subtask2 = new Subtask("main.java.Subtask 2", "Description of subtask 2", Status.NEW, epic1.getId());
        Subtask subtask3 = new Subtask("main.java.Subtask 3", "Description of subtask 3", Status.DONE, epic1.getId());

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
    public void HistoryShouldStayAt10Size() {
        Task task1 = new Task("main.java.Task 1", "Description 1", Status.NEW);
        taskManager.add(task1);
        for (int i = 0; i < 12; i++) {
            taskManager.getTaskById(task1.getId());
        }
        Assertions.assertEquals(10, historyManager.getHistory().size());
    }


}


