package com.codepath.simpletodo.models;

public class NoteItem {

    public Long id;
    public String detail;
    public Integer isImportant;
    public Integer isUrgent;
    public Integer dueDate;

    public NoteItem(Long id, String detail, Integer isImportant, Integer isUrgent, Integer dueDate)
    {
        this.id = id;
        this.detail = detail;
        this.isImportant = isImportant;
        this.isUrgent = isUrgent;
        this.dueDate = dueDate;
    }
}
