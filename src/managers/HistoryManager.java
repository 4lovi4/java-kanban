package managers;

import java.util.List;
import tasks.Task;

public interface HistoryManager {
	void addTask(Task task);
	void remove(int id);
	List<Task> getHistory();
}
