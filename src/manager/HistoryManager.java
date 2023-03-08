package manager;

import java.util.List;
import models.Task;

public interface HistoryManager {
	void addTask(Task task);
	List<Task> getHistory();


}
