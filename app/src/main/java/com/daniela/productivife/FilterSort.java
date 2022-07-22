package com.daniela.productivife;

import android.util.Log;

import com.daniela.productivife.models.ToDoItem;
import com.daniela.productivife.models.ToDoItemWithUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class FilterSort {
    public static final int ASCENDANT_PRIORITY = 3;
    public static final int DESCENDANT_PRIORITY = 2;
    public static final int ASCENDANT = 1;
    public static final int DESCENDANT = 0;
    public static final int LOW_PRIORITY = 0;
    public static final int NORMAL_PRIORITY = 1;
    public static final int HIGH_PRIORITY = 2;
    public static final int ALL_PRIORITY = 3;

    //Quicksort implementation
    public static boolean sort (List<ToDoItem> toDoItems, int from, int to, int direction) throws ParseException {
        if (toDoItems.size() <=1){
            Log.i("Sort", "Couldn't get list");
        }
        if (from < to){
            int pivot = from;
            int left = from + 1;
            int right = to;
            while (left <= right){
                if (direction==ASCENDANT || direction==DESCENDANT){ //Sort by due date in a certain direction
                    while (left<=to && compareDates(toDoItems.get(pivot), toDoItems.get(left), direction)){ //pivot date > left date
                        left++;
                    }
                    while (right>from && !compareDates(toDoItems.get(pivot), toDoItems.get(right), direction)){ //pivot date < right date
                        right--;
                    }
                }
                else if (direction==ASCENDANT_PRIORITY || direction==DESCENDANT_PRIORITY){ // sort by priority
                    while (left<=to && comparePriority(toDoItems.get(pivot), toDoItems.get(left), direction)){ //pivot priority > left priority
                        left++;
                    }
                    while (right>from && !comparePriority(toDoItems.get(pivot), toDoItems.get(right), direction)){ //pivot priority < right priority
                        right--;
                    }
                }
                if (left < right){
                    Collections.swap(toDoItems, left, right);
                }
            }
            Collections.swap(toDoItems, pivot, left-1);
            sort(toDoItems, from, right-1, direction); //recursive
            sort(toDoItems, right+1, to, direction); //recursive
        }
        return false;
    }

    //Compare 2 dates (used to sort by due date)
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

    //Get priority
    public static int getPriority(ToDoItem toDoItem){
        if (toDoItem.getPriority().equals("Low")){
            return 1;
        }
        else if (toDoItem.getPriority().equals("Normal")){
            return 2;
        }
        else if (toDoItem.getPriority().equals("High")){
            return 3;
        }
        return 0;
    }

    //Compare 2 priorities (used to sort by priority)
    public static boolean comparePriority(ToDoItem toDoItemPivot, ToDoItem toDoItemSide, int direction) throws ParseException {
        if (direction==ASCENDANT_PRIORITY){
            if (getPriority(toDoItemPivot)>getPriority(toDoItemSide)){
                return true;
            }
            return false;
        }
        else if (direction == DESCENDANT_PRIORITY){
            if (getPriority(toDoItemPivot)>getPriority(toDoItemSide)){
                return false;
            }
            return true;
        }
        else{
            return false;
        }
    }
}
