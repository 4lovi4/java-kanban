package manager;

import java.util.ArrayList;
import models.Epic;
import models.SubTask;
import models.Task;

public interface TaskManager {
	int getTaskIdCounter();

	ArrayList<Task> getAllTasksList();

	ArrayList<Epic> getAllEpicsList();

	ArrayList<SubTask> getAllSubTasksList();

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
}
