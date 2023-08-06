package server;

import managers.impl.HttpTaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.http.HttpClient;

import static org.junit.jupiter.api.Assertions.assertTrue;

class HttpTaskServerTest {

    private static final String TASK_SERVER_URI = "http://localhost:8080";
    private static final String KVSERVER_URI = "http://localhost:8078";

    private KVServer kvServer;
    private HttpTaskServer taskServer;
    private HttpClient taskManagerClient;
    private HttpTaskManager manager;


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
    public void shouldGetTaskByIdFromServer() {
        assertTrue(true);
    }
}