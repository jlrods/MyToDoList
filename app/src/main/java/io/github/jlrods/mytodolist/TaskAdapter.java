package io.github.jlrods.mytodolist;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
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

public class TaskAdapter extends
    RecyclerView.Adapter<TaskAdapter.ViewHolder>{
    protected TasksDB tasks;
    protected Cursor cursor;
    //protected String query;
    protected LayoutInflater inf;
    protected Context context;
    protected View.OnClickListener onClickListener;

    public TaskAdapter(Context context,TasksDB tasks ,Cursor cursor){
        this.tasks = tasks;
        this.cursor= cursor;
        this.context = context;
        inf =(LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public Cursor getCursor() {
        return cursor;
    }
    public void setCursor(Cursor cursor) {
        this.cursor = cursor;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView description, date, priority,category;
        public CheckBox checkBox;
        public ViewHolder(View itemView) {
            super(itemView);
            description = (TextView) itemView.findViewById(R.id.description);
            date = (TextView) itemView.findViewById(R.id.date);
            priority = (TextView) itemView.findViewById(R.id.priority);
            category = (TextView) itemView.findViewById(R.id.category);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkBox);
        }//End of ViewHolder constructor
    }//End of ViewHolder inner class

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Inflate the task element layout within the RecycleViewer
        View view = inf.inflate(R.layout.task_element,null);
        view.setOnClickListener(onClickListener);
        return new ViewHolder(view);
    }//End of onCreateViewHolder method

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //id++;
        cursor.moveToPosition(position);
        Task task = tasks.extractTask(cursor);
        customizeViewHolder(holder,task);
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }


    //Method to cutomize each viewholder with each task data
    public void customizeViewHolder(ViewHolder h,Task task){
        h.description.setText(task.getDescription());
        h.date.setText(DateFormat.getDateInstance().format(
                new Date(task.getDateCreated())));
        h.category.setText(task.getCategory().toString());
        h.priority.setText(task.getPriority().toString());
        if(task.isSelected()){
            h.checkBox.setChecked(true);
        }
    }
}
