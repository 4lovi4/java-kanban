package managers;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskType;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager implements TaskManager {

    private File storageFile;
    private final String STORAGE_HEADER = "id,type,name,status,description,epic\n";

    public FileBackedTasksManager(File file) {
        super();
        this.storageFile = file;
        if (this.storageFile.exists()) {
            try {
                if (storageFile.createNewFile()) {
                    System.out.println("Создан новый файл: " + storageFile.getAbsolutePath());
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public int addNewTask(Task task) {
        int taskId = super.addNewTask(task);
        save();
        return taskId;
    }

    @Override
    public int addNewEpic(Epic epic) {
        int epicId = super.addNewEpic(epic);
        save();
        return epicId;
    }

    @Override
    public int addNewSubTask(SubTask subTask) {
        int subTaskId = super.addNewSubTask(subTask);
        save();
        return subTaskId;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteSubTask(int id) {
        super.deleteSubTask(id);
        save();
    }

    @Override
    public Task getTask(int id) {
        Task task = super.getTask(id);
        save();
        return task;
    }

    @Override
    public SubTask getSubTask(int id) {
        SubTask subTask = super.getSubTask(id);
        save();
        return subTask;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = super.getEpic(id);
        save();
        return epic;
    }

    public void save() throws ManagerSaveException {
        Writer taskWriter = null;
        List<Task> allTasks = getAllTasks();
        List<Epic> allEpics = getAllEpics();
        List<SubTask> allSubTasks = getAllSubTasks();
        try {
            taskWriter = new FileWriter(storageFile, false);
            taskWriter.write(STORAGE_HEADER);
            for (Task task : allTasks) {
                taskWriter.write(toString(task) + "\n");
            }
            for (Epic epic : allEpics) {
                taskWriter.write(toString(epic) + "\n");
            }
            for (SubTask subTask : allSubTasks) {
                taskWriter.write(toString(subTask) + "\n");
            }
            String historyLine = historyToString(this.historyManager);
            taskWriter.write("\n" + historyLine);
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения списка задач в файл!", e);
        } finally {
            if (taskWriter != null) {
                try {
                    taskWriter.close();
                } catch (IOException e) {
                    throw new ManagerSaveException("Ошибка сохранения списка задач в файл!", e);
                }
            }
        }
    }

    public static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager manager = new FileBackedTasksManager(file);
        String content = "";
        try {
            content = Files.readString(file.toPath(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String[] contentLines = content.split("\n");
        for (int i = 1; i < contentLines.length - 2; i++) {
            Task task = manager.fromString(contentLines[i]);
            int taskId = task.getId();
            manager.setTaskIdCounter(taskId + 1);
            if (task instanceof Epic) {
                manager.epics.put(taskId, (Epic) task);
            } else if (task instanceof SubTask) {
                manager.subTasks.put(taskId, (SubTask) task);
            } else {
                manager.tasks.put(taskId, task);
            }
            String historyLine = contentLines[contentLines.length - 1];
            List<Integer> historyIds = historyFromString(historyLine);
            if (historyIds.size() > 0) {
                for (Integer id : historyIds) {
                    if (manager.tasks.containsKey(id)) {
                        manager.historyManager.addTask(manager.tasks.get(id));
                    } else if (manager.subTasks.containsKey(id)) {
                        manager.historyManager.addTask(manager.subTasks.get(id));
                    } else if (manager.epics.containsKey(id)) {
                        manager.historyManager.addTask(manager.epics.get(id));
                    }
                }
            }
        }
        return manager;
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

    Task fromString(String taskValue) throws TaskFormatException {
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
        } catch (IndexOutOfBoundsException | IllegalArgumentException e) {
            System.out.println();
            throw new TaskFormatException("Неверный формат сохранения задачи: " + taskValue, e);
        }
        return result;
    }

    static String historyToString(HistoryManager manager) {
        StringBuilder historyLine = new StringBuilder();
        List<Task> history = manager.getHistory();
        if (history.size() == 0) {
            return "";
        }
        for (Task task : history) {
            historyLine.append(task.getId() + ",");
        }
        historyLine.deleteCharAt(historyLine.length() - 1);
        return historyLine.toString();
    }

    static List<Integer> historyFromString(String historyLine) {
        ArrayList<Integer> history = new ArrayList<>();
        for (String id : historyLine.split(",")) {
            history.add(Integer.valueOf(id));
        }
        return history;
    }

    public static void main(String[] args) {
        final String FILENAME = "saved_tasks.csv";
        FileBackedTasksManager manager = new FileBackedTasksManager(new File(FILENAME));
        Task task = new Task(1, "Задача 1", "NEW", "описание задачи");
        Epic epic = new Epic(2, "Эпик 1000", "NEW", "описание эпика");
        SubTask subTask = new SubTask(3, "Эпик 1", "NEW", "описание подзадачи", 2);
        int taskId = manager.addNewTask(task);
        int subTaskId = manager.addNewSubTask(subTask);
        int epicId = manager.addNewEpic(epic);
        epic.setId(epicId);
        epic.addSubTaskId(subTaskId);
        manager.updateEpic(epic);
        manager.getTask(taskId);
        manager.getSubTask(subTaskId);
        FileBackedTasksManager manager1 = FileBackedTasksManager.loadFromFile(new File(FILENAME));
        System.out.println(manager1.getAllTasks());
        System.out.println(manager1.getAllEpics());
        System.out.println(manager1.getAllSubTasks());
        System.out.println(manager1.getHistory());
    }
}

class TaskFormatException extends RuntimeException {
    public TaskFormatException(String errorMessage, Throwable error) {
        super(errorMessage, error);
    }
}

class ManagerSaveException extends RuntimeException {
    public ManagerSaveException(String errorMessage, Throwable error) {
        super(errorMessage, error);
    }
}


