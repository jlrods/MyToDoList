package io.github.jlrods.mytodolist;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CursorAdapter;
import android.widget.Toast;
import java.util.Calendar;


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
                    //Set the text in date text view according to the new date
                    this.tvDate.setText(this.getDateString(calendar));
                    int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
                    int minute = calendar.get(Calendar.MINUTE);
                    //Declare and initialize empty string objects to construct the full time text
                    String time = this.getTimeString(hourOfDay,minute);
                    //Call method to set hour and minutes
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
                        //Get the grocery details from the UI
                        Grocery newGrocery = (Grocery) this.getItemFromUIData();
                        //Check the new grocery object is not empty.
                        if(newGrocery !=null) {
                            //Get data from the grocery stored on the DB under _id passed via extras
                            Cursor tempCursor = MainActivity.db.runQuery("SELECT * FROM GROCERIES WHERE _id = " + newGrocery.getId());
                            //Declare and instantiate old grocery object
                            Grocery oldGrocery = null;
                            //Check the cursor that hold the old grocery data is not empty
                            if (tempCursor.moveToNext()) {
                                oldGrocery = MainActivity.db.extractGrocery(tempCursor);
                                //Since isSelected attribute is unknown at this stage, make the new grocery to match the old one for that attribute
                                newGrocery.setSelected(oldGrocery.isSelected());
                                if (newGrocery.getDescription().equals(oldGrocery.getDescription()) && newGrocery.getType().toString().equals(oldGrocery.getType().toString())) {
                                    //Error message as no changes has been detected
                                    Toast.makeText(this, "No changes recorded. The grocery is the same as before.", Toast.LENGTH_LONG).show();
                                } else {
                                    //Update DB with new data
                                    MainActivity.db.updateItem(newGrocery);
                                    //Check the grocery in DB is the same sent as param
                                    //Retrieve the just modified grocery from DB
                                    tempCursor = MainActivity.db.runQuery("SELECT * FROM GROCERIES WHERE _id = " + newGrocery.getId());
                                    //Check the cursor is not empty
                                    if (tempCursor.moveToNext()) {
                                        //If not empty, extract the grocery
                                        oldGrocery = MainActivity.db.extractGrocery(tempCursor);
                                    }//End of if statement to check cursor is not empty
                                    //Check the three attributes that define a grocery as per the GROCERIES TABLE are the same (grocery objects before and after updates)
                                    if (newGrocery.getDescription().equals(oldGrocery.getDescription())
                                            && newGrocery.getType().toString().equals(oldGrocery.getType().toString())
                                            && newGrocery.isSelected() == oldGrocery.isSelected()) {
                                        //If they are the same notify data set change
                                        result = this.updateDataList(this.extras.getString("sql"));
                                        //result = this.updateDataList("SELECT * FROM GROCERIES ORDER BY TypeOfGrocery ASC");
                                    } else {
                                        //Display error message
                                        Toast.makeText(this, "Error: Failed to update the grocery", Toast.LENGTH_LONG).show();
                                    }//End of if else statement to check the update was successful
                                }//End of if else statement tha tchecks the groceries are not the same
                            }//End of if to check the temp cursor is not empty
                        }else{
                            //Display error message
                            Toast.makeText(this, "Error: Failed to update the grocery", Toast.LENGTH_LONG).show();
                        }
                }else{
                    //For all other tasks proceed as follows
                    //Get the task details from the UI
                    Task newTask = this.getItemFromUIData();
                    //Check the new task object is not null or empty.
                    if(newTask != null){
                        //Declare and instantiate a old task object to hold date of task with id passed in via activity extras
                        Task oldTask = null;
                        //Declare and instantiate a new cursor object to hold data from sql query to retrieve the max value from _id column in GROCERIES table
                        Cursor tempCursor = MainActivity.db.runQuery("SELECT * FROM TASK WHERE _id = "+newTask.getId());
                        //Check the cursor that holds the old task data is not empty
                        if(tempCursor.moveToNext()){
                            //Populate task object data with data from cursor that holds the old task data
                            oldTask = MainActivity.db.extractTask(tempCursor);
                            //Since isSelected attribute is unknown at this stage, make the new grocery to match the old one for that attribute
                            newTask.setSelected(oldTask.isSelected());
                            newTask.setArchived(oldTask.isArchived());
                            newTask.setDone(oldTask.isDone());
                            newTask.setDateCreated(oldTask.getDateCreated());
                            //Check that old and new tasks are not the same
                            if(newTask.getDescription().equals(oldTask.getDescription())
                                    && newTask.getCategory().toString().equals(oldTask.getCategory().toString())
                                    && newTask.getPriority().equals(oldTask.getPriority())
                                    && newTask.isAppointment()== oldTask.isAppointment()
                                    && newTask.getDueDate() == oldTask.getDueDate()
                                    && newTask.getNotes().equals(oldTask.getNotes())){
                                //Error message since no changes were detected
                                Toast.makeText(this,"No changes recorded. The task is the same as before.",Toast.LENGTH_LONG).show();
                            }else{
                                //If there is at least one difference, update the DB
                                //Update DB with new data
                                MainActivity.db.updateItem(newTask);
                                //Check the task in DB is the same sent as param
                                //Retrieve the just modified grocery from DB
                                tempCursor = MainActivity.db.runQuery("SELECT * FROM TASK WHERE _id = "+newTask.getId());
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
                                    result = this.updateDataList(this.extras.getString("sql"));
                                    //result = this.updateDataList("SELECT * FROM TASK ORDER BY Category ASC");
                                }//End of if statement that checks the DB update matches the data
                            }//End of if else statements that checks the task has changed in some way
                        }//End of if statement that checks the temp cursor is not empty
                    }else{
                        //Display an error message

                    }//End of if else statement that check the task is not null
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

