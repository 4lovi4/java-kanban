package managers;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskType;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager implements TaskManager {

    public void save() {
    }

    String toString(Task task) {
        TaskType taskType;
        if (task instanceof Task) {
            taskType = TaskType.TASK;
        }
        else if (task instanceof Epic) {
            taskType = TaskType.EPIC;
        }
        return task.getId() + "," + task.getName() + "," + task.getStatus() + "," + task.getDescription() + "," + "";
    }

    String toString(SubTask subTask) {
        return subTask.getId() + "," + TaskType.SUBTASK + "," + subTask.getName() + "," + subTask.getStatus() + "," + subTask.getDescription()
                + "," + subTask.getEpicId();
    }

    Task fromString(String taskValue) {
        String[] taskFields = taskValue.split(",");

        return new Task();
    }
}
