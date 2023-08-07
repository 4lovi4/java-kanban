package server;

import com.google.gson.Gson;
import managers.impl.HttpTaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Status;
import tasks.Task;

import java.io.IOException;
import java.net.http.HttpClient;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertTrue;

class HttpTaskServerTest {

    private static final String TASK_SERVER_URI = "http://localhost:8080";
    private static final String KVSERVER_URI = "http://localhost:8078";

    private KVServer kvServer;
    private HttpTaskServer taskServer;
    private HttpClient taskManagerClient;
    private HttpTaskManager manager;
    private static Gson gson;

    @BeforeAll
    public static void setUpAll() {
        gson = JsonAdapter.getGson();
    }

    @BeforeEach
    void setUp() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        manager = new HttpTaskManager(KVSERVER_URI);
        taskServer = new HttpTaskServer(manager);
        taskServer.start();
        taskManagerClient = HttpClient.newHttpClient();
    }

    @AfterEach
    void tearDown() {
        taskServer.stop();
        kvServer.stop();
    }

    @Test
    public void shouldCreateAndGetTaskByIdFromServer() {
        Task task = new Task(0, "Таск", Status.IN_PROGRESS.name(), "Описание", LocalDateTime.now().minusDays(1L), 120L);
        int taskId = manager.addNewTask(task);
        task.setId(taskId);
    }
}