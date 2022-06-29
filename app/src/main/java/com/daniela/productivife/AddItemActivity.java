package com.daniela.productivife;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import es.dmoral.toasty.Toasty;

public class AddItemActivity extends AppCompatActivity {

    String[] priorities = {"High", "Normal", "Low"};

    AutoCompleteTextView acPriority;

    ArrayAdapter<String> adapterPriorities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        acPriority = findViewById(R.id.acPriority);

        //Dropdown menu for priorities
        adapterPriorities = new ArrayAdapter<String>(this, R.layout.dropdown_item,priorities);
        acPriority.setAdapter(adapterPriorities);
        acPriority.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String priority = parent.getItemAtPosition(position).toString();
                Toasty.info(AddItemActivity.this, "Item: "+priority, Toast.LENGTH_SHORT).show();
            }
        });
    }
}