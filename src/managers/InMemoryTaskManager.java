package managers;

import tasks.Epic;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {
	private int taskIdCounter;
	private final HashMap<Integer, Task> tasks;
	private final HashMap<Integer, Epic> epics;
	private final HashMap<Integer, SubTask> subTasks;
	protected final InMemoryHistoryManager historyManager;

	public InMemoryTaskManager() {
		taskIdCounter = 0;
		tasks = new HashMap<>();
		epics = new HashMap<>();
		subTasks = new HashMap<>();
		historyManager = (InMemoryHistoryManager) Managers.getDefaultHistory();
	}

	@Override
	public ArrayList<Task> getAllTasks() {
		return new ArrayList<>(tasks.values());
	}

	@Override
	public ArrayList<Epic> getAllEpics() {
		return new ArrayList<>(epics.values());
	}

	@Override
	public ArrayList<SubTask> getAllSubTasks() {
		return new ArrayList<>(subTasks.values());
	}

	@Override
	public void deleteAllTasks() {
		tasks.clear();
	}

	@Override
	public void deleteAllEpics() {
		epics.clear();
		subTasks.clear();
	}

	@Override
	public void deleteAllSubTasks() {
		for (Epic epic : epics.values()) {
			epic.getSubTasksId().clear();
			epics.put(epic.getId(), epic);
			updateEpicStatus(epic.getId());
		}
		subTasks.clear();
	}

	@Override
	public Task getTask(int id) {
		Task task = tasks.get(id);
		historyManager.addTask(task);
		return task;
	}

	@Override
	public Epic getEpic(int id) {
		Epic task = epics.get(id);
		historyManager.addTask(task);
		return task;
	}

	@Override
	public SubTask getSubTask(int id) {
		SubTask task = subTasks.get(id);
		historyManager.addTask(task);
		return task;
	}

	@Override
	public int addNewTask(Task task) {
		taskIdCounter++;
		task.setId(taskIdCounter);
		tasks.put(task.getId(), task);

		return task.getId();
	}

	@Override
	public int addNewEpic(Epic epic) {
		taskIdCounter++;
		epic.setId(taskIdCounter);
		updateEpicStatus(epic.getId());
		epics.put(epic.getId(), epic);

		return epic.getId();
	}

	@Override
	public int addNewSubTask(SubTask subTask) {
		taskIdCounter++;
		subTask.setId(taskIdCounter);
		subTasks.put(subTask.getId(), subTask);
		//Добавление сабтаски в эпик и обновление его статуса
		int epicId = subTask.getEpicId();
		Epic epic = epics.get(epicId);
		if (epic != null && !epic.getSubTasksId().contains(subTask.getId())) {
			epic.addSubTaskId(subTask.getId());
			updateEpicStatus(epic.getId());
		}
		return subTask.getId();
	}

	@Override
	public void updateTask(Task task) {
		if (tasks.containsKey(task.getId())) {
			tasks.put(task.getId(), task);
		}
	}

	@Override
	public void updateEpic(Epic epic) {
		if (epics.containsKey(epic.getId())) {
			epics.put(epic.getId(), epic);
		}
	}

	@Override
	public void updateSubTask(SubTask subTask) {
		if (subTasks.get(subTask.getId()) == null) return;
		Epic epic = epics.get(subTask.getEpicId());
		if (epic == null) return;
		subTasks.put(subTask.getId(), subTask);
		updateEpicStatus(epic.getId());
	}

	@Override
	public void deleteTask(int id) {
		tasks.remove(id);
		historyManager.remove(id);
	}

	@Override
	public void deleteEpic(int id) {
		Epic epic = epics.get(id);
		if (epic != null) {
			for (int idSubTask : epic.getSubTasksId()) {
				subTasks.remove(idSubTask);
				historyManager.remove(idSubTask);
			}
			epics.remove(id);
			historyManager.remove(id);
		}
	}

	@Override
	public void deleteSubTask(int id) {
		SubTask subTask = subTasks.get(id);
		if (subTask != null) {
			Epic epic = epics.get(subTask.getEpicId());
			if (epic != null) {
				epic.deleteSubTaskId(id);
				epics.put(epic.getId(), epic);
				updateEpicStatus(epic.getId());
			}
		}
		subTasks.remove(id);
		historyManager.remove(id);
	}

	@Override
	public ArrayList<SubTask> getSubTasksByEpic(int id) {
		ArrayList<SubTask> result = new ArrayList<>();
		Epic epic = epics.get(id);
		if (epic == null) return result;
		ArrayList<Integer> epicSubTasksId = epic.getSubTasksId();
		if (epicSubTasksId.isEmpty()) return result;
		for (Integer subTaskId : epicSubTasksId) {
			SubTask subTask = subTasks.get(subTaskId);
			result.add(subTask);
		}
		return result;
	}

	private void updateEpicStatus(int id) {
		Epic epic = epics.get(id);
		boolean isAllNew = true;
		boolean isAllDone = true;
		if (epic == null) return;
		ArrayList<SubTask> epicSubTasksList = getSubTasksByEpic(id);
		if (epicSubTasksList.isEmpty()) {
			epic.setStatus(Status.NEW);
			return;
		}
		for (SubTask subTask : epicSubTasksList) {
			if (subTask.getStatus() != Status.NEW) {
				isAllNew = false;
			}
			if (subTask.getStatus() != Status.DONE) {
				isAllDone = false;
			}
		}
		if (isAllNew) {
			epic.setStatus(Status.NEW);
		}
		else if (isAllDone) {
			epic.setStatus(Status.DONE);
		}
		else {
			epic.setStatus(Status.IN_PROGRESS);
		}
	}

	@Override
	public List<Task> getHistory() {
		return historyManager.getHistory();
	}
}
