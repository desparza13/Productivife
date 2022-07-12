package com.daniela.productivife.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.parceler.Parcel;

@Parcel
@Entity (foreignKeys = @ForeignKey(entity = User.class, parentColumns = "uid", childColumns = "userUid"))
public class ToDoItem {
    @PrimaryKey
    @ColumnInfo
    @NonNull
    private String idToDoItem;
    @ColumnInfo
    private String currentDateTime;
    @ColumnInfo
    private String title;
    @ColumnInfo
    private String description = "";
    @ColumnInfo
    private String priority = "Normal";
    @ColumnInfo
    private String dueDate = "";
    @ColumnInfo
    private String place = "";
    @ColumnInfo
    private String status = "Incomplete";
    @ColumnInfo
    private  String userUid;
    @Ignore
    private User user;

    public ToDoItem(){

    }

    public ToDoItem(String idToDoItem, String currentDateTime, String title, String description, String priority, String dueDate, String place, String state, User user) {
        this.idToDoItem = idToDoItem;
        this.currentDateTime = currentDateTime;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.dueDate = dueDate;
        this.place = place;
        this.status = state;
        this.user = user;
        this.userUid = user.getUid();
    }

    //Setters and getters
    public String getIdToDoItem() {
        return idToDoItem;
    }

    public void setIdToDoItem(String idToDoItem) {
        this.idToDoItem = idToDoItem;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }
}
