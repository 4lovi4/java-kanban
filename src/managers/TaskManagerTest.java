package managers;

import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {

    protected TaskManager taskManager;

    public Task createNewTask() {
        Task task = new Task(0, "Задача Есть", "NEW", "Описание задачи");
        return task;
    }

    public Epic createNewEpic() {
        Epic epic = new Epic(0, "Эпик Есть", "NEW", "Описание задачи");
        return epic;
    }

    public SubTask createSubTask(int counter, int epicId) {
        SubTask subTask = new SubTask(0, String.format("Подзадача %d", counter),
                "NEW", String.format("Описание подзадачи %d", counter), epicId);
        return subTask;
    }
}
