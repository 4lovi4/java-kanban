package server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import managers.impl.HttpTaskManager;
import org.junit.jupiter.api.*;
import tasks.Epic;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {

    private static final String TASK_SERVER_URI = "http://localhost:8080";
    private static final String KVSERVER_URI = "http://localhost:8078";

    private KVServer kvServer;
    private HttpTaskServer taskServer;
    private HttpClient taskManagerClient;
    private HttpTaskManager manager;
    private Gson gson;
    private Type taskListType;


    @BeforeEach
    void setUp() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        manager = new HttpTaskManager(KVSERVER_URI);
        taskServer = new HttpTaskServer(manager);
        taskServer.start();
        taskManagerClient = HttpClient.newHttpClient();
        gson = JsonAdapter.getGson();
        taskListType = new TypeToken<ArrayList<Task>>(){}.getType();
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
        Task task = new Task(taskId,
                "Таск",
                Status.IN_PROGRESS.name(),
                "Описание",
                LocalDateTime.now().minusDays(1L),
                120L);
        String taskJson = gson.toJson(task, Task.class);
        HttpRequest taskPostRequest = HttpRequest.newBuilder()
                .uri(uriCreateTask)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        try {
            HttpResponse<String> taskPostResponse = taskManagerClient
                    .send(taskPostRequest, HttpResponse.BodyHandlers.ofString());
            taskId = Integer.parseInt(taskPostResponse.body());
            assertEquals(201, taskPostResponse.statusCode());
        } catch (IOException | InterruptedException e) {
            fail("Ошибка при вызове POST /tasks/task");
            return;
        } catch (NumberFormatException e) {
            fail("Ошибка: в ответе метода POST /tasks/task получено не число типа int");
        }
        task.setId(taskId);
        URI uriGetTask = URI.create(TASK_SERVER_URI + "/tasks/task/" + String.format("?id=%d", taskId));
        HttpRequest taskGetRequest = HttpRequest.newBuilder().uri(uriGetTask).GET().build();
        try {
            HttpResponse<String> taskGetResponse = taskManagerClient
                    .send(taskGetRequest, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, taskGetResponse.statusCode());
            Task taskFromServer = gson.fromJson(taskGetResponse.body(), Task.class);
            assertEquals(task, taskFromServer);
        } catch (IOException | InterruptedException e) {
            fail(String.format("Ошибка при вызове GET /tasks/task/?id=%d", taskId));
        } catch (JsonSyntaxException e) {
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
        Epic epic = new Epic(epicId,
                "Эпичная задача",
                Status.NEW.name(),
                "Описание");

        String epicJson = gson.toJson(epic, Epic.class);
        HttpRequest epicPostRequest = HttpRequest.newBuilder()
                .uri(uriCreateEpic)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        try {
            HttpResponse<String> epicPostResponse = taskManagerClient
                    .send(epicPostRequest, HttpResponse.BodyHandlers.ofString());
            epicId = Integer.parseInt(epicPostResponse.body());
        } catch (IOException | InterruptedException e) {
            fail("Ошибка при вызове POST /tasks/epic");
            return;
        } catch (NumberFormatException e) {
            fail("Ошибка: в ответе метода POST /tasks/epic получено не число типа int");
            return;
        }

        SubTask subTask = new SubTask(subTaskId,
                "Подзадача",
                Status.IN_PROGRESS.name(),
                "Описание",
                epicId,
                LocalDateTime.now().minusDays(1L),
                120L);
        String subTaskJson = gson.toJson(subTask, SubTask.class);
        HttpRequest subTaskPostRequest = HttpRequest.newBuilder()
                .uri(uriCreateSubTask)
                .POST(HttpRequest.BodyPublishers.ofString(subTaskJson))
                .build();

        try {
            HttpResponse<String> subTaskPostResponse = taskManagerClient
                    .send(subTaskPostRequest, HttpResponse.BodyHandlers.ofString());
            subTaskId = Integer.parseInt(subTaskPostResponse.body());
            assertEquals(201, subTaskPostResponse.statusCode());
        } catch (IOException | InterruptedException e) {
            fail("Ошибка при вызове POST /tasks/subtask");
            return;
        } catch (NumberFormatException e) {
            fail("Ошибка: в ответе метода POST /tasks/subtask получено не число типа int");
            return;
        }
        subTask.setId(subTaskId);
        URI uriGetSubTask = URI.create(TASK_SERVER_URI + "/tasks/subtask/" + String.format("?id=%d", subTaskId));
        HttpRequest subTaskGetRequest = HttpRequest.newBuilder()
                .uri(uriGetSubTask)
                .GET()
                .build();
        try {
            HttpResponse<String> subTaskGetResponse = taskManagerClient
                    .send(subTaskGetRequest, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, subTaskGetResponse.statusCode());
            SubTask subTaskFromServer = gson.fromJson(subTaskGetResponse.body(), SubTask.class);
            assertEquals(subTask, subTaskFromServer);
        } catch (IOException | InterruptedException e) {
            fail(String.format("Ошибка при вызове GET /tasks/subtask/?id=%d", subTaskId));
        } catch (JsonSyntaxException e) {
            fail(String.format("Ошибка при десериализации объекта типа SubTask"));
        }
    }

    @Test
    @DisplayName("Создание новой задачи типа Epic и получение эпика по id")
    public void shouldCreateAndGetEpicIdFromServer() {
        int epicId = 0;
        URI uriCreateEpic = URI.create(TASK_SERVER_URI + "/tasks/epic");

        Epic epic = new Epic(epicId,
                "Эпичная задача",
                Status.NEW.name(),
                "Описание");

        String epicJson = gson.toJson(epic, Epic.class);
        HttpRequest epicPostRequest = HttpRequest
                .newBuilder()
                .uri(uriCreateEpic)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        try {
            HttpResponse<String> epicPostResponse = taskManagerClient
                    .send(epicPostRequest, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, epicPostResponse.statusCode());
            epicId = Integer.parseInt(epicPostResponse.body());
        } catch (IOException | InterruptedException e) {
            fail("Ошибка при вызове POST /tasks/epic");
            return;
        } catch (NumberFormatException e) {
            fail("Ошибка: в ответе метода POST /tasks/epic получено не число типа int");
            return;
        }

        epic.setId(epicId);
        URI uriGetEpic = URI.create(TASK_SERVER_URI + "/tasks/epic/" + String.format("?id=%d", epicId));
        HttpRequest epicGetRequest = HttpRequest.newBuilder()
                .uri(uriGetEpic)
                .GET()
                .build();
        try {
            HttpResponse<String> epicGetResponse = taskManagerClient
                    .send(epicGetRequest, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, epicGetResponse.statusCode());
            Epic epicFromServer = gson.fromJson(epicGetResponse.body(), Epic.class);
            assertEquals(epic, epicFromServer);
        } catch (IOException | InterruptedException e) {
            fail(String.format("Ошибка при вызове GET /tasks/epic/?id=%d", epicId));
        } catch (JsonSyntaxException e) {
            fail(String.format("Ошибка при десериализации объекта типа Epic"));
        }
    }

    @Test
    @DisplayName("Удаление задачи типа Task, запрос GET /tasks/task?id= возвращает код 404")
    public void shouldDeleteTaskFromServer() {
        int taskId = 0;
        URI uriCreateTask = URI.create(TASK_SERVER_URI + "/tasks/task");
        Task task = new Task(taskId,
                "Таск",
                Status.DONE.name(),
                "Описание",
                LocalDateTime.now().minusDays(1L),
                120L);
        String taskJson = gson.toJson(task, Task.class);
        HttpRequest taskPostRequest = HttpRequest.newBuilder()
                .uri(uriCreateTask)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        try {
            HttpResponse<String> taskPostResponse = taskManagerClient
                    .send(taskPostRequest, HttpResponse.BodyHandlers.ofString());
            taskId = Integer.parseInt(taskPostResponse.body());
            assertEquals(201, taskPostResponse.statusCode());
        } catch (IOException | InterruptedException e) {
            fail("Ошибка при вызове POST /tasks/task");
            return;
        } catch (NumberFormatException e) {
            fail("Ошибка: в ответе метода POST /tasks/task получено не число типа int");
        }

        URI uriDeleteTask = URI.create(TASK_SERVER_URI + "/tasks/task/" + String.format("?id=%d", taskId));
        HttpRequest taskDeleteRequest = HttpRequest.newBuilder()
                .uri(uriDeleteTask)
                .DELETE()
                .build();
        try {
            HttpResponse<String> taskDeleteResponse = taskManagerClient
                    .send(taskDeleteRequest, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, taskDeleteResponse.statusCode());
            String messageFromServer = taskDeleteResponse.body();
            assertEquals(String.format("Задача id = %d удалена", taskId), messageFromServer);
        } catch (IOException | InterruptedException e) {
            fail(String.format("Ошибка при вызове DELETE /tasks/task/?id=%d", taskId));
        } catch (JsonSyntaxException e) {
            fail(String.format("Ошибка при удалении объекта типа Task"));
        }

        URI uriGetTask = URI.create(TASK_SERVER_URI + "/tasks/task/" + String.format("?id=%d", taskId));
        HttpRequest taskGetRequest = HttpRequest.newBuilder().uri(uriGetTask).GET().build();
        try {
            HttpResponse<String> taskGetResponse = taskManagerClient
                    .send(taskGetRequest, HttpResponse.BodyHandlers.ofString());
            assertEquals(404, taskGetResponse.statusCode());
            String errorMessageFromServer = taskGetResponse.body();
            assertEquals(String.format("Задача %d не найдена", taskId), errorMessageFromServer);
        } catch (IOException | InterruptedException e) {
            fail(String.format("Ошибка при вызове GET /tasks/task/?id=%d", taskId));
        }
    }

    @Test
    @DisplayName("Удаление задачи типа Epic, запрос GET /tasks/epic?id= возвращает код 404")
    public void shouldDeleteEpicFromServer() {
        int epicId = 0;
        URI uriCreateTask = URI.create(TASK_SERVER_URI + "/tasks/epic");
        Epic epic = new Epic(epicId,
                "Эпик",
                Status.NEW.name(),
                "Описание");
        String epicJson = gson.toJson(epic, Epic.class);
        HttpRequest epicPostRequest = HttpRequest.newBuilder()
                .uri(uriCreateTask)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();
        try {
            HttpResponse<String> epicPostResponse = taskManagerClient
                    .send(epicPostRequest, HttpResponse.BodyHandlers.ofString());
            epicId = Integer.parseInt(epicPostResponse.body());
            assertEquals(201, epicPostResponse.statusCode());
        } catch (IOException | InterruptedException e) {
            fail("Ошибка при вызове POST /tasks/task");
            return;
        } catch (NumberFormatException e) {
            fail("Ошибка: в ответе метода POST /tasks/task получено не число типа int");
        }

        URI uriDeleteEpic = URI.create(TASK_SERVER_URI + "/tasks/epic/" + String.format("?id=%d", epicId));
        HttpRequest epicDeleteRequest = HttpRequest.newBuilder()
                .uri(uriDeleteEpic)
                .DELETE()
                .build();
        try {
            HttpResponse<String> epicDeleteResponse = taskManagerClient
                    .send(epicDeleteRequest, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, epicDeleteResponse.statusCode());
            String messageFromServer = epicDeleteResponse.body();
            assertEquals(String.format("Эпик id = %d удален", epicId), messageFromServer);
        } catch (IOException | InterruptedException e) {
            fail(String.format("Ошибка при вызове DELETE /tasks/epic/?id=%d", epicId));
        } catch (JsonSyntaxException e) {
            fail(String.format("Ошибка при удалении объекта типа Epic"));
        }

        URI uriGetTask = URI.create(TASK_SERVER_URI + "/tasks/epic/" + String.format("?id=%d", epicId));
        HttpRequest epicGetRequest = HttpRequest.newBuilder().uri(uriGetTask).GET().build();
        try {
            HttpResponse<String> epicGetResponse = taskManagerClient
                    .send(epicGetRequest, HttpResponse.BodyHandlers.ofString());
            assertEquals(404, epicGetResponse.statusCode());
            String errorMessageFromServer = epicGetResponse.body();
            assertEquals(String.format("Эпик %d не найден", epicId), errorMessageFromServer);
        } catch (IOException | InterruptedException e) {
            fail(String.format("Ошибка при вызове GET /tasks/epic/?id=%d", epicId));
        }
    }

    @Test
    @DisplayName("Удаление подзадачи типа SubTask, запрос GET /tasks/subtask?id= возвращает код 404")
    public void shouldDeleteSubTaskFromServer() {
        int subTaskId = 0;
        URI uriCreateSubTask = URI.create(TASK_SERVER_URI + "/tasks/subtask");
        SubTask subTask = new SubTask(subTaskId,
                "Подзадача",
                Status.DONE.name(),
                "Описание",
                100,
                LocalDateTime.now().minusDays(1L),
                120L);
        String subTaskJson = gson.toJson(subTask, SubTask.class);
        HttpRequest subTaskPostRequest = HttpRequest.newBuilder()
                .uri(uriCreateSubTask)
                .POST(HttpRequest.BodyPublishers.ofString(subTaskJson))
                .build();
        try {
            HttpResponse<String> subTaskPostResponse = taskManagerClient
                    .send(subTaskPostRequest, HttpResponse.BodyHandlers.ofString());
            subTaskId = Integer.parseInt(subTaskPostResponse.body());
            assertEquals(201, subTaskPostResponse.statusCode());
        } catch (IOException | InterruptedException e) {
            fail("Ошибка при вызове POST /tasks/subtask");
            return;
        } catch (NumberFormatException e) {
            fail("Ошибка: в ответе метода POST /tasks/subtask получено не число типа int");
        }

        URI uriDeleteSubTask = URI.create(TASK_SERVER_URI + "/tasks/subtask/" + String.format("?id=%d", subTaskId));
        HttpRequest subTaskDeleteRequest = HttpRequest.newBuilder()
                .uri(uriDeleteSubTask)
                .DELETE()
                .build();
        try {
            HttpResponse<String> subTaskDeleteResponse = taskManagerClient
                    .send(subTaskDeleteRequest, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, subTaskDeleteResponse.statusCode());
            String messageFromServer = subTaskDeleteResponse.body();
            assertEquals(String.format("Подзадача id = %d удалена", subTaskId), messageFromServer);
        } catch (IOException | InterruptedException e) {
            fail(String.format("Ошибка при вызове DELETE /tasks/subtask/?id=%d", subTaskId));
        } catch (JsonSyntaxException e) {
            fail(String.format("Ошибка при удалении объекта типа SubTask"));
        }

        URI uriGetSubTask = URI.create(TASK_SERVER_URI + "/tasks/subtask/" + String.format("?id=%d", subTaskId));
        HttpRequest subTaskGetRequest = HttpRequest.newBuilder().uri(uriGetSubTask).GET().build();
        try {
            HttpResponse<String> subTaskGetResponse = taskManagerClient
                    .send(subTaskGetRequest, HttpResponse.BodyHandlers.ofString());
            assertEquals(404, subTaskGetResponse.statusCode());
            String errorMessageFromServer = subTaskGetResponse.body();
            assertEquals(String.format("Подзадача %d не найдена", subTaskId), errorMessageFromServer);
        } catch (IOException | InterruptedException e) {
            fail(String.format("Ошибка при вызове GET /tasks/subtask/?id=%d", subTaskId));
        }
    }

    @Test
    @DisplayName("Получение списка всех задач методом GET /tasks/")
    public void shouldGetAllTasks() throws InterruptedException {
        int subTaskId = 0;
        int epicId = 0;
        int taskId = 0;
        URI uriCreateTask = URI.create(TASK_SERVER_URI + "/tasks/task");
        URI uriCreateSubTask = URI.create(TASK_SERVER_URI + "/tasks/subtask");
        URI uriCreateEpic = URI.create(TASK_SERVER_URI + "/tasks/epic");

        Task task = new Task(taskId,
                "Задача",
                Status.DONE.name(),
                "Описание",
                LocalDateTime.now().minusHours(3L),
                90L);
        Epic epic = new Epic(epicId,
                "Эпичная задача",
                Status.NEW.name(),
                "Описание");
        SubTask subTask = new SubTask(subTaskId,
                "Подзадача",
                Status.IN_PROGRESS.name(),
                "Описание",
                epicId,
                LocalDateTime.now().minusDays(1L),
                120L);

        try {
            String epicJson = gson.toJson(epic, Epic.class);
            HttpRequest epicPostRequest = HttpRequest.newBuilder()
                    .uri(uriCreateEpic)
                    .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                    .build();
            HttpResponse<String> epicPostResponse = taskManagerClient
                    .send(epicPostRequest, HttpResponse.BodyHandlers.ofString());
            epicId = Integer.parseInt(epicPostResponse.body());
        }
        catch (IOException | InterruptedException e) {
            fail("Ошибка при вызове добавления новых задач типа Epic");
            return;
        } catch (NumberFormatException e) {
            fail("Ошибка: в ответе методов создания эпика получено не число типа int");
            return;
        }

        Thread.sleep(1000L);

        try {
            String taskJson = gson.toJson(task, Task.class);
            HttpRequest taskPostRequest = HttpRequest.newBuilder()
                    .uri(uriCreateTask)
                    .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                    .build();
            HttpResponse<String> taskPostResponse = taskManagerClient
                    .send(taskPostRequest, HttpResponse.BodyHandlers.ofString());
            taskId = Integer.parseInt(taskPostResponse.body());
        }
        catch (IOException | InterruptedException e) {
            fail("Ошибка при вызове добавления новых задач типа Task");
            return;
        } catch (NumberFormatException e) {
            fail("Ошибка: в ответе методов создания задачи получено не число типа int");
            return;
        }

        Thread.sleep(1000L);

        subTask.setEpicId(epicId);

        try {
            String subTaskJson = gson.toJson(subTask, SubTask.class);
            HttpRequest subTaskPostRequest = HttpRequest.newBuilder()
                    .uri(uriCreateSubTask)
                    .POST(HttpRequest.BodyPublishers.ofString(subTaskJson))
                    .build();
            HttpResponse<String> subTaskPostResponse = taskManagerClient
                    .send(subTaskPostRequest, HttpResponse.BodyHandlers.ofString());
            subTaskId = Integer.parseInt(subTaskPostResponse.body());
        }
        catch (IOException | InterruptedException e) {
            fail("Ошибка при вызове добавления новых задач типа SubTask");
            return;
        } catch (NumberFormatException e) {
            fail("Ошибка: в ответе методов создания подзадачи получено не число типа int");
            return;
        }

        task.setId(taskId);
        epic.setId(epicId);
        subTask.setId(subTaskId);
        URI uriGetTasks = URI.create(TASK_SERVER_URI + "/tasks/");
        HttpRequest allTasksGetRequest = HttpRequest.newBuilder()
                .uri(uriGetTasks)
                .GET()
                .build();
        try {
            HttpResponse<String> allTasksGetResponse = taskManagerClient
                    .send(allTasksGetRequest, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, allTasksGetResponse.statusCode());
            List<Task> allTasksFromServer = gson.fromJson(allTasksGetResponse.body(), taskListType);

            assertEquals(3, allTasksFromServer.size());
            int finalEpicId = epicId;
            int finalTaskId = taskId;
            int finalSubTaskId = subTaskId;
            assertAll( () -> assertTrue(allTasksFromServer.stream().map(t -> t.getId())
                            .collect(Collectors
                                    .toSet()).contains(Integer.valueOf(finalEpicId))),
                    () ->  assertTrue(allTasksFromServer.stream().map(t -> t.getId())
                            .collect(Collectors
                                    .toSet()).contains(Integer.valueOf(finalTaskId))),
                    () -> assertTrue(allTasksFromServer.stream().map(t -> t.getId())
                            .collect(Collectors
                                    .toSet()).contains(Integer.valueOf(finalSubTaskId)))
            );
        } catch (IOException | InterruptedException e) {
            fail("Ошибка при вызове GET /tasks/");
        }
    }

    @Test
    @DisplayName("Возвращает пустой список при запросе всех задач, которые не добавлены на сервер")
    public void shouldReturnEmptyTasksPrioritizedList() {
        URI uriGetTasks = URI.create(TASK_SERVER_URI + "/tasks/");
        HttpRequest allTasksGetRequest = HttpRequest.newBuilder()
                .uri(uriGetTasks)
                .GET()
                .build();
        try {
            HttpResponse<String> allTasksGetResponse = taskManagerClient
                    .send(allTasksGetRequest, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, allTasksGetResponse.statusCode());
            List<Task> allTasksFromServer = gson.fromJson(allTasksGetResponse.body(), taskListType);
            assertTrue(allTasksFromServer.isEmpty());
        }
        catch (IOException | InterruptedException e) {
            fail("Ошибка при вызове GET /tasks/");
        }
    }
}