package managers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskType;

import static managers.FileBackedTasksManager.loadFromFile;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {

    private static final String TEST_FILENAME = "test_tasks.csv";
    private static final String EMPTY_TEST_FILENAME = "empty_test_tasks.csv";
    private static final String WRONG_TEST_FILENAME = "wrong_test_tasks.csv";
    @Override
    FileBackedTasksManager getTaskManager() {
        return new FileBackedTasksManager(new File(TEST_FILENAME));
    }

    public Task createTaskFromText(String text) {
        Task task = new Task();
        String[] taskFields = text.split(",", -1);
        if (TaskType.valueOf(taskFields[1]).equals(TaskType.EPIC)) {
            task = new Epic();
        } else if (TaskType.valueOf(taskFields[1]).equals(TaskType.SUBTASK)) {
            task = new SubTask();
            ((SubTask) task).setEpicId(Integer.valueOf(taskFields[5]));
        }
        int taskId = Integer.valueOf(taskFields[0]);
        task.setId(taskId);
        task.setName(taskFields[2]);
        task.setStatus(Status.valueOf(taskFields[3]));
        task.setDescription(taskFields[4]);
        LocalDateTime startTime = taskFields[6].equals("") ? null : LocalDateTime.parse(taskFields[6], DateTimeFormatter.ISO_DATE_TIME);
        task.setStartTime(startTime);
        Long duration = taskFields[7].equals("") ? null : Long.valueOf(taskFields[7]);
        task.setDuration(duration);
        return task;
    }

    @Test
    public void shouldSaveTasksAllTypesInFileCorrectly() throws IOException {
        Task task = createNewTask(100);
        int taskId = taskManager.addNewTask(task);
        Epic epic = createNewEpic(10);
        int epicId = taskManager.addNewEpic(epic);
        SubTask subTask = createSubTask(1, epicId);
        int subTaskId = taskManager.addNewSubTask(subTask);
        String fileContentExpected = "id,type,name,status,description,epic,start_time,duration,end_time\n" +
                String.format("%d,TASK,Задача 100 Есть,NEW,Описание задачи 100,,,,\n", taskId) +
                String.format("%d,EPIC,Эпик 10 Есть,NEW,Описание задачи 10,,,0,\n", epicId) +
                String.format("%d,SUBTASK,Подзадача 1,NEW,Описание подзадачи 1,%d,,,\n\n", subTaskId, epicId);
        assertDoesNotThrow(() -> taskManager.save());
        Path path = Paths.get(TEST_FILENAME);
        assertTrue(Files.exists(path));
        String fileContent = Files.readString(path);
        assertEquals(fileContentExpected, fileContent, "В файле записаны неверные данные");
    }

    @Test
    public void shouldSaveEmptyTasksInFile() throws IOException {
        String fileContentExpected = "id,type,name,status,description,epic,start_time,duration,end_time\n\n";
        assertDoesNotThrow(() -> taskManager.save());
        Path path = Paths.get(TEST_FILENAME);
        assertTrue(Files.exists(path));
        String fileContent = Files.readString(path);
        assertEquals(fileContentExpected, fileContent, "В файле записаны неверные данные");
    }

    @Test
    public void shouldLoadTasksFromFileWithEmptyHistory() throws IOException {
        String headerText = "id,type,name,status,description,epic,start_time,duration,end_time\n";
        String taskText = "150,TASK,Загрузка Задачи,DONE,Описание загрузки задачи,,,,\n";
        String epicText = "1,EPIC,Загрузка Эпика,IN_PROGRESS,Описание загрузки эпика,,,,\n";
        String subTaskText = "10,SUBTASK,Загрузка Подзадачи,IN_PROGRESS,Описание загрузки подзадачи,1,,,\n\n";
        try (FileWriter writer = new FileWriter(TEST_FILENAME, false)) {
            writer.write(headerText + taskText + epicText + subTaskText);
        }
        FileBackedTasksManager manager = loadFromFile(new File(TEST_FILENAME));

        Task taskFromText = createTaskFromText(taskText);
        Task taskLoadedFromFile = manager.getTask(taskFromText.getId());
        assertEquals(taskFromText, taskLoadedFromFile);

        Epic epicFromText = (Epic) createTaskFromText(epicText);
        Epic epicLoadedFromFile = manager.getEpic(epicFromText.getId());
        assertEquals(epicFromText, epicLoadedFromFile);

        SubTask subTaskFromText = (SubTask) createTaskFromText(subTaskText);
        SubTask subTaskLoadedFromFile = manager.getSubTask(subTaskFromText.getId());
        assertEquals(subTaskFromText, subTaskLoadedFromFile);
    }

    @Test
    public void shouldLoadTasksFromFileWithHistory() throws IOException {
        String headerText = "id,type,name,status,description,epic,start_time,duration,end_time\n";
        String taskText = "150,TASK,Загрузка Задачи,DONE,Описание загрузки задачи,,,,\n";
        String epicText = "1,EPIC,Загрузка Эпика,IN_PROGRESS,Описание загрузки эпика,,,,\n";
        String subTaskText = "10,SUBTASK,Загрузка Подзадачи,IN_PROGRESS,Описание загрузки подзадачи,1,,,\n\n";
        String historyText = "1,100,12\n";
        try (FileWriter writer = new FileWriter(TEST_FILENAME, false)) {
            writer.write(headerText + taskText + epicText + subTaskText + historyText);
        }
        FileBackedTasksManager manager = loadFromFile(new File(TEST_FILENAME));

        Task taskFromFile = new Task();
        String[] taskFields = taskText.split(",");
        int taskIdFromFile = Integer.valueOf(taskFields[0]);
        taskFromFile.setId(taskIdFromFile);
        taskFromFile.setName(taskFields[2]);
        taskFromFile.setStatus(Status.valueOf(taskFields[3]));
        taskFromFile.setDescription(taskFields[4]);
        Task loadTask = manager.getTask(taskIdFromFile);
        assertEquals(taskFromFile, loadTask);

        Epic epicFromFile = new Epic();
        String[] epicFields = epicText.split(",");
        int epicIdFromFile = Integer.valueOf(epicFields[0]);
        epicFromFile.setId(epicIdFromFile);
        epicFromFile.setName(epicFields[2]);
        epicFromFile.setStatus(Status.valueOf(epicFields[3]));
        epicFromFile.setDescription(epicFields[4]);
        Epic loadEpic = manager.getEpic(epicIdFromFile);
        assertEquals(epicFromFile, loadEpic);

        SubTask subTaskFromFile = new SubTask();
        String[] subTaskFields = subTaskText.trim().split(",");
        int subTaskIdFromFile = Integer.valueOf(subTaskFields[0]);
        subTaskFromFile.setId(subTaskIdFromFile);
        subTaskFromFile.setName(subTaskFields[2]);
        subTaskFromFile.setStatus(Status.valueOf(subTaskFields[3]));
        subTaskFromFile.setDescription(subTaskFields[4]);
        subTaskFromFile.setEpicId(Integer.valueOf(subTaskFields[5]));
        SubTask loadSubTask = manager.getSubTask(subTaskIdFromFile);
        assertEquals(subTaskFromFile, loadSubTask);
    }

    @Test
    public void shouldLoadEmptyTasksListsFromFileWithOnlyCsvHeader() throws IOException{
        String headerText = "id,type,name,status,description,epic,start_time,duration,end_time\n";
        try (FileWriter writer = new FileWriter(TEST_FILENAME, false)) {
            writer.write(headerText);
        }
        FileBackedTasksManager manager = loadFromFile(new File(TEST_FILENAME));
        assertTrue(manager.getAllTasks().isEmpty(), "Список задач не пустой");
        assertTrue(manager.getAllEpics().isEmpty(), "Список всех эпиков не пустой");
        assertTrue(manager.getAllSubTasks().isEmpty(), "Список подзадач не пустой");
    }

    @Test
    public void shouldLoadEmptyTasksListsFromEmptyFile() throws IOException{
        Path testFilePath = Paths.get(TEST_FILENAME);
        Files.deleteIfExists(testFilePath);
        Files.createFile(testFilePath);
        FileBackedTasksManager manager = loadFromFile(testFilePath.toFile());
        assertTrue(manager.getAllTasks().isEmpty(), "Список задач не пустой");
        assertTrue(manager.getAllEpics().isEmpty(), "Список всех эпиков не пустой");
        assertTrue(manager.getAllSubTasks().isEmpty(), "Список подзадач не пустой");
    }
}
