package managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;

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
				"Размер списка задач в истории не равен 1 после добавления единственной задачи");
		assertEquals(task, manager.getHistory().get(0), "Задача из истории не равна переданной для сохранения");
	}
}
