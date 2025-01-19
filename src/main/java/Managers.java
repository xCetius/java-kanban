package main.java;

import java.io.File;

public class Managers {
    private final TaskManager taskManager;
    private static final HistoryManager historyManager = new InMemoryHistoryManager();

    public Managers() {
        this.taskManager = new InMemoryTaskManager();

    }

    public TaskManager getDefault() {
        return this.taskManager;
    }

    public static HistoryManager getDefaultHistory() {
        return historyManager;
    }

    public FileBackedTaskManager getFileBackedTaskManager(File file) {
        return new FileBackedTaskManager(file);
    }
}