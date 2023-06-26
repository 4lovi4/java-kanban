package managers;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {

	abstract T getTaskManager();

	T taskManager;

	@BeforeEach
	public void prepareTest() {
		taskManager = getTaskManager();
	}

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
	public void shouldGetSubTasksByEpicId() {
		Epic epic = createNewEpic(1);
		int epicId = taskManager.addNewEpic(epic);
		SubTask subTask = createSubTask(1, epicId);
		int subTaskId = taskManager.addNewSubTask(subTask);
		subTask.setId(subTaskId);
		assertEquals(1, taskManager.getSubTasksByEpic(epicId).size(), "Размер списка подзадач эпика не равен 1");
		assertEquals(subTask, taskManager.getSubTasksByEpic(epicId).get(0), "Подзадача из эпика не равна добавленной");
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
		Task newTask = createNewTask(2);
		newTask.setId(taskId);
		newTask.setName("Измененная задач");
		newTask.setStatus(Status.IN_PROGRESS);
		newTask.setDescription("Новое описание задачи");
		assertNotEquals(newTask, task);
		taskManager.updateTask(newTask);
		Task taskUpdated = taskManager.getTask(taskId);
		assertNotNull(taskUpdated);
		assertEquals(newTask, taskUpdated, "Изменённая задача не равна ожидаемой");
	}

	@Test
	public void shouldNotUpdateWrongTask() {
		Task task = createNewTask(1);
		int taskId = taskManager.addNewTask(task);
		Task wrongTask = createNewTask(2);
		int wrongTaskId = 1_000_000;
		assertNotEquals(wrongTaskId, taskId);
		wrongTask.setId(wrongTaskId);
		wrongTask.setName("Измененная задач");
		wrongTask.setStatus(Status.IN_PROGRESS);
		wrongTask.setDescription("Новое описание задачи");
		taskManager.updateTask(wrongTask);
		assertFalse(taskManager.getAllTasks().contains(wrongTask), "Список всех задач содержит задачу с неверным Id");
	}

	@Test
	public void shouldNotUpdateEmptyTasksList() {
		Task task = createNewTask(1);
		task.setStatus(Status.DONE);
		taskManager.updateTask(task);
		assertTrue(taskManager.getAllTasks().isEmpty(), "Список задач не пустой");
	}

	@Test
	public void shouldUpdateSubTask() {
		Epic epic1 = createNewEpic(1);
		Epic epic2 = createNewEpic(2);
		int epicId1 = taskManager.addNewEpic(epic1);
		int epicId2 = taskManager.addNewEpic(epic2);
		SubTask subTask = createSubTask(1, epicId1);
		int subTaskId = taskManager.addNewSubTask(subTask);
		SubTask newSubTask = createSubTask(2, epicId2);
		newSubTask.setId(subTaskId);
		newSubTask.setName("Измененная подзадача");
		newSubTask.setStatus(Status.DONE);
		newSubTask.setDescription("Новое описание подзадачи");
		assertNotEquals(newSubTask, subTask);
		taskManager.updateSubTask(newSubTask);
		Task subTaskUpdated = taskManager.getSubTask(subTaskId);
		assertNotNull(subTaskUpdated);
		assertEquals(newSubTask, subTaskUpdated, "Изменённая подзадача не равна ожидаемой");
	}

	@Test
	public void shouldNotUpdateWrongSubTask() {
		Epic epic1 = createNewEpic(1);
		Epic epic2 = createNewEpic(2);
		int epicId1 = taskManager.addNewEpic(epic1);
		int epicId2 = taskManager.addNewEpic(epic2);
		SubTask subTask = createSubTask(1, epicId1);
		int subTaskId = taskManager.addNewSubTask(subTask);
		int wrongSubTaskId = 1_000_000;
		assertNotEquals(wrongSubTaskId, subTaskId);
		SubTask wrongSubTask = createSubTask(2, epicId2);
		wrongSubTask.setId(wrongSubTaskId);
		wrongSubTask.setName("Измененная подзадача");
		wrongSubTask.setStatus(Status.DONE);
		wrongSubTask.setDescription("Новое описание подзадачи");
		assertNotEquals(wrongSubTask, subTask);
		taskManager.updateSubTask(wrongSubTask);
		assertFalse(taskManager.getAllTasks().contains(wrongSubTask), "Список всех подзадач содержит подзадачу с неверным Id");
	}

	@Test
	public void shouldNotUpdateEmptySubTasksList() {
		SubTask subTask = createSubTask(1, 1);
		subTask.setStatus(Status.DONE);
		taskManager.updateSubTask(subTask);
		assertTrue(taskManager.getAllSubTasks().isEmpty(), "Список подзадач не пустой");
	}

	@Test
	public void shouldUpdateEpic() {
		Epic epic = createNewEpic(1);
		int epicId = taskManager.addNewEpic(epic);
		Epic newEpic = createNewEpic(2);
		SubTask subTask = createSubTask(1, epicId);
		int subTaskId = taskManager.addNewSubTask(subTask);
		newEpic.setId(epicId);
		newEpic.setName("Измененный эпик");
		newEpic.setStatus(Status.IN_PROGRESS);
		newEpic.setDescription("Новое описание эпика");
		newEpic.addSubTaskId(subTaskId);
		assertNotEquals(newEpic, epic);
		taskManager.updateEpic(newEpic);
		Task epicUpdated = taskManager.getEpic(epicId);
		assertNotNull(epicUpdated);
		assertEquals(newEpic, epicUpdated, "Изменённая задача не равна ожидаемой");
	}

	@Test
	public void shouldNotUpdateWrongEpic() {
		Epic epic = createNewEpic(1);
		int epicId = taskManager.addNewEpic(epic);
		Epic wrongEpic = createNewEpic(2);
		SubTask subTask = createSubTask(1, epicId);
		int subTaskId = taskManager.addNewSubTask(subTask);
		int wrongEpicId = 1_000_000;
		assertNotEquals(wrongEpicId, epicId);
		wrongEpic.setId(wrongEpicId);
		wrongEpic.setName("Измененный эпик");
		wrongEpic.setStatus(Status.IN_PROGRESS);
		wrongEpic.setDescription("Новое описание эпика");
		wrongEpic.addSubTaskId(subTaskId);
		assertNotEquals(wrongEpic, epic);
		taskManager.updateEpic(wrongEpic);
		assertFalse(taskManager.getAllTasks().contains(wrongEpic), "Список всех эпиков содержит эпик с неверным Id");
	}

	@Test
	public void shouldNotUpdateEmptyEpicList() {
		Epic epic = createNewEpic(1);
		epic.setStatus(Status.DONE);
		taskManager.updateEpic(epic);
		assertTrue(taskManager.getAllEpics().isEmpty(), "Список эпиков не пустой");
	}

	@Test
	public void shouldDeleteTask() {
		Task task = createNewTask(1);
		int taskId = taskManager.addNewTask(task);
		taskManager.deleteTask(taskId);
		assertThrows(NullPointerException.class, () -> taskManager.getTask(taskId));
		assertTrue(taskManager.getAllTasks().isEmpty(), "Список всех задач не пустой после удаления единственной задачи");
	}

	@Test
	public void shouldNotDeleteTaskWithWrongId() {
		Task task = createNewTask(1);
		int taskId = taskManager.addNewTask(task);
		int wrongTaskId = 1_000_000;
		assertNotEquals(wrongTaskId, taskId);
		taskManager.deleteTask(wrongTaskId);
		assertEquals(1, taskManager.getAllTasks().size(), "Размер списка задач изменился после удаления задачи с неправильным id");
	}

	@Test
	public void shouldNotInvokeExceptionForDeleteTaskFromEmptyList() {
		int taskId = 1;
		assertTrue(taskManager.getAllTasks().isEmpty());
		assertDoesNotThrow(() -> taskManager.deleteTask(taskId));
	}

	@Test
	public void shouldDeleteEpic() {
		Epic epic = createNewEpic(1);
		int epicId = taskManager.addNewEpic(epic);
		taskManager.deleteEpic(epicId);
		assertThrows(NullPointerException.class, () -> taskManager.getEpic(epicId));
		assertTrue(taskManager.getAllEpics().isEmpty(), "Список всех эпиков не пустой после удаления единственного эпика");
	}

	@Test
	public void shouldDeleteEpicWithSubTask() {
		Epic epic = createNewEpic(1);
		int epicId = taskManager.addNewEpic(epic);
		SubTask subTask = createSubTask(1, epicId);
		int subTaskId = taskManager.addNewSubTask(subTask);
		subTask.setId(subTaskId);
		assertTrue(taskManager.getSubTasksByEpic(epicId).contains(subTask));
		taskManager.deleteEpic(epicId);
		assertThrows(NullPointerException.class, () -> taskManager.getEpic(epicId));
		assertThrows(NullPointerException.class, () -> taskManager.getSubTask(subTaskId));
		assertTrue(taskManager.getAllEpics().isEmpty(), "Список всех эпиков не пустой после удаления единственного эпика");
		assertTrue(taskManager.getAllSubTasks().isEmpty(), "Список всех подзадач не пустой после удаления единственной подзадачи эпика");
	}


	@Test
	public void shouldNotDeleteEpicWithWrongId() {
		Epic epic = createNewEpic(1);
		int epicId = taskManager.addNewEpic(epic);
		int wrongEpicId = 1_000_000;
		assertNotEquals(wrongEpicId, epicId);
		taskManager.deleteEpic(wrongEpicId);
		assertEquals(1, taskManager.getAllEpics().size(), "Размер списка эпиков изменился после удаления эпика с неправильным id");
	}

	@Test
	public void shouldNotInvokeExceptionForDeleteEpicFromEmptyList() {
		int epicId = 1;
		assertTrue(taskManager.getAllEpics().isEmpty());
		assertDoesNotThrow(() -> taskManager.deleteEpic(epicId));
	}


	@Test
	public void shouldDeleteSubTask() {
		SubTask subTask = createSubTask(1, 1);
		int subTaskId = taskManager.addNewSubTask(subTask);
		taskManager.deleteSubTask(subTaskId);
		assertThrows(NullPointerException.class, () -> taskManager.getSubTask(subTaskId));
		assertTrue(taskManager.getAllSubTasks().isEmpty(), "Список подзадач не пустой после удаления единственной подзадачи");
	}

	@Test
	public void shouldDeleteSubTaskFromEpic() {
		Epic epic = createNewEpic(1);
		int epicId = taskManager.addNewEpic(epic);
		SubTask subTask = createSubTask(1, epicId);
		int subTaskId = taskManager.addNewSubTask(subTask);
		subTask.setId(subTaskId);
		assertTrue(taskManager.getSubTasksByEpic(epicId).contains(subTask));
		taskManager.deleteSubTask(subTaskId);
		assertTrue(taskManager.getSubTasksByEpic(epicId).isEmpty(), "Список подзадач для эпика не пустой после удаления единственной подзадачи");
		assertThrows(NullPointerException.class, () -> taskManager.getSubTask(subTaskId));
		assertTrue(taskManager.getAllSubTasks().isEmpty(), "Список всех подзадач не пустой после удаления единственной подзадачи эпика");
	}

	@Test
	public void shouldNotDeleteSubTaskWithWrongId() {
		SubTask subTask = createSubTask(1, 1);
		int subTaskId = taskManager.addNewSubTask(subTask);
		int wrongSubTaskId = 1_000_000;
		assertNotEquals(wrongSubTaskId, subTaskId);
		taskManager.deleteSubTask(wrongSubTaskId);
		assertEquals(1, taskManager.getAllSubTasks().size(), "Размер списка подзадач изменился после удаления подзадачи с неправильным id");
	}

	@Test
	public void shouldNotInvokeExceptionForDeleteSubTaskFromEmptyList() {
		int subTaskId = 1;
		assertTrue(taskManager.getAllSubTasks().isEmpty());
		assertDoesNotThrow(() -> taskManager.deleteSubTask(subTaskId));
	}

	@Test
	public void shouldDeleteAllTasks() {
		Task task = createNewTask(1);
		taskManager.addNewTask(task);
		taskManager.deleteAllTasks();
		assertTrue(taskManager.getAllTasks().isEmpty(), "Список задач не пустой после удаления всех задач");
	}

	@Test
	public void shouldDeleteAllTasksWithEmptyTaskList() {
		assertDoesNotThrow(() -> taskManager.deleteAllTasks());
		assertTrue(taskManager.getAllTasks().isEmpty(), "Список задач не пустой после удаления всех задач");
	}

	@Test
	public void shouldDeleteAllEpics() {
		Epic epic = createNewEpic(1);
		taskManager.addNewEpic(epic);
		taskManager.deleteAllEpics();
		assertTrue(taskManager.getAllEpics().isEmpty(), "Список эпиков не пустой после удаления всех эпиков");
	}

	@Test
	public void shouldDeleteAllEpicsWithEmptyEpicList() {
		assertDoesNotThrow(() -> taskManager.deleteAllEpics());
		assertTrue(taskManager.getAllEpics().isEmpty(), "Список эпиков не пустой после удаления всех эпиков");
	}

	@Test
	public void shouldDeleteAllSubTasks() {
		SubTask subTask = createSubTask(1, 1);
		taskManager.addNewSubTask(subTask);
		taskManager.deleteAllSubTasks();
		assertTrue(taskManager.getAllSubTasks().isEmpty(), "Список подзадач не пустой после удаления всех подзадач");
	}

	@Test
	public void shouldDeleteAllSubTasksWithEmptySubTaskList() {
		assertDoesNotThrow(() -> taskManager.deleteAllSubTasks());
		assertTrue(taskManager.getAllSubTasks().isEmpty(), "Список подзадач не пустой после удаления всех подзадач");
	}
}