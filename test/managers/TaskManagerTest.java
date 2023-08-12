package managers;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;


abstract class TaskManagerTest<T extends TaskManager> {

	abstract T getTaskManager();

	T taskManager;

	@BeforeEach
	public void setUp() throws IOException {
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
	@DisplayName("Эпик без подзадач имеет статус NEW")
	public void shouldReturnEpicNewStatusForEmptySubTasks() {
		Epic epic = createNewEpic(1);
		assertTrue(epic.getSubTasksId().isEmpty(), "Список id подзадач не пустой");
		assertEquals(Status.NEW, epic.getStatus(), String.format("Статус эпика с пустым списком подзадач не равен %s", Status.NEW));
	}

	@Test
	@DisplayName("Эпик со всеми подзадачами в статусе NEW сам имеет статус NEW")
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
	@DisplayName("Эпик со всеми подзадачами в статусе DONE сам имеет статус DONE")
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
	@DisplayName("Эпик с подзадачами в статусе DONE и NEW сам имеет статус IN_PROGRESS")
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
	@DisplayName("Эпик со всеми подзадачами в статусе IN_PROGRESS сам имеет статус IN_PROGRESS")
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
	@DisplayName("Эпик с подзадачами в статусе IN_PROGRESS и NEW сам имеет статус IN_PROGRESS")
	public void shouldReturnEpicInProgressStatusForNewAndInProgressSubTasks() {
		Epic epic = createNewEpic(1);
		int epicId = taskManager.addNewEpic(epic);
		SubTask subTask1 = createSubTask(0, epicId);
		assertEquals(Status.NEW, subTask1.getStatus());
		SubTask subTask2 = createSubTask(1, epicId);
		subTask2.setStatus(Status.IN_PROGRESS);
		taskManager.addNewSubTask(subTask1);
		taskManager.addNewSubTask(subTask2);
		assertEquals(Status.IN_PROGRESS, epic.getStatus(), String.format("Статус эпика со всеми подзадачами в статусе NEW и IN_PROGRESS не равен %s", Status.IN_PROGRESS));
	}

	@Test
	@DisplayName("Получение списка всех задач типа Task")
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
	@DisplayName("Получение пустого списка всех задач типа Task")
	public void shouldGetAllTasksEmptyList() {
		List<Task> allTasks = taskManager.getAllTasks();
		assertNotNull(allTasks);
		assertTrue(allTasks.isEmpty(), "Список всех задач не пустой");
	}

	@Test
	@DisplayName("Получение списка всех задач типа Epic")
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
	@DisplayName("Получение пустого списка всех задач типа Epic")
	public void shouldGetAllEpicsEmptyList() {
		List<Epic> allEpics = taskManager.getAllEpics();
		assertNotNull(allEpics);
		assertTrue(allEpics.isEmpty(), "Список всех эпиков не пустой");
	}

	@Test
	@DisplayName("Получение списка всех подзадач типа SubTask")
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
	@DisplayName("Получение пустого списка подзадач типа SubTask")
	public void shouldGetAllSubTasksEmptyList() {
		List<SubTask> allSubTasks = taskManager.getAllSubTasks();
		assertNotNull(allSubTasks);
		assertTrue(allSubTasks.isEmpty(), "Список всех подзадач не пустой");
	}

	@Test
	@DisplayName("Добавление и получение задачи типа Task по id")
	public void shouldAddAndGetNewTask() {
		Task task = createNewTask(1);
		int taskId = taskManager.addNewTask(task);
		task.setId(taskId);
		Task taskAdded = taskManager.getTask(taskId);
		assertNotNull(taskAdded);
		assertEquals(task, taskAdded, "Добавленная задача не равна ожидаемой");
	}

	@Test
	@DisplayName("Добавление и получение задачи типа Epic по id")
	public void shouldAddAndGetNewEpic() {
		Epic epic = createNewEpic(1);
		int epicId = taskManager.addNewEpic(epic);
		epic.setId(epicId);
		Epic epicAdded = taskManager.getEpic(epicId);
		assertNotNull(epicAdded);
		assertEquals(epic, epicAdded, "Добавленный эпик не равен ожидаемому");
	}

	@Test
	@DisplayName("Добавление и получение подзадачи типа SubTask по id")
	public void shouldAddAndGetNewSubTask() {
		SubTask subTask = createSubTask(1, 0);
		int subTaskId = taskManager.addNewSubTask(subTask);
		subTask.setId(subTaskId);
		SubTask subTaskAdded = taskManager.getSubTask(subTaskId);
		assertNotNull(subTaskAdded);
		assertEquals(subTask, subTaskAdded, "Добавленная задача не равна ожидаемой");
	}

	@Test
	@DisplayName("Получение подзадачи SubTask по id Epic")
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
	@DisplayName("Получение null при запросе Task с неправильным id")
	public void ShouldGetNullOnWrongTaskId() {
		Task task = createNewTask(1);
		int taskId = taskManager.addNewTask(task);
		int wrongTaskId = 1_000_000;
		assertNotEquals(wrongTaskId, taskId);
		assertNull(taskManager.getTask(wrongTaskId));
	}

	@Test
	@DisplayName("Получение null при запросе Epic с неправильным id")
	public void ShouldGetNullOnWrongEpicId() {
		Epic epic = createNewEpic(1);
		int epicId = taskManager.addNewEpic(epic);
		int wrongEpicId = 1_000_000;
		assertNotEquals(wrongEpicId, epicId);
		assertNull(taskManager.getEpic(wrongEpicId));
	}

	@Test
	@DisplayName("Получение null при запросе SubTask с неправильным id")
	public void ShouldGetNullOnWrongSubTaskId() {
		SubTask subTask = createSubTask(1, 0);
		int subTaskId = taskManager.addNewSubTask(subTask);
		int wrongSubTaskId = 1_000_000;
		assertNotEquals(wrongSubTaskId, subTaskId);
		assertNull(taskManager.getSubTask(wrongSubTaskId));
	}

	@Test
	@DisplayName("Получение null при пустом списке задач Task")
	public void ShouldGetNullOnEmptyTasksList() {
		int taskId = 1;
		assertTrue(taskManager.getAllTasks().isEmpty());
		assertNull(taskManager.getTask(taskId));
	}

	@Test
	@DisplayName("Получение null при пустом списке задач Epic")
	public void ShouldGetNullOnEmptyEpicsList() {
		int epicId = 1;
		assertTrue(taskManager.getAllEpics().isEmpty());
		assertNull(taskManager.getEpic(epicId));
	}

	@Test
	@DisplayName("Получение null при пустом списке подзадач subTask")
	public void ShouldGetNullOnEmptySubTasksList() {
		int subTaskId = 1;
		assertTrue(taskManager.getAllSubTasks().isEmpty());
		assertNull(taskManager.getSubTask(subTaskId));
	}


	@Test
	@DisplayName("Обновление задачи Task")
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
	@DisplayName("Не обновляется задача Task с неправильным id")
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
	@DisplayName("Не обновляется задача Task при пустом списке задач")
	public void shouldNotUpdateEmptyTasksList() {
		Task task = createNewTask(1);
		task.setStatus(Status.DONE);
		taskManager.updateTask(task);
		assertTrue(taskManager.getAllTasks().isEmpty(), "Список задач не пустой");
	}

	@Test
	@DisplayName("Обновление подзадачи SubTask")
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
	@DisplayName("Не обновляется подзадача SubTask с неправильным id")
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
	@DisplayName("Не обновляется подзадача SubTask при пустом списке задач")
	public void shouldNotUpdateEmptySubTasksList() {
		SubTask subTask = createSubTask(1, 1);
		subTask.setStatus(Status.DONE);
		taskManager.updateSubTask(subTask);
		assertTrue(taskManager.getAllSubTasks().isEmpty(), "Список подзадач не пустой");
	}

	@Test
	@DisplayName("Обновление задачи Epic")
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
	@DisplayName("Не обновляется задача Epic с неправильным id")
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
	@DisplayName("Не обновляется задача Epic при пустом списке задач")
	public void shouldNotUpdateEmptyEpicList() {
		Epic epic = createNewEpic(1);
		epic.setStatus(Status.DONE);
		taskManager.updateEpic(epic);
		assertTrue(taskManager.getAllEpics().isEmpty(), "Список эпиков не пустой");
	}

	@Test
	@DisplayName("Удаление задачи Task")
	public void shouldDeleteTask() {
		Task task = createNewTask(1);
		int taskId = taskManager.addNewTask(task);
		taskManager.deleteTask(taskId);
		assertNull(taskManager.getTask(taskId));
		assertTrue(taskManager.getAllTasks().isEmpty(), "Список всех задач не пустой после удаления единственной задачи");
	}

	@Test
	@DisplayName("Не удаляется задача Task с неправильным id")
	public void shouldNotDeleteTaskWithWrongId() {
		Task task = createNewTask(1);
		int taskId = taskManager.addNewTask(task);
		int wrongTaskId = 1_000_000;
		assertNotEquals(wrongTaskId, taskId);
		taskManager.deleteTask(wrongTaskId);
		assertEquals(1, taskManager.getAllTasks().size(), "Размер списка задач изменился после удаления задачи с неправильным id");
	}

	@Test
	@DisplayName("Не выбрасывается исключение при удалении задачи Task из пустого списка")
	public void shouldNotInvokeExceptionForDeleteTaskFromEmptyList() {
		int taskId = 1;
		assertTrue(taskManager.getAllTasks().isEmpty());
		assertDoesNotThrow(() -> taskManager.deleteTask(taskId));
	}

	@Test
	@DisplayName("Удаление задачи Epic")
	public void shouldDeleteEpic() {
		Epic epic = createNewEpic(1);
		int epicId = taskManager.addNewEpic(epic);
		taskManager.deleteEpic(epicId);
		assertNull(taskManager.getEpic(epicId));
		assertTrue(taskManager.getAllEpics().isEmpty(), "Список всех эпиков не пустой после удаления единственного эпика");
	}

	@Test
	@DisplayName("Удаление задачи Epic с подзадачами")
	public void shouldDeleteEpicWithSubTask() {
		Epic epic = createNewEpic(1);
		int epicId = taskManager.addNewEpic(epic);
		SubTask subTask = createSubTask(1, epicId);
		int subTaskId = taskManager.addNewSubTask(subTask);
		subTask.setId(subTaskId);
		assertTrue(taskManager.getSubTasksByEpic(epicId).contains(subTask));
		taskManager.deleteEpic(epicId);
		assertNull(taskManager.getEpic(epicId));
		assertNull(taskManager.getSubTask(subTaskId));
		assertTrue(taskManager.getAllEpics().isEmpty(), "Список всех эпиков не пустой после удаления единственного эпика");
		assertTrue(taskManager.getAllSubTasks().isEmpty(), "Список всех подзадач не пустой после удаления единственной подзадачи эпика");
	}


	@Test
	@DisplayName("Не удаляется задача Epic с неправильным id")
	public void shouldNotDeleteEpicWithWrongId() {
		Epic epic = createNewEpic(1);
		int epicId = taskManager.addNewEpic(epic);
		int wrongEpicId = 1_000_000;
		assertNotEquals(wrongEpicId, epicId);
		taskManager.deleteEpic(wrongEpicId);
		assertEquals(1, taskManager.getAllEpics().size(), "Размер списка эпиков изменился после удаления эпика с неправильным id");
	}

	@Test
	@DisplayName("Не выбрасывается исключение при удалении задачи Epic из пустого списка")
	public void shouldNotInvokeExceptionForDeleteEpicFromEmptyList() {
		int epicId = 1;
		assertTrue(taskManager.getAllEpics().isEmpty());
		assertDoesNotThrow(() -> taskManager.deleteEpic(epicId));
	}


	@Test
	@DisplayName("Удаление подзадачи SubTask")
	public void shouldDeleteSubTask() {
		SubTask subTask = createSubTask(1, 1);
		int subTaskId = taskManager.addNewSubTask(subTask);
		taskManager.deleteSubTask(subTaskId);
		assertNull(taskManager.getSubTask(subTaskId));
		assertTrue(taskManager.getAllSubTasks().isEmpty(), "Список подзадач не пустой после удаления единственной подзадачи");
	}

	@Test
	@DisplayName("Удаление подзадачи SubTask из эпика")
	public void shouldDeleteSubTaskFromEpic() {
		Epic epic = createNewEpic(1);
		int epicId = taskManager.addNewEpic(epic);
		SubTask subTask = createSubTask(1, epicId);
		int subTaskId = taskManager.addNewSubTask(subTask);
		subTask.setId(subTaskId);
		assertTrue(taskManager.getSubTasksByEpic(epicId).contains(subTask));
		taskManager.deleteSubTask(subTaskId);
		assertTrue(taskManager.getSubTasksByEpic(epicId).isEmpty(), "Список подзадач для эпика не пустой после удаления единственной подзадачи");
		assertNull(taskManager.getSubTask(subTaskId));
		assertTrue(taskManager.getAllSubTasks().isEmpty(), "Список всех подзадач не пустой после удаления единственной подзадачи эпика");
	}

	@Test
	@DisplayName("Не удаляется подзадача SubTask с неправильным id")
	public void shouldNotDeleteSubTaskWithWrongId() {
		SubTask subTask = createSubTask(1, 1);
		int subTaskId = taskManager.addNewSubTask(subTask);
		int wrongSubTaskId = 1_000_000;
		assertNotEquals(wrongSubTaskId, subTaskId);
		taskManager.deleteSubTask(wrongSubTaskId);
		assertEquals(1, taskManager.getAllSubTasks().size(), "Размер списка подзадач изменился после удаления подзадачи с неправильным id");
	}

	@Test
	@DisplayName("Не выбрасывается исключение при удалении подзадачи SubTask из пустого списка")
	public void shouldNotInvokeExceptionForDeleteSubTaskFromEmptyList() {
		int subTaskId = 1;
		assertTrue(taskManager.getAllSubTasks().isEmpty());
		assertDoesNotThrow(() -> taskManager.deleteSubTask(subTaskId));
	}

	@Test
	@DisplayName("Удаление всех задач Task")
	public void shouldDeleteAllTasks() {
		Task task = createNewTask(1);
		taskManager.addNewTask(task);
		taskManager.deleteAllTasks();
		assertTrue(taskManager.getAllTasks().isEmpty(), "Список задач не пустой после удаления всех задач");
	}

	@Test
	@DisplayName("Удаление всех задач Task при пустом списке")
	public void shouldDeleteAllTasksWithEmptyTaskList() {
		assertDoesNotThrow(() -> taskManager.deleteAllTasks());
		assertTrue(taskManager.getAllTasks().isEmpty(), "Список задач не пустой после удаления всех задач");
	}

	@Test
	@DisplayName("Удаление всех задач Epic")
	public void shouldDeleteAllEpics() {
		Epic epic = createNewEpic(1);
		taskManager.addNewEpic(epic);
		taskManager.deleteAllEpics();
		assertTrue(taskManager.getAllEpics().isEmpty(), "Список эпиков не пустой после удаления всех эпиков");
	}

	@Test
	@DisplayName("Удаление всех задач Epic при пустом списке")
	public void shouldDeleteAllEpicsWithEmptyEpicList() {
		assertDoesNotThrow(() -> taskManager.deleteAllEpics());
		assertTrue(taskManager.getAllEpics().isEmpty(), "Список эпиков не пустой после удаления всех эпиков");
	}

	@Test
	@DisplayName("Удаление всех подзадач SubTask")
	public void shouldDeleteAllSubTasks() {
		SubTask subTask = createSubTask(1, 1);
		taskManager.addNewSubTask(subTask);
		taskManager.deleteAllSubTasks();
		assertTrue(taskManager.getAllSubTasks().isEmpty(), "Список подзадач не пустой после удаления всех подзадач");
	}

	@Test
	@DisplayName("Удаление всех подзадач SubTask при пустом списке")
	public void shouldDeleteAllSubTasksWithEmptySubTaskList() {
		assertDoesNotThrow(() -> taskManager.deleteAllSubTasks());
		assertTrue(taskManager.getAllSubTasks().isEmpty(), "Список подзадач не пустой после удаления всех подзадач");
	}

	@Test
	@DisplayName("Установка времени начала задачи")
	public void shouldSetAndReturnTaskStartTime() {
		Task task = createNewTask(1);
		LocalDateTime startTimeToSet = (LocalDateTime.of(2023, 6, 24, 12, 0, 0));
		assertDoesNotThrow(() -> task.setStartTime(startTimeToSet));
		LocalDateTime startTimeActual = task.getStartTime();
		assertNotNull(startTimeActual);
		assertEquals(startTimeToSet, startTimeActual, "Время начала задачи успешно задано");
	}

	@Test
	@DisplayName("Установка длительности задачи")
	public void shouldSetAndReturnTaskDuration() {
		Task task = createNewTask(1);
		Long durationToSet = 100L;
		assertDoesNotThrow(() -> task.setDuration(durationToSet));
		Long durationActual = task.getDuration();
		assertNotNull(durationActual);
		assertEquals(durationToSet, durationActual, "Длительность задачи в минутах успешно задано");
	}
	
	@Test
	@DisplayName("Получение времени окончания задачи")
	public void shouldReturnTaskCorrectEndTime() {
		Task task = createNewTask(1);
		LocalDateTime startTimeToSet = (LocalDateTime.of(2023, 6, 24, 12, 0, 0));
		LocalDateTime endTimeExpected = (LocalDateTime.of(2023, 6, 24, 13, 0, 0));
		Long durationToSet = 60L;
		task.setStartTime(startTimeToSet);
		task.setDuration(durationToSet);
		LocalDateTime endTimeActual = task.getEndTime();
		assertNotNull(endTimeActual);
		assertEquals(endTimeExpected, endTimeActual, "Время окончания задачи не равно времени старта + длительность задачи");
	}

	@Test
	@DisplayName("Нельзя получить время окончания задачи если не установлено время начала")
	public void shouldReturnNullEndTimeIfStartTimeNotSet() {
		Task task = createNewTask(1);
		assertDoesNotThrow(task::getEndTime);
		assertNull(task.getEndTime(), "Время окончания задачи не Null");
	}

	@Test
	@DisplayName("Нельзя получить время окончания задачи если не установлена длительность")
	public void shouldReturnNullEndTimeIfDurationNotSet() {
		Task task = createNewTask(1);
		LocalDateTime startTimeToSet = (LocalDateTime.of(2023, 6, 24, 12, 0, 0));
		task.setStartTime(startTimeToSet);
		assertDoesNotThrow(task::getEndTime);
		assertNull(task.getEndTime(), "Время окончания задачи не Null");
	}

	@Test
	@DisplayName("Получение времени окончания подзадачи SubTask")
	public void shouldReturnSubTaskCorrectEndTime() {
		SubTask subTask = createSubTask(1, 10);
		LocalDateTime startTimeToSet = (LocalDateTime.of(2023, 6, 24, 12, 0, 0));
		LocalDateTime endTimeExpected = (LocalDateTime.of(2023, 6, 24, 13, 0, 0));
		Long durationToSet = 60L;
		subTask.setStartTime(startTimeToSet);
		subTask.setDuration(durationToSet);
		LocalDateTime endTimeActual = subTask.getEndTime();
		assertNotNull(endTimeActual);
		assertEquals(endTimeExpected, endTimeActual, "Время окончания подзадачи не равно времени старта + длительность подзадачи");
	}

	@Test
	@DisplayName("Вычисление времени окончания эпика Epic по времени подзадач")
	public void shouldCalculateEpicEndTimeAndDuration() {
		Epic epic = createNewEpic(1);
		int epicId = taskManager.addNewEpic(epic);
		SubTask subTask1 = createSubTask(1, epicId);
		SubTask subTask2 = createSubTask(2, epicId);
		LocalDateTime startTime1 = LocalDateTime.of(2023, 6, 24, 9, 30);
		Long duration1 = 60L;
		LocalDateTime startTime2 = LocalDateTime.of(2023, 6, 24, 11, 40);
		Long duration2 = 120L;
		subTask1.setStartTime(startTime1);
		subTask1.setDuration(duration1);
		subTask2.setStartTime(startTime2);
		subTask2.setDuration(duration2);
		LocalDateTime endTime2 = subTask2.getEndTime();
		taskManager.addNewSubTask(subTask1);
		taskManager.addNewSubTask(subTask2);
		LocalDateTime epicStartTime = epic.getStartTime();
		Long epicDuration = epic.getDuration();
		LocalDateTime epicEndTime = epic.getEndTime();
		assertEquals(duration1 + duration2, epicDuration, "Продолжительность эпика не равна сумме длительности подзадач");
		assertEquals(startTime1, epicStartTime, "Время начала эпика не совпадает с самым ранним временем начала подзадач");
		assertEquals(endTime2, epicEndTime, "Время окончания эпика не совпадает с самым поздним временем окончания подзадач");
	}

	@Test
	@DisplayName("Получениен списка задач приоритезированного по времени")
	public void shouldGetPrioritizedByStartTimeTasks() {
		Task taskWithTime = createNewTask(1);
		LocalDateTime startTime1 = LocalDateTime.of(2023, 6, 24, 12, 0);
		Long duration1 = 60L;
		taskWithTime.setStartTime(startTime1);
		taskWithTime.setDuration(duration1);
		int taskIdWithTime = taskManager.addNewTask(taskWithTime);
		taskWithTime.setId(taskIdWithTime);
		Task taskWithoutTime = createNewTask(2);
		int taskIdWithoutTime = taskManager.addNewTask(taskWithoutTime);
		taskWithoutTime.setId(taskIdWithoutTime);
		Epic epic = createNewEpic(1);
		int epicId = taskManager.addNewEpic(epic);
		epic.setId(epicId);
		SubTask subTask = createSubTask(1, epicId);
		LocalDateTime startTime2 = LocalDateTime.of(2023, 6, 24, 13, 0);
		Long duration2 = 120L;
		subTask.setStartTime(startTime2);
		subTask.setDuration(duration2);
		int subTaskId = taskManager.addNewSubTask(subTask);
		subTask.setId(subTaskId);
		ArrayList<Task> expectedPrioritizedTasks = new ArrayList<>();
		expectedPrioritizedTasks.add(taskWithTime);
		expectedPrioritizedTasks.add(epic);
		expectedPrioritizedTasks.add(subTask);
		expectedPrioritizedTasks.add(taskWithoutTime);
		ArrayList<Task> actualPrioritizedTasks = taskManager.getPrioritizedTasks();
		assertEquals(expectedPrioritizedTasks, actualPrioritizedTasks,
				"Список задач всех типов приоритезированный по времени старта не соответствует ожидаемому");
	}
 }
