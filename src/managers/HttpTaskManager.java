package managers;

import com.google.gson.Gson;
import server.KvsTaskClient;

import java.io.File;

public class HttpTaskManager extends FileBackedTasksManager {

    Gson gson;
    KvsTaskClient kvsClient;

    public HttpTaskManager(String kvsUrl)
    {
        super(null);
        String[] kvsUrlParts = kvsUrl.replaceAll("^http://", "").split(":");
        String host = kvsUrlParts[0];
        int port = Integer.parseInt(kvsUrlParts[1]);
        kvsClient = new KvsTaskClient();
    }

    @Override
    public void save() {

    }

    public void loadFromServer() {

    }
}
