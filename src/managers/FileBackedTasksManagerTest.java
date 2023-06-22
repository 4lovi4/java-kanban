package managers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;
import static managers.FileBackedTasksManager.loadFromFile;

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
        String epicText = "1,EPIC,Загрузка Эпика,IN_PROGRESS,Описание загрузки эпика,\n";
        String subTaskText = "10,SUBTASK,Загрузка Подзадачи,IN_PROGRESS,Описание загрузки подзадачи,1\n\n";
        try (FileWriter writer = new FileWriter(TEST_FILENAME, false)) {
            writer.write(headerText + taskText + epicText + subTaskText);
        }
        FileBackedTasksManager manager = loadFromFile(new File(TEST_FILENAME));
        Task task = new Task();
        String[] taskFields = taskText.split(",");
        task.setId(Integer.valueOf(taskFields[0]));
        task.setName(taskFields[2]);
        task.setStatus(Status.valueOf(taskFields[3]));
        task.setDescription(taskFields[4]);
        Task loadTask = manager.getTask(task.getId());
        assertEquals(task, loadTask);
    }
}
