package com.daniela.productivife;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.daniela.productivife.models.ToDoItem;
import com.daniela.productivife.models.ToDoItemDao;
import com.google.gson.Gson;

import org.parceler.Parcels;

public class ToDoItemDetailsActivity extends AppCompatActivity {
    private ToDoItem toDoItem;
    private TextView tvDetailsTitle;
    private TextView tvDetailsDueDate;
    private TextView tvDetailsPriority;
    private CardView cvPriority;
    private TextView tvDetailsState;
    private TextView tvDetailsDescription;

    private ToDoItemDao toDoItemDao;

    private static final String TAG = "ToDoItemDetailsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_item_details);

        tvDetailsTitle = findViewById(R.id.tvDetailsTitle);
        tvDetailsDueDate = findViewById(R.id.tvDetailsDueDate);
        tvDetailsPriority = findViewById(R.id.tvDetailsPriority);
        cvPriority = findViewById(R.id.cvPriority);
        tvDetailsState = findViewById(R.id.tvDetailsState);
        tvDetailsDescription = findViewById(R.id.tvDetailsDescription);

        toDoItemDao = ((BackupDatabaseApplication) getApplicationContext()).getBackupDatabase().toDoItemDao();

        toDoItem = (ToDoItem) Parcels.unwrap(getIntent().getParcelableExtra(ToDoItem.class.getSimpleName()));
        if (toDoItem==null){
            Gson gson = new Gson();
            SharedPreferences sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
            String json = sharedPreferences.getString("lastDetails", "");
            toDoItem = gson.fromJson(json, ToDoItem.class);
            Log.d(TAG, toDoItem.toString());
        }
        Log.d(TAG, String.format("Showing details for '%s'", toDoItem.getTitle()));
        tvDetailsTitle.setText(toDoItem.getTitle());
        tvDetailsDueDate.setText(toDoItem.getDueDate());

        //Change color according to priority
        if(toDoItem.getPriority().equals("Low")){
            cvPriority.setCardBackgroundColor(getResources().getColor(R.color.colorGreyOpacity));
        } else if (toDoItem.getPriority().equals("Normal")){
            cvPriority.setCardBackgroundColor(getResources().getColor(R.color.colorPrimaryAccentOpacity));
        } else if (toDoItem.getPriority().equals("High")){
            cvPriority.setCardBackgroundColor(getResources().getColor(R.color.colorRedOpacity));
        }
        tvDetailsPriority.setText(toDoItem.getPriority());
        tvDetailsState.setText(toDoItem.getStatus());
        tvDetailsDescription.setText(toDoItem.getDescription());

        //Set description
        if (toDoItem.getDescription() == ""){
            tvDetailsDescription.setText("No description");
        }
        else{
            tvDetailsDescription.setText(toDoItem.getDescription());
        }
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

    //Upper menu (Logout)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.edit:{
                Log.d(TAG, "Editing item");
                Intent intent = new Intent(ToDoItemDetailsActivity.this, EditToDoItemActivity.class);
                intent.putExtra(ToDoItem.class.getSimpleName(), Parcels.wrap(toDoItem));
                // show the activity
                startActivity(intent);
                overridePendingTransition(R.anim.zoom_out, R.anim.static_animation);
                return true;
            }
        }
        return  super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, ShowListActivity.class));
    }

}