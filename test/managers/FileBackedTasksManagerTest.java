package managers;

import exception.ManagerSaveException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import managers.impl.FileBackedTasksManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskType;

import static managers.impl.FileBackedTasksManager.loadFromFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;


class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {

    private static final String TEST_FILENAME = "test_tasks.csv";
    private static final String EMPTY_TEST_FILENAME = "empty_test_tasks.csv";
    private static final String NON_EXISTING_FILENAME = "not_existing_file.csv";
    @Override
    FileBackedTasksManager getTaskManager() {
        return new FileBackedTasksManager(new File(TEST_FILENAME));
    }

    @BeforeEach
    public void prepareFiles() throws IOException {
        Path emptyFilePath = Paths.get(EMPTY_TEST_FILENAME);
        Files.createFile(emptyFilePath);
    }

    @AfterEach
    public void tearDownFiles() throws IOException {
        Path emptyFilePath = Paths.get(EMPTY_TEST_FILENAME);
        Files.deleteIfExists(emptyFilePath);
        Path nonExistingFilePath = Paths.get(NON_EXISTING_FILENAME);
        Files.deleteIfExists(nonExistingFilePath);
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
    @DisplayName("Сохранение всех задач в файл")
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
    @DisplayName("Сохранение всех задач с заданным временем и длительностью в файл")
    public void shouldSaveTasksAllTypesInFileCorrectlyWithTime() throws IOException {
        Task task = createNewTask(100);
        int taskId = taskManager.addNewTask(task);
        task.setStartTime(LocalDateTime.of(2023, 6, 24, 14, 30, 30));
        task.setDuration(85L);
        Epic epic = createNewEpic(10);
        int epicId = taskManager.addNewEpic(epic);
        SubTask subTask = createSubTask(1, epicId);
        int subTaskId = taskManager.addNewSubTask(subTask);
        subTask.setStartTime(LocalDateTime.of(2023, 6, 24, 11, 30, 20));
        subTask.setDuration(10L);
        taskManager.updateSubTask(subTask);
        String fileContentExpected = "id,type,name,status,description,epic,start_time,duration,end_time\n" +
                String.format("%d,TASK,Задача 100 Есть,NEW,Описание задачи 100,,2023-06-24T14:30:30,85,2023-06-24T15:55:30\n", taskId) +
                String.format("%d,EPIC,Эпик 10 Есть,NEW,Описание задачи 10,,2023-06-24T11:30:20,10,2023-06-24T11:40:20\n", epicId) +
                String.format("%d,SUBTASK,Подзадача 1,NEW,Описание подзадачи 1,%d,2023-06-24T11:30:20,10,2023-06-24T11:40:20\n\n", subTaskId, epicId);
        assertDoesNotThrow(() -> taskManager.save());
        Path path = Paths.get(TEST_FILENAME);
        assertTrue(Files.exists(path));
        String fileContent = Files.readString(path);
        assertEquals(fileContentExpected, fileContent, "В файле записаны неверные данные");
    }

    @Test
    @DisplayName("Сохранение пустого списка задач в файл")
    public void shouldSaveEmptyTasksInFile() throws IOException {
        String fileContentExpected = "id,type,name,status,description,epic,start_time,duration,end_time\n\n";
        assertDoesNotThrow(() -> taskManager.save());
        Path path = Paths.get(TEST_FILENAME);
        assertTrue(Files.exists(path));
        String fileContent = Files.readString(path);
        assertEquals(fileContentExpected, fileContent, "В файле записаны неверные данные");
    }

    @Test
    @DisplayName("Загрузка задач из пустого файла")
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
    @DisplayName("Загрузка задач и истории запросов из файла")
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
    @DisplayName("Загрузка задач и истории запросов с указанным временем из файла")
    public void shouldLoadTasksFromFileWithTime() throws IOException {
        String headerText = "id,type,name,status,description,epic,start_time,duration,end_time\n";
        String taskText = "150,TASK,Загрузка Задачи,DONE,Описание загрузки задачи,,2023-06-24T18:00:00,30,2023-06-24T18:30:00\n";
        String epicText = "1,EPIC,Загрузка Эпика,IN_PROGRESS,Описание загрузки эпика,,2023-06-24T12:00:00,70,2023-06-24T13:10:00\n";
        String subTaskText = "10,SUBTASK,Загрузка Подзадачи,IN_PROGRESS,Описание загрузки подзадачи,1,2023-06-24T12:00:00,70,2023-06-24T13:10:00\n\n";
        String historyText = "1,100,12\n";
        try (FileWriter writer = new FileWriter(TEST_FILENAME, false)) {
            writer.write(headerText + taskText + epicText + subTaskText + historyText);
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
    @DisplayName("Загрузка задач из пустого файла только с заголовком")
    public void shouldLoadEmptyTasksAllTypesFromFileWithOnlyCsvHeader() throws IOException{
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
    @DisplayName("Загрузка пустых списков задач из пустого файла")
    public void shouldLoadEmptyTasksAllTypesFromEmptyFile() throws IOException{
        FileBackedTasksManager manager = loadFromFile(new File(EMPTY_TEST_FILENAME));
        assertTrue(manager.getAllTasks().isEmpty(), "Список задач не пустой");
        assertTrue(manager.getAllEpics().isEmpty(), "Список всех эпиков не пустой");
        assertTrue(manager.getAllSubTasks().isEmpty(), "Список подзадач не пустой");
    }

    @Test
    @DisplayName("Исключениеесли указано неправльное имя файла")
    public void shouldThrowExceptionLoadingFromNonExistingFile() throws IOException{
        assertThrows(ManagerSaveException.class, () -> loadFromFile(new File(NON_EXISTING_FILENAME)));
    }
}
