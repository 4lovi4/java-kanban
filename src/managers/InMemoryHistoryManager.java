package managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import tasks.Task;

public class InMemoryHistoryManager implements HistoryManager {

    private final HashMap<Integer, Node<Task>> taskHistory;
    private Node<Task> last;
    private Node<Task> first;

    public void linkLast(Task item) {
        Node<Task> lastNode = last;
        Node<Task> newNode = new Node(item, null, lastNode);
        last = newNode;
        if (lastNode == null) {
            first = newNode;
        } else {
            lastNode.setNextNode(newNode);
        }
    }

    public List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        Node<Task> currentNode = first;
        while (currentNode != null) {
            tasks.add(currentNode.getData());
            currentNode = currentNode.getNextNode();
        }
        return tasks;
    }

    public void removeNode(Node<Task> node) {
        Node<Task> next = node.getNextNode();
        Node<Task> prev = node.getPrevNode();
        if (prev != null) {
            if (next != null) {
                prev.setNextNode(next);
            }
            else {
                prev.setNextNode(null);
                last = prev;
            }
        }
        else {
            if (next != null) {
                next.setPrevNode(null);
                first = next;
            }
            else {
                first = null;
                last = null;
            }
        }
    }

    public InMemoryHistoryManager() {
        taskHistory = new HashMap<>();
    }

    @Override
    public void addTask(Task task) {
        int taskId = task.getId();
        if (taskHistory.containsKey(taskId)) {
            remove(taskId);
        }
        linkLast(task);
        Node<Task> newNode = last;
        taskHistory.put(taskId, newNode);
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void remove(int id) {
        Node<Task> requestedNode = taskHistory.remove(id);
        if (requestedNode != null) {
            removeNode(requestedNode);
        }
    }
}
