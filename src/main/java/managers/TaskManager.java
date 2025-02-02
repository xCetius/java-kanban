package main.java.managers;

import main.java.domain.Epic;
import main.java.domain.Subtask;
import main.java.domain.Task;

import java.util.List;

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

    Task getSubTaskById(int id);

    Task getEpicById(int id);

    List<Task> getTasks();

    List<Subtask> getSubTasks();

    List<Epic> getEpics();

    void deleteTaskById(int id);

    void deleteSubTaskById(int id);

    Epic getEpicBySubId(int id);

    void deleteEpicById(int id);

    List<Subtask> getEpicSubs(int epicId);

    List<Task> getPrioritizedTasks();


}
