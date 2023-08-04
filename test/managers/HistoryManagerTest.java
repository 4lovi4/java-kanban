package managers;

import managers.impl.InMemoryHistoryManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {
	private InMemoryHistoryManager manager;

	@BeforeEach
	public void prepareTest() {
		manager = new InMemoryHistoryManager();
	}

	@Test
	public void shouldAddTaskToHistory() {
		Task task = new Task(100, "Задача в Истории", "NEW", "Эту задачу добавим в историю");
		manager.addTask(task);
		assertEquals(1, manager.getHistory().size(),
				"Размер истории задач не равен 1 после добавления единственной задачи");
		assertEquals(task, manager.getHistory().get(0), "Задача из истории не равна переданной для сохранения");
	}

	@Test
	public void shouldRenewAddedTaskWithExistedIdToHistory() {
		Task task1 = new Task(100, "Первая задача", "NEW", "Эту задачу добавим в историю");
		Task task2 = new Task(100, "Вторая задача", "DONE", "Эту задачу добавим в историю");
		manager.addTask(task1);
		manager.addTask(task2);
		assertEquals(1, manager.getHistory().size(), "Размер истории задач не равен 1");
		assertEquals(task2, manager.getHistory().get(0), "Задача из истории не равна переданной для сохранения");
	}

	@Test
	public void shouldAddEpicWithoutSubtasksToHistory() {
		Epic epic = new Epic(100, "Эпик в Истории", "NEW", "Этот эпик добавим в историю");
		manager.addTask(epic);
		assertEquals(1, manager.getHistory().size(),
				"Размер истории задач не равен 1 после добавления единственного эпика");
		assertEquals(epic, manager.getHistory().get(0), "Эпик из истории не равен переданному для сохранения");
	}

	@Test
	public void shouldAddEpicWithSubtasksToHistory() {
		Epic epic = new Epic(100, "Эпик в Истории", "NEW", "Этот эпик добавим в историю");
		epic.addSubTaskId(1000);
		manager.addTask(epic);
		assertEquals(1, manager.getHistory().size(),
				"Размер истории задач не равен 1 после добавления единственного эпика");
		assertEquals(epic, manager.getHistory().get(0), "Эпик из истории не равен переданному для сохранения");
	}

	@Test
	public void shouldAddSubTaskToHistory() {
		SubTask subTask = new SubTask(1000, "Подзадача в Истории", "NEW", "Эту подзадачу добавим в историю", 100);
		manager.addTask(subTask);
		assertEquals(1, manager.getHistory().size(),
				"Размер истории задач не равен 1 после добавления единственной подзадачи");
		assertEquals(subTask, manager.getHistory().get(0), "Подзадача из истории не равна переданной для сохранения");
	}

	@Test
	public void shouldRemoveTaskFromHistory() {
		Task task = new Task(100, "Задача в Истории", "NEW", "Эту задачу добавим в историю");
		manager.addTask(task);
		int taskId = task.getId();
		manager.remove(taskId);
		assertTrue(manager.getHistory().isEmpty(),
				"История задач не пустая после удаления единственной задачи");
	}

	@Test
	public void shouldNotThrowExceptionOnRemoveWrongTaskIdFromHistory() {
		Task task = new Task(100, "Задача в Истории", "NEW", "Эту задачу добавим в историю");
		manager.addTask(task);
		int taskId = task.getId();
		int wrongTaskId = 1000;
		assertNotEquals(wrongTaskId, taskId);
		assertDoesNotThrow(() -> manager.remove(wrongTaskId));
		assertEquals(1, manager.getHistory().size(), "Размер истории задач изменился после удаления");
	}

	@Test
	public void shouldNotThrowExceptionOnRemoveFromEmptyHistory() {
		int taskId = 1000;
		assertTrue(manager.getHistory().isEmpty(), "История задач не пустая");
		assertDoesNotThrow(() -> manager.remove(taskId));
	}

	@Test
	public void shouldRemoveEpicFromHistory() {
		Epic epic = new Epic(100, "Эпик в Истории", "NEW", "Этот эпик добавим в историю");
		epic.addSubTaskId(1000);
		manager.addTask(epic);
		int epicId = epic.getId();
		manager.remove(epicId);
		assertTrue(manager.getHistory().isEmpty(),
				"История задач не пустая после удаления единственного эпика");
	}

	@Test
	public void shouldRemoveSubTaskFromHistory() {
		SubTask subTask = new SubTask(1000, "Подзадача в Истории", "NEW", "Эту подзадачу добавим в историю", 100);
		manager.addTask(subTask);
		int subTaskId = subTask.getId();
		manager.remove(subTaskId);
		assertTrue(manager.getHistory().isEmpty(),
				"История задач не пустая после удаления единственной подзадачи");
	}

	@Test
	public void shouldReturnHistory() {
		Task task = new Task(10, "Задача в Истории", "NEW", "Эту задачу добавим в историю");
		Epic epic = new Epic(100, "Эпик в Истории", "NEW", "Этот эпик добавим в историю");
		epic.addSubTaskId(1000);
		SubTask subTask = new SubTask(1000, "Подзадача в Истории", "NEW", "Эту подзадачу добавим в историю", 100);
		manager.addTask(task);
		manager.addTask(subTask);
		manager.addTask(epic);
		List<Task> history = manager.getHistory();
		assertEquals(3, history.size(), "Размер полученной истории не равен количеству добавленных задач");
	}

	@Test
	public void shouldReturnEmptyHistory() {
		assertTrue(manager.getHistory().isEmpty(), "История не пустая");
	}
}
