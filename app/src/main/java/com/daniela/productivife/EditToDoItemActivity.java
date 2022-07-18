package com.daniela.productivife;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Delete;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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
import com.daniela.productivife.models.ToDoItemDao;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import es.dmoral.toasty.Toasty;

public class EditToDoItemActivity extends AppCompatActivity {
    private static final String TAG = "EditToDoItem";
    private String[] priorities = {"High", "Normal", "Low"};
    private String[] states = {"Incomplete", "Complete"};

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

    private ToDoItemDao toDoItemDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_to_do_item);

        toDoItemDao = ((BackupDatabaseApplication) getApplicationContext()).getBackupDatabase().toDoItemDao();

        //Create ActionBar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setDisplayShowHomeEnabled(true); //Arrow back to home fragment
        actionBar.setDisplayHomeAsUpEnabled(true);

        //Calendar that will aid on displaying the current day on the date picker for due date
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        //Find views in layout
        etEditTitle = findViewById(R.id.etEditTitle);
        etEditDescription = findViewById(R.id.etEditDescription);
        acEditPriority = findViewById(R.id.acEditPriority);
        etEditDate = findViewById(R.id.etEditDate);
        etEditPlace = findViewById(R.id.etEditPlace);
        acEditState = findViewById(R.id.acEditState);
        btnUpdate = findViewById(R.id.btnUpdate);

        //Get item sent through the intent from details activity
        toDoItem = (ToDoItem) Parcels.unwrap(getIntent().getParcelableExtra(ToDoItem.class.getSimpleName()));
        if (toDoItem==null){
            Gson gson = new Gson();
            SharedPreferences sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
            String json = sharedPreferences.getString("lastDetails", "");
            toDoItem = gson.fromJson(json, ToDoItem.class);
            Log.d(TAG, toDoItem.toString());
        }
        Log.d(TAG, String.format("Editing '%s'", toDoItem.getTitle()));
        //Set to-do item's information on the views
        etEditTitle.setText(toDoItem.getTitle());
        etEditDescription.setText(toDoItem.getDescription());
        acEditPriority.setText(toDoItem.getPriority());
        etEditDate.setText(toDoItem.getDueDate());
        etEditPlace.setText(toDoItem.getPlace());
        acEditState.setText(toDoItem.getStatus());

        databaseReference = FirebaseDatabase.getInstance().getReference();

        createDropdownPriorities();
        createDropdownState();
        createDatePicker(year, month, day);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateItem();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("lastActivity", getClass().getName());
        Gson gson = new Gson();
        String json = gson.toJson(toDoItem);
        editor.putString("lastDetails", json);
        editor.commit();
    }

    private void updateItem() {
        if (InternetConnection.checkConnection(EditToDoItemActivity.this)){
            //Get information
            String title = etEditTitle.getText().toString();
            String description = etEditDescription.getText().toString();
            String priority = acEditPriority.getText().toString();
            String dueDate = etEditDate.getText().toString();
            String place = etEditPlace.getText().toString();
            String status = acEditState.getText().toString();

            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    //Edit local backup
                    toDoItemDao.updateAllToDoItem(toDoItem.getIdToDoItem(),
                            toDoItem.getCurrentDateTime(),
                            title,
                            description,
                            priority,
                            dueDate,
                            place,
                            status,
                            toDoItem.getUserUid());
                }
            });

            //Find the to-do item on the database (Firebase)
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = firebaseDatabase.getReference("ToDoItems");
            Query query = databaseReference.orderByChild("idToDoItem").equalTo(toDoItem.getIdToDoItem());
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        //Update values
                        dataSnapshot.getRef().child("title").setValue(title);
                        dataSnapshot.getRef().child("description").setValue(description);
                        dataSnapshot.getRef().child("priority").setValue(priority);
                        dataSnapshot.getRef().child("dueDate").setValue(dueDate);
                        dataSnapshot.getRef().child("place").setValue(place);
                        dataSnapshot.getRef().child("state").setValue(status);
                    }
                    Toasty.success(EditToDoItemActivity.this, "To-do item successfully updated").show();
                    Intent intent = new Intent(EditToDoItemActivity.this, ShowListActivity.class);
                    startActivity(intent);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toasty.error(EditToDoItemActivity.this, "Error while editing to-do item").show();
                }
            });
        } else {
            Toasty.error(EditToDoItemActivity.this, "Unable to edit item. Retry when you have network connection").show();
        }

    }

    private void createDropdownPriorities(){ //This way you ensure the user won't use options not considered or misspell a word
        //Dropdown menu for priorities
        adapterPriorities = new ArrayAdapter<String>(this, R.layout.dropdown_item,priorities);
        acEditPriority.setAdapter(adapterPriorities);
    }

    private void createDropdownState(){ //This way you ensure the user won't use options not considered or misspell a word
        //Dropdown menu for state (complete, incomplete)
        adapterStates = new ArrayAdapter<String>(this, R.layout.dropdown_item,states);
        acEditState.setAdapter(adapterStates);
    }

    private void createDatePicker(int year, int month, int day) { //Display calendar so that the user can easily pick a date
        //Create datepicker dialog and display it
        etEditDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(EditToDoItemActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        String formatDay;
                        String formatMonth;
                        //format day and month so that they always have two digits
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(EditToDoItemActivity.this, ToDoItemDetailsActivity.class);
        intent.putExtra(ToDoItem.class.getSimpleName(), Parcels.wrap(toDoItem));
        // show the activity
        startActivity(intent);
    }
}