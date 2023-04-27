package managers;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskType;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager implements TaskManager {

    private String filename;

    public FileBackedTasksManager(String filename) {
        super();
        this.filename = filename;
    }

    public void save() {
    }

    String toString(Task task) {
        TaskType taskType = TaskType.TASK;
        if (task instanceof Epic) {
            taskType = TaskType.EPIC;
        }
        return task.getId() + "," + taskType + "," + task.getName() + "," + task.getStatus() + "," + task.getDescription() + "," + "";
    }

    String toString(SubTask subTask) {
        return subTask.getId() + "," + TaskType.SUBTASK + "," + subTask.getName() + "," + subTask.getStatus() + "," + subTask.getDescription()
                + "," + subTask.getEpicId();
    }

    Task fromString(String taskValue) {
        String[] taskFields = taskValue.split(",");

        return new Task();
    }

    public static void main(String[] args) {
        FileBackedTasksManager manager = new FileBackedTasksManager("saved_tasks.csv");
        Task task = new Task(1, "Задача 1", "NEW", "описание задачи");
        Epic epic = new Epic(2, "Эпик 1", "NEW", "описание эпика");
        SubTask subTask = new SubTask(3, "Эпик 1", "NEW", "описание подзадачи", 2);
        System.out.println(manager.toString(subTask));
    }
}
