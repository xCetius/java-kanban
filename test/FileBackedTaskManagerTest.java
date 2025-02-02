import main.java.*;
import main.java.enums.Status;
import main.java.exceptions.ManagerSaveException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static main.java.FileBackedTaskManager.loadFromFile;

import java.io.*;
import java.util.List;

public class FileBackedTaskManagerTest {
    FileBackedTaskManager taskManager;

    File file;
    File testBackupFile;
    BufferedReader reader;

    @BeforeEach
    public void beforeEach() throws IOException {
        file = File.createTempFile("test", "csv");
        testBackupFile = new File("test/resource/TestBackupFile.csv");
        taskManager = Managers.getFileBackedTaskManager(file);
        reader = new BufferedReader(new FileReader(file));

    }

    @AfterEach
    public void afterEach() {
        taskManager.clearTasks();
        taskManager.clearEpics();
    }

    @Test
    public void shouldBeNotNull() {
        Assertions.assertNotNull(taskManager);
    }

    @Test
    public void shouldSaveEmptyFile() throws ManagerSaveException {
        taskManager.save();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            Assertions.assertEquals("id,type,name,status,description,epic", reader.readLine());
            Assertions.assertNull(reader.readLine());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void shouldLoadEmptyFile() throws ManagerSaveException {
        loadFromFile(file);
    }

    @Test
    public void shouldSaveTasks() throws IOException {

        Task task1 = new Task("Task 1", "Description 1", Status.NEW);
        Task task2 = new Task("Task 2", "Description 2", Status.IN_PROGRESS);

        taskManager.add(task1);
        taskManager.add(task2);

        Epic epic1 = new Epic("Epic 1", "Epic description");

        taskManager.add(epic1);

        Subtask subTask1 = new Subtask("Subtask 1", "Subtask description", Status.NEW, epic1.getId());

        taskManager.add(subTask1);


        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            Assertions.assertEquals("id,type,name,status,description,epic", reader.readLine());
            Assertions.assertEquals(taskManager.toString(task1), reader.readLine());
            Assertions.assertEquals(taskManager.toString(task2), reader.readLine());
            Assertions.assertEquals(taskManager.toString(epic1), reader.readLine());
            Assertions.assertEquals(taskManager.toString(subTask1), reader.readLine());
        }
    }

    @Test
    public void shouldLoadMultipleTasks() throws ManagerSaveException {

        Task task1 = new Task(1, "Task 1", Status.NEW, "Description 1");
        Task task2 = new Task(2, "Task 2", Status.IN_PROGRESS, "Description 2");
        Epic epic1 = new Epic(3, "Epic 1", Status.NEW, "Epic description");
        Subtask subTask1 = new Subtask(4, "Subtask 1", Status.NEW, "Subtask description", 3);


        FileBackedTaskManager loadedManager = loadFromFile(testBackupFile);


        List<Task> tasks = loadedManager.getTasks();
        List<Epic> epics = loadedManager.getEpics();
        List<Subtask> subtasks = loadedManager.getSubTasks();

        Assertions.assertEquals(2, tasks.size());
        Assertions.assertEquals(1, epics.size());
        Assertions.assertEquals(1, subtasks.size());

        Assertions.assertEquals(task1, tasks.get(0));
        Assertions.assertEquals(task2, tasks.get(1));
        Assertions.assertEquals(epic1, epics.get(0));
        Assertions.assertEquals(subTask1, subtasks.get(0));
    }

    @Test
    public void shouldSaveAndLoadEpicWithoutSubtasks() throws ManagerSaveException {
        Epic epic = new Epic("Epic 1", "Epic description");
        taskManager.add(epic);

        FileBackedTaskManager loadedManager = loadFromFile(file);

        List<Epic> loadedEpics = loadedManager.getEpics();

        Assertions.assertEquals(1, loadedEpics.size());
        Assertions.assertEquals(epic, loadedEpics.get(0));
    }

    @Test
    public void shouldSaveAndLoadSubtaskLinkedToEpic() throws ManagerSaveException {
        Epic epic = new Epic("Epic 1", "Epic description");
        taskManager.add(epic);

        Subtask subtask = new Subtask("Subtask 1", "Subtask description", Status.NEW, epic.getId());
        taskManager.add(subtask);

        FileBackedTaskManager loadedManager = loadFromFile(file);

        List<Epic> loadedEpics = loadedManager.getEpics();
        List<Subtask> loadedSubtasks = loadedManager.getSubTasks();

        Assertions.assertEquals(1, loadedEpics.size());
        Assertions.assertEquals(epic, loadedEpics.get(0));

        Assertions.assertEquals(1, loadedSubtasks.size());
        Assertions.assertEquals(subtask, loadedSubtasks.get(0));
        Assertions.assertEquals(epic.getId(), loadedSubtasks.get(0).getEpicId());
    }

    @Test
    public void shouldSaveAndLoadTasksAfterUpdate() throws ManagerSaveException {
        Task task = new Task("Task 1", "Task description", Status.NEW);
        taskManager.add(task);

        task.setName("Updated Task 1");
        task.setDescription("Updated description");
        task.setStatus(Status.IN_PROGRESS);
        taskManager.update(task);

        FileBackedTaskManager loadedManager = loadFromFile(file);

        List<Task> loadedTasks = loadedManager.getTasks();

        Assertions.assertEquals(1, loadedTasks.size());
        Assertions.assertEquals(task, loadedTasks.get(0));
    }

    @Test
    public void shouldClearAllTasksAndSave() throws ManagerSaveException {
        Task task1 = new Task("Task 1", "Description 1", Status.NEW);
        taskManager.add(task1);

        Epic epic1 = new Epic("Epic 1", "Epic description");
        taskManager.add(epic1);

        Subtask subtask1 = new Subtask("Subtask 1", "Subtask description", Status.NEW, epic1.getId());
        taskManager.add(subtask1);


        taskManager.clearTasks();
        taskManager.clearEpics();
        taskManager.clearSubTasks();

        FileBackedTaskManager loadedManager = loadFromFile(file);

        Assertions.assertTrue(loadedManager.getTasks().isEmpty());
        Assertions.assertTrue(loadedManager.getEpics().isEmpty());
        Assertions.assertTrue(loadedManager.getSubTasks().isEmpty());
    }

    @Test
    public void shouldDeleteTaskById() throws ManagerSaveException {
        Task task1 = new Task("Task 1", "Description 1", Status.NEW);
        taskManager.add(task1);

        taskManager.deleteTaskById(task1.getId());

        FileBackedTaskManager loadedManager = loadFromFile(file);

        Assertions.assertTrue(loadedManager.getTasks().isEmpty());
    }

    @Test
    public void shouldDeleteEpicWithSubtasks() throws ManagerSaveException {
        Epic epic = new Epic("Epic 1", "Epic description");
        taskManager.add(epic);

        Subtask subtask = new Subtask("Subtask 1", "Subtask description", Status.NEW, epic.getId());
        taskManager.add(subtask);

        taskManager.deleteEpicById(epic.getId());

        FileBackedTaskManager loadedManager = loadFromFile(file);

        Assertions.assertTrue(loadedManager.getEpics().isEmpty());
        Assertions.assertTrue(loadedManager.getSubTasks().isEmpty());
    }
}





