package io.github.jlrods.mytodolist;

import android.util.Log;
import java.util.Date;

/**
 * Created by rodjose1 on 01/08/2018.
 */

public class Task {
    //Attributes definition
    private int id;
    private String description;
    private Category category;
    private Priority priority;
    private boolean isDone;
    private boolean isAppointment;
    private long dueDate;
    private boolean isArchived;
    private String notes;
    private long dateCreated;
    private long dateClosed;
    private boolean isSelected;

    //Default constructor
    public Task(){
        Log.d("EntDefTask","Enter default constructor in the Task class.");
        this.id = -1;
        this.description ="";
        this.category = null;
        this.priority = Priority.NONE;
        this.isDone = false;
        this.isAppointment = false;
        this.dueDate = -1;
        this.isArchived = false;
        this.notes = "";
        this.dateCreated =System.currentTimeMillis() ;
        this.dateClosed = -1;
        this.isSelected = false;
        Log.d("ExtDefTask","Exit default constructor in the Task class.");
    }//End of default constructor

    //Grocery constructor
    public Task(int id,String groceryName, Category category,boolean isSelected){
        //Call default constructor
        this();
        Log.d("Ent3ArgTask","Enter 3 argument constructor in the Task class.");
        //Set attributes as per parameters
        this.id =id;
        this.description = groceryName;
        this.category = category;
        this.isSelected = isSelected;
        Log.d("Ent3ArgTask","Enter 3 argument constructor in the Task class.");
    }// End of Grocery constructor

    //Constructor for no task selected
    public Task(int id,String descripttion,Category category,Priority priority,boolean isDone,
                boolean isAppointment, long dueDate,boolean isArchived,String notes){
        Log.d("Ent9ArgTask","Enter 9 argument constructor in the Task class.");
        this.id = id;
        this.description =descripttion;
        this.category = category;
        this.priority = priority;
        this.isDone = isDone;
        this.isAppointment = isAppointment;
        this.dueDate = dueDate;
        this.isArchived = isArchived;
        this.notes = notes;
        this.dateCreated = System.currentTimeMillis() ;
        this.dateClosed = -1;
        this.isSelected = isSelected;
        Log.d("Ext9Arg","Exit full 9 argument onstructor in the Task class.");
    }//End of full constructor

    //Full constructor
    public Task(int id,String descripttion,Category category,Priority priority,boolean isDone,
                boolean isAppointment, long dueDate,boolean isArchived,String notes,boolean isSelected,long dateClosed){
        //Call the 9 argument constructor and pass in the parameters
        this(id,descripttion,category,priority,isDone,isAppointment,dueDate,isArchived,notes);
        Log.d("EntFullTask","Enter full constructor in the Task class.");
        this.isSelected = isSelected;
        this.dateClosed = dateClosed;
        Log.d("ExtFullTask","Exit full constructor in the Task class.");
    }//End of full constructor

    //Getter and setter methods
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    public boolean isAppointment() {
        return isAppointment;
    }

    public void setAppointment(boolean appointment) {
        isAppointment = appointment;
    }

    public long getDueDate() {
        return dueDate;
    }

    public void setDueDate(long dueDate) {
        this.dueDate = dueDate;
    }

    public boolean isArchived() {
        return isArchived;
    }

    public void setArchived(boolean archived) {
        isArchived = archived;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public long getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(long dateCreated) {
        this.dateCreated = dateCreated;
    }

    public long getDateClosed() {
        return dateClosed;
    }

    public void setDateClosed(long dateClosed) {
        this.dateClosed = dateClosed;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String toString(){
        return this.getDescription();
    }// End of toString method
}//End of the  Task class
