package com.daniela.productivife;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;

import es.dmoral.toasty.Toasty;

public class AddItemActivity extends AppCompatActivity {

    String[] priorities = {"High", "Normal", "Low"};

    AutoCompleteTextView acPriority;
    TextInputEditText etDate;

    ArrayAdapter<String> adapterPriorities;
    DatePickerDialog.OnDateSetListener onDateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        final Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        acPriority = findViewById(R.id.acPriority);
        etDate = findViewById(R.id.etDate);

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

        //Date picker for due date
        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        AddItemActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        onDateSetListener, year, month, day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });
        onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month+1;
                String date = dayOfMonth+"/"+month+"/"+year;
                etDate.setText(date);
            }
        };
    }
}