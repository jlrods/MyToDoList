package io.github.jlrods.mytodolist;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by rodjose1 on 08/08/2018.
 */

//Class that inherits from TaskAdapter to handle UI for grocery objects
public class GroceryAdapter extends TaskAdapter{

    //Gocery Adapter constructor
    public GroceryAdapter(Context context,TasksDB tasks ,Cursor cursor){
        //Call super constructor. No additional attributes for this class
        super(context,tasks,cursor);
    }//End of GroceryAdapter constructor

    //Define ViewHolder class which inherits from the TaksAdapter.ViewHolder class
    public static class ViewHolder extends TaskAdapter.ViewHolder{
        //Declare UI objects  from the grocery_element layout to represent them within the logic
        public TextView grocery, type;
        public CheckBox checkBox;
        //Define the ViewHolder constructor
        public ViewHolder(View itemView) {
            //Call super constructor method
            super(itemView);
            Log.d("Ent_GroceryVHCreator","Enter ViewHolder constructor in the GroceryAdapter class.");
            //Instantiate variables to represent logically the item layout components
            checkBox = (CheckBox) itemView.findViewById(R.id.cbGrocery);
            grocery = (TextView)  itemView.findViewById(R.id.tvGroceryName);
            type = (TextView) itemView.findViewById(R.id.tvGroceryType);
            Log.d("Ext_GroceryVHCreator","Exit ViewHolder constructor in the GroceryAdapter class.");
        }//End of ViewHolder constructor
    }//End of ViewHolder inner class

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d("Ent_GroceryVHOnCreate","Enter the onCreateViewHolder method in the GroceryAdapter class.");
        //Inflate the task element layout within the RecycleViewer
        View view = inf.inflate(R.layout.grocery_element,null);
        Log.d("Ext_GroceryVHOnCreate","Exit the onCreateViewHolder method in the GroceryAdapter class.");
        return new ViewHolder(view);
    }//End of onCreateViewHolder method

    @Override
    public void onBindViewHolder(TaskAdapter.ViewHolder holder, int position) {
        //Call method that will customize each view holder with specific data coming from database
        this.customizeViewHolderGrocery((GroceryAdapter.ViewHolder)holder,position);
    }//End of onBindViewHolder method

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    //Method to cutomize each viewholder with each task data. This does not override the same method in the TaskAdapter class as the argument types are different
    public void customizeViewHolderGrocery (ViewHolder h,int position){
        Log.d("Ent_customozeGroceryVH","Enter the curotmizeViewHolder method in the GroceryAdapter class.");
        //Move current cursor to positon passed in as parameter
        cursor.moveToPosition(position);
        //Declare and instantiate a Task object by extracting data from cursor in the given position
        Grocery grocery = tasks.extractGrocery(cursor);
        //Set the view holder text for the grocery item: grocery name and type
        h.grocery.setText(grocery.getDescription());
        h.type.setText(grocery.getType().toString());
        //Check the isSelected attribute of the grocery and then check the checkbox if required otherwise, set to not checked
        if(grocery.isSelected()){
            h.checkBox.setChecked(true);
        }else{
            h.checkBox.setChecked(false);
        }//End of if statement
        //Include the item's adapter position and the checkbox state in the list
        itemStateArray.put(position,grocery.isSelected());
        h.checkBox.setOnCheckedChangeListener(this.onCheckedChangeListener);
        Log.d("Ext_customozeGroceryVH","Exit the curotmizeViewHolder method in the GroceryAdapter class.");
    }//End of customizeViewHolderGrocery


}//End of GroceryAdapter class
