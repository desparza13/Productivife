package com.daniela.productivife;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.daniela.productivife.models.ToDoItem;

import java.util.List;

public class ToDoItemAdapter extends RecyclerView.Adapter<ToDoItemAdapter.ViewHolder> {
    private Context context;
    private List<ToDoItem> toDoItems;

    public ToDoItemAdapter(Context context, List<ToDoItem> toDoItems){
        this.context = context;
        this.toDoItems = toDoItems;
    }

    private ToDoItemAdapter.ClickListener mClickListener;

    public interface ClickListener{
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
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

    public class ViewHolder extends RecyclerView.ViewHolder{
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
        }

        public void bind(ToDoItem toDoItem)
        {
            tvItemTitle.setText(toDoItem.getTitle());
            tvItemStatus.setText(toDoItem.getState());
            tvItemDueDate.setText(toDoItem.getDueDate());
            tvItemPriority.setText(toDoItem.getPriority());
            tvItemPlace.setText(toDoItem.getPlace());
            tvItemDescription.setText(toDoItem.getDescription());
        }
    }




}
