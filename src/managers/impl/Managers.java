package managers.impl;

import managers.HistoryManager;
import managers.TaskManager;
import managers.impl.FileBackedTasksManager;
import managers.impl.HttpTaskManager;
import managers.impl.InMemoryHistoryManager;

import java.io.File;

public class Managers {

	private static final String KV_SERVER_URI = "http://localhost:8078";
	private static final String TASKS_FILENAME = "server_tasks.csv";


	public static TaskManager getDefault() {
		return new HttpTaskManager(KV_SERVER_URI);
	}

	public static TaskManager getFileBackedTaskManager() {
		File taskFile = new File(TASKS_FILENAME);
		return new FileBackedTasksManager(taskFile);
	}

	public static HistoryManager getDefaultHistory() {
		return new InMemoryHistoryManager();
	}
}
