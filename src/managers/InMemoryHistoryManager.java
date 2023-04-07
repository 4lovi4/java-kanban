package managers;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import tasks.Task;

public class InMemoryHistoryManager implements HistoryManager {

	private final CustomLinkedList<Task> taskHistory;

	public InMemoryHistoryManager() {
		taskHistory = new CustomLinkedList<>();
	}



	@Override
	public void addTask(Task task) {
		taskHistory.add(task);
	}

	@Override
	public void remove(int id) {
		taskHistory.remove(id);
	}

	@Override
	public List<Task> getHistory() {
		return taskHistory.getTasks();
	}

	public class CustomLinkedList<T> extends LinkedList {
		int size = 0;
		Node<T> last;
		Node<T> first;

		public void linkLast(T item) {
			Node<T> l = last;
			Node<T> newNode = new Node(item, null, l);
			last = newNode;
			if (l == null) {
				first = newNode;
			}
			else {
				l.setNextNode(newNode);
			}
			size++;
		}

		public List<T> getTasks() {
			List<T> tasks = new ArrayList<>();
			for (Node<T> t = first; t != null; t.getNextNode()) {
				tasks.add(t.getData());
			}
			return tasks;
		}

		public void removeNode(Node<T> node) {
			Node<T> next = node.getNextNode();
			Node<T> prev = node.getPrevNode();
			if (next != null && prev != null) {
				prev.setNextNode(next);
				next.setPrevNode(prev);
			}
			else if (next == null) {
				prev.setNextNode(null);
			}
			else if (prev == null) {
				next.setPrevNode(null);
			}
		}

	}
}
