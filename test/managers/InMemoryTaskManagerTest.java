package managers;

import managers.impl.InMemoryTaskManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    InMemoryTaskManager getTaskManager() {
        return new InMemoryTaskManager();
    }

    @Test
    public void managerShouldHaveEmptyAllTypesTasksLists() {
        assertTrue(taskManager.getAllTasks().isEmpty() || taskManager.getAllSubTasks().isEmpty()
                || taskManager.getAllEpics().isEmpty());
    }
}
