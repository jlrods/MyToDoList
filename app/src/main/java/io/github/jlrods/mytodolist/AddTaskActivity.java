package io.github.jlrods.mytodolist;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by rodjose1 on 03/09/2018.
 */

public class AddTaskActivity extends DisplayTaskActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Ent_OnCreateAddTask","Enter the onCreate method in the AddTaskActivity class.");
        Log.d("Ext_OnCreateAddTask","Exit the OnCreate method in the AddTaskActivity class.");
    }//End of OnCreate method


    //Method to inflate the menu into the addTaskActivity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_add_task_grocery, menu);
        return true;
    }// Find fe OnCreateOptionsMenu

    //Method to check the menu item selected and execute the corresponding actions
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("Ent_onOptItmAdd","Enter onOptionsItemSelected method in the AddTaskActivity class.");
        //Boolean to return method result
        boolean result = false;
        //Check the id of item selected in menu
        switch (item.getItemId()) {
            //If id is the one for adding the task or grocery into the list
            case R.id.add_task_save:
                //Check the current task category is Groceries
                if(this.extras.getString("category").equals(MainActivity.getGroceryCategory())){

                    Grocery grocery = (Grocery) this.getItemFromUIData();
                    if(grocery != null){
                        //Declare and instantiate to invalid value a new int var to hold the returned int from the addItem method which will correspond with the grocery just created
                        int idFromDB = -1;
                        //Call the addItem method and receive the id sent from method
                        idFromDB    = MainActivity.db.addItem(grocery);
                        //Check the id from the DB is valid and different than the dummy one.
                        if(idFromDB != -1){

                            result = this.updateDataList("SELECT * FROM GROCERIES ORDER BY TypeOfGrocery ASC");
                        }//End of if statement to check the idFromDB is not -1
                    }else{
                        //Display error message for failing add grocery in DB

                    }//End of if else statement to check the grocery description is not empty
                }else{
                        Task newTask = this.getItemFromUIData();
                        if(newTask != null){
                            //Declare and instantiate a new int var to hold the returned int from the addItem method which will correspond with the grocery just created
                            int idFromDB = -1;
                            //Call the addItem method and receive the id sent from method
                            idFromDB = MainActivity.db.addItem(newTask);
                            //Check the id is correct by comparing the one used to create the Grocery object and the id coming from the DB after the insert item transaction.
                            if(idFromDB!=-1){

                                result = this.updateDataList("SELECT * FROM TASK ORDER BY Category ASC");
                            }else{
                                //Display a message in case the ids do not match
                                Toast.makeText(this,R.string.taskAddedFailed,Toast.LENGTH_SHORT).show();
                                result = false;
                            }//End of if else statement to check the ids
                        }else{
                            //Display error message
                        }//End of if else statement that checks the new task is not null
                }//End of if else statement that checks the category passed in as extra info coming from MainActivity
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
        Log.d("Ext_onOptItmAdd","Exit onOptionsItemSelected method in the AddTaskActivity class.");
        return result;
    }// Fin de onOptionsItemSelected method

    //Method to get The hour from
    private Date getHour(){
        Log.d("Ent_getHour","Enter the getHour method in the AddTaskActivity class.");
        //Declare and initialize a calender with current time and date
        Calendar calendar = Calendar.getInstance();
        //Declare and instantiate a new date object
        Date date;
        //Check the date text is empty
        if(tvHour.getText().equals("")){
            //If empty, set hour and minute of current date object to be at 00:00
            calendar.set(Calendar.HOUR_OF_DAY,0);
            calendar.set(Calendar.MINUTE,0);
            date = calendar.getTime();
        }else{
            //In case tvHour text is not empty, get the text from text view
            String time = tvHour.getText().toString();
            //Check if there is AM substring in it (This will mean it's morning time)
            if(time.contains("AM")){
                //If it has AM, set the hour time as per text (1 will be 1, 11 will be 11)
                calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(time.substring(0,2)));
            }else{
                //If it's not morning time, check if it's hour 12
                if(time.substring(0,2).equals("12")){
                    //if it is 12 PM, set the hour to be 12
                    calendar.set(Calendar.HOUR_OF_DAY,12);
                }else{
                    //Finally, if it's not morning or 12 PM, add 12 hours to read time (1, will be 13 and 10 will be 22)
                    calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(time.substring(0,2))+12);
                }//End of if else statement to check time is 12
            }//End of if else statement to check it's morning time
            //Set callendar minute to be whatever is in substring from 3 to 5
            calendar.set(Calendar.MINUTE,Integer.valueOf(time.substring(3,5)));
            date = calendar.getTime();
        }//End of if else statement that check the hour text is not empty
        Log.d("Ext_getHour","Exit the getHour method in the AddTaskActivity class.");
        return date;
    }//End of getHour method
}//End of AddTaskActivity