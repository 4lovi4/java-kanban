package tasks;

import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {

    protected final ArrayList<Integer> subTasksId;

    public Epic() {
        super();
        subTasksId = new ArrayList<>();
    }

    public Epic(String name, String description, int id) {
        super(name, description, id);
        this.subTasksId = new ArrayList<>();
    }

    public ArrayList<Integer> getSubTasksIdList() {
        return subTasksId;
    }

    public void addToSubTasksIdList(int subTaskId) {
        subTasksId.add(subTaskId);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subTaskIdList=" + subTasksId +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
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
