package com.daniela.productivife.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.daniela.productivife.R;
import com.daniela.productivife.ToDoItemDetailsActivity;
import com.daniela.productivife.models.ToDoItem;

import org.parceler.Parcels;

import java.util.List;

public class ToDoItemAdapter extends RecyclerView.Adapter<ToDoItemAdapter.ViewHolder> {
    private Context context;
    private List<ToDoItem> toDoItems;

    public ToDoItemAdapter(Context context, List<ToDoItem> toDoItems){
        this.context = context;
        this.toDoItems = toDoItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_todo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ToDoItem toDoItem = toDoItems.get(position);
        holder.bind(toDoItem);
    }

    @Override
    public int getItemCount() {
        return toDoItems.size();
    }

    public void addAll(List<ToDoItem> list){
        toDoItems.addAll(list);
        notifyDataSetChanged();
    }

    public void clear() {
        toDoItems.clear();
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView tvItemTitle;
        TextView tvItemStatus;
        TextView tvItemDueDate;
        TextView tvItemPriority;
        TextView tvItemPlace;
        TextView tvItemDescription;

        public ViewHolder(@NonNull View view) {
            super(view);
            tvItemTitle = view.findViewById(R.id.tvItemTitle);
            tvItemStatus = view.findViewById(R.id.tvItemStatus);
            tvItemDueDate = view.findViewById(R.id.tvItemDueDate);
            tvItemPriority = view.findViewById(R.id.tvItemPriority);
            tvItemPlace= view.findViewById(R.id.tvItemPlace);
            tvItemDescription = view.findViewById(R.id.tvItemDescription);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.i("Gesture","click");
            // gets item position
            int position = getBindingAdapterPosition();
            // make sure the position is valid, i.e. actually exists in the view
            if (position != RecyclerView.NO_POSITION) {
                // get the movie at the position, this won't work if the class is static
                ToDoItem toDoItem = toDoItems.get(position);
                // create intent for the new activity
                Intent intent = new Intent(context, ToDoItemDetailsActivity.class);
                intent.putExtra(ToDoItem.class.getSimpleName(), Parcels.wrap(toDoItem));
                // show the activity
                context.startActivity(intent);
            }
        }

        public void bind(ToDoItem toDoItem)
        {
            tvItemTitle.setText(toDoItem.getTitle());
            tvItemStatus.setText(toDoItem.getStatus());
            tvItemDueDate.setText(toDoItem.getDueDate());
            tvItemPriority.setText(toDoItem.getPriority());
            tvItemPlace.setText(toDoItem.getPlace());
            tvItemDescription.setText(toDoItem.getDescription());
        }

    }




}
