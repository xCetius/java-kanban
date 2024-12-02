package main.java;


import java.util.List;

public interface HistoryManager {

    void add(Task task);

    void add(Epic epic);

    void add(Subtask subtask);

    List<Task> getHistory();
}
