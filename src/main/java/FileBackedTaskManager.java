package main.java;

import main.java.enums.Status;
import main.java.enums.TaskType;
import main.java.exceptions.ManagerSaveException;

import java.io.*;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    FileBackedTaskManager(File file) {
        this.file = file;
    }

    public static FileBackedTaskManager loadFromFile(File file) throws IOException {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            while (reader.ready()) {
                String line = reader.readLine();
                if (line.equals("id,type,name,status,description,epic")) {
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

            writer.write("id,type,name,status,description,epic\n");

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
        try {
            save();
        } catch (ManagerSaveException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void add(Epic epic) {
        super.add(epic);
        try {
            save();
        } catch (ManagerSaveException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void add(Task task) {
        super.add(task);
        try {
            save();
        } catch (ManagerSaveException e) {
            e.printStackTrace();
        }
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
        try {
            save();
        } catch (ManagerSaveException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Epic epic) {
        super.update(epic);
        try {
            save();
        } catch (ManagerSaveException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Subtask subtask) {
        super.update(subtask);
        try {
            save();
        } catch (ManagerSaveException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void clearTasks() {
        super.clearTasks();
        try {
            save();
        } catch (ManagerSaveException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void clearSubTasks() {
        super.clearSubTasks();
        try {
            save();
        } catch (ManagerSaveException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void clearEpics() {
        super.clearEpics();
        try {
            save();
        } catch (ManagerSaveException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        try {
            save();
        } catch (ManagerSaveException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteSubTaskById(int id) {
        super.deleteSubTaskById(id);
        try {
            save();
        } catch (ManagerSaveException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        try {
            save();
        } catch (ManagerSaveException e) {
            e.printStackTrace();
        }
    }


}
