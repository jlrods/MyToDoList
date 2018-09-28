package io.github.jlrods.mytodolist;

import android.util.Log;

/**
 * Created by rodjose1 on 01/08/2018.
 */

public class GroceryType extends Category{
    //Attributes definition
    //private int id;
    //private String name;
    private boolean isInFilter;

    //Default constructor
    public GroceryType(){
        super();
        isInFilter = false;
        Log.d("ExtDefGroceryType","Exit default constructor in the GroceryType class.");
    }//End of default constructor

    //Full constructor
    public GroceryType(int id,String name,boolean isInFilter){
        super(id,name);
        this.isInFilter = isInFilter;
        Log.d("ExtFullGroceryType","Exit full constructor in the GroceryType class.");
    }//End of Full Category constructor

    public boolean isInFilter() {
        return isInFilter;
    }

    public void setInFilter(boolean inFilter) {
        isInFilter = inFilter;
    }


}// End of GroceryType class
