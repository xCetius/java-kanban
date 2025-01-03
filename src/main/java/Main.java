package main.java;

public class Main {
    public static void main(String[] args) {
        Managers managers = new Managers();

        TaskManager taskManager = managers.getDefault();
        HistoryManager historyManager = Managers.getDefaultHistory();

        // Создаём задачи
        Task task1 = new Task("Task 1", "Description 1", Status.NEW);
        Task task2 = new Task("Task 2", "Description 2", Status.IN_PROGRESS);
        taskManager.add(task1);
        taskManager.add(task2);

        Epic epic1 = new Epic("Epic 1", "Epic description");
        taskManager.add(epic1);

        Subtask subTask1 = new Subtask("Subtask 1", "Subtask description", Status.NEW, epic1.getId());
        Subtask subTask2 = new Subtask("Subtask 2", "Subtask description", Status.IN_PROGRESS, epic1.getId());
        Subtask subTask3 = new Subtask("Subtask 3", "Subtask description", Status.IN_PROGRESS, epic1.getId());
        taskManager.add(subTask1);
        taskManager.add(subTask2);
        taskManager.add(subTask3);

        // Запросы к задачам
        taskManager.getTaskById(task1.getId());
        taskManager.getSubTaskById(subTask3.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getSubTaskById(subTask2.getId());
        taskManager.getEpicById(epic1.getId());
        taskManager.getSubTaskById(subTask1.getId());

        // Выводим историю
        System.out.println("История просмотров:");
        historyManager.getHistory().forEach(System.out::println);

        // Удаляем задачу
        taskManager.deleteTaskById(task1.getId());
        System.out.println("После удаления Task 1:");
        historyManager.getHistory().forEach(System.out::println);

        // Удаляем эпик
        taskManager.deleteEpicById(epic1.getId());
        System.out.println("После удаления Epic 1:");
        historyManager.getHistory().forEach(System.out::println);


    }

}
