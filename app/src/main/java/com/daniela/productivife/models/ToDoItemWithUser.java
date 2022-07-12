package com.daniela.productivife.models;

import androidx.room.Embedded;

import java.util.ArrayList;
import java.util.List;

public class ToDoItemWithUser {
    //Embedded notation flattens the properties of the User object into the object, preserving encapsulation
    @Embedded
    User user;

    @Embedded
    ToDoItem toDoItem;

    public static List<ToDoItem> getToDoItemsList(List<ToDoItemWithUser> toDoItemWithUsers) {
        List<ToDoItem> toDoItems = new ArrayList<>();
        for (int i = 0; i < toDoItemWithUsers.size(); i++){
            ToDoItem toDoItem = toDoItemWithUsers.get(i).toDoItem;
            toDoItem.setUser(toDoItemWithUsers.get(i).user);
            toDoItems.add(toDoItem);
        }
        return toDoItems;
    }
}
