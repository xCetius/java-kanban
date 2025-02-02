package main.java.managers;

import java.io.File;

public class Managers {


    public static InMemoryTaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static FileBackedTaskManager getFileBackedTaskManager(File file) {
        return new FileBackedTaskManager(file);
    }
}