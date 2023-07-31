package server;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KvsTaskClient {
    private int port = 8078;
    private String host = "localhost";
    private URI uri;
    private String apiToken;
    HttpClient client;


    public KvsTaskClient() {
        String uriStr =  String.format("http://%s:%d/%s", host, port);
        apiToken = "DEBUG";
        client = HttpClient.newHttpClient();
    }

    public KvsTaskClient(int port, String host) {
        this.port = port;
        this.host = host;
        this.client = HttpClient.newHttpClient();
        String uriStr =  String.format("http://%s:%d/%s", host, port);
        URI registerUri = URI.create(String.format(uriStr, "register"));
        HttpRequest registerRequest = HttpRequest.newBuilder().uri(registerUri).GET().build();
        try {
            HttpResponse<String> registerResponse = client.send(registerRequest, HttpResponse.BodyHandlers.ofString());
            apiToken = registerResponse.body();
        }
        catch (IOException | InterruptedException e) {
            throw new RuntimeException(String.format("Ошибка клиента в запросе GET %s", uri.getPath()));
        }
    }
}
