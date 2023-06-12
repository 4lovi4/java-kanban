package managers;

import org.junit.jupiter.api.BeforeAll;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    InMemoryTaskManager getTaskManager() {
        return new InMemoryTaskManager();
    }

}
