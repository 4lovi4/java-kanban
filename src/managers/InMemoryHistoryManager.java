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
        if (requestedNode != null) {
            taskNodes.removeNode(requestedNode);
        }
    }

    /*
    CustomLinkedList - класс внутри InMemoryHistoryManager,
    так я понял ТЗ - мы не создаём отдельный файл для этого класса
    Для класса CustomLinkedList создаётся объект taskNodes, поэтому не использую static
    */
    public class CustomLinkedList<T> {
        /*
        поле size хранит размер списка, изменяется при удалении добавлении ноды в список
        при реализации ТЗ оно явно не используется, но мне кажется такое поле
        */
        private int size = 0;
        private Node<T> last;
        private Node<T> first;

        public void linkLast(T item) {
            Node<T> lastNode = last;
            Node<T> newNode = new Node(item, null, lastNode);
            last = newNode;
            if (lastNode == null) {
                first = newNode;
            } else {
                lastNode.setNextNode(newNode);
            }
            size++;
        }

        public List<T> getTasks() {
            List<T> tasks = new ArrayList<>();
            Node<T> currentNode = first;
            while (currentNode != null) {
                tasks.add(currentNode.getData());
                currentNode = currentNode.getNextNode();
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
            } else if (next == null && prev != null) {
                prev.setNextNode(null);
                last = prev;
            } else if (prev == null && next != null) {
                next.setPrevNode(null);
                first = next;
            }
            size--;
        }
        // Добавил getter для полей списка, т.к. сделал их приватными
        public int getSize() {
            return size;
        }

        public Node<T> getLast() {
            return last;
        }

        public Node<T> getFirst() {
            return first;
        }
    }
}
