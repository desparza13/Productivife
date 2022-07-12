package com.daniela.productivife;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.daniela.productivife.models.ToDoItem;
import com.daniela.productivife.models.ToDoItemDao;
import com.daniela.productivife.models.User;

@Database(entities = {ToDoItem.class, User.class}, version = 3)
public abstract class BackupDatabase extends RoomDatabase {
    public abstract ToDoItemDao toDoItemDao();

    public SupportSQLiteDatabase getSQliteDB() {
        return this.getOpenHelper().getWritableDatabase();
    }
    //Database name to be used
    public static final String NAME = "SQLBackup";
}
