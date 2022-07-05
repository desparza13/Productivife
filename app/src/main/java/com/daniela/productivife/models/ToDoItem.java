package com.daniela.productivife.models;

public class ToDoItem {
    private String idToDoItem;
    private String userUid;
    private String userEmail;
    private String currentDateTime;
    private String title;
    private String description = "";
    private String priority = "Normal";
    private String dueDate = "";
    private String place = "";
    private String state = "Incomplete";

    public ToDoItem(){

    }

    public ToDoItem(String idToDoItem, String userUid, String userEmail, String currentDateTime, String title, String description, String priority, String dueDate, String place, String state) {
        this.idToDoItem = idToDoItem;
        this.userUid = userUid;
        this.userEmail = userEmail;
        this.currentDateTime = currentDateTime;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.dueDate = dueDate;
        this.place = place;
        this.state = state;
    }

    public String getIdToDoItem() {
        return idToDoItem;
    }

    public void setIdToDoItem(String idToDoItem) {
        this.idToDoItem = idToDoItem;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getCurrentDateTime() {
        return currentDateTime;
    }

    public void setCurrentDateTime(String currentDateTime) {
        this.currentDateTime = currentDateTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
