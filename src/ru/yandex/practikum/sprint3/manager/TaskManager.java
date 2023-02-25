package ru.yandex.practikum.sprint3.manager;

import ru.yandex.practikum.sprint3.models.Epic;
import ru.yandex.practikum.sprint3.models.Status;
import ru.yandex.practikum.sprint3.models.SubTask;
import ru.yandex.practikum.sprint3.models.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private int taskIdCounter;
    private HashMap<Integer, Task> simpleTaskMap;
    private HashMap<Integer, Epic> epicMap;
    private HashMap<Integer, SubTask> subTaskMap;

    public TaskManager() {
        taskIdCounter = 0;
        simpleTaskMap = new HashMap<>();
        epicMap = new HashMap<>();
        subTaskMap = new HashMap<>();
    }

    public int incrementTaskIdCounter() {
        return ++taskIdCounter;
    }

    public int getTaskIdCounter() {
        return taskIdCounter;
    }

    public ArrayList<Task> getAllTasksList() {
        return new ArrayList<>(simpleTaskMap.values());
    }

    public ArrayList<Epic> getEpicsList() {
        return new ArrayList<>(epicMap.values());
    }

    public ArrayList<SubTask> getSubTasksList() {
        return new ArrayList<>(subTaskMap.values());
    }

    public void clearAllTasks() {
        simpleTaskMap.clear();
    }

    public void clearAllEpics() {
        epicMap.clear();
    }

    public void clearAllSubTasks() {
        subTaskMap.clear();
    }

    public Task getTask(int id) {
        return simpleTaskMap.get(id);
    }

    public Epic getEpic(int id) {
        return epicMap.get(id);
    }

    public SubTask getSubTask(int id) {
        return subTaskMap.get(id);
    }

    public void create(Task task) {
        task.setId(incrementTaskIdCounter());
        simpleTaskMap.put(task.getId(), task);
    }

    public void create(Epic epic) {
        epic.setId(incrementTaskIdCounter());
        simpleTaskMap.put(epic.getId(), epic);
    }

    public void create(SubTask subTask) {
        subTask.setId(incrementTaskIdCounter());
        simpleTaskMap.put(subTask.getId(), subTask);
    }

    public void update(Task task) {
        simpleTaskMap.put(task.getId(), task);
    }

    public void update(Epic epic) {
        epicMap.put(epic.getId(), epic);
    }

    public void update(SubTask subTask) {
        subTaskMap.put(subTask.getId(), subTask);
    }

    public void deleteTask(int id) {
        simpleTaskMap.remove(id);
    }

    public void deleteEpic(int id) {
        epicMap.remove(id);
    }

    public void deleteSubTask(int id) {
        subTaskMap.remove(id);
    }

    public ArrayList<SubTask> getSubTasksByEpic(int id) {
        ArrayList<SubTask> result = new ArrayList<>();
        Epic epic = epicMap.get(id);
        if (epic == null) return result;
        ArrayList<Integer> epicSubTasksIdList = epic.getSubTasksIdList();
        if (epicSubTasksIdList.isEmpty()) return result;
        for (Integer subTaskId : epicSubTasksIdList) {
            SubTask subTask = getSubTask(subTaskId);
            result.add(subTask);
        }
        return result;
    }

    private void updateEpicStatus(int id) {
        Epic epic = getEpic(id);
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
}
