package main.java;

import java.util.ArrayList;

public interface HistoryManager {

    void add(Task task);

    void add(Epic epic);

    void add(Subtask subtask);

    ArrayList<Task> getHistory();
}
