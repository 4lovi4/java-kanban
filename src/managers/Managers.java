package managers;

import java.io.File;

public class Managers {
	public static TaskManager getDefault() {
		return new InMemoryTaskManager();
	}

	public static TaskManager getFileBackedTaskManager() {
		File taskFile = new File("server_tasks.csv");
		return new FileBackedTasksManager(taskFile);
	}

	public static HistoryManager getDefaultHistory() {
		return new InMemoryHistoryManager();
	}
}
