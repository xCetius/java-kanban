public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        // Создаём задачи
        Task task1 = new Task("Task 1", "Description 1", Status.NEW);
        Task task2 = new Task("Task 2", "Description 2", Status.IN_PROGRESS);
        taskManager.add(task1);
        taskManager.add(task2);
        // Создаём эпики
        Epic epic1 = new Epic("Epic 1", "Epic description 1");
        Epic epic2 = new Epic("Epic 2", "Epic description 2");
        taskManager.add(epic1);
        taskManager.add(epic2);
        // Создаём подзадачи
        Subtask subtask1 = new Subtask("Subtask 1", "Description of subtask 1", Status.NEW, epic1.getId());
        Subtask subtask2 = new Subtask("Subtask 2", "Description of subtask 2", Status.NEW, epic1.getId());
        Subtask subtask3 = new Subtask("Subtask 3", "Description of subtask 3", Status.DONE, epic2.getId());
        taskManager.add(subtask1);
        taskManager.add(subtask2);
        taskManager.add(subtask3);

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

        // Обновляем объекты в TaskManager
        taskManager.update(task1);
        taskManager.update(subtask1);
        taskManager.update(subtask2);
        taskManager.update(epic1);
        taskManager.update(epic2);

        // Распечатываем списки после изменений статусов
        System.out.println("\nAfter status updates:");
        System.out.println("Task 1 status: " + task1.getStatus());
        System.out.println("Epic 1 status: " + epic1.getStatus());
        System.out.println("Epic 2 status: " + epic2.getStatus());
        for (Subtask subtask : taskManager.getSubTasks()) {
            System.out.println("Subtask status: " + subtask.getStatus());
        }

        // Проверяем, что статус эпика рассчитывается правильно
        System.out.println("\nEpic 1 should be IN_PROGRESS (due to one subtask in progress and one done): " + epic1.getStatus());
        System.out.println("Epic 2 should be DONE (because its only subtask is DONE): " + epic2.getStatus());

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
    }
}
