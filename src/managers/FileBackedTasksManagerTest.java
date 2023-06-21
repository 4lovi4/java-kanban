package managers;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {

    private static final String TEST_FILENAME = "test_tasks.csv";

    @Override
    FileBackedTasksManager getTaskManager() {
        return new FileBackedTasksManager(new File(TEST_FILENAME));
    }


    @Test
    public void shouldSaveEmptyTasksList() {
        assertDoesNotThrow(() -> taskManager.save());
        Path path = Paths.get(TEST_FILENAME);
        assertTrue(Files.exists(path));
    }
}
