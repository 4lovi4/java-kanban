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

    Task fromString(String taskValue) throws TaskFormatException{
        String[] taskFields = taskValue.split(",");
        Task result = new Task();
        try {
            TaskType taskType = TaskType.valueOf(taskFields[1]);
            switch (taskType) {
                case EPIC:
                    result = new Epic(Integer.valueOf(taskFields[0]), taskFields[2], taskFields[3], taskFields[4]);
                    break;
                case TASK:
                    result = new Task(Integer.valueOf(taskFields[0]), taskFields[2], taskFields[3], taskFields[4]);
                    break;
                case SUBTASK:
                    result = new SubTask(Integer.valueOf(taskFields[0]), taskFields[2], taskFields[3], taskFields[4], Integer.valueOf(taskFields[5]));
                    break;
            }
        }
        catch (IndexOutOfBoundsException | IllegalArgumentException e) {
            System.out.println();
            throw new TaskFormatException("Неверный формат сохранения задачи: " + taskValue, e);
        }
        return result;
    }

    static String historyToString(HistoryManager manager) {
        StringBuilder historyLine = new StringBuilder();
        List<Task> history = manager.getHistory();
        for (Task task: history) {
            historyLine.append(Integer.toString(task.getId()) + ",");
        }
        historyLine.deleteCharAt(historyLine.length() - 1);
        return historyLine.toString();
    }

    static List<Integer> historyFromString(String historyLine) {
        ArrayList<Integer> history = new ArrayList<>();
        for (String id: historyLine.split(",")) {
            history.add(Integer.valueOf(id))
        }
        return history;
    }

    public static void main(String[] args) {
        FileBackedTasksManager manager = new FileBackedTasksManager("saved_tasks.csv");
        Task task = new Task(1, "Задача 1", "NEW", "описание задачи");
        Epic epic = new Epic(2, "Эпик 1", "NEW", "описание эпика");
        SubTask subTask = new SubTask(3, "Эпик 1", "NEW", "описание подзадачи", 2);
        System.out.println(manager.toString(task));
        System.out.println(manager.toString(epic));
        System.out.println(manager.toString(subTask));
    }
};

class TaskFormatException extends RuntimeException {
    public TaskFormatException(String errorMessage, Throwable error) {
        super(errorMessage, error);
    }
}

