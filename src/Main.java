import ru.yandex.practikum.sprint3.models.Epic;
import ru.yandex.practikum.sprint3.models.Status;
import ru.yandex.practikum.sprint3.models.SubTask;
import ru.yandex.practikum.sprint3.models.Task;
import ru.yandex.practikum.sprint3.manager.*;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = new TaskManager();
        Task task1 = new Task("Задача1", "Простая задача", 0, Status.NEW);
        Task task2 = new Task("Задача2", "Задача уже делается", 0, Status.IN_PROGRESS);
        Epic epic1 = new Epic();
        Epic epic2 = new Epic();
        SubTask subTask1 = new SubTask(epic1.getId());
        SubTask subTask2 = new SubTask(epic1.getId());
        SubTask subTask3 = new SubTask(epic2.getId());
    }
}
