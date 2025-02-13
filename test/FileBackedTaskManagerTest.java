
import main.java.domain.Epic;
import main.java.managers.FileBackedTaskManager;
import main.java.managers.Managers;
import main.java.domain.Subtask;
import main.java.domain.Task;
import main.java.enums.Status;
import main.java.exceptions.ManagerSaveException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static main.java.managers.FileBackedTaskManager.loadFromFile;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {


    File file;
    File testBackupFile = new File("test/resource/TestBackupFile.csv");
    BufferedReader reader;

    @Override
    protected FileBackedTaskManager createTaskManager() {
        try {
            file = File.createTempFile("test", "csv");
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при создании временного файла", e);
        }
        return Managers.getFileBackedTaskManager(file);
    }

    @BeforeEach
    public void beforeEach() throws IOException {
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
            Assertions.assertEquals(FileBackedTaskManager.COLUMN_NAMES, reader.readLine());
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

        Task task1 = new Task("Task 1", "Description 1", Status.NEW, task1StartTime, task1Duration);
        Task task2 = new Task("Task 2", "Description 2", Status.IN_PROGRESS, task2StartTime, task2Duration);

        taskManager.add(task1);
        taskManager.add(task2);

        Epic epic1 = new Epic("Epic 1", "Epic description");

        taskManager.add(epic1);

        Subtask subTask1 = new Subtask("Subtask 1", "Subtask description", Status.NEW, epic1.getId(), subtask1StartTime, subtask1Duration);

        taskManager.add(subTask1);

        epic1.setStartTime(subtask1StartTime);
        epic1.setDuration(subtask1Duration);


        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            Assertions.assertEquals(FileBackedTaskManager.COLUMN_NAMES, reader.readLine());
            Assertions.assertEquals(taskManager.toString(task1), reader.readLine());
            Assertions.assertEquals(taskManager.toString(task2), reader.readLine());
            Assertions.assertEquals(taskManager.toString(epic1), reader.readLine());
            Assertions.assertEquals(taskManager.toString(subTask1), reader.readLine());
        }
    }

    @Test
    public void shouldLoadMultipleTasks() throws ManagerSaveException {

        Task task1 = new Task(1, "Task 1", Status.NEW, "Description 1", task1StartTime, task1Duration);
        Task task2 = new Task(2, "Task 2", Status.IN_PROGRESS, "Description 2", task2StartTime, task2Duration);
        Epic epic1 = new Epic(3, "Epic 1", Status.NEW, "Epic description");
        Subtask subTask1 = new Subtask(4, "Subtask 1", Status.NEW, "Subtask description", 3, subtask1StartTime, subtask1Duration);


        FileBackedTaskManager loadedManager = loadFromFile(testBackupFile);


        List<Task> tasks = loadedManager.getTasks();
        List<Epic> epics = loadedManager.getEpics();
        List<Subtask> subtasks = loadedManager.getSubTasks();

        Assertions.assertEquals(2, tasks.size());
        Assertions.assertEquals(1, epics.size());
        Assertions.assertEquals(1, subtasks.size());

        Assertions.assertEquals(task1, tasks.get(0));
        Assertions.assertEquals(task2, tasks.get(1));
        Assertions.assertEquals(epic1, epics.getFirst());
        Assertions.assertEquals(subTask1, subtasks.getFirst());
    }

    @Test
    public void shouldSaveAndLoadEpicWithoutSubtasks() throws ManagerSaveException {
        Epic epic = new Epic("Epic 1", "Epic description");
        taskManager.add(epic);

        FileBackedTaskManager loadedManager = loadFromFile(file);

        List<Epic> loadedEpics = loadedManager.getEpics();

        Assertions.assertEquals(1, loadedEpics.size());
        Assertions.assertEquals(epic, loadedEpics.getFirst());
    }

    @Test
    public void shouldSaveAndLoadSubtaskLinkedToEpic() throws ManagerSaveException {
        Epic epic = new Epic("Epic 1", "Epic description");
        taskManager.add(epic);

        Subtask subtask = new Subtask("Subtask 1", "Subtask description", Status.NEW, epic.getId(), subtask1StartTime, subtask1Duration);
        taskManager.add(subtask);

        FileBackedTaskManager loadedManager = loadFromFile(file);

        List<Epic> loadedEpics = loadedManager.getEpics();
        List<Subtask> loadedSubtasks = loadedManager.getSubTasks();

        Assertions.assertEquals(1, loadedEpics.size());
        Assertions.assertEquals(epic, loadedEpics.getFirst());

        Assertions.assertEquals(1, loadedSubtasks.size());
        Assertions.assertEquals(subtask, loadedSubtasks.getFirst());
        Assertions.assertEquals(epic.getId(), loadedSubtasks.getFirst().getEpicId());
    }

    @Test
    public void shouldSaveAndLoadTasksAfterUpdate() throws ManagerSaveException {
        Task task = new Task("Task 1", "Task description", Status.NEW, task1StartTime, task1Duration);
        taskManager.add(task);

        task.setName("Updated Task 1");
        task.setDescription("Updated description");
        task.setStatus(Status.IN_PROGRESS);
        taskManager.update(task);

        FileBackedTaskManager loadedManager = loadFromFile(file);

        List<Task> loadedTasks = loadedManager.getTasks();

        Assertions.assertEquals(1, loadedTasks.size());
        Assertions.assertEquals(task, loadedTasks.getFirst());
    }

    @Test
    public void shouldClearAllTasksAndSave() throws ManagerSaveException {
        Task task1 = new Task("Task 1", "Description 1", Status.NEW, task1StartTime, task1Duration);
        taskManager.add(task1);

        Epic epic1 = new Epic("Epic 1", "Epic description");
        taskManager.add(epic1);

        Subtask subtask1 = new Subtask("Subtask 1", "Subtask description", Status.NEW, epic1.getId(), subtask1StartTime, subtask1Duration);
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
        Task task1 = new Task("Task 1", "Description 1", Status.NEW, task1StartTime, task1Duration);
        taskManager.add(task1);

        taskManager.deleteTaskById(task1.getId());

        FileBackedTaskManager loadedManager = loadFromFile(file);

        Assertions.assertTrue(loadedManager.getTasks().isEmpty());
    }

    @Test
    public void shouldDeleteEpicWithSubtasks() throws ManagerSaveException {
        Epic epic = new Epic("Epic 1", "Epic description");
        taskManager.add(epic);

        Subtask subtask = new Subtask("Subtask 1", "Subtask description", Status.NEW, epic.getId(), subtask1StartTime, subtask1Duration);
        taskManager.add(subtask);

        taskManager.deleteEpicById(epic.getId());

        FileBackedTaskManager loadedManager = loadFromFile(file);

        Assertions.assertTrue(loadedManager.getEpics().isEmpty());
        Assertions.assertTrue(loadedManager.getSubTasks().isEmpty());
    }

    @Test
    public void testSaveThrowsExceptionWhenFileIsNotWritable() {
        // Создаем путь к файлу, который недоступен для записи (например, директория)
        Path unwritableFile = Paths.get("/unwritable/directory/file.txt");

        FileBackedTaskManager badManager = Managers.getFileBackedTaskManager(unwritableFile.toFile());

        Assertions.assertThrows(ManagerSaveException.class, badManager::save, "Метод save() должен выбрасывать ManagerSaveException, если файл недоступен для записи");
    }

    @Test
    public void testSaveDoesNotThrowExceptionWhenFileIsWritable() {
        // Создаем путь к временному файлу, который доступен для записи
        Path writableFile = Paths.get("temp_file.txt");

        FileBackedTaskManager goodManager = Managers.getFileBackedTaskManager(writableFile.toFile());

        Assertions.assertDoesNotThrow(goodManager::save, "Метод save() не должен выбрасывать исключение, если файл доступен для записи");


    }
}








