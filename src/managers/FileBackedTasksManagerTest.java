package managers;

import java.io.File;
import java.io.IOException;
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
    public void shouldSaveTasksListInFile() throws IOException {
        Task task = createNewTask(100);
        int taskId = taskManager.addNewTask(task);
        Epic epic = createNewEpic(10);
        int epicId = taskManager.addNewEpic(epic);
        SubTask subTask = createSubTask(1, epicId);
        int subTaskId = taskManager.addNewSubTask(subTask);
        String fileContentExpected = "id,type,name,status,description,epic,start_time,duration,end_time\n" +
                String.format("%d,TASK,Задача 100 Есть,NEW,Описание задачи 100,\n", taskId) +
                String.format("%d,EPIC,Эпик 10 Есть,NEW,Описание задачи 10,\n", epicId) +
                String.format("%d,SUBTASK,Подзадача 1,NEW,Описание подзадачи 1,%d\n\n", subTaskId, epicId);
        assertDoesNotThrow(() -> taskManager.save());
        Path path = Paths.get(TEST_FILENAME);
        assertTrue(Files.exists(path));
        String fileContent = Files.readString(path);
        assertEquals(fileContentExpected, fileContent, "В файле записаны неверные данные");
    }

    @Test
    public void shouldSaveEmptyTasksListInFile() throws IOException {
        String fileContentExpected = "id,type,name,status,description,epic,start_time,duration,end_time\n\n";
        assertDoesNotThrow(() -> taskManager.save());
        Path path = Paths.get(TEST_FILENAME);
        assertTrue(Files.exists(path));
        String fileContent = Files.readString(path);
        assertEquals(fileContentExpected, fileContent, "В файле записаны неверные данные");
    }

    @Test
    public void shouldLoadTasksListFromFile() throws IOException {
        String headerText = "id,type,name,status,description,epic,start_time,duration,end_time\n";
        String taskText = "150,TASK,Загрузка Задачи,DONE,Описание загрузки задачи,\n";
        String epicText = "1,EPIC,Загрузка Эпика,PROGRESS,Описание загрузки эпика,\n";
        String subTaskText = "%d,SUBTASK,Загрузка Подзадачи,PROGRESS,Описание подзадачи 1,1\n\n";
    }
}
