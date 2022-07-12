package com.daniela.productivife.models;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

//Defining data access objects
//Declaring the types of SQL queries performed in the data access object layer (DAO) (CRUD)
@Dao
public interface ToDoItemDao {
    @Query("SELECT * FROM ToDoItem" +
            " INNER JOIN User ON ToDoItem.userUid = User.uid" +
            " ORDER BY dueDate ASC")
    List<ToDoItemWithUser> toDoItems();
    @Query("SELECT * FROM ToDoItem") List<ToDoItem> toDoItem();


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertModel(ToDoItem toDoItem);
    @Insert (onConflict = OnConflictStrategy.REPLACE)
    void insertModel(List<ToDoItem> toDoItems);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertModel(User user);

    @Update
    void update(ToDoItem toDoItem);
    @Update
    void updateAll(List<ToDoItem> toDoItems);

    @Delete
    void delete(ToDoItem toDoItem);
    @Delete
    void deleteAll(List<ToDoItem> toDoItems);
}
