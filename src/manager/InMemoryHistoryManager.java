package manager;

import java.util.ArrayList;
import java.util.List;
import models.Task;
public class InMemoryHistoryManager implements HistoryManager{

	private final ArrayList<Task> taskHistoryList;
	private int taskHistoryCounter;
	private final int TASK_HISTORY_SIZE = 10;

	public InMemoryHistoryManager() {
		taskHistoryList = new ArrayList<>(TASK_HISTORY_SIZE);
		taskHistoryCounter = 0;
	}

	@Override
	public void addTask(Task task) {
		if (taskHistoryList.size() == TASK_HISTORY_SIZE) {
			taskHistoryCounter = 0;
		}
		taskHistoryList.add(taskHistoryCounter, task);
		taskHistoryCounter++;
	}

	@Override
	public List<Task> getHistory() {
		return taskHistoryList;
	}
}
