package tasks;

import java.time.LocalDateTime;
import java.util.Objects;

public class SubTask extends Task{
    private int epicId;

    public SubTask() {
        super();
    }

    public SubTask(int epicId) {
        super();
        this.epicId = epicId;
    }

    public SubTask( int id, String name, String description, int epicId) {
        super(id, name, description);
        this.epicId = epicId;
    }

    public SubTask( int id, String name, String status, String description, int epicId) {
        super(id, name, status, description);
        this.epicId = epicId;
    }

    public SubTask(int id, String name, String status, String description,
                   int epicId, LocalDateTime startTime, Long duration) {
        super(id, name, status, description, startTime, duration);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "epicId=" + epicId +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", duration=" + duration +
                ", startTime=" + startTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SubTask subTask = (SubTask) o;
        return id == subTask.id && Objects.equals(name, subTask.name)
                && Objects.equals(description, subTask.description)
                && status == subTask.status  && epicId == subTask.epicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }
}
