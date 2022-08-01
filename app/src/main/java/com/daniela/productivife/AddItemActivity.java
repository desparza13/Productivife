package com.daniela.productivife;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.sqlite.db.SupportSQLiteDatabase;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.text.CaseMap;
import android.os.AsyncTask;
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
import com.daniela.productivife.models.ToDoItemDao;
import com.daniela.productivife.models.ToDoItemWithUser;
import com.daniela.productivife.models.User;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import es.dmoral.toasty.Toasty;

public class AddItemActivity extends AppCompatActivity {
    private static final String TAG = "AddItem";
    private String[] priorities = {"High", "Normal", "Low"};

    private TextInputEditText etTitle;
    private TextInputEditText etDescription;
    private AutoCompleteTextView acPriority;
    private TextInputEditText etDate;
    private TextInputEditText etPlace;
    private Button btnAdd;

    private ToDoItemDao toDoItemDao;

    private ArrayAdapter<String> adapterPriorities;

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        //Create ActionBar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true); //Arrow back to home fragment
        actionBar.setDisplayHomeAsUpEnabled(true);

        //Calendar that will aid on displaying the current day on the date picker for due date
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        toDoItemDao = ((BackupDatabaseApplication) getApplicationContext()).getBackupDatabase().toDoItemDao();

        //Find views in layout
        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        acPriority = findViewById(R.id.acPriority);
        etDate = findViewById(R.id.etDate);
        etPlace = findViewById(R.id.etPlace);
        btnAdd = findViewById(R.id.btnAdd);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        createDropdownPriorities();
        createDatePicker(year, month, day);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem();
            }
        });
    }

    private void createDropdownPriorities(){ //This way you ensure the user won't use options not considered or misspell a word
        //Dropdown menu for priorities (High, normal and low)
        adapterPriorities = new ArrayAdapter<String>(this, R.layout.dropdown_item,priorities);
        acPriority.setAdapter(adapterPriorities);
    }

    private void createDatePicker(int year, int month, int day) { //Display calendar so that the user can easily pick a date
        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Create date pickerdialog and display it
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddItemActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        String formatDay;
                        String formatMonth;
                        //Format day and month so that they always are formed of two digits
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
    public boolean onSupportNavigateUp() { //Arrow back button
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("lastActivity", getClass().getName());
        editor.commit();
    }

    private User getUser(){ //Get current logged in user from shared preference and create a model, this way each to-do item is owned by a user
        SharedPreferences sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "email");
        String uid = sharedPreferences.getString("uid", "uid");
        Log.i(TAG, uid+" "+email);
        User user = new User(uid, email);
        return user;
    }

    private String getCurrentDateTime(){ //Get the datetime from the device following the format used
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
        User user = getUser();

        //Validate required data
        if (user.getUid().equals("") || user.getEmail().equals("")){
            Toasty.error(AddItemActivity.this, "Can't retrieve user",Toast.LENGTH_SHORT).show();
        }else if (title.equals("")){
            Toasty.warning(AddItemActivity.this, "Title can't be empty",Toast.LENGTH_SHORT).show();
        } else if(priority.equals("")) {
            Toasty.error(AddItemActivity.this, "Priority can't be empty",Toast.LENGTH_SHORT).show();
        } else if (dueDate.equals("")){
            Toasty.warning(AddItemActivity.this, "Due date can't be empty",Toast.LENGTH_SHORT).show();
        } else {
            //Create id for the to do item, made out of the user email+currentDateTime
            String idToDoItem = user.getEmail()+"/"+currentDateTime;
            //Create to do item through the model
            ToDoItem toDoItem = new ToDoItem(
                    idToDoItem,
                    currentDateTime,
                    title,
                    description,
                    priority,
                    dueDate,
                    place,
                    "Incomplete",
                    user);
            String userToDoItem = databaseReference.push().getKey();
            //Establish database name
            String dbName = "ToDoItems";
            //Push new object to the database
            databaseReference.child(dbName).child(userToDoItem).setValue(toDoItem);
            //Add to local database
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    toDoItemDao.addItem(toDoItem.getIdToDoItem(),
                            toDoItem.getCurrentDateTime(),
                            toDoItem.getTitle(),
                            toDoItem.getDescription(),
                            toDoItem.getPriority(),
                            toDoItem.getDueDate(),
                            toDoItem.getPlace(),
                            toDoItem.getStatus(),
                            toDoItem.getUser().getUid());
                }
            });
            //Check if it was uploaded to Firebase
            if (InternetConnection.checkConnection(this)){
                FirebaseDatabase fbDatabase = FirebaseDatabase.getInstance();
                DatabaseReference databaseReference = fbDatabase.getReference("ToDoItems");
                Query query = databaseReference.orderByChild("idToDoItem").equalTo(toDoItem.getIdToDoItem());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            //Let the user know the item was created
                            Toasty.success(AddItemActivity.this, "To Do Item successfully created",Toast.LENGTH_SHORT).show();
                        }
                        else {
                            //Erase from local database
                            AsyncTask.execute(new Runnable() {
                                @Override
                                public void run() {
                                    toDoItemDao.deleteToDoItem(idToDoItem);
                                }
                            });
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.i(TAG, "Unable to retrieve user and save it in backup");
                    }
                });
            }
            else{
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        toDoItemDao.deleteToDoItem(idToDoItem);
                    }
                });
            }

            //Go to main menu
            onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }
}