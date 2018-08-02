package io.github.jlrods.mytodolist;

import android.util.Log;

/**
 * Created by rodjose1 on 01/08/2018.
 */

public class Category {
    //Attributes definition
    private int id;
    private String name;

    //Default constructor
    public Category(){
        Log.d("EntDefCategory","Enter default constructor in the Category class.");
        this.id = -1;
        this.name ="";
        Log.d("ExtDefCategory","Exit default constructor in the Category class.");
    }//End of default constructor

    //Full constructor
    public Category(int id,String name){
        Log.d("EntFullCategory","Enter full constructor in the Category class.");
        this.id = id;
        this.name = name;
        Log.d("ExtFullCategory","Exit full constructor in the Category class.");
    }//End of Full Category constructor

    //Getter and setter methods
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    //Method to print category name when a strign is required
    public String toString(){
        return this.getName();
    }//End of toString method
}//End of Category class
