package managers.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import managers.HistoryManager;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskType;
import exception.TaskFormatException;
import exception.ManagerSaveException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;


public class FileBackedTasksManager extends InMemoryTaskManager {

    private File storageFile;
    private static final String STORAGE_HEADER = "id,type,name,status,description,epic,start_time,duration,end_time\n";

    public FileBackedTasksManager() {
        super();
    }

    public FileBackedTasksManager(File file) {
        super();
        this.storageFile = file;
    }

    public void initializeFile() {
        if (this.storageFile.exists()) {
            try {
                if (storageFile.createNewFile()) {
                    System.out.println("Создан новый файл: " + storageFile.getAbsolutePath());
                }
            } catch (IOException e) {
                throw new ManagerSaveException("Ошибка создания файла " + this.storageFile.getName(), e);
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

    public void save() {
        List<Task> allTasks = getAllTasks();
        List<Epic> allEpics = getAllEpics();
        List<SubTask> allSubTasks = getAllSubTasks();

        try (FileWriter taskWriter = new FileWriter(storageFile, false)) {
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
        }
    }

    public static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager manager = new FileBackedTasksManager(file);
        String content = "";

        try {
            content = Files.readString(file.toPath(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения из файла " + file.getName(), e);
        }

        String[] contentLines = content.split("\n", -1);
        if (contentLines.length == 0 || contentLines[0].equals("")) {
            return manager;
        }
        int i = 1;
        while (!contentLines[i].equals("")) {
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
            if (!historyIds.isEmpty()) {
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
            i++;
        }
        return manager;
    }

    private String toString(Task task) {
        String taskString = "";
        TaskType taskType = TaskType.TASK;
        String startTimeText = task.getStartTime() != null
                ? task.getStartTime().format(DateTimeFormatter.ISO_DATE_TIME) : "";
        String endTimeText = task.getEndTime() != null
                ? task.getEndTime().format(DateTimeFormatter.ISO_DATE_TIME) : "";
        String durationText = task.getDuration() != null
                ? task.getDuration().toString() : "";
        if (task instanceof Epic) {
            taskType = TaskType.EPIC;
        }
        taskString = String.format("%d,%s,%s,%s,%s,,%s,%s,%s", task.getId(), taskType, task.getName(), task.getStatus(),
                task.getDescription(), startTimeText, durationText, endTimeText);
        if (task instanceof SubTask) {
            taskString = String.format("%d,%s,%s,%s,%s,%d,%s,%s,%s", task.getId(), TaskType.SUBTASK, task.getName(),
                    task.getStatus(), task.getDescription(), ((SubTask) task).getEpicId(),
                    startTimeText, durationText, endTimeText);
        }
        return taskString;
    }

    private Task fromString(String taskValue) throws TaskFormatException {
        String[] taskFields = taskValue.split(",", -1);
        Task result;

        try {
            TaskType taskType = TaskType.valueOf(taskFields[1]);
            LocalDateTime startTime = taskFields[6].isEmpty() ? null :
                    LocalDateTime.parse(taskFields[6], DateTimeFormatter.ISO_DATE_TIME);
            Long duration = taskFields[7].isEmpty() ? null :
                    Long.valueOf(taskFields[7]);
            LocalDateTime endTime = taskFields[8].isEmpty() ? null :
                    LocalDateTime.parse(taskFields[8], DateTimeFormatter.ISO_DATE_TIME);
            switch (taskType) {
                case EPIC:
                    result = new Epic(Integer.parseInt(taskFields[0]), taskFields[2], taskFields[3], taskFields[4]);
                    break;
                case TASK:
                    result = new Task(Integer.parseInt(taskFields[0]), taskFields[2], taskFields[3], taskFields[4]);
                    break;
                case SUBTASK:
                    result = new SubTask(Integer.parseInt(taskFields[0]), taskFields[2], taskFields[3], taskFields[4], Integer.parseInt(taskFields[5]));
                    break;
                default:
                    result = new Task();
                    break;
            }
            result.setStartTime(startTime);
            result.setDuration(duration);
        } catch (IndexOutOfBoundsException e) {
            throw new TaskFormatException("Число полей в сохранённой строке не соответствует ожидаемому: " + taskValue, e);
        } catch (IllegalArgumentException | DateTimeParseException e) {
            throw new TaskFormatException("Неверный формат данных в сохранённой строке: " + taskValue, e);
        }
        return result;
    }

    public static String historyToString(HistoryManager manager) {
        StringBuilder historyLine = new StringBuilder();
        List<Task> history = manager.getHistory();
        String emptyLine = "";
        if (history.isEmpty()) {
            return emptyLine;
        }
        for (Task task : history) {
            historyLine.append(task.getId()).append(",");
        }
        historyLine.deleteCharAt(historyLine.length() - 1);
        return historyLine.toString();
    }

    public static List<Integer> historyFromString(String historyLine) {
        ArrayList<Integer> history = new ArrayList<>();
        if (historyLine.isEmpty()) {
            return history;
        }
        for (String id : historyLine.split(",")) {
            history.add(Integer.valueOf(id));
        }
        return history;
    }

    public static void main(String[] args) {
        final String filename = "saved_tasks.csv";
        FileBackedTasksManager manager = new FileBackedTasksManager(new File(filename));
        manager.initializeFile();
        Task task = new Task(10, "Задача 100", "NEW", "описание задачи");
        Epic epic = new Epic(20, "Эпик 1000", "NEW", "описание эпика");
        SubTask subTask = new SubTask(30, "Эпик 1", "NEW", "описание подзадачи", 2);
        int taskId = manager.addNewTask(task);
        int subTaskId = manager.addNewSubTask(subTask);
        int epicId = manager.addNewEpic(epic);
        epic.setId(epicId);
        epic.addSubTaskId(subTaskId);
        manager.updateEpic(epic);
        manager.getTask(taskId);
        manager.getSubTask(subTaskId);
        FileBackedTasksManager manager1 = FileBackedTasksManager.loadFromFile(new File(filename));
        System.out.println(manager1.getAllTasks());
        System.out.println(manager1.getAllEpics());
        System.out.println(manager1.getAllSubTasks());
        System.out.println(manager1.getHistory());
    }
}


