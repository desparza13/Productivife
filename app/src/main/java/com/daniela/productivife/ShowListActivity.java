package com.daniela.productivife;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.daniela.productivife.models.ToDoItem;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class ShowListActivity extends AppCompatActivity {
    public static final String TAG = "ShowList";
    private RecyclerView rvToDoItems;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private LinearLayoutManager linearLayoutManager;

    private FirebaseRecyclerAdapter<ToDoItem, ToDoItemAdapter.ViewHolder> firebaseRecyclerAdapter;
    private FirebaseRecyclerOptions<ToDoItem> firebaseRecyclerOptions;

    private List<ToDoItem> toDoItems;
    private ToDoItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_list);

        //Create ActionBar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("My to do items");
        actionBar.setDisplayShowHomeEnabled(true); //Arrow back to home fragment
        actionBar.setDisplayHomeAsUpEnabled(true);

        rvToDoItems = findViewById(R.id.rvToDoItems);
        rvToDoItems.setHasFixedSize(true); //the recycler view will adapt to the list size

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("ToDoItems");

        toDoItems = new ArrayList<>();
        adapter = new ToDoItemAdapter(this, toDoItems);
        rvToDoItems.setLayoutManager(new LinearLayoutManager(this));
        rvToDoItems.setAdapter(adapter);
        populateToDoList();
    }

    private void populateToDoList() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren())
                {
                    ToDoItem toDoItem = dataSnapshot.getValue(ToDoItem.class);
                    toDoItems.add(toDoItem);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toasty.error(ShowListActivity.this, "Couldn't retrieve to do items", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

}