package main.java.managers;

import main.java.domain.Epic;
import main.java.domain.Subtask;
import main.java.domain.Task;
import main.java.enums.Status;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.TreeSet;

public class InMemoryTaskManager implements TaskManager {
    private final TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    public static final HistoryManager historyManager = Managers.getDefaultHistory();
    private int nextId = 1;


    @Override
    public void add(Task task) {
        Task newTask = task.clone();
        task.setId(nextId);
        newTask.setId(nextId);
        tasks.put(newTask.getId(), newTask);
        if (hasStartTime(task)) {
            if (hasOverlap(newTask)) {
                throw new IllegalArgumentException("Ошибка: задача пересекается с уже существующей.");
            }
            prioritizedTasks.add(newTask);
        }

        nextId++;
    }

    @Override
    public void add(Epic epic) {
        Epic newEpic = epic.clone();
        epic.setId(nextId);
        newEpic.setId(nextId);
        epics.put(newEpic.getId(), newEpic);
        calculateEpicStatus(nextId);
        nextId++;

    }

    @Override
    public void add(Subtask subtask) {

        Subtask newSubtask = subtask.clone();
        newSubtask.setId(nextId);
        subtask.setId(nextId);
        subtasks.put(newSubtask.getId(), newSubtask);
        Epic epic = epics.get(newSubtask.getEpicId());
        epic.addSubTasksId(nextId);
        calculateEpicStatus(newSubtask.getEpicId());
        if (hasStartTime(subtask)) {
            if (hasOverlap(newSubtask)) {
                throw new IllegalArgumentException("Ошибка: задача пересекается с уже существующей.");
            }
            prioritizedTasks.add(newSubtask);
        }

        nextId++;

    }

    @Override
    public void update(Task task) {
        Task updatedTask = task.clone();
        tasks.put(updatedTask.getId(), updatedTask);
        if (hasStartTime(updatedTask)) {
            if (hasOverlap(updatedTask)) {
                throw new IllegalArgumentException("Ошибка: задача пересекается с уже существующей.");
            }
            prioritizedTasks.remove(updatedTask);
            prioritizedTasks.add(updatedTask);
        }

    }


    @Override
    public void update(Epic epic) {
        Epic updatedEpic = epic.clone();
        epics.put(updatedEpic.getId(), updatedEpic);

    }

    @Override
    public void update(Subtask subtask) {
        Subtask oldSubtask = subtasks.get(subtask.getId());
        Subtask newSubtask = subtask.clone();
        if (newSubtask.getEpicId() != oldSubtask.getEpicId()) { //Если мы решили поменять у сабтаски эпик
            Epic oldEpic = getEpicBySubId(oldSubtask.getId());
            oldEpic.removeSubTasksId(oldSubtask.getId()); //удаляем из старого эпика привязку
            Epic newEpic = epics.get(newSubtask.getEpicId());
            newEpic.addSubTasksId(newSubtask.getId()); //Линкуем к новому эпику
        }
        subtasks.put(newSubtask.getId(), newSubtask);
        if (hasStartTime(newSubtask)) {
            if (hasOverlap(newSubtask)) {
                throw new IllegalArgumentException("Ошибка: задача пересекается с уже существующей.");
            }
            prioritizedTasks.remove(newSubtask);
            prioritizedTasks.add(newSubtask);
        }
        calculateEpicStatus(newSubtask.getEpicId());
    }

    @Override
    public void clearTasks() {
        for (int id : tasks.keySet()) {
            historyManager.remove(id);
        }
        tasks.clear();
        prioritizedTasks.clear();

    }

    @Override
    public void clearSubTasks() {
        for (int id : subtasks.keySet()) {
            Subtask subToRemove = subtasks.get(id);
            prioritizedTasks.remove(subToRemove);
            historyManager.remove(id);
        }
        subtasks.clear();
        epics.forEach((k, v) -> {
                    v.removeAllSubTasks();
                    calculateEpicStatus(k);
                }
        );
    }

    @Override
    public void clearEpics() {
        for (int id : epics.keySet()) {
            historyManager.remove(id);
        }
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
    public Subtask getSubTaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
        }
        return subtask;

    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;

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
        Task taskToRemove = tasks.get(id);
        prioritizedTasks.remove(taskToRemove);
        if (tasks.containsKey(id)) {
            tasks.remove(id);
            historyManager.remove(id);
        }
    }

    @Override
    public void deleteSubTaskById(int id) {
        Subtask subtaskToRemove = subtasks.get(id);
        prioritizedTasks.remove(subtaskToRemove);
        if (subtasks.containsKey(id)) {
            historyManager.remove(id);
            Epic epic = getEpicBySubId(id);
            subtasks.remove(id);
            epic.removeSubTasksId(id);
            calculateEpicStatus(epic.getId());
        }

    }

    @Override
    public Epic getEpicBySubId(int id) {
        Subtask subtask = subtasks.get(id);
        return epics.get(subtask.getEpicId());
    }

    @Override
    public void deleteEpicById(int id) {
        if (epics.containsKey(id)) {
            historyManager.remove(id);
            Epic epic = epics.get(id);
            for (int subtaskId : epic.getSubTasksIds()) {
                Subtask subtaskToRemove = subtasks.get(subtaskId);
                prioritizedTasks.remove(subtaskToRemove);
                historyManager.remove(subtaskId);
                subtasks.remove(subtaskId);
            }
            epics.remove(id);
        }
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

        epic.calculateStartTime(subtasks);
        epic.calculateDuration(subtasks);

        List<Subtask> epicSubs = getEpicSubs(epicId);

        // 1. Проверяем, есть ли подзадачи
        if (epicSubs.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        // 2. Проверяем статусы подзадач
        boolean allNew = true;
        boolean allDone = true;

        for (Subtask subtask : epicSubs) {
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

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    public boolean hasStartTime(Task task) {
        return task.getStartTime() != null;
    }

    private boolean isOverlapping(Task task1, Task task2) {
        if (task1.getStartTime() == null || task2.getStartTime() == null) {
            return false;
        }
        LocalDateTime start1 = task1.getStartTime();
        LocalDateTime end1 = task1.getEndTime();
        LocalDateTime start2 = task2.getStartTime();
        LocalDateTime end2 = task2.getEndTime();

        return start1.isBefore(end2) && start2.isBefore(end1);
    }

    private boolean hasOverlap(Task newTask) {
        return prioritizedTasks.stream()
                .anyMatch(existingTask -> isOverlapping(existingTask, newTask) && !existingTask.equals(newTask));
    }


}
