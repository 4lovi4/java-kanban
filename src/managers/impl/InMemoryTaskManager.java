package managers.impl;

import managers.TaskManager;
import tasks.Epic;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
	public void setTaskIdCounter(int taskIdCounter) {
		this.taskIdCounter = taskIdCounter;
	}

	private int taskIdCounter;
	protected final HashMap<Integer, Task> tasks;
	protected final HashMap<Integer, Epic> epics;
	protected final HashMap<Integer, SubTask> subTasks;
	protected final InMemoryHistoryManager historyManager;

	protected TreeSet<Task> prioritizedTasks = new TreeSet<>(new CompareStartTime());
//			Comparator.comparing(Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder()))
//			.thenComparing(Task::getId, Comparator.nullsLast(Comparator.naturalOrder())));

	static class CompareStartTime implements Comparator<Task> {

		@Override
		public int compare(Task o1, Task o2) {
			if (o1.equals(o2)) {
				return 0;
			}
			if (o1.getId() == o2.getId()) {
				return 0;
			}
			if (o1.getStartTime() == null && o2.getStartTime() == null) {
				return Integer.compare(o1.getId(), o2.getId());
			}
			if (o1.getStartTime() != null && o2.getStartTime() == null) {
				return -1;
			}
			if (o1.getStartTime() == null && o2.getStartTime() != null) {
				return 1;
			}
			if (o1.getStartTime().equals(o2.getStartTime())) {
				return Integer.compare(o1.getId(), o2.getId());
			} else {
				return o1.getStartTime().compareTo(o2.getStartTime());
			}
		}
	}

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
			updateEpicTime(epic.getId());
		}
		subTasks.clear();
	}

	@Override
	public Task getTask(int id) {
		Task task = tasks.get(id);
		if (!Objects.isNull(task)) {
			historyManager.addTask(task);
		}
		return task;
	}

	@Override
	public Epic getEpic(int id) {
		Epic task = epics.get(id);
		if (!Objects.isNull(task)) {
			historyManager.addTask(task);
		}
		return task;
	}

	@Override
	public SubTask getSubTask(int id) {
		SubTask task = subTasks.get(id);
		if (!Objects.isNull(task)) {
			historyManager.addTask(task);
		}
		return task;
	}

	@Override
	public int addNewTask(Task task) {
		if (!validateTaskTime(task)) {
			return -1;
		}
		taskIdCounter++;
		task.setId(taskIdCounter);
		tasks.put(task.getId(), task);
		if (prioritizedTasks.stream().filter(t -> t.getId() == task.getId()).findFirst().isEmpty()) {
			prioritizedTasks.add(task);
		}
		return task.getId();
	}

	@Override
	public int addNewEpic(Epic epic) {
		taskIdCounter++;
		epic.setId(taskIdCounter);
		updateEpicStatus(epic.getId());
		epics.put(epic.getId(), epic);
		updateEpicTime(epic.getId());
		if (prioritizedTasks.stream().filter(t -> t.getId() == epic.getId()).findFirst().isEmpty()) {
			prioritizedTasks.add(epic);
		}
		return epic.getId();
	}

	@Override
	public int addNewSubTask(SubTask subTask) {
		if (!validateTaskTime(subTask)) {
			return -1;
		}
		taskIdCounter++;
		subTask.setId(taskIdCounter);
		subTasks.put(subTask.getId(), subTask);
		//Добавление сабтаски в эпик и обновление его статуса
		int epicId = subTask.getEpicId();
		Epic epic = epics.get(epicId);
		if (epic != null && !epic.getSubTasksId().contains(subTask.getId())) {
			prioritizedTasks.remove(epic);
			epic.addSubTaskId(subTask.getId());
			updateEpicStatus(epic.getId());
			updateEpicTime(epic.getId());
			prioritizedTasks.add(epic);
		}
		prioritizedTasks.add(subTask);
		return subTask.getId();
	}

	@Override
	public void updateTask(Task task) {
		if (tasks.containsKey(task.getId())) {
			tasks.put(task.getId(), task);
		}
		Optional<Task> taskInPrioritized = prioritizedTasks.stream().filter(t -> t.getId() == task.getId()).findFirst();
		if (taskInPrioritized.isPresent()) {
			prioritizedTasks.remove(taskInPrioritized.get());
			prioritizedTasks.add(task);
		}
	}

	@Override
	public void updateEpic(Epic epic) {
		if (epics.containsKey(epic.getId())) {
			epics.put(epic.getId(), epic);
		}
		Optional<Task> taskInPrioritized = prioritizedTasks.stream().filter(t -> t.getId() == epic.getId()).findFirst();
		if (taskInPrioritized.isPresent()) {
			prioritizedTasks.remove(taskInPrioritized.get());
			prioritizedTasks.add(epic);
		}
	}

	@Override
	public void updateSubTask(SubTask subTask) {
		if (subTasks.get(subTask.getId()) == null) return;
		Epic epic = epics.get(subTask.getEpicId());
		if (epic == null) return;
		subTasks.put(subTask.getId(), subTask);
		updateEpicStatus(epic.getId());
		updateEpicTime(epic.getId());
		Optional<Task> taskInPrioritized = prioritizedTasks.stream().filter(t -> t.getId() == subTask.getId()).findFirst();
		if (taskInPrioritized.isPresent()) {
			prioritizedTasks.remove(taskInPrioritized.get());
			prioritizedTasks.add(subTask);
		}
	}

	@Override
	public void deleteTask(int id) {
		Task task = tasks.get(id);
		if (task != null) {
			tasks.remove(id);
			historyManager.remove(id);
			prioritizedTasks.remove(task);
		}
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
			prioritizedTasks.remove(epic);
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
				updateEpicTime(epic.getId());
			}
			prioritizedTasks.remove(subTask);
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
		} else if (isAllDone) {
			epic.setStatus(Status.DONE);
		} else {
			epic.setStatus(Status.IN_PROGRESS);
		}
	}

	public void updateEpicTime(int id) {
		Epic epic = epics.get(id);
		ArrayList<SubTask> epicSubTasksList = getSubTasksByEpic(id);
		Optional<SubTask> subTaskMinStartTime = epicSubTasksList.stream().filter(subTask -> subTask.getStartTime() != null)
				.min(Comparator.comparing(Task::getStartTime));
		LocalDateTime minStartTime = subTaskMinStartTime.isEmpty() ? null : subTaskMinStartTime.get().getStartTime();
		Optional<SubTask> subTaskMaxEndTime = epicSubTasksList.stream().filter(subTask -> subTask.getEndTime() != null)
				.max(Comparator.comparing(Task::getStartTime));
		LocalDateTime maxEndTime = subTaskMaxEndTime.isEmpty() ? null : subTaskMaxEndTime.get().getEndTime();
		Long commonDuration = epicSubTasksList.stream().filter(subTask -> subTask.getDuration() != null)
				.map(subTask -> subTask.getDuration()).reduce(0L, Long::sum);
		epic.setStartTime(minStartTime);
		epic.setDuration(commonDuration);
		epic.setEndTime(maxEndTime);
	}

	@Override
	public List<Task> getHistory() {
		return historyManager.getHistory();
	}

	@Override
	public ArrayList<Task> getPrioritizedTasks() {
		return new ArrayList<>(prioritizedTasks);
	}

	@Override
	public boolean validateTaskTime(Task task) {
		if (task.getStartTime() == null ||
				task.getClass() == Epic.class) {
			return true;
		}
		return !getPrioritizedTasks().stream().filter(t -> t.getClass() != Epic.class && t.getStartTime() != null && t.getEndTime() != null)
				.anyMatch(t -> (task.getStartTime().isAfter(t.getStartTime()) && task.getEndTime().isBefore(t.getEndTime()))
						|| (task.getStartTime().isAfter(t.getStartTime()) && task.getEndTime().isAfter(t.getEndTime())
						&& task.getStartTime().isBefore(t.getEndTime()))
						|| (task.getStartTime().isBefore(t.getStartTime()) && task.getEndTime().isAfter(t.getStartTime()))
				);
	}
}
