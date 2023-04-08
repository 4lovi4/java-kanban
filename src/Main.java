import tasks.Epic;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;
import managers.*;

import java.util.ArrayList;

public class Main {

	public static void main(String[] args) {
		InMemoryTaskManager manager = (InMemoryTaskManager) Managers.getDefault();

		// Создание эпиков и подзадач
		Epic epicOne = new Epic("Эпик 1", "Первый эпик", 0);
		Epic epicTwo = new Epic("Эпик 2", "Второй эпик", 0);
		SubTask subTaskOne = new SubTask("Подзадача 1", "Подзадача из 1го эпика", 0, 0);
		SubTask subTaskTwo = new SubTask("Подзадача 2", "Подзадача из 1го эпика", 0, 0);
		SubTask subTaskThree = new SubTask("Подзадача 3", "Подзадача из 2го эпика", 0, 0);

		manager.addNewEpic(epicOne);
		manager.addNewEpic(epicTwo);
		manager.addNewSubTask(subTaskOne);
		manager.addNewSubTask(subTaskTwo);
		manager.addNewSubTask(subTaskThree);
		ArrayList<Task> allTasks = manager.getAllTasks();
		ArrayList<Epic> allEpics = manager.getAllEpics();
		ArrayList<SubTask> allSubTasks = manager.getAllSubTasks();

		// Вывод списков всех созданных задач по типам
		System.out.println(allTasks);
		System.out.println(allEpics);
		System.out.println(allSubTasks);

		epicOne = allEpics.get(0);
		epicTwo = allEpics.get(1);
		subTaskOne = allSubTasks.get(0);
		subTaskTwo = allSubTasks.get(1);
		subTaskThree = allSubTasks.get(2);
		subTaskOne.setEpicId(epicOne.getId());
		subTaskTwo.setEpicId(epicOne.getId());
		subTaskThree.setEpicId(epicOne.getId());
		subTaskOne.setStatus(Status.IN_PROGRESS);
		subTaskTwo.setStatus(Status.DONE);
		subTaskThree.setStatus(Status.DONE);
		epicOne.addSubTaskId(subTaskOne.getId());
		epicOne.addSubTaskId(subTaskTwo.getId());
		epicOne.addSubTaskId(subTaskThree.getId());
		manager.updateEpic(epicOne);
		manager.updateEpic(epicTwo);
		manager.updateSubTask(subTaskOne);
		manager.updateSubTask(subTaskTwo);
		manager.updateSubTask(subTaskThree);

		// Проверяем историю запросов задач
		manager.getEpic(epicTwo.getId());
		manager.getEpic(epicTwo.getId());
		System.out.println("История запросов: " + manager.getHistory());
		manager.getEpic(epicOne.getId());
		manager.getSubTask(subTaskThree.getId());
		manager.getSubTask(subTaskTwo.getId());
		manager.getEpic(epicOne.getId());
		manager.getEpic(epicTwo.getId());
		manager.getSubTask(subTaskOne.getId());
		System.out.println("История запросов: " + manager.getHistory());
		manager.deleteSubTask(subTaskThree.getId());
		System.out.println("История запросов: " + manager.getHistory());
		manager.deleteEpic(epicOne.getId());
		System.out.println("История запросов: " + manager.getHistory());
	}
}
