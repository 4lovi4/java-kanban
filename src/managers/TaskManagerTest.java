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
        SubTask subTask2 = createSubTask(1, epicId);
        taskManager.addNewSubTask(subTask1);
        taskManager.addNewSubTask(subTask2);
        assertEquals(Status.NEW, subTask1.getStatus());
        assertEquals(Status.NEW, subTask2.getStatus());
        assertEquals(Status.NEW, epic.getStatus(), String.format("Статус эпика со всеми подзадачами в статусе %s не равен %s", Status.NEW, Status.NEW));
    }

    @Test
    public void shouldReturnEpicDoneStatusForAllDoneSubTasks() {
        Epic epic = createNewEpic();
        int epicId = taskManager.addNewEpic(epic);
        SubTask subTask1 = createSubTask(0, epicId);
        subTask1.setStatus(Status.DONE);
        SubTask subTask2 = createSubTask(1, epicId);
        subTask2.setStatus(Status.DONE);
        taskManager.addNewSubTask(subTask1);
        taskManager.addNewSubTask(subTask2);
        assertEquals(Status.DONE, epic.getStatus(), String.format("Статус эпика со всеми подзадачами в статусе %s не равен %s", Status.DONE, Status.DONE));
    }

    @Test
    public void shouldReturnEpicInProgressStatusForDoneAndNewSubTasks() {
        Epic epic = createNewEpic();
        int epicId = taskManager.addNewEpic(epic);
        SubTask subTask1 = createSubTask(0, epicId);
        subTask1.setStatus(Status.NEW);
        SubTask subTask2 = createSubTask(1, epicId);
        subTask2.setStatus(Status.DONE);
        taskManager.addNewSubTask(subTask1);
        taskManager.addNewSubTask(subTask2);
        assertEquals(Status.IN_PROGRESS, epic.getStatus(), String.format("Статус эпика с подзадачами в статусе %s и %s не равен %s", Status.NEW, Status.DONE, Status.IN_PROGRESS));
    }

    @Test
    public void shouldReturnEpicInProgressStatusForAllInProgressSubTasks() {
        Epic epic = createNewEpic();
        int epicId = taskManager.addNewEpic(epic);
        SubTask subTask1 = createSubTask(0, epicId);
        subTask1.setStatus(Status.IN_PROGRESS);
        SubTask subTask2 = createSubTask(1, epicId);
        subTask2.setStatus(Status.IN_PROGRESS);
        taskManager.addNewSubTask(subTask1);
        taskManager.addNewSubTask(subTask2);
        assertEquals(Status.IN_PROGRESS, epic.getStatus(), String.format("Статус эпика со всеми подзадачами в статусе %s не равен %s", Status.IN_PROGRESS, Status.IN_PROGRESS));
    }
}
