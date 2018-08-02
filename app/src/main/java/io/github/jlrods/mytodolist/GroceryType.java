package io.github.jlrods.mytodolist;

import android.util.Log;

/**
 * Created by rodjose1 on 01/08/2018.
 */

public class GroceryType extends Category{
    //Attributes definition
    private int id;
    private String name;

    //Default constructor
    public GroceryType(){
        super();
        Log.d("ExtDefGroceryType","Exit default constructor in the GroceryType class.");
    }//End of default constructor

    //Full constructor
    public GroceryType(int id,String name){
        super(id,name);
        Log.d("ExtFullGroceryType","Exit full constructor in the GroceryType class.");
    }//End of Full Category constructor
}// End of GroceryType class
