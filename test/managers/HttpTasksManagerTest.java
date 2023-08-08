package managers;

import exception.KVServerConnectionException;
import managers.impl.FileBackedTasksManager;
import managers.impl.HttpTaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.KVServer;
import server.KvsTaskClient;
import tasks.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static managers.impl.HttpTaskManager.*;
import static org.junit.jupiter.api.Assertions.*;


class HttpTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {

    private static final String KVSERVER_URL = "http://localhost:8078";
    private KVServer kvServer;
    private KvsTaskClient kvsTaskClient;

    @Override
    HttpTaskManager getTaskManager() {
        return new HttpTaskManager(KVSERVER_URL);
    }

    @BeforeEach
    public void setUp() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        kvsTaskClient = new KvsTaskClient();
        taskManager = getTaskManager();
    }

    @AfterEach
    public void tearDown() {
        kvServer.stop();
    }

    @Test
    public void shouldSaveAllTasksToServerCorrectly() {
        Task task = createNewTask(100);
        LocalDateTime startTime = LocalDateTime.now().minusDays(1L);
        Long duration = 60L;
        task.setStartTime(startTime);
        task.setDuration(duration);
        int taskId = taskManager.addNewTask(task);
        String serverDataExpected = "[" +
                String.format("{\"id\":%d,\"name\":\"%s\",\"description\":\"%s\",\"status\":\"%s\",\"duration\":%d,\"startTime\":\"%s\"}",
                        taskId, task.getName(), task.getDescription(), task.getStatus().toString(), duration, startTime.format(DateTimeFormatter.ISO_DATE_TIME)) +
                "]";
        assertDoesNotThrow(() -> taskManager.save());
        String serverData = kvsTaskClient.load(TASK_KEY);
        assertEquals(serverDataExpected, serverData);
    }

    @Test
    public void shouldSaveAllEpicsToServerCorrectly() {
        LocalDateTime startTime = LocalDateTime.now().minusDays(1L);
        Long duration = 60L;
        Epic epic = createNewEpic(100);
        int epicId = taskManager.addNewEpic(epic);
        SubTask subTask = createSubTask(200, epicId);
        subTask.setStartTime(startTime);
        subTask.setDuration(duration);
        int subTaskId = taskManager.addNewSubTask(subTask);
        epic.setId(epicId);
        taskManager.updateEpic(epic);
        String serverDataExpected = "[" +
                String.format("{\"subTasksId\":[%d],\"endTime\":\"%s\",\"id\":%d,\"name\":\"%s\",\"description\":\"%s\",\"status\":\"%s\",\"duration\":%d,\"startTime\":\"%s\"}",
                        subTaskId, epic.getEndTime().format(DateTimeFormatter.ISO_DATE_TIME), epicId, epic.getName(),
                        epic.getDescription(), epic.getStatus().toString(), duration, startTime.format(DateTimeFormatter.ISO_DATE_TIME)) +
                "]";
        assertDoesNotThrow(() -> taskManager.save());
        String serverData = kvsTaskClient.load(EPIC_KEY);
        assertEquals(serverDataExpected, serverData);
    }

    @Test
    public void shouldSaveAllSubTasksToServerCorrectly() {
        LocalDateTime startTime = LocalDateTime.now().minusDays(1L);
        Long duration = 60L;
        Epic epic = createNewEpic(100);
        int epicId = taskManager.addNewEpic(epic);
        SubTask subTask = createSubTask(200, epicId);
        subTask.setStartTime(startTime);
        subTask.setDuration(duration);
        int subTaskId = taskManager.addNewSubTask(subTask);
        epic.setId(epicId);
        taskManager.updateEpic(epic);
        String serverDataExpected = "[" +
                String.format("{\"epicId\":%d,\"id\":%d,\"name\":\"%s\",\"description\":\"%s\",\"status\":\"%s\",\"duration\":%d,\"startTime\":\"%s\"}",
                        epicId, subTaskId, subTask.getName(), subTask.getDescription(), subTask.getStatus().toString(), duration, startTime.format(DateTimeFormatter.ISO_DATE_TIME)) +
                "]";
        assertDoesNotThrow(() -> taskManager.save());
        String serverData = kvsTaskClient.load(SUBTASK_KEY);
        assertEquals(serverDataExpected, serverData);
    }

    @Test
    public void shouldSaveAllHistoryToServerCorrectly() {
        Task task = createNewTask(1);
        Epic epic = createNewEpic(50);
        LocalDateTime startTime = LocalDateTime.now().minusDays(1L);
        Long duration = 60L;
        task.setStartTime(startTime);
        task.setDuration(duration);
        int taskId = taskManager.addNewTask(task);
        int epicId = taskManager.addNewEpic(epic);
        SubTask subTask = createSubTask(100, epicId);
        subTask.setStartTime(startTime.plusHours(2));
        subTask.setDuration(duration);
        int subTaskId = taskManager.addNewSubTask(subTask);
        taskManager.getTask(taskId);
        taskManager.getEpic(epicId);
        taskManager.getSubTask(subTaskId);
        String serverDataExpected = "[" +
                String.format("{\"id\":%d,\"name\":\"%s\",\"description\":\"%s\",\"status\":\"%s\",\"duration\":%d,\"startTime\":\"%s\"},",
                        taskId, task.getName(), task.getDescription(), task.getStatus().toString(), duration, startTime.format(DateTimeFormatter.ISO_DATE_TIME)) +
                String.format("{\"subTasksId\":[%d],\"endTime\":\"%s\",\"id\":%d,\"name\":\"%s\",\"description\":\"%s\",\"status\":\"%s\",\"duration\":%d,\"startTime\":\"%s\"},",
                        subTaskId, subTask.getEndTime().format(DateTimeFormatter.ISO_DATE_TIME), epicId, epic.getName(), epic.getDescription(), epic.getStatus().toString(),
                        duration, subTask.getStartTime().format(DateTimeFormatter.ISO_DATE_TIME)) +
                String.format("{\"epicId\":%d,\"id\":%d,\"name\":\"%s\",\"description\":\"%s\",\"status\":\"%s\",\"duration\":%d,\"startTime\":\"%s\"}",
                        epicId, subTaskId, subTask.getName(), subTask.getDescription(), subTask.getStatus().toString(),
                        duration, subTask.getStartTime().format(DateTimeFormatter.ISO_DATE_TIME)) +
                "]";
        assertDoesNotThrow(() -> taskManager.save());
        String serverData = kvsTaskClient.load(HISTORY_KEY);
        assertEquals(serverDataExpected, serverData);
    }

    @Test
    public void shouldThrowExceptionLoadingFromWrongServerUri() throws IOException{
        assertThrows(KVServerConnectionException.class, () -> loadFromServer("http://fail.server:9090"));
    }
}
