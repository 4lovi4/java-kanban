import managers.impl.HttpTaskManager;
import managers.impl.Managers;
import server.HttpTaskServer;
import server.KVServer;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.io.IOException;

public class Main {

	public static void main(String[] args) throws IOException {
		HttpTaskManager manager = (HttpTaskManager) Managers.getDefault();

		KVServer kvServer = new KVServer();
		kvServer.start();

		HttpTaskServer taskServer = new HttpTaskServer(manager);
		taskServer.start();

		// Создание эпиков и подзадач
		Epic epicOne = new Epic(0,"Эпик 1", "NEW", "Первый эпик");
		SubTask subTaskOne = new SubTask(0, "Подзадача 1", "NEW", "Подзадача 1 из 1го эпика", 0);
		SubTask subTaskTwo = new SubTask(0, "Подзадача 2", "NEW", "Подзадача 2 из 1го эпика", 0);
		Task taskOne = new Task(0, "Задача 1", "NEW", "Обычная задача");

		int taskId = manager.addNewTask(taskOne);
		int epicId = manager.addNewEpic(epicOne);
		subTaskOne.setEpicId(epicId);
		subTaskTwo.setEpicId(epicId);
		int subTaskOneId = manager.addNewSubTask(subTaskOne);
		int subTaskTwoId = manager.addNewSubTask(subTaskTwo);

		System.out.println(manager.getAllTasks());
		System.out.println(manager.getAllEpics());
		System.out.println(manager.getAllSubTasks());
		System.out.println(manager.getHistory());

		manager.getTask(taskId);
		manager.getEpic(epicId);
		System.out.println(manager.getEpic(epicId).getSubTasksId());
		manager.getSubTask(subTaskOneId);

		System.out.println(manager.getHistory());

		manager.deleteSubTask(subTaskTwoId);

		System.out.println(manager.getAllSubTasks());
		System.out.println(manager.getEpic(epicId));
		System.out.println(manager.getEpic(epicId).getSubTasksId());

		System.out.println(manager.getHistory());

		taskServer.stop();
		kvServer.stop();
	}
}
