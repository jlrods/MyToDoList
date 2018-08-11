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

public class GroceryAdapter extends
        RecyclerView.Adapter<GroceryAdapter.ViewHolder>{
    protected TasksDB tasks;
    protected Cursor cursor;
    //protected String query;
    protected LayoutInflater inf;
    protected Context context;
    protected View.OnClickListener onClickListener;

    public GroceryAdapter(Context context,TasksDB tasks ,Cursor cursor){
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
        public TextView grocery, type;
        public CheckBox checkBox;
        public ViewHolder(View itemView) {
            super(itemView);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkBoxGrocery);
            grocery = (TextView)  itemView.findViewById(R.id.groceryName);
            type = (TextView) itemView.findViewById(R.id.groceryType);
        }//End of ViewHolder constructor
    }//End of ViewHolder inner class

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Inflate the task element layout within the RecycleViewer
        View view = inf.inflate(R.layout.grocery_element,null);
        view.setOnClickListener(onClickListener);
        return new ViewHolder(view);
    }//End of onCreateViewHolder method

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //id++;
        cursor.moveToPosition(position);
        Grocery grocery = tasks.extractGrocery(cursor);
        customizeViewHolder(holder,grocery);
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }


    //Method to cutomize each viewholder with each task data
    public void customizeViewHolder(ViewHolder h,Grocery grocery){
        h.grocery.setText(grocery.getDescription());
        h.type.setText(grocery.getType().toString());
        if(grocery.isSelected()){
            h.checkBox.setChecked(true);
        }
    }
}
