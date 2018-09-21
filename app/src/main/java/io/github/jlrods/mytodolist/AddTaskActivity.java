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

public class AddTaskActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener,TimePickerDialog.OnTimeSetListener {

    //Define global variables
    TextView tvTaskTag;
    EditText etDescription;
    TextView tvCategoryTag;
    Spinner spCategory;
    LinearLayout layoutPriority;
    TextView tvPriorityTag;
    Spinner spPriority;
    ImageView imgLogoPriority;
    LinearLayout layoutAppointment;
    CheckBox cbIsAppointment;
    ImageView imgLogoDate;
    LinearLayout layoutDate;
    TextView tvDateTag;
    TextView tvDate;
    LinearLayout layoutHour;
    TextView tvHourTag;
    TextView tvHour;
    ImageView imgLogoHour;
    LinearLayout layoutNotes;
    ImageView imgLogoNotes;
    TextView tvNotesTag;
    EditText etNotes;

    Cursor cCategory;
    Cursor cPriority;
    Task task;
    Grocery grocery;
    Bundle extras;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Ent_OnCreateAddTask","Enter the onCreate method in the AddTaskActivity class.");
        setContentView(R.layout.activity_add_task);
        extras = getIntent().getExtras();
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
        this.cbIsAppointment.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        hideAppointmentDetails(isChecked);
                    }
                }
        );
        this.layoutNotes = findViewById(R.id.layoutNotes);
        String sql = "";
        String table="";
        SpinnerAdapter adapterCategory;
        SpinnerAdapter adapterPriority;
        //Check the current poperty where the activity was called form (Groceries or any other task)
        if(extras.getString("category").equals(MainActivity.getGroceryCategory())){
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
            //For all other tasks
            //Set the description input text hint to refer to a task description
            this.etDescription.setHint(R.string.taskDescHint);
            //Set the sql quesry to inquire CATEGORY table where the id is not the one for All and Groceries categories
            sql = "SELECT * FROM CATEGORY WHERE _id NOT IN ( " +MainActivity.findCategoryByName(MainActivity.getAllCategory()).getId()+
                    ", "+MainActivity.findCategoryByName(MainActivity.getGroceryCategory()).getId()+")";
            //Set hint for category spinner to refer to a task category
            this.spCategory.setPrompt("Select the task category...");
            //Run a sql query that will retrive the current priority items from DB
            cPriority = MainActivity.db.runQuery("SELECT * FROM PRIORITY");
            //Instantiate a new adapterPriority with current data set from cursor c
            adapterPriority = new SpinnerAdapter(this,cPriority,CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
            //Set the priority spinner adapter to be the one defined above
            this.spPriority.setAdapter(adapterPriority);
            //Instantiate all the elements required for this activity... the ones not instantiated for adding a grocery task
            //Allt he appointment related views
            this.imgLogoDate = findViewById(R.id.imgLogoDate);
            this.imgLogoDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeDate();
                }
            });
            this.tvDate = findViewById(R.id.tvDate);
            this.tvDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeDate();
                }
            });
            this.tvDate = findViewById(R.id.tvDate);
            this.tvHour = findViewById(R.id.tvHour);
            this.tvHour.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeHour();
                }
            });
            this.imgLogoHour = findViewById(R.id.imgLogoHour);
            this.imgLogoHour.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeHour();
                }
            });
            this.etNotes = findViewById(R.id.etNotes);
        }//End of if else statement to check currrent category when the add task button was pressed
        //Run a a sql query to retrieve the categroy data. This will vary depending on the current category when add button was pressed
        cCategory = MainActivity.db.runQuery(sql);
        //Set dataset of the category adapter , using cursor object above
        adapterCategory = new SpinnerAdapter(this,cCategory, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        //Set the adapter for the Category spinner
        spCategory.setAdapter(adapterCategory);
        Log.d("Ext_OnCreateAddTask","Exit the OnCreate method in the AddTaskActivity class.");
    }//End of OnCreate method

    private void hideAppointmentDetails(boolean isChecked){
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



    //Method to inflate the menu into the addTaskActivity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_add_task_grocery, menu);
        return true;
    }// Find fe OnCreateOptionsMenu

    //Method to check the menu item selected and execute the correspinding actions
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
                        //Move temp cursor to first position
                        //Check the cursor is not empty
                        if(tempCursor != null && tempCursor.getCount() >0){
                            tempCursor.moveToFirst();
                        }//End of if statement
                        //Declare and instantiate int var to hold id retrived from DB and increment it by 1
                        int id = tempCursor.getInt(0)+1;
                        //Declare and instantiate a new GroceryType object to hold all the grocery info
                        Grocery grocery = new Grocery(id,groceryName,MainActivity.findCategoryByName(MainActivity.getGroceryCategory()),type,false);
                        //Declare and instantiate a new int var to hold the returned int from the addItem method which will correspond with the grocery just created
                        int idFromDB = MainActivity.db.addItem(grocery);
                        //Check the id is correct by comparing the one used to create the Grocery object and the id coming from the DB after the insert item transaction.
                        if(id == idFromDB){
                            //Check if there is any filter activated to call the proper sql...
                            /*if(MainActivity.getSelectedTypes().size()>0){

                            }else if(){}*/
                            //Notify data set change
                            MainActivity.updateRecyclerViewData("SELECT * FROM GROCERIES ORDER BY TypeOfGrocery ASC");
                            //Display  message saying the grocery was added to the list
                            Toast.makeText(this,R.string.groceryAdded,Toast.LENGTH_SHORT).show();
                            result = true;
                            finish();
                        }else{
                            Toast.makeText(this,R.string.groceryAddedFailed,Toast.LENGTH_SHORT).show();
                            result = false;
                        }
                    }else{
                        //Display error message via toast
                        Toast.makeText(this,R.string.groceryNameEmpty,Toast.LENGTH_SHORT).show();
                    }
                }else{
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

                        Priority priority = Priority.findPriorityById(this.spPriority.getSelectedItemPosition());
                        long dueDate = -1;
                        if(this.cbIsAppointment.isChecked()){
                            dueDate = this.getAppointmentDate();
                        }
                        String notes = this.etNotes.getText().toString();
                        //Declare and instantiate a new cursor object to hold data from sql query to retrieve the max value from _id column in GROCERIES table
                        Cursor tempCursor = MainActivity.db.runQuery("SELECT MAX(_id) FROM TASK");
                        //Check the cursor is not empty
                        if(tempCursor != null && tempCursor.getCount() >0){
                            //Move temp cursor to first position
                            tempCursor.moveToFirst();
                        }//End of if statement
                        //Declare and instantiate int var to hold id retrived from DB and increment it by 1
                        int id = tempCursor.getInt(0)+1;
                        //Declare and instantiate a new GroceryType object to hold all the grocery info
                        Task newTask = new Task(id,taskDescription,MainActivity.findCategoryByName(categoryName),priority,false,this.cbIsAppointment.isChecked(),dueDate,false,notes);
                        //Declare and instantiate a new int var to hold the returned int from the addItem method which will correspond with the grocery just created
                        int idFromDB = MainActivity.db.addItem(newTask);
                        //Check the id is correct by comparing the one used to create the Grocery object and the id coming from the DB after the insert item transaction.
                        if(id == idFromDB){
                            //Check if there is any filter activated to call the proper sql...
                            /*if(MainActivity.getSelectedTypes().size()>0){

                            }else if(){}*/
                            //Notify data set change
                            MainActivity.updateRecyclerViewData("SELECT * FROM TASK ORDER BY Category ASC");
                            //Display  message saying the grocery was added to the list
                            Toast.makeText(this,R.string.groceryAdded,Toast.LENGTH_SHORT).show();
                            result = true;
                            finish();
                        }else{
                            Toast.makeText(this,R.string.groceryAddedFailed,Toast.LENGTH_SHORT).show();
                            result = false;
                        }
                        result = true;
                    }else {
                        //Display error message via toast
                        Toast.makeText(this, R.string.taskDesEmpty, Toast.LENGTH_SHORT).show();//End of
                    }//End of if else statement to check the task description is not empty
                }//End of if else statement that checks the category passed in as extra info coming from MainActivity
                //Add to a data base
                break;
            case R.id.add_task_cancel:
                result = true;
                finish();
                break;
            default:
                result = super.onOptionsItemSelected(item);
                finish();
                break;
        }
        return result;
    }// Fin de onOptionsItemSelected method

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
            //Set the arguments into the diaglo box object
            dialogBox.setArguments(args);
        }//End of if else statement to check the tvDate text is empty or not
        Log.d("Ext_chageDate","Exit changeDate method in the AddTaskActivity class.");
    }//End of changeDate method

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
    }//End of changeHour method


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year,month,dayOfMonth);
        SimpleDateFormat format = new SimpleDateFormat(MainActivity.getDateFormat());
        Date date = new Date(calendar.getTimeInMillis());
        this.tvDate.setText(format.format(date));
    }//End of DatePicker method

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Calendar calendar = Calendar.getInstance();
        //SimpleDateFormat format = new SimpleDateFormat(MainActivity.getDateFormat());
        //Date date = new Date(calendar.getTimeInMillis());
        String amPm = "";
        String hour = "";
        String min="0";
        hour += hourOfDay;
        if(hourOfDay <12){
            amPm = "AM";
            if(hourOfDay >9 ){
                hour = String.valueOf(hourOfDay);
            }else {
                hour = "0" + String.valueOf(hourOfDay);
            }
        }else if(hourOfDay == 12){
            amPm = "PM";
        }else if(hourOfDay>12){
            amPm = "PM";
            if(hourOfDay > 21){
                hour = String.valueOf(hourOfDay-12);
            }else{
                hour = "0" + String.valueOf(hourOfDay-12);
            }
        }
        if(minute < 10){
            min += minute;
        }else{
            min= String.valueOf(minute);
        }
        this.tvHour.setText(hour+":"+min+" "+amPm);
    }//End of onTimeSet method

    private long getAppointmentDate(){
        Calendar calendar = Calendar.getInstance();
        Date date;
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
        calendar.setTime(date);
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
        }
        date = calendar.getTime();
        return date.getTime();
    }//Endof getAppointmentDate method

    private Date getHour(){
        Calendar calendar = Calendar.getInstance();
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
        }
        return date;
    }//End of getHour method
}//End of AddTaskActivity

