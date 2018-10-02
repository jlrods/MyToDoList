package io.github.jlrods.mytodolist;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by rodjose1 on 08/08/2018.
 */

//Class to handle the HoldViewer objectes within the recyclerView + bind data to each item
public class TaskAdapter extends
    RecyclerView.Adapter<TaskAdapter.ViewHolder>{
    //Define the class attributes
    //Ad database class manager to access data from database
    protected TasksDB tasks;
    //Cursor to retrieve, hold and manipulate data from database
    protected Cursor cursor;
    //Inflator object to inflate UI
    protected LayoutInflater inf;
    protected Context context;
    //protected View.OnClickListener onClickListener;
    protected CheckBox.OnCheckedChangeListener onCheckedChangeListener;
    // sparse boolean array for checking the state of the items
    protected static SparseBooleanArray itemStateArray= new SparseBooleanArray();
    protected View.OnClickListener onClickListener;
    public TaskAdapter(Context context,TasksDB tasks ,Cursor cursor){
        Log.d("Ent_TaskAdapterConst","Enter the TaskAdapter constructor in the TaskAdapter class.");
        //Make object attributes to match values from parameters
        this.tasks = tasks;
        this.cursor= cursor;
        this.context = context;
        inf =(LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Log.d("Ext_TaskAdapterConst","Exit the TaskAdapter constructor in the TaskAdapter class.");
    }//End of TaskAdapter constructor

    public Cursor getCursor() {
        return cursor;
    }
    public void setCursor(Cursor cursor) {
        this.cursor = cursor;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        //Declare UI objects  from the task_element layout to represent them within the logic
        public TextView description, date, priority,category;
        public CheckBox checkBox;
        //Define the ViewHolder constructor
        public ViewHolder(View itemView) {
            //Call super constructor method
            super(itemView);
            Log.d("Ent_TaskVHCreator","Enter ViewHolder constructor in the TaskAdapter class.");
            //DInstantiate variables to represent logically the item layout components
            description = (TextView) itemView.findViewById(R.id.tvDescription);
            date = (TextView) itemView.findViewById(R.id.tvDate);
            priority = (TextView) itemView.findViewById(R.id.tvPriority);
            category = (TextView) itemView.findViewById(R.id.tvCategory);
            checkBox = (CheckBox) itemView.findViewById(R.id.cbTask);
            Log.d("Ext_TaskVHCreator","Exit ViewHolder constructor in the TaskAdapter class.");
        }//End of ViewHolder constructor
    }//End of ViewHolder inner class

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d("Ent_TaskVHOnCreate","Enter the onCreateViewHolder method in the TaskAdapter class.");
        //Inflate the task element layout within the RecycleViewer
        View view = inf.inflate(R.layout.task_element,null);
        view.setOnClickListener(onClickListener);
        Log.d("Ext_TaskVHOnCreate","Exit the onCreateViewHolder method in the TaskAdapter class.");
        return new ViewHolder(view);
    }//End of onCreateViewHolder method

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //Call method that will customize each view holder with specific data coming from database
        customizeViewHolder(holder,position);
    }//End of onBindViewHolder

    @Override
    public int getItemCount() {return cursor.getCount(); }

    //Method to customize each viewholder with each task data
    public void customizeViewHolder(ViewHolder h,int position){
        Log.d("Ent_customozeTaksVH","Enter the curotmizeViewHolder method in the TaskAdapter class.");
        //Move current cursor to positon passed in as parameter
        cursor.moveToPosition(position);
        //Declare and instantiate a Task object by extracting data from cursor in the given position
        Task task = tasks.extractTask(cursor);
        //Set the view holder text for the task description, date, category and priority
        h.description.setText(task.getDescription());
        //Declare and instantiate a new DateFormat object to display date in current format (This format may change based on the app settings)
        SimpleDateFormat format = new SimpleDateFormat(MainActivity.getDateFormat());
        Date date = new Date(task.getDateCreated());
        h.date.setText(format.format(date));
        h.category.setText(task.getCategory().toString());
        h.priority.setText(task.getPriority().toString());
        //Check the isSelected attribute of the task and then check the checkbox if required otherwise, set to not checked
        if(task.isSelected()){
            h.checkBox.setChecked(true);
        }else{
            h.checkBox.setChecked(false);
        }//End of if else statement
        //Include the item's adapter position and the checkbox state in the list
        itemStateArray.put(position,task.isSelected());
        //Set the onCheckedChangeListener to the current viewholder's checkbox
        h.checkBox.setOnCheckedChangeListener(this.onCheckedChangeListener);
        //Check if task is done so it can be highlighted
        if(task.isDone()){
            h.description.setText(Html.fromHtml("<font color='"+MainActivity.getDoneColor()+"'>"+ h.description.getText()+"</font>"));
            //h.description.setHighlightColor(MainActivity.getHighlightColor());
            //h.description.setPaintFlags(h.description.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            h.description.setBackgroundColor(Color.parseColor(MainActivity.getDoneHighlighter()));
            //h.description.setTextColor(MainActivity.getHighlightColor());
        }else{
            //h.description.setText(h.description.getText()+"</font>"));
            //h.description.setPaintFlags(h.description.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
            //h.description.setTextColor(MainActivity.getPrimaryTextColor());
            h.description.setBackgroundColor(Color.parseColor(MainActivity.getWhiteBackground()));
        }
        Log.d("Ext_customozeTaksVH","Exit the curotmizeViewHolder method in the TaskAdapter class.");
    }//End of customizeViewHolder method

    //Method to set the onCheckedChangeListener for each checkbox within the recycler view
    public void setOnItemCheckedChangeListener(CheckBox.OnCheckedChangeListener listener) {
        this.onCheckedChangeListener = listener;
    }//End of setOnItemCheckedChangeListener

    //Method to update the key value list that keeps record of current statate of each task's check box
    public void updateItemIsSelected(int position,boolean isSelected){
        //Add or overwrite the checkbox list
        itemStateArray.put(position,isSelected);
    }//End of updateItemIsSelected method

    public void setOnItemClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

}//End of TaskAdapter class
