package main.java.domain;

import main.java.managers.FileBackedTaskManager;
import main.java.enums.Status;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private int epicId;

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    public Subtask(String name, String description, Status status, int epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public Subtask(int id, String name, Status status, String description, int epicId, LocalDateTime startTime, Duration duration) {
        super(id, name, status, description, startTime, duration);
        this.epicId = epicId;
    }

    public Subtask(String name, String description, Status status, int epicId, LocalDateTime startTime, Duration duration) {

        super(name, description, status, startTime, duration);
        this.epicId = epicId;
    }

    public Subtask(Subtask other) {
        super(other);
        this.epicId = other.epicId;
    }

    @Override
    public Subtask clone() {
        return new Subtask(this);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() +
                "{name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", status=" + getStatus() +
                ", epicId=" + getEpicId() +
                ", startTime=" + getStartTime().format(FileBackedTaskManager.dateTimeFormatter) +
                ", duration=" + getDuration() +
                '}';
    }

}
