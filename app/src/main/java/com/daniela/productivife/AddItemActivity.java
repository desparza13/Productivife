package com.daniela.productivife;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.text.CaseMap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import com.daniela.productivife.models.ToDoItem;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import es.dmoral.toasty.Toasty;

public class AddItemActivity extends AppCompatActivity {
    private static final String TAG = "AddItem";
    private String[] priorities = {"High", "Normal", "Low"};
    String userUid;
    String userEmail;

    private TextInputEditText etTitle;
    private TextInputEditText etDescription;
    private AutoCompleteTextView acPriority;
    private TextInputEditText etDate;
    private TextInputEditText etPlace;
    private Button btnAdd;

    private ArrayAdapter<String> adapterPriorities;

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        //Create ActionBar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setDisplayShowHomeEnabled(true); //Arrow back to home fragment
        actionBar.setDisplayHomeAsUpEnabled(true);

        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        acPriority = findViewById(R.id.acPriority);
        etDate = findViewById(R.id.etDate);
        etPlace = findViewById(R.id.etPlace);
        btnAdd = findViewById(R.id.btnAdd);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        createDropdownPriorities();
        createDatePicker(year, month, day);

        getValues();

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem();
            }
        });

    }
    private void createDropdownPriorities(){
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
    /*
    private void createDatePicker(int year, int month, int day){
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
     */
    private void createDatePicker(int year, int month, int day) {
        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddItemActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        String formatDay;
                        String formatMonth;
                        if (day<10){
                            formatDay = "0"+String.valueOf(day);
                        } else{
                            formatDay = String.valueOf(day);
                        }
                        month = month+1;
                        if (month<10){
                            formatMonth = "0"+String.valueOf(month);
                        } else {
                            formatMonth = String.valueOf(month);
                        }
                        etDate.setText(formatDay + "/" + formatMonth + "/" + year);
                    }
                }
                ,year,month,day);
                datePickerDialog.show();
            }
        });
    }

        @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    private void getValues(){
        userUid = getIntent().getStringExtra("uid");
        userEmail = getIntent().getStringExtra("email");
        Log.i(TAG, userUid+" "+userEmail);
        getCurrentDateTime();

    }

    private String getCurrentDateTime(){
        String registrationDateTime = new SimpleDateFormat("dd-MM-yyyy/HH:mm:ss a",
                Locale.getDefault()).format(System.currentTimeMillis());
        return registrationDateTime;
    }
    private void addItem(){
        //Get information
        String currentDateTime = getCurrentDateTime();
        String title = etTitle.getText().toString();
        String description = etDescription.getText().toString();
        String priority = acPriority.getText().toString();
        String dueDate = etDate.getText().toString();
        String place = etPlace.getText().toString();

        //Validate required data
        if (userUid.equals("") || userEmail.equals("")){
            Toasty.error(AddItemActivity.this, "Can't retrieve user",Toast.LENGTH_SHORT).show();
        }else if (title.equals("")){
            Toasty.warning(AddItemActivity.this, "Title can't be empty",Toast.LENGTH_SHORT).show();
        } else if(priority.equals("")) {
            Toasty.error(AddItemActivity.this, "Priority can't be empty",Toast.LENGTH_SHORT).show();
        } else if (dueDate.equals("")){
            Toasty.warning(AddItemActivity.this, "Due date can't be empty",Toast.LENGTH_SHORT).show();
        } else {
            //Create id for the to do item, made out of the user email+currentDateTime
            String idToDoItem = userEmail+"/"+currentDateTime;
            //Create to do item through the model
            ToDoItem toDoItem = new ToDoItem(idToDoItem,
                    userUid,
                    userEmail,
                    currentDateTime,
                    title,
                    description,
                    priority,
                    dueDate,
                    place,
                    "Incomplete");
            String userToDoItem = databaseReference.push().getKey();
            //Establish database name
            String dbName = "ToDoItems";
            databaseReference.child(dbName).child(userToDoItem).setValue(toDoItem);
            Toasty.success(AddItemActivity.this, "To Do Item successfully created",Toast.LENGTH_SHORT).show();
            onBackPressed();
        }
    }

}