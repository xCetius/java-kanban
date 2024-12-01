package main.java;

public class Main {
    public static void main(String[] args) {
        /*
        TaskManager taskManager;
        HistoryManager historyManager;

        Managers managers = new Managers();
        taskManager = managers.getDefault();
        historyManager = Managers.getDefaultHistory();

        // Создаём задачи
        Task task1 = new Task("main.java.Task 1", "Description 1", Status.NEW);
        Task task2 = new Task("main.java.Task 2", "Description 2", Status.IN_PROGRESS);
        taskManager.add(task1);
        taskManager.add(task2);
        // Создаём эпики
        Epic epic1 = new Epic("main.java.Epic 1", "main.java.Epic description 1");
        Epic epic2 = new Epic("main.java.Epic 2", "main.java.Epic description 2");
        taskManager.add(epic1);
        taskManager.add(epic2);
        // Создаём подзадачи
        Subtask subtask1 = new Subtask("main.java.Subtask 1", "Description of subtask 1", Status.NEW, epic1.getId());
        Subtask subtask2 = new Subtask("main.java.Subtask 2", "Description of subtask 2", Status.NEW, epic1.getId());
        Subtask subtask3 = new Subtask("main.java.Subtask 3", "Description of subtask 3", Status.DONE, epic2.getId());
        taskManager.add(subtask1);
        taskManager.add(subtask2);
        taskManager.add(subtask3);

        System.out.println("Должно быть ID = 1");

        System.out.println(taskManager.getTaskById(1));

        // Распечатываем текущие списки задач, эпиков и подзадач
        System.out.println("Tasks:");
        for (Task task : taskManager.getTasks()) {
            System.out.println(task);
        }

        System.out.println("Epics:");
        for (Epic epic : taskManager.getEpics()) {
            System.out.println(epic);
        }

        System.out.println("Subtasks:");
        for (Subtask subtask : taskManager.getSubTasks()) {
            System.out.println(subtask);
        }

        // Изменяем статусы
        task1.setStatus(Status.DONE);
        subtask1.setStatus(Status.IN_PROGRESS);
        subtask2.setStatus(Status.DONE);
        epic1.setStatus(Status.IN_PROGRESS);
        epic2.setStatus(Status.DONE);

        // Обновляем объекты в main.java.TaskManager
        taskManager.update(task1);
        taskManager.update(subtask1);
        taskManager.update(subtask2);
        taskManager.update(epic1);
        taskManager.update(epic2);

        // Распечатываем списки после изменений статусов
        System.out.println("\nAfter status updates:");
        System.out.println("main.java.Task 1 status: " + task1.getStatus());
        System.out.println("main.java.Epic 1 status: " + epic1.getStatus());
        System.out.println("main.java.Epic 2 status: " + epic2.getStatus());
        for (Subtask subtask : taskManager.getSubTasks()) {
            System.out.println("main.java.Subtask status: " + subtask.getStatus());
        }

        // Проверяем, что статус эпика рассчитывается правильно
        System.out.println("\nmain.java.Epic 1 should be IN_PROGRESS (due to one subtask in progress and one done): " + epic1.getStatus());
        System.out.println("main.java.Epic 2 should be DONE (because its only subtask is DONE): " + epic2.getStatus());

        // Удаляем одну задачу и один эпик
        taskManager.deleteTaskById(task1.getId()); // Удаляем task1
        taskManager.deleteEpicById(epic2.getId()); // Удаляем epic2

        // Распечатываем списки после удаления
        System.out.println("\nAfter deletion:");
        System.out.println("Tasks:");
        for (Task task : taskManager.getTasks()) {
            System.out.println(task);
        }

        System.out.println("Epics:");
        for (Epic epic : taskManager.getEpics()) {
            System.out.println(epic);
        }

        System.out.println("Subtasks:");
        for (Subtask subtask : taskManager.getSubTasks()) {
            System.out.println(subtask);
        }

        taskManager.getSubTaskById(5);
        taskManager.getSubTaskById(6);
        taskManager.getEpicById(3);

        System.out.println(historyManager.getHistory());

        printAllTasks(taskManager, historyManager);

    }

    private static void printAllTasks(TaskManager manager, HistoryManager historyManager) {
        System.out.println("Задачи:");
        for (Task task : manager.getTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : manager.getEpics()) {
            System.out.println(epic);

            for (Task task : manager.getEpicSubs(epic.getId())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getSubTasks()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : historyManager.getHistory()) {
            System.out.println(task);
        }
    }

         */
    }
}
