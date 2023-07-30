package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import managers.Managers;
import managers.TaskManager;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskType;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;

public class HttpTaskServer {
    private final static int TASK_SERVER_PORT = 8080;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final HttpServer server;
    protected TaskManager manager;
    Gson gson;


    public HttpTaskServer(TaskManager manager) throws IOException {
        server = HttpServer.create(new InetSocketAddress("localhost", TASK_SERVER_PORT), 0);
        this.manager = manager;
        server.createContext("/tasks", new TaskManagerHandler());
        gson = new Gson();
    }

    public void start() {
        System.out.println("Сервер менеджер задач запущен и доступен по адресу " + server.getAddress().toString());
        server.start();
    }

    public void stop() {
        System.out.println("Сервер менеджера задач остановлен");
        server.stop(1);
    }


    private class TaskManagerHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            String query = exchange.getRequestURI().getRawQuery();
            InputStream stream = exchange.getRequestBody();
            String body = new String(stream.readAllBytes(), DEFAULT_CHARSET);

            if (method.equals("GET")) {
                if (path.equals("/tasks")) {
                    ArrayList<Task> prioritizedTasks = manager.getPrioritizedTasks();
                    byte[] tasksPayload = gson.toJson(prioritizedTasks).getBytes();
                    exchange.sendResponseHeaders(200, tasksPayload.length);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(tasksPayload);
                    }
                }
                if (path.equals("/tasks/task")) {
                    String[] queryIdParam = query.split("=");
                    if (!queryIdParam[0].equals("id")) {
                        byte[] message = "Неверный формат id задачи в запросе".getBytes();
                        exchange.sendResponseHeaders(400, message.length);
                        try (OutputStream os = exchange.getResponseBody()) {
                            os.write(message);
                            return;
                        }
                    }

                    int taskId = 0;
                    try {
                        taskId = Integer.parseInt(queryIdParam[1]);
                    } catch (NumberFormatException | NullPointerException e) {
                        byte[] message = "Неверный формат id задачи в запросе".getBytes();
                        exchange.sendResponseHeaders(400, message.length);
                        try (OutputStream os = exchange.getResponseBody()) {
                            os.write(message);
                            return;
                        }
                    }
                    String taskData =  getTaskById(TaskType.TASK, taskId);
                    if (taskData.equals("")) {
                        byte[] message = String.format("Задача %d не найдена", taskId).getBytes();
                        exchange.sendResponseHeaders(404, message.length);
                        try (OutputStream os = exchange.getResponseBody()) {
                            os.write(message);
                        }
                    }
                    else {
                        byte[] taskPayload = taskData.getBytes();
                        exchange.sendResponseHeaders(200, taskPayload.length);
                        try (OutputStream os = exchange.getResponseBody()) {
                            os.write(taskPayload);
                        }
                    }
                }
                else if (path.equals("/tasks/epic")) {

                }
                else if (path.equals("/tasks/subtask")) {

                }
            }
            if (method.equals("DELETE") && path.matches("/tasks/task")) {
            }

            if (method.equals("POST") && path.matches("/tasks/task")) {

            }
        }

        private String getTaskById(TaskType taskType, int id) {
            String taskPayload = "";
            switch (taskType) {
                case TASK: {
                    Task task = manager.getTask(id);
                    if (!Objects.isNull(task)) {
                        taskPayload = gson.toJson(task, Task.class);
                    }
                    break;
                }
                case EPIC: {
                    Epic epic = manager.getEpic(id);
                    if (!Objects.isNull(epic)) {
                        taskPayload = gson.toJson(epic, Epic.class);
                    }
                    break;
                }
                case SUBTASK: {
                    SubTask subTask = manager.getSubTask(id);
                    if (!Objects.isNull(subTask)) {
                        subTask = manager.getSubTask(id);
                        taskPayload = gson.toJson(subTask, SubTask.class);
                    }
                    break;
                }
            }
            return taskPayload;
        }
    }
}
