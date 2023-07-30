package managers;

import java.io.File;

public class HttpTaskManager extends FileBackedTasksManager {
    public HttpTaskManager(File file) {
        super(file);
    }
}
