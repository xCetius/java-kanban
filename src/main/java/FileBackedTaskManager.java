package main.java;

import main.java.enums.Status;
import main.java.enums.TaskType;
import main.java.exceptions.ManagerSaveException;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;
    private static final String COLUMN_NAMES = "id,type,name,status,description,epic";

    FileBackedTaskManager(File file) {
        this.file = file;
    }

    public static FileBackedTaskManager loadFromFile(File file) throws ManagerSaveException {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            while (reader.ready()) {
                String line = reader.readLine();
                if (line.equals(COLUMN_NAMES)) {
                    continue;
                }
                Task task = manager.fromString(line);
                if (task instanceof Epic) {
                    manager.load((Epic) task);
                } else if (task instanceof Subtask) {
                    manager.load((Subtask) task);
                } else {
                    manager.load(task);
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }

        return manager;

    }

    public void save() throws ManagerSaveException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            StringBuilder result = new StringBuilder();

            writer.write(COLUMN_NAMES + "\n");

            for (Task task : getTasks()) {
                result.append(toString(task)).append("\n");
            }
            for (Epic task : getEpics()) {
                result.append(toString(task)).append("\n");
            }
            for (Subtask task : getSubTasks()) {
                result.append(toString(task)).append("\n");
            }

            writer.write(result.toString());
        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }

    }

    public String toString(Task task) {
        return task.getId() + "," + TaskType.TASK + "," + task.getName() + "," + task.getStatus() + "," + task.getDescription() + ",";
    }

    public String toString(Subtask task) {
        return task.getId() + "," + TaskType.SUBTASK + "," + task.getName() + "," + task.getStatus() + "," + task.getDescription() + "," + task.getEpicId();
    }

    public String toString(Epic task) {
        return task.getId() + "," + TaskType.EPIC + "," + task.getName() + "," + task.getStatus() + "," + task.getDescription() + ",";
    }

    public Task fromString(String value) {
        List<String> list = List.of(value.split(","));
        TaskType taskType = TaskType.valueOf(list.get(1));
        if (taskType == TaskType.TASK) {
            return new Task(Integer.parseInt(list.get(0)), list.get(2), Status.valueOf(list.get(3)), list.get(4));
        } else if (taskType == TaskType.EPIC) {
            return new Epic(Integer.parseInt(list.get(0)), list.get(2), Status.valueOf(list.get(3)), list.get(4));
        } else {
            return new Subtask(Integer.parseInt(list.get(0)), list.get(2), Status.valueOf(list.get(3)), list.get(4), Integer.parseInt(list.get(5)));
        }
    }


    @Override
    public void add(Subtask subtask) {
        super.add(subtask);
        save();
    }

    @Override
    public void add(Epic epic) {
        super.add(epic);
        save();
    }

    @Override
    public void add(Task task) {
        super.add(task);
        save();
    }

    private void load(Subtask subtask) {
        super.add(subtask);
    }


    private void load(Epic epic) {
        super.add(epic);
    }

    private void load(Task task) {
        super.add(task);
    }

    @Override
    public void update(Task task) {
        super.update(task);
        save();
    }

    @Override
    public void update(Epic epic) {
        super.update(epic);
        save();
    }

    @Override
    public void update(Subtask subtask) {
        super.update(subtask);
        save();

    }

    @Override
    public void clearTasks() {
        super.clearTasks();
        save();
    }

    @Override
    public void clearSubTasks() {
        super.clearSubTasks();
        save();
    }

    @Override
    public void clearEpics() {
        super.clearEpics();
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteSubTaskById(int id) {
        super.deleteSubTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }


}
