package com.daniela.productivife;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.beardedhen.androidbootstrap.TypefaceProvider;

public class ToDoMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TypefaceProvider.registerDefaultIconSets();
        setContentView(R.layout.activity_to_do_menu);
    }
}