package com.daniela.productivife;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.daniela.productivife.adapters.ToDoItemAdapter;
import com.daniela.productivife.models.ToDoItem;
import com.daniela.productivife.models.ToDoItemDao;
import com.daniela.productivife.models.ToDoItemWithUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import es.dmoral.toasty.Toasty;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class ShowListActivity extends AppCompatActivity{
    public static final String TAG = "ShowList";
    private String loggedUser;
    private int appliedFilter = FilterSort.ALL_PRIORITY;
    private RecyclerView rvToDoItems;
    private DatabaseReference databaseReference;

    private Dialog dialog;

    private List<ToDoItem> toDoItems;
    private List<ToDoItem> toDoItemsFromDB;
    private List<ToDoItem> filteredItems;
    private List<ToDoItem> searched;
    private ToDoItemAdapter adapter;

    ToDoItemDao toDoItemDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_list);

        //Get user
        SharedPreferences sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        loggedUser = sharedPreferences.getString("uid", "uid");

        //Create ActionBar
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("My to do items");
        actionBar.setDisplayShowHomeEnabled(true); //Arrow back to home fragment
        actionBar.setDisplayHomeAsUpEnabled(true);

        SearchView searchView = findViewById(R.id.svSearchTitle);
        rvToDoItems = findViewById(R.id.rvToDoItems);
        rvToDoItems.setHasFixedSize(true); //the recycler view will adapt to the list size

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("ToDoItems");

        dialog = new Dialog(ShowListActivity.this);

        toDoItems = new ArrayList<>();
        filteredItems = new ArrayList<>();
        adapter = new ToDoItemAdapter(this, toDoItems);
        rvToDoItems.setLayoutManager(new LinearLayoutManager(this));
        rvToDoItems.setAdapter(adapter);

        toDoItemDao = ((BackupDatabaseApplication) getApplicationContext()).getBackupDatabase().toDoItemDao();
        populateToDoList();

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "Showing to-do items from SQL database");
                List<ToDoItemWithUser> toDoItemWithUsers = toDoItemDao.toDoItems();
                toDoItemsFromDB = ToDoItemWithUser.getToDoItemsList(toDoItemWithUsers);
                filterList(appliedFilter);
                Log.i(TAG, filteredItems.toString());
                adapter.clear();
                adapter.addAll(filteredItems);
                ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        itemTouchHelper.attachToRecyclerView(rvToDoItems);
                    }
                });
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public boolean onQueryTextChange(String s) {
                searched = filteredItems.stream()
                        .filter(str -> str.getTitle().toLowerCase().trim().contains(s.toLowerCase()))
                        .collect(Collectors.toList());
                adapter.clear();
                adapter.addAll(searched);
                return false;
            }
        });
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            String idToDoItem = filteredItems.get(viewHolder.getBindingAdapterPosition()).getIdToDoItem();

            switch (direction) {
                case ItemTouchHelper.LEFT:
                    Button btnCancel;
                    Button btnDelete;
                    dialog.setContentView(R.layout.dialog_delete);
                    btnCancel = dialog.findViewById(R.id.btnCancel);
                    btnDelete = dialog.findViewById(R.id.btnDelete);

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
                    break;
                case ItemTouchHelper.RIGHT:
                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            toDoItemDao.markAsCompleted(idToDoItem);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toasty.success(ShowListActivity.this, "Item marked as completed").show();
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        }
                    });

                    break;
            }

        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(ShowListActivity.this, R.color.colorRed))
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(ShowListActivity.this, R.color.colorAccent))
                    .addSwipeLeftActionIcon(R.drawable.ic_delete)
                    .addSwipeRightActionIcon(R.drawable.ic_check)
                    .create()
                    .decorate();

            super.onChildDraw(c, recyclerView, viewHolder, dX/4, dY, actionState, isCurrentlyActive);
        }
    };

    private void sort(int direction){
        try {
            FilterSort.sort(filteredItems, 0, filteredItems.size()-1, direction);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        adapter.clear();
        adapter.addAll(filteredItems);
    }

    private void filterList(int appliedFilter){
        filteredItems.clear();
        if (loggedUser != "uid"){
            for (ToDoItem toDoItem : toDoItemsFromDB){
                if (toDoItem.getUserUid().equals(loggedUser)){
                    if (appliedFilter==FilterSort.LOW_PRIORITY && toDoItem.getPriority().equals("Low")){
                        filteredItems.add(toDoItem);
                    }
                    else if (appliedFilter==FilterSort.NORMAL_PRIORITY && toDoItem.getPriority().equals("Normal")){
                        filteredItems.add(toDoItem);
                    }
                    else if (appliedFilter==FilterSort.HIGH_PRIORITY && toDoItem.getPriority().equals("High")){
                        filteredItems.add(toDoItem);
                    }
                    else if (appliedFilter==FilterSort.ALL_PRIORITY){
                        filteredItems.add(toDoItem);
                    }
                }
            }
        } else {
            Toasty.error(ShowListActivity.this, "An error occured retrieving the user").show();
        }
    }

    private void deleteToDoItem(String idToDoItem) {
        if (InternetConnection.checkConnection(ShowListActivity.this)){
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    toDoItemDao.deleteToDoItem(idToDoItem);
                    List<ToDoItemWithUser> toDoItemWithUsers = toDoItemDao.toDoItems();
                    toDoItemsFromDB = ToDoItemWithUser.getToDoItemsList(toDoItemWithUsers);
                    startActivity(new Intent(ShowListActivity.this, ShowListActivity.class));
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
        } else {
            Toasty.error(ShowListActivity.this, "Unable to delete item. Retry when you have network connection").show();
            startActivity(new Intent(ShowListActivity.this, ShowListActivity.class));
        }

    }
    private void updateList(){
        filterList(appliedFilter);
        Log.i(TAG, filteredItems.toString());
        adapter.clear();
        adapter.addAll(filteredItems);
    }
    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("lastActivity", getClass().getName());
        editor.commit();
    }

    private void populateToDoList() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren())
                {
                    ToDoItem toDoItem = dataSnapshot.getValue(ToDoItem.class);
                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            if (toDoItemDao.getToDoItem(toDoItem.getIdToDoItem())==null){
                                if (toDoItemDao.getUser(toDoItem.getUserUid())==null){
                                    toDoItemDao.addUser(toDoItem.getUserUid(), toDoItem.getUser().getEmail());
                                }
                                toDoItemDao.addItem(toDoItem.getIdToDoItem(),
                                        toDoItem.getCurrentDateTime(),
                                        toDoItem.getTitle(),
                                        toDoItem.getDescription(),
                                        toDoItem.getPriority(),
                                        toDoItem.getDueDate(),
                                        toDoItem.getPlace(),
                                        toDoItem.getStatus(),
                                        toDoItem.getUserUid()
                                );
                            }
                        }
                    });
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
    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_showlist, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int item_id = item.getItemId();
        switch(item_id){
            case R.id.btnAll:
                appliedFilter = FilterSort.ALL_PRIORITY;
                updateList();
                break;
            case R.id.btnHighPriority:
                appliedFilter = FilterSort.HIGH_PRIORITY;
                updateList();
                break;
            case R.id.btnNormalPriority:
                appliedFilter = FilterSort.NORMAL_PRIORITY;
                updateList();
                break;
            case R.id.btnLowPriority:
                appliedFilter = FilterSort.LOW_PRIORITY;
                updateList();
                break;
            case R.id.btnAscendant:
                sort(FilterSort.ASCENDANT);
                break;
            case R.id.btnDescendant:
                sort(FilterSort.DESCENDANT);
                break;
            case R.id.btnAscendantPriority:
                sort(FilterSort.ASCENDANT_PRIORITY);
                break;
            case R.id.btnDescendantPriority:
                sort(FilterSort.DESCENDANT_PRIORITY);
                break;
        }
        return true;
    }
}