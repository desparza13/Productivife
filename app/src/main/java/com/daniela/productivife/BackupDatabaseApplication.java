package com.daniela.productivife;

import android.app.Application;

import androidx.room.Room;

public class BackupDatabaseApplication extends Application {
    BackupDatabase backupDatabase;

    @Override
    public void onCreate() {
        super.onCreate();
        //when upgrading versions, kill the original tables by using fallbackToDestructiveMigration()
        backupDatabase = Room.databaseBuilder(this, BackupDatabase.class, BackupDatabase.NAME).fallbackToDestructiveMigration().build();
    }
    public  BackupDatabase getBackupDatabase() {
        return backupDatabase;
    }

}
