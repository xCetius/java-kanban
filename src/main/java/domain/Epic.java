package main.java.domain;

import main.java.managers.FileBackedTaskManager;
import main.java.enums.Status;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Epic extends Task {


    private final List<Integer> subTasksIds = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
    }

    public Epic(int id, String name, Status status, String description) {
        super(id, name, status, description, null, null);
    }

    public Epic(int id, String name, Status status, String description, LocalDateTime startTime, Duration duration) {
        super(id, name, status, description, startTime, duration);
    }

    public Epic(Epic other) {
        super(other);
        this.subTasksIds.addAll(other.subTasksIds);
    }


    public void calculateDuration(Map<Integer, Subtask> subtasks) {

        Duration duration =
                subTasksIds.stream()
                        //.filter(subtasks::containsKey)
                        .map(subtasks::get)
                        .map(Task::getDuration)
                        .filter(Objects::nonNull)
                        .reduce(Duration.ZERO, Duration::plus);
        if (duration == null) {
            duration = Duration.ZERO;
        }
        this.duration = duration;

    }

    public void calculateStartTime(Map<Integer, Subtask> subtasks) {
        this.startTime = subTasksIds.stream()
                .map(subtasks::get)
                .map(Task::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(LocalDateTime.now());
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
                ", startTime=" + startTime.format(FileBackedTaskManager.dateTimeFormatter) +
                ", duration=" + duration.toMinutes() +
                '}';
    }
}
