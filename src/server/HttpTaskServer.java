package server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
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
import java.util.List;
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
        gson = JsonAdapter.getGson();
    }

    public void start() {
        System.out.println("Сервер менеджера задач запущен и доступен по адресу " + server.getAddress().toString());
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
                if (path.matches("^/tasks/?$")) {
                    ArrayList<Task> prioritizedTasks = manager.getPrioritizedTasks();
                    byte[] tasksPayload = gson.toJson(prioritizedTasks).getBytes();
                    exchange.sendResponseHeaders(200, tasksPayload.length);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(tasksPayload);
                    }
                }

                if (path.matches("^/tasks/task/?\\?id=\\d+$")) {
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
                    String taskData = getTaskById(TaskType.TASK, taskId);
                    if (taskData.equals("")) {
                        byte[] message = String.format("Задача %d не найдена", taskId).getBytes();
                        exchange.sendResponseHeaders(404, message.length);
                        try (OutputStream os = exchange.getResponseBody()) {
                            os.write(message);
                        }
                    } else {
                        byte[] taskPayload = taskData.getBytes();
                        exchange.sendResponseHeaders(200, taskPayload.length);
                        try (OutputStream os = exchange.getResponseBody()) {
                            os.write(taskPayload);
                        }
                    }
                } else if (path.matches("^/tasks/epic/?\\?id=\\d+$")) {
                    String[] queryIdParam = query.split("=");
                    if (!queryIdParam[0].equals("id")) {
                        byte[] message = "Неверный формат id эпика в запросе".getBytes();
                        exchange.sendResponseHeaders(400, message.length);
                        try (OutputStream os = exchange.getResponseBody()) {
                            os.write(message);
                            return;
                        }
                    }

                    int epicId = 0;
                    try {
                        epicId = Integer.parseInt(queryIdParam[1]);
                    } catch (NumberFormatException | NullPointerException e) {
                        byte[] message = "Неверный формат id эпика в запросе".getBytes();
                        exchange.sendResponseHeaders(400, message.length);
                        try (OutputStream os = exchange.getResponseBody()) {
                            os.write(message);
                            return;
                        }
                    }
                    String taskData = getTaskById(TaskType.EPIC, epicId);
                    if (taskData.equals("")) {
                        byte[] message = String.format("Эпик %d не найдена", epicId).getBytes();
                        exchange.sendResponseHeaders(404, message.length);
                        try (OutputStream os = exchange.getResponseBody()) {
                            os.write(message);
                        }
                    } else {
                        byte[] epicPayload = taskData.getBytes();
                        exchange.sendResponseHeaders(200, epicPayload.length);
                        try (OutputStream os = exchange.getResponseBody()) {
                            os.write(epicPayload);
                        }
                    }
                } else if (path.matches("^/tasks/subtask/?\\?id=\\d$")) {
                    String[] queryIdParam = query.split("=");
                    if (!queryIdParam[0].equals("id")) {
                        byte[] message = "Неверный формат id подзадачи в запросе".getBytes();
                        exchange.sendResponseHeaders(400, message.length);
                        try (OutputStream os = exchange.getResponseBody()) {
                            os.write(message);
                            return;
                        }
                    }

                    int subTaskId = 0;
                    try {
                        subTaskId = Integer.parseInt(queryIdParam[1]);
                    } catch (NumberFormatException | NullPointerException e) {
                        byte[] message = "Неверный формат id подзадачи в запросе".getBytes();
                        exchange.sendResponseHeaders(400, message.length);
                        try (OutputStream os = exchange.getResponseBody()) {
                            os.write(message);
                            return;
                        }
                    }
                    String taskData = getTaskById(TaskType.TASK, subTaskId);
                    if (taskData.equals("")) {
                        byte[] message = String.format("Подзадача %d не найдена", subTaskId).getBytes();
                        exchange.sendResponseHeaders(404, message.length);
                        try (OutputStream os = exchange.getResponseBody()) {
                            os.write(message);
                        }
                    } else {
                        byte[] subTaskPayload = taskData.getBytes();
                        exchange.sendResponseHeaders(200, subTaskPayload.length);
                        try (OutputStream os = exchange.getResponseBody()) {
                            os.write(subTaskPayload);
                        }
                    }
                } else if (path.matches("^/tasks/history/?$")) {
                    List<Task> historyTask = manager.getHistory();
                    byte[] tasksPayload = gson.toJson(historyTask).getBytes();
                    exchange.sendResponseHeaders(200, tasksPayload.length);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(tasksPayload);
                    }
                }
                else {
                    byte[] message = String.format("Задан неправильный endpoint GET %s", path).getBytes();
                    exchange.sendResponseHeaders(400, message.length);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(message);
                    }
                }
            }
            if (method.equals("DELETE")) {
                if (path.equals("^/tasks/task/?.*$")) {
                    if (query.isEmpty()) {
                        deleteTasks(TaskType.TASK, -1);
                        byte[] message = "Все обычные задачи удалены".getBytes();
                        exchange.sendResponseHeaders(204, message.length);
                        try (OutputStream os = exchange.getResponseBody()) {
                            os.write(message);
                        }
                    } else {
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
                        if (taskId <= 0) {
                            byte[] message = "id задачи должен быть положительным числом".getBytes();
                            exchange.sendResponseHeaders(400, message.length);
                            try (OutputStream os = exchange.getResponseBody()) {
                                os.write(message);
                                return;
                            }
                        } else {
                            deleteTasks(TaskType.TASK, taskId);
                            byte[] message = String.format("Задача id = %d удалена", taskId).getBytes();
                            exchange.sendResponseHeaders(204, message.length);
                            try (OutputStream os = exchange.getResponseBody()) {
                                os.write(message);
                            }
                        }
                    }
                }
                else if (path.matches("^/tasks/subtask/?.*$")) {
                    if (query.isEmpty()) {
                        deleteTasks(TaskType.SUBTASK, -1);
                        byte[] message = "Все подзадачи удалены".getBytes();
                        exchange.sendResponseHeaders(204, message.length);
                        try (OutputStream os = exchange.getResponseBody()) {
                            os.write(message);
                        }
                    } else {
                        String[] queryIdParam = query.split("=");
                        if (!queryIdParam[0].equals("id")) {
                            byte[] message = "Неверный формат id подзадачи в запросе".getBytes();
                            exchange.sendResponseHeaders(400, message.length);
                            try (OutputStream os = exchange.getResponseBody()) {
                                os.write(message);
                                return;
                            }
                        }

                        int subTaskId = 0;
                        try {
                            subTaskId = Integer.parseInt(queryIdParam[1]);
                        } catch (NumberFormatException | NullPointerException e) {
                            byte[] message = "Неверный формат id подзадачи в запросе".getBytes();
                            exchange.sendResponseHeaders(400, message.length);
                            try (OutputStream os = exchange.getResponseBody()) {
                                os.write(message);
                                return;
                            }
                        }
                        if (subTaskId <= 0) {
                            byte[] message = "id подзадачи должен быть положительным числом".getBytes();
                            exchange.sendResponseHeaders(400, message.length);
                            try (OutputStream os = exchange.getResponseBody()) {
                                os.write(message);
                                return;
                            }
                        } else {
                            deleteTasks(TaskType.SUBTASK, subTaskId);
                            byte[] message = String.format("Подзадача id = %d удалена", subTaskId).getBytes();
                            exchange.sendResponseHeaders(204, message.length);
                            try (OutputStream os = exchange.getResponseBody()) {
                                os.write(message);
                            }
                        }
                    }
                }
                else if (path.matches("^/tasks/epic/?.*$")) {
                    if (query.isEmpty()) {
                        deleteTasks(TaskType.EPIC, -1);
                        byte[] message = "Все эпики удалены".getBytes();
                        exchange.sendResponseHeaders(204, message.length);
                        try (OutputStream os = exchange.getResponseBody()) {
                            os.write(message);
                        }
                    } else {
                        String[] queryIdParam = query.split("=");
                        if (!queryIdParam[0].equals("id")) {
                            byte[] message = "Неверный формат id эпика в запросе".getBytes();
                            exchange.sendResponseHeaders(400, message.length);
                            try (OutputStream os = exchange.getResponseBody()) {
                                os.write(message);
                                return;
                            }
                        }

                        int epicId = 0;
                        try {
                            epicId = Integer.parseInt(queryIdParam[1]);
                        } catch (NumberFormatException | NullPointerException e) {
                            byte[] message = "Неверный формат id эпика в запросе".getBytes();
                            exchange.sendResponseHeaders(400, message.length);
                            try (OutputStream os = exchange.getResponseBody()) {
                                os.write(message);
                                return;
                            }
                        }
                        if (epicId <= 0) {
                            byte[] message = "id эпика должен быть положительным числом".getBytes();
                            exchange.sendResponseHeaders(400, message.length);
                            try (OutputStream os = exchange.getResponseBody()) {
                                os.write(message);
                                return;
                            }
                        } else {
                            deleteTasks(TaskType.TASK, epicId);
                            byte[] message = String.format("Эпик id = %d удален", epicId).getBytes();
                            exchange.sendResponseHeaders(204, message.length);
                            try (OutputStream os = exchange.getResponseBody()) {
                                os.write(message);
                            }
                        }
                    }
                }
                else {
                    byte[] message = String.format("Задан неправильный endpoint DELETE %s", path).getBytes();
                    exchange.sendResponseHeaders(400, message.length);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(message);
                    }
                }
            }

            if (method.equals("POST")) {
                if (path.matches("^/tasks/task/?$")) {
                    Task taskData;
                    try {
                        taskData = gson.fromJson(body, Task.class);
                    } catch (JsonSyntaxException e) {
                        byte[] message = "Неправильный формат json для задачи".getBytes();
                        exchange.sendResponseHeaders(400, message.length);
                        try (OutputStream os = exchange.getResponseBody()) {
                            os.write(message);
                            return;
                        }
                    }
                    int taskId = createUpdateTask(taskData);
                    exchange.sendResponseHeaders(204, Integer.toString(taskId).getBytes().length);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(Integer.toString(taskId).getBytes());
                    }
                }
                else if (path.equals("^/tasks/subtask/?$")) {
                    SubTask subTaskData;
                    try {
                        subTaskData = gson.fromJson(body, SubTask.class);
                    } catch (JsonSyntaxException e) {
                        byte[] message = "Неправильный формат json для подзадачи".getBytes();
                        exchange.sendResponseHeaders(400, message.length);
                        try (OutputStream os = exchange.getResponseBody()) {
                            os.write(message);
                            return;
                        }
                    }
                    int subTaskId = createUpdateTask(subTaskData);
                    exchange.sendResponseHeaders(204, Integer.toString(subTaskId).getBytes().length);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(Integer.toString(subTaskId).getBytes());
                    }
                }
                else if (path.matches("^/tasks/epic/?$")) {
                    Epic epicData;
                    try {
                        epicData = gson.fromJson(body, Epic.class);
                    } catch (JsonSyntaxException e) {
                        byte[] message = "Неправильный формат json для эпика".getBytes();
                        exchange.sendResponseHeaders(400, message.length);
                        try (OutputStream os = exchange.getResponseBody()) {
                            os.write(message);
                            return;
                        }
                    }
                    int epicId = createUpdateTask(epicData);
                    exchange.sendResponseHeaders(204, Integer.toString(epicId).getBytes().length);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(Integer.toString(epicId).getBytes());
                    }
                }
                else {
                    byte[] message = String.format("Задан неправильный endpoint POST %s", path).getBytes();
                    exchange.sendResponseHeaders(400, message.length);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(message);
                    }
                }
            }

            byte[] message = String.format("Неправильный метод % %", method, path).getBytes();
            exchange.sendResponseHeaders(405, message.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(message);
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

        private void deleteTasks(TaskType taskType, int id) {
            switch (taskType) {
                case TASK: {
                    if (id > 0) {
                        manager.deleteTask(id);
                    } else {
                        manager.deleteAllTasks();
                    }
                }
                case SUBTASK: {
                    if (id > 0) {
                        manager.deleteSubTask(id);
                    } else {
                        manager.deleteAllSubTasks();
                    }
                }
                case EPIC: {
                    if (id > 0) {
                        manager.deleteEpic(id);
                    } else {
                        manager.deleteAllEpics();
                    }
                }
            }
        }

        private int createUpdateTask(Task task) {
            int responseId = task.getId();
            if (task instanceof SubTask) {
                if (Objects.isNull(manager.getSubTask(responseId))) {
                    responseId = manager.addNewSubTask((SubTask) task);
                } else {
                    manager.updateSubTask((SubTask) task);
                }
            } else if (task instanceof Epic) {
                if (Objects.isNull(manager.getEpic(responseId))) {
                    responseId = manager.addNewEpic((Epic) task);
                } else {
                    manager.updateEpic((Epic) task);
                }
            } else {
                if (Objects.isNull(manager.getTask(responseId))) {
                    responseId = manager.addNewTask(task);
                } else {
                    manager.updateTask(task);
                }
            }
            return responseId;
        }
    }
}
