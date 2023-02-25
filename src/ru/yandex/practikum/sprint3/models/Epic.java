package ru.yandex.practikum.sprint3.models;

import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {

    private ArrayList<Integer> subTaskList;

    public Epic() {
        super();
        subTaskList = new ArrayList<>();
        status = Status.NEW;
    }

    public Epic(String name, String description, int id, Status status) {
        super(name, description, id, status);
        this.status = status;
        this.subTaskList = new ArrayList<>();
    }

    public ArrayList<Integer> getSubTasksIdList() {
        return subTaskList;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", subTaskList=" + subTaskList +
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
                && status == epic.status && Objects.equals(subTaskList, epic.subTaskList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subTaskList);
    }
}
