package server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import managers.impl.HttpTaskManager;
import org.junit.jupiter.api.*;
import tasks.Epic;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {

    private static final String TASK_SERVER_URI = "http://localhost:8080";
    private static final String KVSERVER_URI = "http://localhost:8078";

    private KVServer kvServer;
    private HttpTaskServer taskServer;
    private HttpClient taskManagerClient;
    private HttpTaskManager manager;
    private Gson gson;


    @BeforeEach
    void setUp() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        manager = new HttpTaskManager(KVSERVER_URI);
        taskServer = new HttpTaskServer(manager);
        taskServer.start();
        taskManagerClient = HttpClient.newHttpClient();
        gson = JsonAdapter.getGson();
    }

    @AfterEach
    void tearDown() {
        taskServer.stop();
        kvServer.stop();
    }

    @Test
    @DisplayName("Создание новой задачи типа Task и получение задачи по id")
    public void shouldCreateAndGetTaskByIdFromServer() {
        int taskId = 0;
        URI uriCreateTask = URI.create(TASK_SERVER_URI + "/tasks/task");
        Task task = new Task(taskId, "Таск", Status.IN_PROGRESS.name(), "Описание", LocalDateTime.now().minusDays(1L), 120L);
        String taskJson = gson.toJson(task, Task.class);
        HttpRequest taskPostRequest = HttpRequest.newBuilder().uri(uriCreateTask).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        try {
            HttpResponse<String> taskPostResponse = taskManagerClient.send(taskPostRequest, HttpResponse.BodyHandlers.ofString());
            taskId = Integer.parseInt(taskPostResponse.body());
            assertEquals(201, taskPostResponse.statusCode());
        } catch (IOException | InterruptedException e) {
            fail("Ошибка при вызове POST /tasks/task");
            return;
        }
        catch (NumberFormatException e) {
            fail("Ошибка: в ответе метода POST /tasks/task получено не число типа int");
        }
        task.setId(taskId);
        URI uriGetTask = URI.create(TASK_SERVER_URI + "/tasks/task/" + String.format("?id=%d", taskId));
        HttpRequest taskGetRequest = HttpRequest.newBuilder().uri(uriGetTask).GET().build();
        try {
            HttpResponse<String> taskGetResponse = taskManagerClient.send(taskGetRequest, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, taskGetResponse.statusCode());
            Task taskFromServer = gson.fromJson(taskGetResponse.body(), Task.class);
            assertEquals(task, taskFromServer);
        }
        catch (IOException | InterruptedException e) {
            fail(String.format("Ошибка при вызове GET /tasks/task/?id=%d", taskId));
        }
        catch (JsonSyntaxException e) {
            fail(String.format("Ошибка при десериализации объекта типа Task"));
        }
    }

    @Test
    @DisplayName("Создание новой подзадачи типа SubTask и получение подзадачи по id")
    public void shouldCreateAndGetSubTaskByIdFromServer() {
        int subTaskId = 0;
        int epicId = 0;
        URI uriCreateSubTask = URI.create(TASK_SERVER_URI + "/tasks/subtask");
        URI uriCreateEpic = URI.create(TASK_SERVER_URI + "/tasks/epic");
        Epic epic = new Epic(epicId, "Эпичная задача", Status.NEW.name(), "Описание");

        String epicJson = gson.toJson(epic, Epic.class);
        HttpRequest epicPostRequest = HttpRequest.newBuilder().uri(uriCreateEpic).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();

        try {
            HttpResponse<String> epicPostResponse = taskManagerClient.send(epicPostRequest, HttpResponse.BodyHandlers.ofString());
            epicId = Integer.parseInt(epicPostResponse.body());
        }
        catch (IOException | InterruptedException e) {
            fail("Ошибка при вызове POST /tasks/epic");
            return;
        }
        catch (NumberFormatException e) {
            fail("Ошибка: в ответе метода POST /tasks/epic получено не число типа int");
            return;
        }

        SubTask subTask = new SubTask(subTaskId, "Подзадача", Status.IN_PROGRESS.name(), "Описание",
                epicId, LocalDateTime.now().minusDays(1L), 120L);
        String subTaskJson = gson.toJson(subTask, SubTask.class);
        HttpRequest subTaskPostRequest = HttpRequest.newBuilder().uri(uriCreateSubTask).POST(HttpRequest.BodyPublishers.ofString(subTaskJson)).build();

        try {
            HttpResponse<String> subTaskPostResponse = taskManagerClient.send(subTaskPostRequest, HttpResponse.BodyHandlers.ofString());
            subTaskId = Integer.parseInt(subTaskPostResponse.body());
            assertEquals(201, subTaskPostResponse.statusCode());
        } catch (IOException | InterruptedException e) {
            fail("Ошибка при вызове POST /tasks/subtask");
            return;
        }
        catch (NumberFormatException e) {
            fail("Ошибка: в ответе метода POST /tasks/subtask получено не число типа int");
            return;
        }
        subTask.setId(subTaskId);
        URI uriGetSubTask = URI.create(TASK_SERVER_URI + "/tasks/subtask/" + String.format("?id=%d", subTaskId));
        HttpRequest subTaskGetRequest = HttpRequest.newBuilder().uri(uriGetSubTask).GET().build();
        try {
            HttpResponse<String> subTaskGetResponse = taskManagerClient.send(subTaskGetRequest, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, subTaskGetResponse.statusCode());
            SubTask subTaskFromServer = gson.fromJson(subTaskGetResponse.body(), SubTask.class);
            assertEquals(subTask, subTaskFromServer);
        }
        catch (IOException | InterruptedException e) {
            fail(String.format("Ошибка при вызове GET /tasks/subtask/?id=%d", subTaskId));
        }
        catch (JsonSyntaxException e) {
            fail(String.format("Ошибка при десериализации объекта типа SubTask"));
        }
    }
}