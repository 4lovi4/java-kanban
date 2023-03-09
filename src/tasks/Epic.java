package tasks;

import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {

    protected ArrayList<Integer> subTaskIdList;

    public Epic() {
        super();
        subTaskIdList = new ArrayList<>();
    }

    public Epic(String name, String description, int id) {
        super(name, description, id);
        this.subTaskIdList = new ArrayList<>();
    }

    public ArrayList<Integer> getSubTasksIdList() {
        return subTaskIdList;
    }

    public void addToSubTasksIdList(int subTaskId) {
        subTaskIdList.add(subTaskId);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subTaskIdList=" + subTaskIdList +
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
                && status == epic.status && Objects.equals(subTaskIdList, epic.subTaskIdList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subTaskIdList);
    }
}
