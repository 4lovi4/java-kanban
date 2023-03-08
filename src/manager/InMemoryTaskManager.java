package manager;

import models.Epic;
import models.Status;
import models.SubTask;
import models.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {
    private int taskIdCounter;
    private final HashMap<Integer, Task> simpleTaskMap;
    private final HashMap<Integer, Epic> epicMap;
    private final HashMap<Integer, SubTask> subTaskMap;
    private final ArrayList<Task> taskHistoryList;
    private int taskHistoryCounter;
    private final int TASK_HISTORY_SIZE = 10;

    public InMemoryTaskManager() {
        taskIdCounter = 0;
        simpleTaskMap = new HashMap<>();
        epicMap = new HashMap<>();
        subTaskMap = new HashMap<>();
        taskHistoryList = new ArrayList<>(TASK_HISTORY_SIZE);
        taskHistoryCounter = 0;
    }

    @Override
    public int getTaskIdCounter() {
        return taskIdCounter;
    }

    @Override
    public ArrayList<Task> getAllTasksList() {
        return new ArrayList<>(simpleTaskMap.values());
    }

    @Override
    public ArrayList<Epic> getAllEpicsList() {
        return new ArrayList<>(epicMap.values());
    }

    @Override
    public ArrayList<SubTask> getAllSubTasksList() {
        return new ArrayList<>(subTaskMap.values());
    }

    @Override
    public void deleteAllTasks() {
        simpleTaskMap.clear();
    }

    @Override
    public void deleteAllEpics() {
        epicMap.clear();
    }

    @Override
    public void deleteAllSubTasks() {
        subTaskMap.clear();
    }

    @Override
    public Task getTask(int id) {
        iterateTaskHistory(id);
        Task task = simpleTaskMap.get(id);
        taskHistoryList.add(taskHistoryCounter, task);
        taskHistoryCounter++;
        return simpleTaskMap.get(id);
    }

    @Override
    public Epic getEpic(int id) {
        iterateTaskHistory(id);
        Task task = epicMap.get(id);
        taskHistoryList.add(taskHistoryCounter, task);
        taskHistoryCounter++;
        return epicMap.get(id);
    }

    @Override
    public SubTask getSubTask(int id) {
        iterateTaskHistory(id);
        Task task = subTaskMap.get(id);
        taskHistoryList.add(taskHistoryCounter, task);
        taskHistoryCounter++;
        return subTaskMap.get(id);
    }

    @Override
    public int addNewTask(Task task) {
        taskIdCounter++;
        task.setId(taskIdCounter);
        simpleTaskMap.put(task.getId(), task);
        return task.getId();
    }

    @Override
    public int addNewEpic(Epic epic) {
        taskIdCounter++;
        epic.setId(taskIdCounter);
        updateEpicStatus(epic.getId());
        epicMap.put(epic.getId(), epic);
        return epic.getId();
    }

    @Override
    public int addNewSubTask(SubTask subTask) {
        taskIdCounter++;
        subTask.setId(taskIdCounter);
        subTaskMap.put(subTask.getId(), subTask);
        return subTask.getId();
    }

    @Override
    public void updateTask(Task task) {
        if (simpleTaskMap.containsKey(task.getId())) {
            simpleTaskMap.put(task.getId(), task);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epicMap.containsKey(epic.getId())) {
            epicMap.put(epic.getId(), epic);
        }
        updateEpicStatus(epic.getId());
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        if (subTaskMap.containsKey(subTask.getId())) {
            Epic epic = epicMap.get(subTask.getEpicId());
            epic.addToSubTasksIdList(subTask.getId());
            subTaskMap.put(subTask.getId(), subTask);
            epicMap.put(epic.getId(), epic);
            updateEpicStatus(epic.getId());
        }
    }

    @Override
    public void deleteTask(int id) {
        simpleTaskMap.remove(id);
    }

    @Override
    public void deleteEpic(int id) {
        epicMap.remove(id);
    }

    @Override
    public void deleteSubTask(int id) {
        subTaskMap.remove(id);
    }

    @Override
    public ArrayList<SubTask> getSubTasksByEpic(int id) {
        ArrayList<SubTask> result = new ArrayList<>();
        Epic epic = epicMap.get(id);
        if (epic == null) return result;
        ArrayList<Integer> epicSubTasksIdList = epic.getSubTasksIdList();
        if (epicSubTasksIdList.isEmpty()) return result;
        for (Integer subTaskId : epicSubTasksIdList) {
            SubTask subTask = subTaskMap.get(subTaskId);
            result.add(subTask);
        }
        return result;
    }

    private void updateEpicStatus(int id) {
        Epic epic = epicMap.get(id);
        boolean isAllNew = true;
        boolean isAllDone = true;
        if (epic == null) return;
        ArrayList<SubTask> epicSubTasksList = getSubTasksByEpic(id);
        if (epicSubTasksList.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }
        for (SubTask subTask : epicSubTasksList) {
            if (subTask.getStatus() != Status.NEW) isAllNew = false;
            if (subTask.getStatus() != Status.DONE) isAllDone = false;
        }
        if (isAllNew) epic.setStatus(Status.NEW);
        else if (isAllDone) epic.setStatus(Status.DONE);
        else epic.setStatus(Status.IN_PROGRESS);
    }

    private void iterateTaskHistory(int id) {
        if (taskHistoryList.size() == TASK_HISTORY_SIZE) {
            taskHistoryCounter = 0;
        }
    }

    @Override
    public List<Task> getHistory() {
        return taskHistoryList;
    }
}
