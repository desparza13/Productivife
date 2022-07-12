package com.daniela.productivife;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.daniela.productivife.models.ToDoItem;
import com.daniela.productivife.models.ToDoItemDao;
import com.daniela.productivife.models.ToDoItemWithUser;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class ShowListActivity extends AppCompatActivity{
    public static final String TAG = "ShowList";
    private RecyclerView rvToDoItems;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private Dialog dialog;

    private List<ToDoItem> toDoItems;
    private List<ToDoItem> toDoItemsFromDB;
    private ToDoItemAdapter adapter;

    ToDoItemDao toDoItemDao;

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

        dialog = new Dialog(ShowListActivity.this);

        toDoItems = new ArrayList<>();
        adapter = new ToDoItemAdapter(this, toDoItems);
        rvToDoItems.setLayoutManager(new LinearLayoutManager(this));
        rvToDoItems.setAdapter(adapter);
        //populateToDoList();

        toDoItemDao = ((BackupDatabaseApplication) getApplicationContext()).getBackupDatabase().toDoItemDao();

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "Showing to-do items from SQL database");
                List<ToDoItemWithUser> toDoItemWithUsers = toDoItemDao.toDoItems();
                toDoItemsFromDB = ToDoItemWithUser.getToDoItemsList(toDoItemWithUsers);
                adapter.clear();
                adapter.addAll(toDoItemsFromDB);
                ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        itemTouchHelper.attachToRecyclerView(rvToDoItems);
                    }
                });
            }
        });
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            Button btnCancel;
            Button btnDelete;
            dialog.setContentView(R.layout.dialog_delete);
            btnCancel = dialog.findViewById(R.id.btnCancel);
            btnDelete = dialog.findViewById(R.id.btnDelete);

            String idToDoItem = toDoItemsFromDB.get(viewHolder.getBindingAdapterPosition()).getIdToDoItem();

            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    adapter.notifyDataSetChanged();
                }
            });

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteToDoItem(idToDoItem);
                    toDoItems.clear();
                    dialog.dismiss();
                }
            });
            dialog.show();
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addBackgroundColor(ContextCompat.getColor(ShowListActivity.this, R.color.colorRed))
                    .addActionIcon(R.drawable.ic_delete)
                    .create()
                    .decorate();

            super.onChildDraw(c, recyclerView, viewHolder, dX/4, dY, actionState, isCurrentlyActive);
        }
    };

    private void deleteToDoItem(String idToDoItem) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                toDoItemDao.deleteToDoItem(idToDoItem);
                List<ToDoItemWithUser> toDoItemWithUsers = toDoItemDao.toDoItems();
                toDoItemsFromDB = ToDoItemWithUser.getToDoItemsList(toDoItemWithUsers);
                adapter.clear();
                adapter.addAll(toDoItemsFromDB);
                ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        itemTouchHelper.attachToRecyclerView(rvToDoItems);
                    }
                });
            }
        });
        Query query = databaseReference.orderByChild("idToDoItem").equalTo(idToDoItem);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    dataSnapshot.getRef().removeValue();
                }
                Toasty.success(ShowListActivity.this, "The to-do item was successfully deleted", Toast.LENGTH_SHORT).show();
                //boolean successfullyDeleted = true;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toasty.error(ShowListActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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