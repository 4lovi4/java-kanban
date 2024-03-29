package managers;

import java.util.ArrayList;
import java.util.List;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

public interface TaskManager {
	ArrayList<Task> getAllTasks();

	ArrayList<Epic> getAllEpics();

	ArrayList<SubTask> getAllSubTasks();

	void deleteAllTasks();

	void deleteAllEpics();

	void deleteAllSubTasks();

	Task getTask(int id);

	Epic getEpic(int id);

	SubTask getSubTask(int id);

	int addNewTask(Task task);

	int addNewEpic(Epic epic);

	int addNewSubTask(SubTask subTask);

	void updateTask(Task task);

	void updateEpic(Epic epic);

	void updateSubTask(SubTask subTask);

	void deleteTask(int id);

	void deleteEpic(int id);

	void deleteSubTask(int id);

	ArrayList<SubTask> getSubTasksByEpic(int id);

	List<Task> getHistory();

	ArrayList<Task> getPrioritizedTasks();

	boolean validateTaskTime(Task task);
}
