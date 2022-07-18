package com.daniela.productivife;

import android.util.Log;

import com.daniela.productivife.models.ToDoItem;
import com.daniela.productivife.models.ToDoItemWithUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class FilterSort {
    public static int ASCENDANT = 1;
    public static int DESCENDANT = 0;
    public static int LOW_PRIORITY = 0;
    public static int NORMAL_PRIORITY = 1;
    public static int HIGH_PRIORITY = 2;
    public static int ALL_PRIORITY = 3;

    public static boolean sort (List<ToDoItem> toDoItems, int from, int to, int direction) throws ParseException {
        if (toDoItems.size() <=1){
            Log.i("Sort", "Couldn't get list");
        }
        if (from < to){
            int pivot = from;
            int left = from + 1;
            int right = to;
            while (left <= right){
                while (left<=to && compareDates(toDoItems.get(pivot), toDoItems.get(left), direction)){ //pivot date > left date
                    left++;
                }
                while (right>from && !compareDates(toDoItems.get(pivot), toDoItems.get(right), direction)){ //pivot date < right date
                    right--;
                }
                if (left < right){
                    Log.i("Sort", "Swap");
                    Collections.swap(toDoItems, left, right);
                }
            }
            Collections.swap(toDoItems, pivot, left-1);
            sort(toDoItems, from, right-1, direction);
            sort(toDoItems, right+1, to, direction);
        }
        return false;
    }
    public static boolean compareDates(ToDoItem toDoItemPivot, ToDoItem toDoItemSide, int direction) throws ParseException {
        SimpleDateFormat sdFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date datePivot = sdFormat.parse(toDoItemPivot.getDueDate());
        Date dateSide = sdFormat.parse(toDoItemSide.getDueDate());
        if (direction==ASCENDANT){
            if (datePivot.compareTo(dateSide)>0){ //pivot date occurs after side date
                return true;
            }
            return false;
        }
        else if (direction == DESCENDANT){
            if (datePivot.compareTo(dateSide)>0){ //pivot date occurs after side date
                return false;
            }
            return true;
        }
        else{
            return false;
        }
    }
    public static void print(List<ToDoItem> toDoItemWithUsers){
        for (int i=0; i<toDoItemWithUsers.size(); i++){
            Log.i("Sort", toDoItemWithUsers.get(i).getTitle() + toDoItemWithUsers.get(i).getDueDate());
        }
    }

}
