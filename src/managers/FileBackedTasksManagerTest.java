package managers;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @Override
    FileBackedTasksManager getTaskManager() {
        return new FileBackedTasksManager(new File("test_tasks.csv"));
    }
}