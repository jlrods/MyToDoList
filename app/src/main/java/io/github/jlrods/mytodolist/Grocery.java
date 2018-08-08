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
    public Grocery(int id,String groceryName, Category category, GroceryType type,boolean isSelected){
        super(id,groceryName,category,isSelected);
        this.type = type;
    }

    //Getter and Setter method
    public GroceryType getType(){
        return this.type;
    }
}//End of Grocery class
