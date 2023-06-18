package managers;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    InMemoryTaskManager getTaskManager() {
        return new InMemoryTaskManager();
    }
}
