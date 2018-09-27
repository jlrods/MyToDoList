package io.github.jlrods.mytodolist;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by rodjose1 on 24/09/2018.
 */

public class EditTaskActivity extends DisplayTaskActivity {
    protected Cursor cItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Call the super.OnCreate method
        super.onCreate(savedInstanceState);
        Log.d("Ent_onCreEditTask","Enter the onCreate method in the EditTaskActivity class.");
        //Set layout for this activity
        //setContentView(R.layout.activity_add_task);
        //Set data as per task or grocery selected on previous activity
        int id = this.extras.getInt("id");
        //Declare two spinner adapters, one for the category spinner and one for the priority spinner
        SpinnerAdapter adapterCategory;
        SpinnerAdapter adapterPriority;
        if(extras.getString("category").equals(MainActivity.getGroceryCategory())){
            //Retrieve the grocery with id passed via extras
            cItem = MainActivity.db.runQuery("SELECT * FROM GROCERIES WHERE _id = "+id);
            if(cItem.moveToNext()){
                Grocery grocery = MainActivity.db.extractGrocery(cItem);
                this.etDescription.setText(grocery.getDescription());
                //this.cCategory.moveToPosition(grocery.getType().getId());
                this.spCategory.setSelection(grocery.getType().getId()-1);
                adapterCategory = new SpinnerAdapter(this,cCategory, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
                //Set the adapter for the Category spinner
                spCategory.setAdapter(adapterCategory);
            }//End of if statement to check the cursor is not null or empty
        }else{
            //Retrieve the grocery with id passed via extras
            cItem = MainActivity.db.runQuery("SELECT * FROM TASK WHERE _id = "+id);
            if(cItem.moveToNext()){
                Task task = MainActivity.db.extractTask(cItem);
                this.etDescription.setText(task.getDescription());
                //this.cCategory.moveToPosition(task.getCategory().getId()-1);
                adapterCategory = new SpinnerAdapter(this,cCategory, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
                spCategory.setAdapter(adapterCategory);
                int position = adapterCategory.findItemPosition(task.getCategory().toString());
                spCategory.setSelection(position);
                //this.cPriority.moveToPosition(task.getPriority().ordinal());
                adapterPriority = new SpinnerAdapter(this,cPriority,CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
                spPriority.setAdapter(adapterPriority);
                spPriority.setSelection(task.getPriority().ordinal());
                if(task.isAppointment()){
                    //Declare and initialize a new Calendar object with current date and time
                    Calendar calendar = Calendar.getInstance();
                    //Set the year, month and day, which were passed in as params
                    calendar.setTimeInMillis (task.getDueDate());
                    /*//Declare and isntantiate a new DateFormat object to display date in current format (This format may change based on the app settings)
                    SimpleDateFormat format = new SimpleDateFormat(MainActivity.getDateFormat());
                    //Declare and instantiate a new date object, using the date set in calendar. Get the time in millisecs
                    Date date = new Date(calendar.getTimeInMillis());*/
                    //Set the text in date text view according to the new date
                    this.tvDate.setText(this.getDateString(calendar));
                    int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
                    int minute = calendar.get(Calendar.MINUTE);
                    //Declare and initialize empty string objects to construct the full time text
                    String time = this.getTimeString(hourOfDay,minute);
                   /* String amPm = "";
                    String hour = "";
                    String min="0";

                    //Set the hour text to be the same as the hour passed in as a param
                    hour += hourOfDay;
                    //Check if the that param is less than 12
                    if(hourOfDay <12){
                        //That means it's the morning time
                        amPm = "AM";
                        //Check if hour param is greater than 9 (10 or 11)
                        if(hourOfDay >9 ){
                            //If that is the case, hour string is the same as the hour param
                            hour = String.valueOf(hourOfDay);
                        }else {
                            //Otherwise, add a lead 0 to the string (07,08,09,etc)
                            hour = "0" + String.valueOf(hourOfDay);
                        }//end of if else that checks the hour param (morning case)
                    }else if(hourOfDay == 12){
                        //If hour param is equal to 12, it will be considered as afternoon or night time
                        amPm = "PM";
                    }else if(hourOfDay>12){
                        //In case hour time is grater than 12 (13,14,15...)
                        //Set to PM hour
                        amPm = "PM";
                        //Check if greater than 9PM
                        if(hourOfDay > 21){
                            //In that case, set the hour text to be the difference between hour param and 12
                            hour = String.valueOf(hourOfDay-12);
                        }else{
                            //Otherwise, do the same subtraction as above and add a lead zero
                            hour = "0" + String.valueOf(hourOfDay-12);
                        }//End of if else statement to check the hour time (afternoon and night case)
                    }//End of if else statement that checks the hour param
                    //Check the minute param has only one digit (<10)
                    if(minute < 10){
                        //if it is, add the value to the current 0 in the minute string
                        min += minute;
                    }else{
                        //Otherwise, reassign the value of minute string to be equal to exact minute param
                        min= String.valueOf(minute);
                    }//End of if else statement that checks the minute param*/
                    this.tvHour.setText(time);
                }else{
                    this.cbIsAppointment.setChecked(false);
                }//End of if else statement to check isAppointment is true
                this.etNotes.setText(task.getNotes());
            }//End of if statement to check the cursor is not empty
        }//End of if else statement to check the current category variable
        Log.d("Ext_onCreEditTask","Exit the onCreate method in the EditTaskActivity class.");
    }//End of onCreate method

    //Method to inflate the menu into the addTaskActivity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_add_task_grocery, menu);
        return true;
    }// Find fe OnCreateOptionsMenu

    //Method to check the menu item selected and execute the corresponding actions
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("Ent_onOptItmAdd","Enter onOptionsItemSelected method in the EditTaskActivity class.");
        //Boolean to return method result
        boolean result = false;
        switch (item.getItemId()) {
            //If id is the one for adding the task or grocery into the list
            case R.id.add_task_save:
                if(this.extras.getString("category").equals(MainActivity.getGroceryCategory())){
                    //Firstly, get the grocery name input by user. Declare and instantiate a string variable for that
                    String groceryName = this.etDescription.getText().toString();
                    //Check the grocery name is not empty
                    if(!groceryName.trim().equals("")) {
                        //Move to current spinner position the cursor that holds data for that spinner (Grocery type spinner)
                        cCategory.moveToPosition(this.spCategory.getSelectedItemPosition());
                        //Declare and populate a string variable to hold the grocery type selected. Extract data from the cursor at the current position
                        String typeName = cCategory.getString(1);
                        //Find a groceryType via MainActivity method to instantiate a new groceryType variable. This will be used to create a new Grocery object
                        GroceryType type = MainActivity.findGroceryTypeByName(typeName);
                        int id = extras.getInt("id");
                        Cursor tempCursor = MainActivity.db.runQuery("SELECT * FROM GROCERIES WHERE _id = "+id);
                        Grocery oldGrocery = null;
                        if(tempCursor.moveToNext()){
                            oldGrocery = MainActivity.db.extractGrocery(tempCursor);
                        }
                        Grocery newGrocery = new Grocery(id,groceryName,MainActivity.findGroceryTypeByName(MainActivity.getGroceryCategory()),type,false);
                        //Since isSelected attribute is unknown at this stage, make the new grocery to match the old one for that attribute
                        newGrocery.setSelected(oldGrocery.isSelected());
                        if(newGrocery.getDescription().equals(oldGrocery.getDescription()) && newGrocery.getType().toString().equals(oldGrocery.getType().toString())){
                            //Error message
                            Toast.makeText(this,"No changes recorded. The grocery is the same as before.",Toast.LENGTH_LONG).show();
                        }else{
                            //Update DB with new data
                            MainActivity.db.updateItem(newGrocery);
                            //Check the grocery in DB is the same sent as param
                            //Retrieve the just modified grocery from DB
                            tempCursor = MainActivity.db.runQuery("SELECT * FROM GROCERIES WHERE _id = "+id);
                            //Check the cursor is not empty
                            if(tempCursor.moveToNext()){
                                //If not empty, extract the grocery
                                oldGrocery = MainActivity.db.extractGrocery(tempCursor);
                            }//End of if statement to check cursor is not empty
                            //Check the three attributes that define a grocery as per the GROCERIES TABLE are the same (gorcery objects before and after updates)
                            if(newGrocery.getDescription().equals(oldGrocery.getDescription())
                                    && newGrocery.getType().toString().equals(oldGrocery.getType().toString())
                                    && newGrocery.isSelected()== oldGrocery.isSelected()){
                                //If they are the same notify data set change
                                MainActivity.updateRecyclerViewData("SELECT * FROM GROCERIES ORDER BY TypeOfGrocery ASC");
                                //Display message was satisfactory
                                Toast.makeText(this,"The grocery was updated",Toast.LENGTH_SHORT).show();
                                //Set method result to true
                                result = true;
                                //Go back to MainActivity
                                finish();
                            }else{
                                //Display error message
                                Toast.makeText(this,"Error: Failed to update the grocery",Toast.LENGTH_LONG).show();
                            }//End of if else statemetn to check the update was successful
                        }//End of if else statement tha tchecks the groceries are not the same
                    }else {//Display error message via toast
                        Toast.makeText(this,R.string.groceryNameEmpty,Toast.LENGTH_SHORT).show();
                    }//End of if else statement to check the grocery description is not empty
                }else{
                    //For all other tasks proceed as follows
                    //Firstly, get the task description name input by user. Declare and instantiate a string variable for that
                    String taskDescription = this.etDescription.getText().toString();
                    //Check the task description is not empty
                    if(!taskDescription.trim().equals("")){
                        //Move to current Category spinner position the cursor that holds data for that spinner (Category spinner)
                        cCategory.moveToPosition(this.spCategory.getSelectedItemPosition());
                        //Declare and populate a string variable to hold the grocery type selected. Extract data from the cursor at the current position
                        String categoryName = cCategory.getString(1);
                        //Find a Category via MainActivity method to instantiate a new groceryType variable. This will be used to create a new Grocery object
                        Category category = MainActivity.findCategoryByName(categoryName);
                        //Find a Priority via MainActivity method to instantiate a new Priority object.
                        Priority priority = Priority.findPriorityById(this.spPriority.getSelectedItemPosition());
                        //Declare and initialize a long variable to hold the dueDate value. Assume there is not due date originally *set to -1).
                        long dueDate = -1;
                        //Check if the isAppointment check box is checked by user
                        if(this.cbIsAppointment.isChecked()){
                            //if it is, get the due date by calling getAppointmentDate method
                            dueDate = this.getAppointmentDate();
                        }//End of if statement to check the isAppointment checkbox is checked
                        //Get the text from the notes text view
                        String notes = this.etNotes.getText().toString();
                        int id = this.extras.getInt("id");
                        Task oldTask = null;
                        //Declare and instantiate a new cursor object to hold data from sql query to retrieve the max value from _id column in GROCERIES table
                        Cursor tempCursor = MainActivity.db.runQuery("SELECT * FROM TASK WHERE _id = "+id);
                        //Check the cursor is not empty
                        if(tempCursor.moveToNext()){
                            oldTask = MainActivity.db.extractTask(tempCursor);
                        }//End of if statement
                        /*int id,String descripttion,Category category,Priority priority,boolean isDone,
                        boolean isAppointment, long dueDate,boolean isArchived,String notes,boolean isSelected,long dateCreated,long dateClosed*/
                        //Declare and instantiate a new GroceryType object to hold all the grocery info
                        Task newTask = new Task(id,taskDescription,category,priority,oldTask.isDone(),this.cbIsAppointment.isChecked(),dueDate,oldTask.isArchived(),notes,oldTask.isSelected(),oldTask.getDateCreated(),-1);
                        //Since isSelected attribute is unkown at this stage, make the new grocery to match the old one for that attribute
                        newTask.setSelected(oldTask.isSelected());
                        if(newTask.getDescription().equals(oldTask.getDescription())
                                && newTask.getCategory().toString().equals(oldTask.getCategory().toString())
                                && newTask.getPriority().equals(oldTask.getPriority())
                                && newTask.isAppointment()== oldTask.isAppointment()
                                && newTask.getDueDate() == oldTask.getDueDate()
                                && newTask.getNotes().equals(oldTask.getNotes())){
                            //Error message
                            Toast.makeText(this,"No changes recorded. The task is the same as before.",Toast.LENGTH_LONG).show();
                        }else{
                            //Update DB with new data
                            MainActivity.db.updateItem(newTask);
                            //Check the grocery in DB is the same sent as param
                            //Retrieve the just modified grocery from DB
                            tempCursor = MainActivity.db.runQuery("SELECT * FROM TASK WHERE _id = "+id);
                            //Check the cursor is not empty
                            if(tempCursor.moveToNext()){
                                //If not empty, extract the grocery
                                oldTask = MainActivity.db.extractTask(tempCursor);
                            }//End of if statement to check cursor is not empty
                            //Check the attributes that define a task as per the TASK TABLE are the same (task objects before and after updates)
                            if(newTask.getDescription().equals(oldTask.getDescription())
                                    && newTask.getCategory().toString().equals(oldTask.getCategory().toString())
                                    && newTask.getPriority().equals(oldTask.getPriority())
                                    && newTask.isAppointment()== oldTask.isAppointment()
                                    && newTask.getDueDate() == oldTask.getDueDate()
                                    && newTask.getNotes().equals(oldTask.getNotes())){
                                //Notify data set change
                                MainActivity.updateRecyclerViewData("SELECT * FROM TASK ORDER BY Category ASC");
                                //Display  message saying the grocery was added to the list
                                Toast.makeText(this,R.string.taskAdded,Toast.LENGTH_SHORT).show();
                                result = true;
                                finish();
                            }
                        }
                        result = true;
                    }else{
                        //Display an error message
                        Toast.makeText(this,R.string.taskDesEmpty,Toast.LENGTH_SHORT).show();
                    }
                }//End if else statement to check the current category
                break;
            case R.id.add_task_cancel:
                //In case the X icon is pressed, just ignore all the data input and go back to previous activity
                result = true;
                finish();
                break;
            default:
                result = super.onOptionsItemSelected(item);
                finish();
                break;
        }//End of switch statement
        Log.d("Ext_onOptItmAdd","Exit onOptionsItemSelected method in the EditTaskActivity class.");
        return result;
    }//End of onOptionsItemSelected method

}//End of EditTaskActivity class

