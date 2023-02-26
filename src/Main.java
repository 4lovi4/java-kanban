import ru.yandex.practikum.sprint3.models.Epic;
import ru.yandex.practikum.sprint3.models.Status;
import ru.yandex.practikum.sprint3.models.SubTask;
import ru.yandex.practikum.sprint3.models.Task;
import ru.yandex.practikum.sprint3.manager.*;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = new TaskManager();

        // Создание задач всех типов
        Task task1 = new Task("Задача 1", "Простая задача", 0, Status.NEW);
        Task task2 = new Task("Задача 2", "Задача уже делается", 0, Status.IN_PROGRESS);
        Epic epic1 = new Epic("Эпик 1", "Первый эпик", 0, null);
        Epic epic2 = new Epic("Эпик 2", "Второй эпик", 0, null);
        SubTask subTask1 = new SubTask("Подзадача 1", "Подзадача из 1го эпика",
                0, Status.NEW, 0);
        SubTask subTask2 = new SubTask("Подзадача 2", "Подзадача из 1го эпика",
                0, Status.NEW, 0);
        SubTask subTask3 = new SubTask("Подзадача 3", "Подзадача из 2го эпика",
                0, Status.NEW, 0);
        manager.create(task1);
        manager.create(task2);
        manager.create(epic1);
        manager.create(epic2);
        manager.create(subTask1);
        manager.create(subTask2);
        manager.create(subTask3);
        ArrayList<Task> allTasks = manager.getAllTasksList();
        ArrayList<Epic> allEpics = manager.getAllEpicsList();
        ArrayList<SubTask> allSubTasks = manager.getAllSubTasksList();

        // Вывод списков всех созданных задач по типам
        System.out.println(allTasks);
        System.out.println(allEpics);
        System.out.println(allSubTasks);

        // Обновление созданных задач
        task1 = allTasks.get(0);
        task2 = allTasks.get(1);
        task1.setStatus(Status.IN_PROGRESS);
        manager.update(task1);
        task2.setStatus(Status.DONE);
        manager.update(task2);

        epic1 = allEpics.get(0);
        epic2 = allEpics.get(1);
        subTask1 = allSubTasks.get(0);
        subTask2 = allSubTasks.get(1);
        subTask3 = allSubTasks.get(2);
        subTask1.setEpicId(epic1.getId());
        subTask2.setEpicId(epic1.getId());
        subTask3.setEpicId(epic2.getId());
        subTask1.setStatus(Status.IN_PROGRESS);
        subTask2.setStatus(Status.DONE);
        subTask3.setStatus(Status.DONE);
        epic1.addToSubTasksIdList(subTask1.getId());
        epic1.addToSubTasksIdList(subTask2.getId());
        epic2.addToSubTasksIdList(subTask3.getId());
        manager.update(epic1);
        manager.update(epic2);
        manager.update(subTask1);
        manager.update(subTask2);
        manager.update(subTask3);

        //Удалим таску и эпик
        manager.deleteTask(task1.getId());
        manager.deleteEpic(epic2.getId());
    }
}
