package managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import tasks.Task;

public class InMemoryHistoryManager implements HistoryManager {

    private final CustomLinkedList<Task> taskNodes;

    private final HashMap<Integer, Node<Task>> taskHistory;

    public InMemoryHistoryManager() {
        taskNodes = new CustomLinkedList<>();
        taskHistory = new HashMap<>();
    }

    @Override
    public void addTask(Task task) {
        int taskId = task.getId();
        if (taskHistory.containsKey(taskId)) {
            remove(taskId);
        }
        taskNodes.linkLast(task);
        Node<Task> newNode = taskNodes.last;
        taskHistory.put(taskId, newNode);
    }

    @Override
    public List<Task> getHistory() {
        return taskNodes.getTasks();
    }

    @Override
    public void remove(int id) {
        Node<Task> requestedNode = taskHistory.remove(id);
        if (requestedNode != null)
            taskNodes.removeNode(requestedNode);
    }

    public class CustomLinkedList<T> {
        int size = 0;
        Node<T> last;
        Node<T> first;

        public void linkLast(T item) {
            Node<T> l = last;
            Node<T> newNode = new Node(item, null, l);
            last = newNode;
            if (l == null) {
                first = newNode;
            } else {
                l.setNextNode(newNode);
            }
            size++;
        }

        public List<T> getTasks() {
            List<T> tasks = new ArrayList<>();
            for (Node<T> t = first; t != null; t = t.getNextNode()) {
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
            } else if (next == null && prev == null) {
                first = null;
                last = null;
            } else if (next == null) {
                prev.setNextNode(null);
                last = prev;
            } else if (prev == null) {
                next.setPrevNode(null);
                first = next;
            }
            size--;
        }
    }
}
