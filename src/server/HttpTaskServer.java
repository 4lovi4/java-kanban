package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import managers.Managers;
import managers.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class HttpTaskServer {
    private final static int TASK_SERVER_PORT = 8080;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final HttpServer server;
    protected TaskManager manager;



    public HttpTaskServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress("localhost", TASK_SERVER_PORT), 0);
        manager = Managers.getFileBackedTaskManager();
        server.createContext("/tasks/task", new TaskHandler(manager));
    }

    public void start() {
        System.out.println("Сервер таск менеджер запущен м доступен по адресу " + server.getAddress().toString());
        server.start();
    }

    static class TaskHandler implements HttpHandler {
        private final TaskManager serverTaskManager;

        public TaskHandler(TaskManager serverTaskManager) {
            this.serverTaskManager = serverTaskManager;
        }
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            String query = exchange.getRequestURI().getQuery();
            InputStream stream = exchange.getRequestBody();
            String body = new String(stream.readAllBytes(), DEFAULT_CHARSET);

            if (method.equals("GET") && path.matches("/tasks/task")) {
                exchange.sendResponseHeaders(200, 0);
                serverTaskManager.getTask(0);
            }
            else if (method.equals("DELETE") && path.matches("/tasks/task")) {

            }
            else if (method.equals("POST") && path.matches("/tasks/task")) {

            }
        }
    }
}
