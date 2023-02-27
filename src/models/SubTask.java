package models;

import java.util.Objects;

public class SubTask extends Task{
    private int epicId;

    public SubTask(int epicId) {
        super();
        this.epicId = epicId;
    }

    public SubTask(String name, String description, int id, Status status, int epicId) {
        super(name, description, id, status);
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
        String printDescription = "null";
        if (description != null) {
            printDescription = Integer.toString(description.length());
        }
        return "SubTask{" +
                "epicId=" + epicId +
                ", name='" + name + '\'' +
                ", description='" + printDescription + '\'' +
                ", id=" + id +
                ", status=" + status +
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
