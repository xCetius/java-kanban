package main.java;

import main.java.enums.Status;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Integer> subTasksIds = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
    }

    public Epic(int id, String name, Status status, String description) {
        super(id, name, status, description);
    }

    public Epic(Epic other) {
        super(other);
        this.subTasksIds.addAll(other.subTasksIds);
    }

    public List<Integer> getSubTasksIds() {
        return subTasksIds;
    }

    public void addSubTasksId(int id) {
        if (!subTasksIds.contains(id)) {
            this.subTasksIds.add(id);
        }


    }

    public void removeSubTasksId(int id) {
        if (subTasksIds.contains(id)) {
            for (int i = 0; i < subTasksIds.size(); i++) {
                if (subTasksIds.get(i) == id) {
                    subTasksIds.remove(i);
                }
            }
        }
    }

    public void removeAllSubTasks() {
        subTasksIds.clear();
    }

    @Override
    public Epic clone() {
        return new Epic(this);
    }


    @Override
    public String toString() {
        return this.getClass().getSimpleName() +
                "{name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", status=" + getStatus() +
                ", subTasksIds=" + subTasksIds +
                '}';
    }
}
