package managers;

import java.util.ArrayList;
import java.util.List;
import tasks.Task;

public class InMemoryHistoryManager implements HistoryManager {

	private final ArrayList<Task> taskHistory;
	private int taskHistoryCounter;
	private static final int TASK_HISTORY_SIZE = 10;

	public InMemoryHistoryManager() {
		taskHistory = new ArrayList<>(TASK_HISTORY_SIZE);
		taskHistoryCounter = 0;
	}

	@Override
	public void addTask(Task task) {
		if (taskHistory.size() == TASK_HISTORY_SIZE) {
			taskHistoryCounter = 0;
		}
		taskHistory.add(taskHistoryCounter, task);
		taskHistoryCounter++;
	}

	@Override
	public void remove(int id) {
		taskHistory.remove(id);
	}

	@Override
	public List<Task> getHistory() {
		return taskHistory;
	}
}
