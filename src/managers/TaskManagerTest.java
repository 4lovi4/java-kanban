package managers;

import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {

    abstract TaskManager getTaskManager();

    TaskManager taskManager = getTaskManager();

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

    @Test
    public void shouldReturnEpicNewStatusForEmptySubTasks() {
        Epic epic = createNewEpic();
        assertTrue(epic.getSubTasksId().isEmpty(), "Список id подзадач не пустой");
        assertEquals(Status.NEW, epic.getStatus(), String.format("Статус эпика с пустым списком подзадач не равен %s", Status.NEW));
    }

    @Test
    public void shouldReturnEpicNewStatusForAllNewSubTasks() {
        Epic epic = createNewEpic();
        int epicId = taskManager.addNewEpic(epic);
        SubTask subTask1 = createSubTask(0, epicId);
        SubTask subTask2 = createSubTask(0, epicId);
        taskManager.addNewSubTask(subTask1);
        taskManager.addNewSubTask(subTask2);
        assertEquals(Status.NEW, subTask1.getStatus());
        assertEquals(Status.NEW, subTask2.getStatus());
        assertEquals(Status.NEW, epic.getStatus(), String.format("Статус эпика со всеми подзадачами в статусе %s не равен %s", Status.NEW, Status.NEW));
    }
}
