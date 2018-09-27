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
        //Continue with view objects initialization
        this.layoutNotes = findViewById(R.id.layoutNotes);
        //Declare and initialize empty strings to work with different sql queries during the method
        String sql = "";
        String table="";
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

        //Run a a sql query to retrieve the categroy data. This will vary depending on the current category where add button was pressed from
        cCategory = MainActivity.db.runQuery(sql);
        //Set dataset of the category adapter , using cursor object above
        adapterCategory = new SpinnerAdapter(this,cCategory, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        //Set the adapter for the Category spinner
        spCategory.setAdapter(adapterCategory);
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
        //Declare and isntantiate a new DateFormat object to display date in current format (This format may change based on the app settings)
        SimpleDateFormat format = new SimpleDateFormat(MainActivity.getDateFormat());
        //Declare and instantiate a new date object, using the date set in calendar. Get the time in millisecs
        Date date = new Date(calendar.getTimeInMillis());
        return format.format(date);
    }

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
        //Declare and initialize empty string objects to construct the full time text
        /*tring amPm = "";
        String hour = "";
        String min="0";*/
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
            //Set callendar minute to be whatever is in substring from 3 to 5
            calendar.set(Calendar.MINUTE,Integer.valueOf(time.substring(3,5)));
        }//End of if else statement that checks the hour text view text
        //Initialize the date object with date and time extracted from text views
        date = calendar.getTime();
        Log.d("Ext_getAppointmentDate","Exit the getAppointmentDate method in the AddTaskActivity class.");
        return date.getTime();
    }//Endof getAppointmentDate method
}//End of DisplayTaskActivity
