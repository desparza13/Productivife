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
    @Query("SELECT COUNT(uid) FROM User WHERE uid+:userUid") int getUser(String userUid);
    @Query("SELECT * FROM ToDoItem WHERE idToDoItem=:idToDoItem") ToDoItem getToDoItem(String idToDoItem);

    @Query("INSERT INTO User VALUES (:uid, :email)") void addUser(String uid, String email);
    @Query("INSERT INTO ToDoItem VALUES (:idToDoItem, :currentDateTime, :title, :description, :priority, :dueDate, :place, :status, :userUid)") void addItem(String idToDoItem,
                                                                                                                                                  String currentDateTime,
                                                                                                                                                  String title,
                                                                                                                                                  String description,
                                                                                                                                                  String priority,
                                                                                                                                                  String dueDate,
                                                                                                                                                  String place,
                                                                                                                                                  String status,
                                                                                                                                                  String userUid);


    @Query("UPDATE User SET uid=:uid, email=:email") void  updateAllUser(String uid, String email);
    @Query("UPDATE ToDoItem SET currentDateTime=:currentDateTime, title=:title, description=:description, priority=:priority, dueDate=:dueDate, place=:place, status=:status, userUid=:userUid WHERE idToDoItem=:idToDoItem") void updateAllToDoItem(String idToDoItem,
                                                                                                                                                                                                                                            String currentDateTime,
                                                                                                                                                                                                                                            String title,
                                                                                                                                                                                                                                            String description,
                                                                                                                                                                                                                                            String priority,
                                                                                                                                                                                                                                            String dueDate,
                                                                                                                                                                                                                                            String place,
                                                                                                                                                                                                                                            String status,
                                                                                                                                                                                                                                            String userUid);
    @Query("DELETE FROM User WHERE uid=:uid") void deleteUser(String uid);
    @Query("DELETE FROM ToDoItem WHERE idToDoItem=:idToDoItem") void deleteToDoItem(String idToDoItem);

}
