package io.github.jlrods.mytodolist;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
 * Created by rodjose1 on 24/09/2018.
 */

public abstract class DisplayTaskActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener,TimePickerDialog.OnTimeSetListener {
    //Define global variables
    protected TextView tvTaskTag;
    protected EditText etDescription;
    protected TextView tvCategoryTag;
    protected Spinner spCategory;
    protected LinearLayout layoutPriority;
    protected TextView tvPriorityTag;
    protected Spinner spPriority;
    protected ImageView imgLogoPriority;
    protected LinearLayout layoutAppointment;
    protected CheckBox cbIsAppointment;
    protected ImageView imgLogoDate;
    protected LinearLayout layoutDate;
    protected TextView tvDateTag;
    protected TextView tvDate;
    protected LinearLayout layoutHour;
    protected TextView tvHourTag;
    protected TextView tvHour;
    protected ImageView imgLogoHour;
    protected LinearLayout layoutNotes;
    protected ImageView imgLogoNotes;
    protected TextView tvNotesTag;
    protected EditText etNotes;
    protected Cursor cCategory;
    protected Cursor cPriority;
    protected Task task;
    protected Grocery grocery;
    protected Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Ent_onCreateDisp","Enter onCreate method in the DisplayTaskActivity abstract class.");
        //Set layout for this activity
        setContentView(R.layout.activity_add_task);
        //Extract extra data from Bundle object
        extras = getIntent().getExtras();
        //Initialize view object from layout to have access to them and set different texts and other properties
        this.tvTaskTag = (TextView) findViewById(R.id.tvDescriptionTag);
        this.etDescription = (EditText) findViewById(R.id.etDescription);
        this.tvCategoryTag = (TextView) findViewById(R.id.tvCategoryTag);
        this.spCategory = (Spinner) findViewById(R.id.spCategory);
        this.layoutPriority = findViewById(R.id.layoutPriority);
        this.spPriority = findViewById(R.id.spPriority);
        this.layoutAppointment = findViewById(R.id.layoutAppointment);
        this.layoutDate = findViewById(R.id.layoutDate);
        this.layoutHour = findViewById(R.id.layoutHour);
        this.cbIsAppointment = findViewById(R.id.cbIsAppointment);
        //Set onChangeState listener of check box
        this.cbIsAppointment.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        //Call method to hide appointment detail layouts. Will be visible only if the isAppointment cbox is checked
                        hideAppointmentDetails(isChecked);
                    }//End of onCheckedChanged method
                }//End of onCheckedchangeListener method
        );// End of setonCheckedChagnedListener
        //Declare int variable to define spinner selected item based on the current category
        int spinnerPosition;
        //Continue with view objects initialization
        this.layoutNotes = findViewById(R.id.layoutNotes);
        //Declare and initialize empty strings to work with different sql queries during the method
        String sql = "";
        //String table="";
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
            //Set the spinner hint to refer to a grocery type
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
            adapterPriority = new SpinnerAdapter(this,cPriority, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
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
        //Run a a sql query to retrieve the category data. This will vary depending on the current category where add button was pressed from
        cCategory = MainActivity.db.runQuery(sql);
        //Set dataset of the category adapter , using cursor object above
        adapterCategory = new SpinnerAdapter(this,cCategory, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        //Set the adapter for the Category spinner
        spCategory.setAdapter(adapterCategory);
        //Move the spinner adapter position to the correct category depending on the current category
        //If current category is All or groceries Categories, then leave the position to be the initial one (Home for Tasks and Meats for groceries)
        if(extras.getString("category").equals(MainActivity.getAllCategory()) || extras.getString("category").equals(MainActivity.getGroceryCategory())) {
            spinnerPosition = 0;
        }else{
            //For any other Category, use findItemPosition method from the spinner adapter by passing the category name as paramters
            spinnerPosition = adapterCategory.findItemPosition(extras.getString("category"));
        }//End of if else statement to check the current category name
        //Move adapater position to the postion indicated as per previous logic
        spCategory.setSelection(spinnerPosition);
        Log.d("Ext_onCreateDisp","Exit onCreate method in the DisplayTaskActivity abstract class.");
    }//End of onCreate method

    protected void hideAppointmentDetails(boolean isChecked){
        Log.d("Ent_hideAppoint","Enter the hideAppointmentDetails method in the AddTaskActivity class.");
        //Check the isChecked argument is true
        if(isChecked){
            //If it is true, make hour and date layouts visible
            this.layoutDate.setVisibility(View.VISIBLE);
            this.layoutHour.setVisibility(View.VISIBLE);
        }else{
            //Otherwise, make hour and date layouts invisible and not use any layout room
            this.layoutDate.setVisibility(View.GONE);
            this.layoutHour.setVisibility(View.GONE);
        }//End of if else statement
        Log.d("Ext_hideAppoint","Exit the hideAppointmentDetails method in the AddTaskActivity class.");
    }//End of hideAppointmentDetails method

    //Method to update the Date text view with the date coming from the DialogDateSelector object
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Log.d("Ent_onDateSetAdd","Enter the onDateSet method in the AddTaskActivity class.");
        //Declare and initialize a new Calendar object with current date and time
        Calendar calendar = Calendar.getInstance();
        //Set the year, month and day, which were passed in as params
        calendar.set(year,month,dayOfMonth);
        this.tvDate.setText(this.getDateString(calendar));
        Log.d("Ext_onDateSetAdd","Exit the onDateSet method in the AddTaskActivity class.");
    }//End of DatePicker method

    //Method to get the date string to be displayed in the tvDate view
    protected String getDateString(Calendar calendar){
        Log.d("Ent_getDateStr","Enter the getDateString method in the DisplayTaskActivity abstract class.");
        //Declare and isntantiate a new DateFormat object to display date in current format (This format may change based on the app settings)
        SimpleDateFormat format = new SimpleDateFormat(MainActivity.getDateFormat());
        //Declare and instantiate a new date object, using the date set in calendar. Get the time in millisecs
        Date date = new Date(calendar.getTimeInMillis());
        Log.d("Ext_getDateStr","Exit the getDateString method in the DisplayTaskActivity abstract class.");
        return format.format(date);
    }//End of getDateString method

    //Method to change date on the DialogDateSelector class
    public void changeDate() {
        Log.d("Ent_chageDate","Enter changeDate method in the AddTaskActivity class.");
        //Declare and instantiate a new dialogDateSelector object
        DialogDateSelector dialogBox = new DialogDateSelector();
        //Set this activity to be the on date selected listener
        dialogBox.setOnDateSetListener(this);
        //Declare and instantiate arguments as a bundle object so data can be trasferend (date info)
        Bundle args = new Bundle();
        //Set the arguments just created
        dialogBox.setArguments(args);
        //Display the dialog box for the date picker
        dialogBox.show(this.getSupportFragmentManager(), "dateSelector");
        //Check the date text is empty
        if(tvDate.getText().equals("")){
            //if that is the case, add an agument to hold the current time in millisecs
            args.putLong("date",Calendar.getInstance().getTimeInMillis());
        }else{
            //Otherwise, declare and instantiate a new DateFormat object to define the date format
            SimpleDateFormat format = new SimpleDateFormat(MainActivity.getDateFormat());
            //Declare a new date object
            Date date;
            //Use try catch block to try to read date from date  text field
            try {
                //set the date to be the one parsed from the text in the tvDate view
                date = format.parse(tvDate.getText().toString());
            } catch (ParseException e) {
                //if an error comes up when parsing the expression, create a new date with current time
                date = new Date();
                //Print trace error message
                e.printStackTrace();
            }//End of try catch block
            //Add new argument with date parsed from tvDate view or the one created in case of error
            args.putLong("date",date.getTime());
            //Set the arguments into the dialog box object
            dialogBox.setArguments(args);
        }//End of if else statement to check the tvDate text is empty or not
        Log.d("Ext_chageDate","Exit changeDate method in the AddTaskActivity class.");
    }//End of changeDate method

    //Method to update the time text view with the hour and mins which come from the DialogHourSelector object
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Log.d("Ent_onTimeSetAdd","Enter the onTimeSet method in the AddTaskActivity class.");
        //Declare and initialize a calendar with present time
        Calendar calendar = Calendar.getInstance();
        String time = this.getTimeString(hourOfDay,minute);
        this.tvHour.setText(time);
        Log.d("Ext_onTimeSetAdd","Exit the onTimeSet method in the AddTaskActivity class.");
    }//End of onTimeSet method

    //Method to get the hour string to be displayed in the tvHour view
    protected String getTimeString(int hourOfDay,int minute){
        //Declare and initialize empty string objects to construct the full time text
        String amPm = "";
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
            }//End of if else statement to checkthe hour time (afternoon and night case)
        }//End of if else statement that checks the hour param
        //Check the minute param has only one digit (<10)
        if(minute < 10){
            //if it is, add the value to the current 0 in the minute string
            min += minute;
        }else{
            //Otherwise, reassign the value of minute string to be equal to exact minute param
            min= String.valueOf(minute);
        }//End of if else statement that checks the minute param
        return hour+":"+min+" "+amPm;
    }//End of getTimeString

    public void changeHour() {
        Log.d("Ent_chageHour","Enter changeHour method in the AddTaskActivity class.");
        //Declare and instantiate a new dialogDateSelector object
        DialogHourSelector dialog = new DialogHourSelector();
        //Set this activity to be the on date selected listener
        dialog.setOnTimeSetListener(this);
        //Declare and instantiate arguments as a bundle object so data can be trasferend (date info)
        Bundle args = new Bundle();
        //Declare a new calendar object from abstract class and get instance via method defined for that purpose
        Calendar calendar = Calendar.getInstance();
        //Check the date text is empty
        if(tvHour.getText().equals("")){
            //If empty, set hour and minute of current date object to be at 00:00
            calendar.set(Calendar.HOUR_OF_DAY,0);
            calendar.set(Calendar.MINUTE,0);
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
        }//End of if else to check the tvHour text is empty or not
        //Add the hour argument with the current time in millisecs
        args.putLong("hour",calendar.getTimeInMillis());
        //set arguments for the dialog box
        dialog.setArguments(args);
        //Display the dialog box for the date picker
        dialog.show(this.getSupportFragmentManager(), "hourSelector");
        Log.d("Ext_chageHour","Exit changeHour method in the AddTaskActivity class.");
    }//End of changeHour method

    //Method to get all the appointment date details coming from date and hour text views
    protected long getAppointmentDate(){
        Log.d("Ent_getAppointmentDate","Enter the getAppointmentDate method in the AddTaskActivity class.");
        //Declare and initialize a new calendar with current time
        Calendar calendar = Calendar.getInstance();
        //Declare a new date object
        Date date;
        //Check if date text view is empty
        if(tvDate.getText().equals("")){
            //if that is the case, add an agument to hold the current time in millisecs
            date = Calendar.getInstance().getTime();
        }else{
            //Otherwise, declare and instantiate a new DateFormat object to define the date format
            SimpleDateFormat format = new SimpleDateFormat(MainActivity.getDateFormat());
            //Use try catch block to try to read date from date  text field
            try {
                //set the date to be the one parsed from the text in the tvDate view
                date = format.parse(tvDate.getText().toString());
            } catch (ParseException e) {
                //if an error comes up when parsing the expression, create a new date with current time
                date = new Date();
                //Print trace error message
                e.printStackTrace();
            }//End of try catch block
        }//End of if else statement to check the tvDate text is empty or not
        //Set date recorded in text view into the calendar object
        calendar.setTime(date);
        //Check if the hour text is empty
        if(tvHour.getText().equals("")){
            //If empty, set hour and minute of current date object to be at 00:00
            calendar.set(Calendar.HOUR_OF_DAY,0);
            calendar.set(Calendar.MINUTE,0);
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
            //Set calendar minute to be whatever is in substring from 3 to 5
            calendar.set(Calendar.MINUTE,Integer.valueOf(time.substring(3,5)));
        }//End of if else statement that checks the hour text view text
        //Initialize the date object with date and time extracted from text views
        date = calendar.getTime();
        Log.d("Ext_getAppointmentDate","Exit the getAppointmentDate method in the AddTaskActivity class.");
        return date.getTime();
    }//End of getAppointmentDate method

    protected boolean updateDataList(String sql){
        boolean result = false;
        String message = "";
        //If they are the same notify data set change
        MainActivity.updateRecyclerViewData(sql);
        boolean isGrocery = MainActivity.getCurrentCategory().equals(MainActivity.findCategoryByName(MainActivity.getGroceryCategory()));
        //Display message for satisfactory database inclusion or update
        if(this instanceof AddTaskActivity && isGrocery){
            message = "The new grocery has been successfully added to your grocery list.";
        }else if(this instanceof AddTaskActivity ){
            message = "The new task has been successfully added to your task list";
        }else if(this instanceof EditTaskActivity && isGrocery){
            message = "The grocery was successfully updated.";
        }else if(this instanceof EditTaskActivity){
            message = "The task was successfully updated ";
        }else{
            return false;
        }
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
        //Set method result to true
        result = true;
        //Go back to MainActivity
        finish();
        return result;
    }//End of updateDataList

    protected Task getItemFromUIData(){
        Task item= null;
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
                int id =-1;
                if(this instanceof AddTaskActivity){
                    //Declare and instantiate a new cursor object to hold data from sql query to retrieve the max value from _id column in GROCERIES table
                    Cursor tempCursor = MainActivity.db.runQuery("SELECT MAX(_id) FROM GROCERIES");
                    //Check the cursor is not empty
                    if(tempCursor.moveToNext()){
                        //Declare and instantiate int var to hold first guess of id number by retrieving the max grocery id from DB and increment it by 1. Only used to create a grocery object
                        id = tempCursor.getInt(0)+1;
                    }//End of if statement
                }else if(this instanceof EditTaskActivity){
                    id = extras.getInt("id");
                }else{
                    //Action to be defined
                }
                //Populate item with a grocery object
                item= new Grocery(id,groceryName,MainActivity.findGroceryTypeByName(MainActivity.getGroceryCategory()),type,false);
            }else{
                //Display error message via toast
                Toast.makeText(this,R.string.groceryNameEmpty,Toast.LENGTH_SHORT).show();
            }//End of if else statement to check the grocery description is not empty
        }else{
            //For all the other categories
            //In case the item to be added is a task and not a grocery
            //Firstly, get the task description input by user. Declare and instantiate a string variable for that
            String taskDescription = this.etDescription.getText().toString();
            //Check the grocery name is not empty
            if(!taskDescription.trim().equals("")) {
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
                if (this.cbIsAppointment.isChecked()) {
                    //if it is, get the due date by calling getAppointmentDate method
                    dueDate = this.getAppointmentDate();
                }//End of if statement to check the isAppointment checkbox is checked
                //Get the text from the notes text view
                String notes = this.etNotes.getText().toString();
                int id =-1;
                if(this instanceof AddTaskActivity){
                    //Declare and instantiate a new cursor object to hold data from sql query to retrieve the max value from _id column in GROCERIES table
                    Cursor tempCursor = MainActivity.db.runQuery("SELECT MAX(_id) FROM TASK");
                    //Check the cursor is not empty
                    if(tempCursor.moveToNext()){
                        //Declare and instantiate  int var to hold first guess of id by retrieving max id from Task table and increment it by 1
                        id = tempCursor.getInt(0)+1;
                        item = new Task(id, taskDescription, category, priority, false, this.cbIsAppointment.isChecked(), dueDate, false, notes);
                    }//End of if statement
                }else if(this instanceof EditTaskActivity){
                    id = this.extras.getInt("id");
                    item = new Task(id,taskDescription,category,priority,false,this.cbIsAppointment.isChecked(),dueDate,false ,notes);
                }else{
                    //Action to be defined
                }//End of if else statements to check the instance of the current activity
            }else {
                //Display error message via toast if the task description was left empty
                Toast.makeText(this, R.string.taskDesEmpty, Toast.LENGTH_SHORT).show();//End of
            }//End of if else statement to check the task description is not empty*/
        }//End of if else statement to check the current category
        return item;
    }//End of getItemFromUIDate method
}//End of DisplayTaskActivity
