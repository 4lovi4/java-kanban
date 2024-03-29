package tasks;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {

    protected ArrayList<Integer> subTasksId;

    public Epic() {
        super();
        subTasksId = new ArrayList<>();
    }

    public Epic(int id, String name, String description) {
        super(id, name, description);
        this.subTasksId = new ArrayList<>();
    }

    public Epic(int id, String name, String status, String description) {
        super(id, name, status, description);
        this.subTasksId = new ArrayList<>();
    }

    public Epic(int id, String name, String status, String description, LocalDateTime startTime) {
        super(id, name, status, description, startTime, 0L);
        this.subTasksId = new ArrayList<>();
        this.endTime = LocalDateTime.MAX;
    }

    private LocalDateTime endTime;

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public ArrayList<Integer> getSubTasksId() {
        return subTasksId;
    }

    public void addSubTaskId(int subTaskId) {
        subTasksId.add(subTaskId);
    }

    public void deleteSubTaskId(int subTaskId) {
        subTasksId.remove(Integer.valueOf(subTaskId));
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", duration=" + duration +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return id == epic.id && Objects.equals(name, epic.name)
                && Objects.equals(description, epic.description)
                && status == epic.status && Objects.equals(subTasksId, epic.subTasksId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subTasksId);
    }
}
