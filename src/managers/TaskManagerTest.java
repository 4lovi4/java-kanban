package managers;

import java.util.List;

import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {

	abstract TaskManager getTaskManager();

	TaskManager taskManager = getTaskManager();

	public Task createNewTask(int counter) {
		Task task = new Task(0, String.format("Задача %d Есть", counter),
				"NEW", String.format("Описание задачи %d", counter));
		return task;
	}

	public Epic createNewEpic(int counter) {
		Epic epic = new Epic(0, String.format("Эпик %d Есть", counter), "NEW",
				String.format("Описание задачи %d", counter));
		return epic;
	}

	public SubTask createSubTask(int counter, int epicId) {
		SubTask subTask = new SubTask(0, String.format("Подзадача %d", counter),
				"NEW", String.format("Описание подзадачи %d", counter), epicId);
		return subTask;
	}

	@Test
	public void shouldReturnEpicNewStatusForEmptySubTasks() {
		Epic epic = createNewEpic(1);
		assertTrue(epic.getSubTasksId().isEmpty(), "Список id подзадач не пустой");
		assertEquals(Status.NEW, epic.getStatus(), String.format("Статус эпика с пустым списком подзадач не равен %s", Status.NEW));
	}

	@Test
	public void shouldReturnEpicNewStatusForAllNewSubTasks() {
		Epic epic = createNewEpic(1);
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
		Epic epic = createNewEpic(1);
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
		Epic epic = createNewEpic(1);
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
		Epic epic = createNewEpic(1);
		int epicId = taskManager.addNewEpic(epic);
		SubTask subTask1 = createSubTask(0, epicId);
		subTask1.setStatus(Status.IN_PROGRESS);
		SubTask subTask2 = createSubTask(1, epicId);
		subTask2.setStatus(Status.IN_PROGRESS);
		taskManager.addNewSubTask(subTask1);
		taskManager.addNewSubTask(subTask2);
		assertEquals(Status.IN_PROGRESS, epic.getStatus(), String.format("Статус эпика со всеми подзадачами в статусе %s не равен %s", Status.IN_PROGRESS, Status.IN_PROGRESS));
	}

	@Test
	public void shouldGetAllTasksList() {
		Task task1 = createNewTask(1);
		Task task2 = createNewTask(2);
		int taskId1 = taskManager.addNewTask(task1);
		int taskId2 = taskManager.addNewTask(task2);
		List<Task> allTasks = taskManager.getAllTasks();
		task1.setId(taskId1);
		task2.setId(taskId2);
		assertNotNull(allTasks);
		assertEquals(2, allTasks.size(), "Размер списка задач не равен 2!");
		assertEquals(task1, allTasks.get(0), "Первая задача не совпадает с добавленной");
		assertEquals(task2, allTasks.get(1), "Вторая задача не совпадает с добавленной");
	}

	@Test
	public void shouldGetAllTasksEmptyList() {
		List<Task> allTasks = taskManager.getAllTasks();
		assertNotNull(allTasks);
		assertTrue(allTasks.isEmpty(), "Список всех задач не пустой");
	}

	@Test
	public void shouldGetAllEpicsList() {
		Epic epic1 = createNewEpic(1);
		Epic epic2 = createNewEpic(2);
		int epicId1 = taskManager.addNewEpic(epic1);
		int epicId2 = taskManager.addNewEpic(epic2);
		List<Epic> allEpics = taskManager.getAllEpics();
		epic1.setId(epicId1);
		epic2.setId(epicId2);
		assertNotNull(allEpics);
		assertEquals(2, allEpics.size(), "Размер списка эпиков не равен 2!");
		assertEquals(epic1, allEpics.get(0), "Первый эпик не совпадает с добавленным");
		assertEquals(epic2, allEpics.get(1), "Второй эпик не совпадает с добавленным");
	}

	@Test
	public void shouldGetAllEpicsEmptyList() {
		List<Epic> allEpics = taskManager.getAllEpics();
		assertNotNull(allEpics);
		assertTrue(allEpics.isEmpty(), "Список всех эпиков не пустой");
	}

	@Test
	public void shouldGetAllSubTasksList() {
		SubTask subTask1 = createSubTask(1, 0);
		SubTask subTask2 = createSubTask(2, 0);
		int subTaskId1 = taskManager.addNewSubTask(subTask1);
		int subTaskId2 = taskManager.addNewSubTask(subTask2);
		List<SubTask> allSubTasks = taskManager.getAllSubTasks();
		subTask1.setId(subTaskId1);
		subTask2.setId(subTaskId2);
		assertNotNull(allSubTasks);
		assertEquals(2, allSubTasks.size(), "Размер списка подзадач не равен 2!");
		assertEquals(subTask1, allSubTasks.get(0), "Первая подзадача не совпадает с добавленной");
		assertEquals(subTask2, allSubTasks.get(1), "Вторая подзадача не совпадает с добавленной");
	}

	@Test
	public void shouldGetAllSubTasksEmptyList() {
		List<SubTask> allSubTasks = taskManager.getAllSubTasks();
		assertNotNull(allSubTasks);
		assertTrue(allSubTasks.isEmpty(), "Список всех подзадач не пустой");
	}

	@Test
	public void shouldAddAndGetNewTask() {
		Task task = createNewTask(1);
		int taskId = taskManager.addNewTask(task);
		task.setId(taskId);
		Task taskAdded = taskManager.getTask(taskId);
		assertNotNull(taskAdded);
		assertEquals(task, taskAdded, "Добавленная задача не равна ожидаемой");
	}

	@Test
	public void shouldAddAndGetNewEpic() {
		Epic epic = createNewEpic(1);
		int epicId = taskManager.addNewEpic(epic);
		epic.setId(epicId);
		Epic epicAdded = taskManager.getEpic(epicId);
		assertNotNull(epicAdded);
		assertEquals(epic, epicAdded, "Добавленный эпик не равен ожидаемому");
	}

	@Test
	public void shouldAddAndGetNewSubTask() {
		SubTask subTask = createSubTask(1, 0);
		int subTaskId = taskManager.addNewSubTask(subTask);
		subTask.setId(subTaskId);
		SubTask subTaskAdded = taskManager.getSubTask(subTaskId);
		assertNotNull(subTaskAdded);
		assertEquals(subTask, subTaskAdded, "Добавленная задача не равна ожидаемой");
	}

	@Test
	public void ShouldGetNullOnWrongTaskId() {
		Task task = createNewTask(1);
		int taskId = taskManager.addNewTask(task);
		int wrongTaskId = 1_000_000;
		assertNotEquals(wrongTaskId, taskId);
		assertThrows(NullPointerException.class, () -> taskManager.getTask(wrongTaskId));
	}

	@Test
	public void ShouldGetNullOnWrongEpicId() {
		Epic epic = createNewEpic(1);
		int epicId = taskManager.addNewEpic(epic);
		int wrongEpicId = 1_000_000;
		assertNotEquals(wrongEpicId, epicId);
		assertThrows(NullPointerException.class, () -> taskManager.getEpic(wrongEpicId));
	}

	@Test
	public void ShouldGetNullOnWrongSubTaskId() {
		SubTask subTask = createSubTask(1, 0);
		int subTaskId = taskManager.addNewSubTask(subTask);
		int wrongSubTaskId = 1_000_000;
		assertNotEquals(wrongSubTaskId, subTaskId);
		assertThrows(NullPointerException.class, () -> taskManager.getSubTask(wrongSubTaskId));
	}

	@Test
	public void ShouldGetNullOnEmptyTasksList() {
		int taskId = 1;
		assertTrue(taskManager.getAllTasks().isEmpty());
		assertThrows(NullPointerException.class, () -> taskManager.getTask(taskId));
	}

	@Test
	public void ShouldGetNullOnEmptyEpicsList() {
		int epicId = 1;
		assertTrue(taskManager.getAllEpics().isEmpty());
		assertThrows(NullPointerException.class, () -> taskManager.getEpic(epicId));
	}

	@Test
	public void ShouldGetNullOnEmptySubTasksList() {
		int subTaskId = 1;
		assertTrue(taskManager.getAllSubTasks().isEmpty());
		assertThrows(NullPointerException.class, () -> taskManager.getSubTask(subTaskId));
	}


	@Test
	public void shouldUpdateTask() {
		Task task = createNewTask(1);
		int taskId = taskManager.addNewTask(task);
		task.setId(taskId);
		task.setName("Измененная задач");
		task.setStatus(Status.IN_PROGRESS);
		task.setDescription("Новое описание задачи");
		taskManager.updateTask(task);
		Task taskUpdated = taskManager.getTask(taskId);
		assertNotNull(taskUpdated);
		assertEquals(task, taskUpdated, "Изменённая задача не равна ожидаемой");
	}

	@Test
	public void shouldUpdateSubTask() {
		SubTask subTask = createSubTask(1, 0);
		int subTaskId = taskManager.addNewSubTask(subTask);
		subTask.setId(subTaskId);
		subTask.setName("Измененная подзадача");
		subTask.setStatus(Status.DONE);
		subTask.setDescription("Новое описание подзадачи");
		taskManager.updateTask(subTask);
		Task subTaskUpdated = taskManager.getSubTask(subTaskId);
		assertNotNull(subTaskUpdated);
		assertEquals(subTask, subTaskUpdated, "Изменённая подзадача не равна ожидаемой");
	}

	@Test
	public void shouldUpdateEpic() {
		Epic epic = createNewEpic(1);
		int epicId = taskManager.addNewEpic(epic);
		SubTask subTask = createSubTask(1, epicId);
		int subTaskId = taskManager.addNewSubTask(subTask);
		epic.setId(epicId);
		epic.setName("Измененный эпик");
		epic.setStatus(Status.IN_PROGRESS);
		epic.setDescription("Новое описание епика");
		epic.addSubTaskId(subTaskId);
		taskManager.updateEpic(epic);
		Task epicUpdated = taskManager.getEpic(epicId);
		assertNotNull(epicUpdated);
		assertEquals(epic, epicUpdated, "Изменённая задача не равна ожидаемой");
	}

}
