package ru.yandex.practikum.sprint3.manager;

import ru.yandex.practikum.sprint3.models.Epic;
import ru.yandex.practikum.sprint3.models.SubTask;
import ru.yandex.practikum.sprint3.models.Task;

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

    public void incrementTaskIdCounter() {
        taskIdCounter++;
    }

    public int getTaskIdCounter() {
        return taskIdCounter;
    }

    public HashMap<Integer, Task> getSimpleTaskMap() {
        return simpleTaskMap;
    }

    public HashMap<Integer, Epic> getEpicMap() {
        return epicMap;
    }

    public HashMap<Integer, SubTask> getSubTaskMap() {
        return subTaskMap;
    }
}
