import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, Epic> epics = new HashMap<>();
    HashMap<Integer, Subtask> subtasks = new HashMap<>();
    int nextId = 1;

    public void add(Task task) {
        task.setId(nextId++);
        tasks.put(task.getId(), task);
    }

    public void add(Epic epic) {
        epic.setId(nextId++);
        epics.put(epic.getId(), epic);
    }

    public void add(Subtask subtask) {
        subtask.setId(nextId);
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        epic.addSubTasksId(nextId);
        calculateEpicStatus(epics.get(subtask.getEpicId()));
        nextId++;

    }

    public void update(Task task) {
        tasks.put(task.getId(), task);
    }


    public void update(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    public void update(Subtask subtask) {
        Subtask oldSubtask = subtasks.get(subtask.getId());
        if (subtask.getEpicId() != oldSubtask.getEpicId()) { //Если мы решили поменять у сабтаски эпик
            Epic oldEpic = getEpicBySubId(oldSubtask.getId());
            oldEpic.removeSubTasksId(oldSubtask.getId()); //удаляем из старого эпика привязку
            Epic newEpic = epics.get(subtask.getEpicId());
            newEpic.addSubTasksId(subtask.getId()); //Линкуем к новому эпику
        }
        subtasks.put(subtask.getId(), subtask);
        calculateEpicStatus(epics.get(subtask.getEpicId()));
    }

    public void clearTasks() {
        tasks.clear();

    }

    public void clearSubTasks() {
        subtasks.clear();
        epics.forEach((k, v) -> v.setStatus(Status.NEW));


    }

    public void clearEpics() {
        epics.clear();
        clearSubTasks();

    }

    public Task getTaskById(int id) {
        return tasks.get(id);

    }

    public Subtask getSubTaskById(int id) {
        return subtasks.get(id);

    }

    public Epic getEpicById(int id) {
        return epics.get(id);

    }

    public ArrayList<Task> getTasks() {
        ArrayList<Task> taskList = new ArrayList<>();
        tasks.forEach((k, v) -> taskList.add(v));
        return taskList;
    }

    public ArrayList<Subtask> getSubTasks() {
        ArrayList<Subtask> subTaskList = new ArrayList<>();
        subtasks.forEach((k, v) -> subTaskList.add(v));
        return subTaskList;
    }

    public ArrayList<Epic> getEpics() {
        ArrayList<Epic> epicList = new ArrayList<>();
        epics.forEach((k, v) -> epicList.add(v));
        return epicList;
    }

    public void deleteTaskById(int id) {
        tasks.remove(id);

    }

    public void deleteSubTaskById(int id) {
        Epic epic = getEpicBySubId(id);
        subtasks.remove(id);
        epic.removeSubTasksId(id);
        calculateEpicStatus(epic);


    }

    public Epic getEpicBySubId(int id) {
        Subtask subtask = getSubTaskById(id);
        return epics.get(subtask.getEpicId());
    }

    public void deleteEpicById(int id) {
        Epic epic = getEpicById(id);
        ArrayList<Subtask> subtasksToDelete = new ArrayList<>();
        for (int subId : epic.getSubTasksIds()) {
            Subtask subtask = getSubTaskById(subId);
            subtasksToDelete.add(subtask);
        }

        for (Subtask subtask : subtasksToDelete) {
            subtasks.remove(subtask.getId());
        }


        epics.remove(id);


    }

    public ArrayList<Subtask> getEpicSubs(Epic epic) {
        ArrayList<Subtask> subList = new ArrayList<>();


        for (int id : subtasks.keySet()) {
            Subtask subtask = subtasks.get(id);

            if (subtask.getEpicId() == epic.getId()) {
                subList.add(subtask);
            }
        }

        return subList;
    }


    public void calculateEpicStatus(Epic epic) {
        ArrayList<Subtask> subtasks = getEpicSubs(epic);

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
