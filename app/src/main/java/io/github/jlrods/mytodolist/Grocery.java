package io.github.jlrods.mytodolist;

import android.util.Log;

/**
 * Created by rodjose1 on 01/08/2018.
 */

public class Grocery extends Task{
    private GroceryType type;

    public Grocery(){
        super();
        this.type = null;
    }

    //Grocery constructor
    public Grocery(int id,String groceryName, Category category, GroceryType type){
        super(id,groceryName,category);
        this.type = type;
    }
}//End of Grocery class
