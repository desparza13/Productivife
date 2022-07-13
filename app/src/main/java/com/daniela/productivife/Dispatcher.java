package com.daniela.productivife;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;

public class Dispatcher extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Class<?> activityClass;

        try{
            SharedPreferences sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
            activityClass = Class.forName(sharedPreferences.getString("lastActivity", LoadingActivity.class.getName()));
        } catch (ClassNotFoundException e) {
            activityClass = LoadingActivity.class;
        }

        startActivity(new Intent(this, activityClass));
    }
}
