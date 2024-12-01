package main.java;

import java.util.ArrayList;

public interface TaskManager {
    void add(Task task);

    void add(Epic epic);

    void add(Subtask subtask);

    void update(Task task);

    void update(Epic epic);

    void update(Subtask subtask);

    void clearTasks();

    void clearSubTasks();

    void clearEpics();

    Task getTaskById(int id);

    Subtask getSubTaskById(int id);

    Epic getEpicById(int id);

    ArrayList<Task> getTasks();

    ArrayList<Subtask> getSubTasks();

    ArrayList<Epic> getEpics();

    void deleteTaskById(int id);

    void deleteSubTaskById(int id);

    Epic getEpicBySubId(int id);

    void deleteEpicById(int id);

    ArrayList<Subtask> getEpicSubs(int epicId);


}
