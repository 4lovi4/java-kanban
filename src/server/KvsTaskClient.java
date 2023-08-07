package server;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KvsTaskClient {
    private static final int DEFAULT_PORT = 8078;
    private static final String DEFAULT_HOST = "localhost";

    private int port;
    private String host;
    private String uriStr;
    private String apiToken;
    HttpClient client;

    public KvsTaskClient() {
        uriStr = String.format("http://%s:%d", DEFAULT_HOST, DEFAULT_PORT);
        apiToken = "DEBUG";
        client = HttpClient.newHttpClient();
    }

    public KvsTaskClient(int port, String host) {
        this.port = port;
        this.host = host;
        this.client = HttpClient.newHttpClient();
        uriStr = String.format("http://%s:%d", host, port);
        try {
            URI registerUri = URI.create(String.format(uriStr + "/%s", "register"));
            HttpRequest registerRequest = HttpRequest.newBuilder().uri(registerUri).GET().build();
            HttpResponse<String> registerResponse = client.send(registerRequest, HttpResponse.BodyHandlers.ofString());
            apiToken = registerResponse.body();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Ошибка клиента в запросе GET /register");
        }
    }

    public void save(String key, String value) {
        try {
            URI saveUri = URI.create(String.format(uriStr + "/%s/%s?API_TOKEN=%s", "save", key, apiToken));
            HttpRequest saveRequest = HttpRequest.newBuilder().uri(saveUri).POST(HttpRequest.BodyPublishers.ofString(value)).build();
            HttpResponse<String> saveResponse = client.send(saveRequest, HttpResponse.BodyHandlers.ofString());
        }
        catch (IOException | InterruptedException e) {
            throw new RuntimeException(String.format("Ошибка при сохранении на сервере по ключу %s", key));
        }
    }

    public String load(String key) {
        String responseValue = "";
        try {
            URI saveUri = URI.create(String.format(uriStr + "/%s/%s?API_TOKEN=%s", "load", key, apiToken));
            HttpRequest saveRequest = HttpRequest.newBuilder().uri(saveUri).GET().build();
            HttpResponse<String> loadResponse = client.send(saveRequest, HttpResponse.BodyHandlers.ofString());
            responseValue = loadResponse.body();
        }
        catch (IOException | InterruptedException e) {
            throw new RuntimeException(String.format("Ошибка при сохранении на сервере по ключу %s", key));
        }
        return responseValue;
    }
}
