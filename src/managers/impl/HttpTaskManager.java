package managers.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import managers.impl.FileBackedTasksManager;
import server.JsonAdapter;
import server.KvsTaskClient;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class HttpTaskManager extends FileBackedTasksManager {

    public static final String TASK_KEY = "task";
    public static final String SUBTASK_KEY = "subtask";
    public static final String EPIC_KEY = "epic";
    public static final String HISTORY_KEY = "history";

    Gson gson;
    KvsTaskClient kvsClient;

    public HttpTaskManager(String kvsUrl)
    {
        super();
        String[] kvsUrlParts = kvsUrl.replaceAll("^http://", "").split(":");
        String host = kvsUrlParts[0];
        int port = Integer.parseInt(kvsUrlParts[1]);
        kvsClient = new KvsTaskClient();
        gson = JsonAdapter.getGson();
    }

    @Override
    public void save() {
        List<Task> allTasks = this.getAllTasks();
        List<Epic> allEpics = this.getAllEpics();
        List<SubTask> allSubTasks = this.getAllSubTasks();
        List<Task> history = this.getHistory();

        String tasksJson = gson.toJson(allTasks);
        String epicsJson = gson.toJson(allEpics);
        String subtasksJson = gson.toJson(allSubTasks);
        String historyJson = gson.toJson(history);

        kvsClient.save(TASK_KEY, tasksJson);
        kvsClient.save(EPIC_KEY, epicsJson);
        kvsClient.save(SUBTASK_KEY, subtasksJson);
        kvsClient.save(HISTORY_KEY, historyJson);
    }

    public void loadFromServer() {
        String tasksJson = kvsClient.load(TASK_KEY);
        String epicsJson = kvsClient.load(EPIC_KEY);
        String subtasksJson = kvsClient.load(SUBTASK_KEY);
        String historyJson = kvsClient.load(HISTORY_KEY);

        Type taskListType = new TypeToken<ArrayList<Task>>(){}.getType();
        Type subtaskListType = new TypeToken<ArrayList<SubTask>>(){}.getType();
        Type epicListType = new TypeToken<ArrayList<Epic>>(){}.getType();

        List<Task> allTasks = gson.fromJson(tasksJson, taskListType);
        List<SubTask> allSubTasks = gson.fromJson(subtasksJson, subtaskListType);
        List<Epic> allEpics = gson.fromJson(epicsJson, epicListType);
        List<Task> allHistory = gson.fromJson(historyJson, taskListType);

        for (Task task: allTasks) {
            this.tasks.put(task.getId(), task);
        }

        for (SubTask subTask : allSubTasks) {
            this.subTasks.put(subTask.getId(), subTask);
        }

        for (Epic epic : allEpics) {
            this.epics.put(epic.getId(), epic);
        }

        for (Task historyTask: allHistory) {
            this.historyManager.addTask(historyTask);
        }
    }
}