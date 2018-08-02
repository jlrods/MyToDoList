package io.github.jlrods.mytodolist;

import android.util.Log;

/**
 * Created by rodjose1 on 01/08/2018.
 */

public enum Priority {
    //Define the possible priorities in this app
    NONE("None"),
    LOW("Low"),
    MEDIUM("Medium"),
    HIGH("High"),
    URGENT("Urgent");

    //Attributes definition
    private String name;

    //Full constructor
    Priority(String name){
        Log.d("EntFullCategory","Enter full constructor in the Category class.");
        this.name = name;
        Log.d("ExtFullCategory","Exit full constructor in the Category class.");
    }//End of Full Category constructor


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

    //Method to increase the System ordinal
    public int increaseOrdinal(){
        return this.ordinal()+1;
    }

    //Find a system by receiving the ordinal
    public static Priority findSystem(int ordinal){
        Log.d("Ent_findSystem","Ent findSystem method in the Unit_System enum.");
        //Declare and instantiate Unit_System object to be returned by method
        Priority priority = null;
        //Check the ordinal against the possible ordinals corresponding to each enum item
        switch (ordinal){
            case 1:
                priority = Priority.LOW;
                break;
            case 2:
                priority = Priority.MEDIUM;
                break;
            case 3:
                priority = Priority.HIGH;
                break;
            case 4:
                priority = Priority.URGENT;
                break;
            default:
                priority = Priority.NONE;
                break;
        }//End of switch statement
        Log.d("Ent_findSystem","Ent findSystem method in the Unit_System enum.");
        return priority;
    }// End of findProperty
}//End of Priority class

