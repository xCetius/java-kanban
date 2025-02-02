package main.java.managers;

import main.java.domain.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private Node<Task> head;
    private Node<Task> tail;

    private final Map<Integer, Node<Task>> nodeMap = new HashMap<>();


    @Override
    public void add(Task taskToAdd) {
        Task task = taskToAdd.clone();
        Node<Task> node = nodeMap.get(task.getId());
        if (node != null) {
            removeNode(node);
        }
        linkLast(task);
    }


    @Override
    public void remove(int id) {
        Node<Task> node = nodeMap.remove(id);
        if (node != null) {
            removeNode(node);
        }
    }

    @Override
    public List<Task> getHistory() {
        List<Task> history = new ArrayList<>();
        Node<Task> current = head;
        while (current != null) {
            history.add(current.item);
            current = current.next;
        }
        return history;
    }

    private static class Node<E> {
        E item;
        Node<E> next;
        Node<E> prev;

        Node(Node<E> prev, E element, Node<E> next) {
            this.item = element;
            this.next = next;
            this.prev = prev;
        }
    }

    private void linkLast(Task task) {
        Node<Task> newNode = new Node<>(tail, task, null);
        if (tail != null) {
            tail.next = newNode;
        } else {
            head = newNode;
        }
        tail = newNode;
        nodeMap.put(task.getId(), newNode);
    }

    private void removeNode(Node<Task> node) {
        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            head = node.next;
        }
        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            tail = node.prev;
        }
    }
}
