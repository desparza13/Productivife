package com.daniela.productivife;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.daniela.productivife.models.ToDoItem;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import es.dmoral.toasty.Toasty;

public class EditToDoItemActivity extends AppCompatActivity {
    private static final String TAG = "EditToDoItem";
    private String[] priorities = {"High", "Normal", "Low"};
    private String[] states = {"Incomplete", "Complete"};
    String userUid;
    String userEmail;
    private ToDoItem toDoItem;
    private TextInputEditText etEditTitle;
    private TextInputEditText etEditDescription;
    private AutoCompleteTextView acEditPriority;
    private TextInputEditText etEditDate;
    private TextInputEditText etEditPlace;
    private AutoCompleteTextView acEditState;
    private Button btnUpdate;

    private ArrayAdapter<String> adapterPriorities;
    private ArrayAdapter<String> adapterStates;

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_to_do_item);

        //Create ActionBar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setDisplayShowHomeEnabled(true); //Arrow back to home fragment
        actionBar.setDisplayHomeAsUpEnabled(true);

        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        etEditTitle = findViewById(R.id.etEditTitle);
        etEditDescription = findViewById(R.id.etEditDescription);
        acEditPriority = findViewById(R.id.acEditPriority);
        etEditDate = findViewById(R.id.etEditDate);
        etEditPlace = findViewById(R.id.etEditPlace);
        acEditState = findViewById(R.id.acEditState);
        btnUpdate = findViewById(R.id.btnUpdate);

        toDoItem = (ToDoItem) Parcels.unwrap(getIntent().getParcelableExtra(ToDoItem.class.getSimpleName()));
        Log.d(TAG, String.format("Editing '%s'", toDoItem.getTitle()));
        etEditTitle.setText(toDoItem.getTitle());
        etEditDescription.setText(toDoItem.getDescription());
        acEditPriority.setText(toDoItem.getPriority());
        etEditDate.setText(toDoItem.getDueDate());
        etEditPlace.setText(toDoItem.getPlace());
        acEditState.setText(toDoItem.getState());

        databaseReference = FirebaseDatabase.getInstance().getReference();

        createDropdownPriorities();
        createDropdownState();
        createDatePicker(year, month, day);

        getValues();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateItem();
            }
        });

    }

    private void updateItem() {
    }

    private void createDropdownPriorities(){
        //Dropdown menu for priorities
        adapterPriorities = new ArrayAdapter<String>(this, R.layout.dropdown_item,priorities);
        acEditPriority.setAdapter(adapterPriorities);
        acEditPriority.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String priority = parent.getItemAtPosition(position).toString();
            }
        });
    }
    private void createDropdownState(){
        //Dropdown menu for priorities
        adapterStates = new ArrayAdapter<String>(this, R.layout.dropdown_item,states);
        acEditState.setAdapter(adapterStates);
        acEditState.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String state = parent.getItemAtPosition(position).toString();
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
        etEditDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(EditToDoItemActivity.this, new DatePickerDialog.OnDateSetListener() {
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
                        etEditDate.setText(formatDay + "/" + formatMonth + "/" + year);
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

}