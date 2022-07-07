package com.daniela.productivife;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.daniela.productivife.models.ToDoItem;

import org.parceler.Parcels;

public class ToDoItemDetailsActivity extends AppCompatActivity {
    private ToDoItem toDoItem;
    private TextView tvDetailsTitle;
    private TextView tvDetailsDueDate;
    private TextView tvDetailsPriority;
    private CardView cvPriority;
    private TextView tvDetailsState;
    private TextView tvDetailsDescription;

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

        toDoItem = (ToDoItem) Parcels.unwrap(getIntent().getParcelableExtra(ToDoItem.class.getSimpleName()));
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
        tvDetailsState.setText(toDoItem.getState());

        //Set description
        if (toDoItem.getDescription() == ""){
            tvDetailsDescription.setText("No description");
        }
        else{
            tvDetailsDescription.setText(toDoItem.getDescription());
        }
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
                return true;
            }
        }
        return  super.onOptionsItemSelected(item);
    }

}