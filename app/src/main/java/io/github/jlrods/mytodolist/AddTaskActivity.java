package io.github.jlrods.mytodolist;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
        /*//Set layout for this activity
        //setContentView(R.layout.activity_add_task);
        //Declare and initialize empty strings to work with different sql queries during the method
        String sql = "";
        //Declare two spinner adapters, one for the category spinner and one for the priority spinner
        SpinnerAdapter adapterCategory;
        SpinnerAdapter adapterPriority;
        //Check the current property where the activity was called form (Groceries or any other task)
        if(extras.getString("category").equals(MainActivity.getGroceryCategory())){
            //If the item to be added is a Grocery:
            //Set the taskTag text to Grocery
            this.tvTaskTag.setText(R.string.groceryTag);
            //Set the hint for description to refer to a grocery name instead of a task description
            this.etDescription.setHint(R.string.groceryHint);
            //Set th Category spinner tag to refet to grocery type
            this.tvCategoryTag.setText(R.string.groceryTypeTag);
            //Make all non applicable views not visible
            this.layoutPriority.setVisibility(View.INVISIBLE);
            this.layoutAppointment.setVisibility(View.INVISIBLE);
            this.layoutDate.setVisibility(View.INVISIBLE);
            this.layoutHour.setVisibility(View.INVISIBLE);
            this.layoutNotes.setVisibility(View.INVISIBLE);
            //Populate the grocery type spinner with a list of grocery types available. To do that retrieve a cursor which uses
            //a sql query that queries the GROCERY_TYPE table
            sql = "SELECT * FROM GROCERY_TYPE";
            //Set the sipnner hint to refer to a grocery type
            spCategory.setPrompt("Select the grocery type...");
        }else{
            //For all other task Categories
            //Set the description input text hint to refer to a task description
            this.etDescription.setHint(R.string.taskDescHint);
            //Set the sql query to inquire CATEGORY table where the id is not the one for All or Groceries categories
            sql = "SELECT * FROM CATEGORY WHERE _id NOT IN ( " +MainActivity.findCategoryByName(MainActivity.getAllCategory()).getId()+
                    ", "+MainActivity.findCategoryByName(MainActivity.getGroceryCategory()).getId()+")";
            //Set hint for category spinner to refer to a task category
            this.spCategory.setPrompt("Select the task category...");
            //Run a sql query that will retrieve the current priority items from DB
            cPriority = MainActivity.db.runQuery("SELECT * FROM PRIORITY");
            //Instantiate a new adapterPriority with current data set from cursor c
            adapterPriority = new SpinnerAdapter(this,cPriority,CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
            //Set the priority spinner adapter to be the one defined above
            this.spPriority.setAdapter(adapterPriority);
            //Instantiate all the elements required for this activity... the ones not instantiated when adding a grocery task
            //All the appointment related views:
            this.imgLogoDate = findViewById(R.id.imgLogoDate);
            //Set onClickListener for the Date logo view
            this.imgLogoDate.setOnClickListener(new View.OnClickListener() {
                @Override
                //When the Date logo is clicked on, call the changeDate method
                public void onClick(View v) {
                    changeDate();
                }
            });//End of setOnClickListener
            this.tvDate = findViewById(R.id.tvDate);
            //Set the onClickListener for the Date text view
            this.tvDate.setOnClickListener(new View.OnClickListener() {
                @Override
                //When the Date text view is clicked on, call the changeDate method
                public void onClick(View v) {
                    changeDate();
                }
            });//End onClickListner
            this.tvDate = findViewById(R.id.tvDate);
            this.tvHour = findViewById(R.id.tvHour);
            //Set onClickListner for the hour text view
            this.tvHour.setOnClickListener(new View.OnClickListener() {
                @Override
                //When the hour text view is clicked on, call the changeHour method
                public void onClick(View v) {
                    changeHour();
                }
            });//End of the onClickListner for the hour text view
            this.imgLogoHour = findViewById(R.id.imgLogoHour);
            //Set the onClickLister for the hour logo view
            this.imgLogoHour.setOnClickListener(new View.OnClickListener() {
                @Override
                //When the Hour logo is clicked on, call the changeHour method
                public void onClick(View v) {
                    changeHour();
                }
            });//End of the onClickListener for the hour logo view
            this.etNotes = findViewById(R.id.etNotes);
        }//End of if else statement to check current category when the add task button was pressed

        //Run a a sql query to retrieve the categroy data. This will vary depending on the current category where add button was pressed from
        cCategory = MainActivity.db.runQuery(sql);
        //Set dataset of the category adapter , using cursor object above
        adapterCategory = new SpinnerAdapter(this,cCategory, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        //Set the adapter for the Category spinner
        spCategory.setAdapter(adapterCategory);*/
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
                    //Firstly, get the grocery name input by user. Declare and instantiate a string variable for that
                    String groceryName = this.etDescription.getText().toString();
                    //Check the grocery name is not empty
                    if(!groceryName.trim().equals("")){
                        //Move to current spinner position the cursor that holds data for that spinner (Grocery type spinner)
                        cCategory.moveToPosition(this.spCategory.getSelectedItemPosition());
                        //Declare and populate a string variable to hold the grocery type selected. Extract data from the cursor at the current position
                        String typeName = cCategory.getString(1);
                        //Find a groceryType via MainActivity method to instantiate a new groceryType variable. This will be used to create a new Grocery object
                        GroceryType type = MainActivity.findGroceryTypeByName(typeName);
                        //Declare and instantiate a new cursor object to hold data from sql query to retrieve the max value from _id column in GROCERIES table
                        Cursor tempCursor = MainActivity.db.runQuery("SELECT MAX(_id) FROM GROCERIES");
                        //Check the cursor is not empty
                        if(tempCursor != null && tempCursor.getCount() >0){
                            //Move temp cursor to first position
                            tempCursor.moveToFirst();
                        }//End of if statement
                        //Declare and instantiate int var to hold first guess of id number by retrieving the max grocery id from DB and increment it by 1. Only used to create a grocery object
                        int tempId = tempCursor.getInt(0)+1;
                        //Declare and instantiate a new GroceryType object to hold all the grocery info
                        Grocery grocery = new Grocery(tempId,groceryName,MainActivity.findCategoryByName(MainActivity.getGroceryCategory()),type,false);
                        //Declare and instantiate to invalid value a new int var to hold the returned int from the addItem method which will correspond with the grocery just created
                        int idFromDB = -1;
                        //Call the addItem method and receive the id sent from method
                        idFromDB    = MainActivity.db.addItem(grocery);
                        //Check the id from the DB is valid and different than the dummy one.
                        if(idFromDB != -1){
                            //If not the invalid value, assume the addItem method was successful
                            //Check if there is any filter activated to call the proper sql...
                            //Notify data set change
                            MainActivity.updateRecyclerViewData("SELECT * FROM GROCERIES ORDER BY TypeOfGrocery ASC");
                            //Display  message saying the grocery was added to the list
                            Toast.makeText(this,R.string.groceryAdded,Toast.LENGTH_SHORT).show();
                            result = true;
                            finish();
                        }else{
                            //Display error message in case the id number do not match
                            Toast.makeText(this,R.string.groceryAddedFailed,Toast.LENGTH_SHORT).show();
                            result = false;
                        }//End of if else statement to check ids
                    }else{
                        //Display error message via toast
                        Toast.makeText(this,R.string.groceryNameEmpty,Toast.LENGTH_SHORT).show();
                    }//End of if else statement to check the grocery description is not empty
                }else{
                    //In case the item to be added is a task and not a grocery
                    //Firstly, get the task description input by user. Declare and instantiate a string variable for that
                    String taskDescription = this.etDescription.getText().toString();
                    //Check the grocery name is not empty
                    if(!taskDescription.trim().equals("")){
                        //Move to current Category spinner position the cursor that holds data for that spinner (Category spinner)
                        cCategory.moveToPosition(this.spCategory.getSelectedItemPosition());
                        //Declare and populate a string variable to hold the grocery type selected. Extract data from the cursor at the current position
                        String categoryName = cCategory.getString(1);
                        //Find a Caegory via MainActivity method to instantiate a new groceryType variable. This will be used to create a new Grocery object
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
                        //Declare and instantiate a new cursor object to hold data from sql query to retrieve the max value from _id column in GROCERIES table
                        Cursor tempCursor = MainActivity.db.runQuery("SELECT MAX(_id) FROM TASK");
                        //Check the cursor is not empty
                        if(tempCursor != null && tempCursor.getCount() >0){
                            //Move temp cursor to first position
                            tempCursor.moveToFirst();
                        }//End of if statement
                        //Declare and instantiate  int var to hold first guess of id by retrieving max id from Task table and increment it by 1
                        int tempId = tempCursor.getInt(0)+1;
                        //Declare and instantiate a new GroceryType object to hold all the grocery info
                        Task newTask = new Task(tempId,taskDescription,MainActivity.findCategoryByName(categoryName),priority,false,this.cbIsAppointment.isChecked(),dueDate,false,notes);
                        //Declare and instantiate a new int var to hold the returned int from the addItem method which will correspond with the grocery just created
                        int idFromDB = -1;
                        //Call the addItem method and receive the id sent from method
                        idFromDB = MainActivity.db.addItem(newTask);
                        //Check the id is correct by comparing the one used to create the Grocery object and the id coming from the DB after the insert item transaction.
                        if(idFromDB!=-1){
                            //Check if there is any filter activated to call the proper sql...
                            //Notify data set change
                            MainActivity.updateRecyclerViewData("SELECT * FROM TASK ORDER BY Category ASC");
                            //Display  message saying the grocery was added to the list
                            Toast.makeText(this,R.string.taskAdded,Toast.LENGTH_SHORT).show();
                            result = true;
                            finish();
                        }else{
                            //Display a message in case the ids do not match
                            Toast.makeText(this,R.string.taskAddedFailed,Toast.LENGTH_SHORT).show();
                            result = false;
                        }//End of if esle statement to check the ids
                        result = true;
                    }else {
                        //Display error message via toast if the task description was left empty
                        Toast.makeText(this, R.string.taskDesEmpty, Toast.LENGTH_SHORT).show();//End of
                    }//End of if else statement to check the task description is not empty
                }//End of if else statement that checks the category passed in as extra info coming from MainActivity
                //Add to a data base
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
        }
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