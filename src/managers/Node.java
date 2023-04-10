package managers;

import java.util.Objects;

public class Node<T> {
    private Node<T> prevNode;
    private Node<T> nextNode;
    private T data;

    public Node(T data, Node<T> nextNode, Node<T> prevNode) {
        this.data = data;
        this.prevNode = prevNode;
        this.nextNode = nextNode;
    }

    public Node<T> getPrevNode() {
        return prevNode;
    }

    public void setPrevNode(Node<T> prevNode) {
        this.prevNode = prevNode;
    }

    public Node<T> getNextNode() {
        return nextNode;
    }

    public void setNextNode(Node<T> nextNode) {
        this.nextNode = nextNode;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node<?> node = (Node<?>) o;
        return Objects.equals(prevNode, node.prevNode) && Objects.equals(nextNode, node.nextNode) && Objects.equals(data, node.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(prevNode, nextNode, data);
    }
}
