package main.java;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {


    private static final ArrayList<Task> watchHistory = new ArrayList<>();

    @Override
    public void add(Task taskToAdd) {
        Task task = new Task(taskToAdd);
        if (watchHistory.size() == 10) {
            watchHistory.removeFirst();
            watchHistory.add(task);
        } else {
            watchHistory.add(task);
        }

    }
    @Override
    public void add(Epic taskToAdd) {
        Epic epic = new Epic(taskToAdd);
        if (watchHistory.size() == 10) {
            watchHistory.removeFirst();
            watchHistory.add(epic);
        } else {
            watchHistory.add(epic);
        }

    }
    @Override
    public void add(Subtask taskToAdd) {
        Subtask subtask = new Subtask(taskToAdd);
        if (watchHistory.size() == 10) {
            watchHistory.removeFirst();
            watchHistory.add(subtask);
        } else {
            watchHistory.add(subtask);
        }

    }

    @Override
    public ArrayList<Task> getHistory() {


        return watchHistory;


    }
}
