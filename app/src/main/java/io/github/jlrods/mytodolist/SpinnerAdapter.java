package io.github.jlrods.mytodolist;

import android.content.Context;
import android.database.Cursor;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.RelativeSizeSpan;
import android.text.style.SuperscriptSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by rodjose1 on 05/09/2018.
 */

//Class to handle the adapter objects to link spinners UI and data
public class SpinnerAdapter extends CursorAdapter {
    //Adapter constructor method
    public SpinnerAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        Log.d("Adapter_Constructor","Leaves SpinnerAdapter constructor.");
    }//End of constructor method

    //Method to create a new view
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.spinner_item,parent,false);
    }

    //Method to bind the view and the data via a cursor
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Log.d("Ent_bindView","Enter bindView method to populate spinners in SpinnerAdapter class.");
        //Declare and instantiate a TextView object to hold the unit name and symbol
        TextView tvItem = (TextView) view.findViewById(R.id.tvItem);
        //Declare and instantiate a String to hold the name by stracting data from cursor (Column 1 will hold the name attribute)
        String itemText = cursor.getString(1);
        //Declare and instantiate a String to hold the name by stracting data from cursor, where the column name is Symbol
        //set the text of the TextView with the final result text, which is formatted to show superscipt text
        tvItem.setText(itemText);
        Log.d("Ent_bindView","Exit bindView method to populate spinners in SpinnerAdapter class.");
    }//End of bindView method

}//End of SpinnerAdapter class