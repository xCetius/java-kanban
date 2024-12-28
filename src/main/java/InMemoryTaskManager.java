package main.java;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class InMemoryTaskManager implements TaskManager {
    public final Map<Integer, Task> tasks = new HashMap<>();
    public final Map<Integer, Epic> epics = new HashMap<>();
    public final Map<Integer, Subtask> subtasks = new HashMap<>();
    public final HistoryManager historyManager = Managers.getDefaultHistory();

    private int nextId = 1;


    @Override
    public void add(Task task) {
        Task newTask = new Task(task);
        task.setId(nextId);
        newTask.setId(nextId);
        tasks.put(newTask.getId(), newTask);
        nextId++;
    }

    @Override
    public void add(Epic epic) {
        Epic newEpic = new Epic(epic);
        epic.setId(nextId);
        newEpic.setId(nextId);
        epics.put(newEpic.getId(), newEpic);
        nextId++;
    }

    @Override
    public void add(Subtask subtask) {

        Subtask newSubtask = new Subtask(subtask.getName(), subtask.getDescription(), subtask.getStatus(), subtask.getEpicId());
        newSubtask.setId(nextId);
        subtask.setId(nextId);
        subtasks.put(newSubtask.getId(), newSubtask);
        Epic epic = epics.get(newSubtask.getEpicId());
        epic.addSubTasksId(nextId);
        calculateEpicStatus(newSubtask.getEpicId());
        nextId++;

    }

    @Override
    public void update(Task task) {
        tasks.put(task.getId(), new Task(task.getName(), task.getDescription(), task.getStatus()));
    }


    @Override
    public void update(Epic epic) {
        epics.put(epic.getId(), new Epic(epic.getName(), epic.getDescription()));
    }

    @Override
    public void update(Subtask subtask) {
        Subtask oldSubtask = subtasks.get(subtask.getId());
        Subtask newSubtask = new Subtask(subtask);
        if (newSubtask.getEpicId() != oldSubtask.getEpicId()) { //Если мы решили поменять у сабтаски эпик
            Epic oldEpic = getEpicBySubId(oldSubtask.getId());
            oldEpic.removeSubTasksId(oldSubtask.getId()); //удаляем из старого эпика привязку
            Epic newEpic = epics.get(newSubtask.getEpicId());
            newEpic.addSubTasksId(newSubtask.getId()); //Линкуем к новому эпику
        }
        subtasks.put(newSubtask.getId(), newSubtask);
        calculateEpicStatus(newSubtask.getEpicId());
    }

    @Override
    public void clearTasks() {
        tasks.clear();

    }

    @Override
    public void clearSubTasks() {
        subtasks.clear();
        epics.forEach((k, v) -> {
                    v.removeAllSubTasks();
                    calculateEpicStatus(k);
                }
        );
    }

    @Override
    public void clearEpics() {
        epics.clear();
        clearSubTasks();

    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }

        return task;

    }

    @Override
    public Task getSubTaskById(int id) {
        Task task = subtasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;

    }

    @Override
    public Task getEpicById(int id) {
        Task task = epics.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;

    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Subtask> getSubTasks() {
        List<Subtask> subTaskList = new ArrayList<>();
        subtasks.forEach((k, v) -> subTaskList.add(v));
        return subTaskList;
    }

    @Override
    public List<Epic> getEpics() {
        List<Epic> epicList = new ArrayList<>();
        epics.forEach((k, v) -> epicList.add(v));
        return epicList;
    }

    @Override
    public void deleteTaskById(int id) {
        tasks.remove(id);
        historyManager.remove(id);

    }

    @Override
    public void deleteSubTaskById(int id) {
        historyManager.remove(id);
        Epic epic = getEpicBySubId(id);
        subtasks.remove(id);
        epic.removeSubTasksId(id);
        calculateEpicStatus(epic.getId());


    }

    @Override
    public Epic getEpicBySubId(int id) {

        Subtask subtask = subtasks.get(id);
        return epics.get(subtask.getEpicId());
    }

    @Override
    public void deleteEpicById(int id) {
        historyManager.remove(id);
        Epic epic = epics.get(id);
        for (int subtaskId : epic.getSubTasksIds()) {
            historyManager.remove(subtaskId);
            subtasks.remove(subtaskId);
        }
        epics.remove(id);
    }


    @Override
    public List<Subtask> getEpicSubs(int epicId) {
        Epic epic = epics.get(epicId);

        List<Subtask> subList = new ArrayList<>();


        for (int id : subtasks.keySet()) {
            Subtask subtask = subtasks.get(id);

            if (subtask.getEpicId() == epic.getId()) {
                subList.add(subtask);
            }
        }

        return subList;
    }


    public void calculateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);

        List<Subtask> subtasks = getEpicSubs(epicId);

        // 1. Проверяем, есть ли подзадачи
        if (subtasks.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        // 2. Проверяем статусы подзадач
        boolean allNew = true;
        boolean allDone = true;

        for (Subtask subtask : subtasks) {
            if (subtask.getStatus() != Status.NEW) {
                allNew = false;
            }
            if (subtask.getStatus() != Status.DONE) {
                allDone = false;
            }
        }

        // Устанавливаем статус эпика на основе статусов подзадач
        if (allNew) {
            epic.setStatus(Status.NEW);
        } else if (allDone) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }


}
