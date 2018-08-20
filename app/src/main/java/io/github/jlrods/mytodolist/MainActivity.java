package io.github.jlrods.mytodolist;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    //Define global varaibles and constants
    //Declare a TaskDB object to create the database and manage different db operations
    private TasksDB db;
    //A cursor object to hold data retrieved from database queries
    private Cursor cursor;
    //Declare gobals list to hold the current task objects
    private static ArrayList<Task> tasks;
    //Declare global list to hold the current grocery objectes that exist in the database
    private static ArrayList<Grocery> groceries;
    //Declare global list to hold the current category objects that exist in the database
    private static ArrayList<Category> categories;
    //Declare global list to hold the current grocery objects that exist in the database
    private static ArrayList<GroceryType> groceryTypes;
    //Declare variables to work with RecyclerView class
    private RecyclerView recyclerView;
    public TaskAdapter taskAdapter;
    public GroceryAdapter groceryAdapter;
    private RecyclerView.LayoutManager layoutManager;
    //Declare variable to define the current list that is selected
    Category currentCategory;
    CheckBox cbOnlyChecked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Ent_onCreateMain","Enter onCreate method in MainActivity class.");
        //Instantiate the database manager object
        this.db = new TasksDB(this);
        //Populate the list of Categories
        categories = db.getCategoryList();
        //Populate the list of Grocery types
        groceryTypes = db.getGroceryTypeList();
        //Populate list of groceries
        //groceries = db.getGroceryList("SELECT * FROM GROCERIES ORDER BY TypeOfGrocery ASC");
        //Popoulate the Taksk list
        //tasks = db.getTaskList("SELECT * FROM TASK");
        //Set layout for main activity
        setContentView(R.layout.activity_main);
        //Set currentCategory variable to the default value (default to be set up on the settings menu)
        this.currentCategory= findCategoryByName("All");
        //Find the checkBox in the layout and set the onCheckedChangeListener handler
        this.cbOnlyChecked = this.findViewById(R.id.cbOnlyChecked);
        this.cbOnlyChecked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Call filter checked items only
                    filterCheckedOnly();
                }else{
                    unfilterCheckedOnly();
                }//End of if else statement
            }//End of listener
        });//End of setOncheckedChangeListener method

        //Populate the cursor object with data from the task table (This might be changed from the settings menu, to have a different default start up list)
        cursor = db.runQuery("SELECT * FROM TASK");
        //Move cursor to the first position. The runQuery method returns a cursor with no position set yet
        cursor.moveToFirst();
        //RecycleView settings
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        taskAdapter = new TaskAdapter(this,db,cursor);
        recyclerView.setAdapter(taskAdapter);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        //Set up onCheckedChangeListener handler for the task items
        taskAdapter.setOnItemCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int adapterPosition = recyclerView.getChildAdapterPosition((View) buttonView.getParent());
                cursor.moveToPosition(adapterPosition);
                Task task = db.extractTask(cursor);
                taskAdapter.updateItemIsSelected(adapterPosition,isChecked);
                db.updateIsSelected(currentCategory.getName().toString(),task.getId(),isChecked);
                //Update the data set (cursor object) with most up to date data from database
                cursor = db.runQuery("SELECT * FROM TASK");
                //Move to first row of cursor if not empty
                cursor.moveToFirst();
                //Refresh the new data set on the grocery adapter
                taskAdapter.setCursor(cursor);
            }//End of onCheckedChanged method

        });//End of setOnCheckedChangeListener method

        //Tool bar creation and functionality set up
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Floating button creation and functionality set up
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }//End of onClick method for the floating button
        });//End of setOnClickListener method

        //Side Navigation Menu set up
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Log.d("Ent_onCreateMain","Enter onCreate method in MainActivity class.");
    }//End of onCreate Method

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }else if(id==R.id.search){
            this.search();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Method to find a category by passing in id number
    public static Category findCategoryById(int id){
        Log.d("Ent_FindCatById","Enter the findCategoryById method in the MainActivity class.");
        //Declare and instantiate a null Category object
        Category category = null;
        //Declare and set to false a boolean flag to prompt when the category it's being looked for is found
        Boolean found = false;
        //Declare and initialize an iterator
        int i = 0;
        //Check the list is not empty or null
        if(categories != null && categories.size()>0){
            //Do While loop to iterate through the Category Array List
            do{
                //Check the current id is equal to the one passed in as parameter
                if(categories.get(i).getId() == id){
                    //If the ids are the same, make the category object to point to the current object in the Category Array list
                    category = categories.get(i);
                    //Set boolean flag to true as the Category item was found
                    found = true;
                }else{
                    //Otherwise, increase the iterator
                    i++;
                }//End of if else statement to check current item in the list
            }while(!found && i <= categories.size());
        }//End of if statement to check Category Array List is not null or empty
        Log.d("Ent_FindCatById","Enter the findCategoryById method in the MainActivity class.");
        return category;
    }//End of findCategoryById method

    //Method to find a category by passing in it's name
    public static Category findCategoryByName(String name){
        Log.d("Ent_FindCatByName","Enter the findCategoryByName method in the MainActivity class.");
        //Declare and instantiate a null Category object
        Category category = null;
        //Declare and set to false a boolean flag to prompt when the category it's being looked for is found
        Boolean found = false;
        //Declare and initialize an iterator
        int i = 0;
        //Check the list is not empty or null
        if(categories != null && categories.size()>0){
            //Do While loop to iterate through the Category Array List
            do{
                //Check the current name is equal to the one passed in as parameter
                if(categories.get(i).getName().toLowerCase().equals(name.toLowerCase())){
                    //If the names are the same, make the category object to point to the current object in the Category Array list
                    category = categories.get(i);
                    //Set boolean flag to true as the Category item was found
                    found = true;
                }else{
                    //Otherwise, increase the iterator
                    i++;
                }//End of if else statement to check current item in the list
            }while(!found && i < categories.size());
        }//End of if statement to check Category Array List is not null or empty
        Log.d("Ext_FindCatByName","Exit the findCategoryByName method in the MainActivity class.");
        return category;
    }//End of findCategoryByName method

    //Method to find a GroceryType in the GroceryType Array List by passing in id number
    public static GroceryType findGroceryTypeById(int id){
        Log.d("Ent_FindGroceryTypeById","Enter the findGroceryTypeById method in the MainActivity class.");
        //Declare and instantiate a null GroceryType object
        GroceryType type= null;
        //Declare and set to false a boolean flag to prompt when the GroceryType it's being looked for is found
        Boolean found = false;
        //Declare and initialize an iterator
        int i = 0;
        //Check the list is not empty or null
        if(groceryTypes != null && groceryTypes.size()>0){
            //Do While loop to iterate through the GroceryType Array List
            do{
                if(id == groceryTypes.get(i).getId()){
                    type = groceryTypes.get(i);
                    found = true;
                }else{
                    i++;
                }//End of if else statement to check current item in the list
            }while(!found && i <= groceryTypes.size());
        }//End of if statement to check GroceryType Array List is not null or empty
        Log.d("Ext_FindGroceryTypeById","Exit the findGroceryTypeById method in the MainActivity class.");
        return type;
    }//End of the findGroceryTypeById method

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Log.d("Ent_onNavigationSel","Enter onNavigationItemSelected method in MainActivity class.");
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_all) {
            // Handle the the All task list menu option
            //Set the current category to be All
            this.currentCategory = findCategoryByName("All");
            //Run sql query to retrieve all the tasks in the Task table
            this.cursor = db.runQuery("SELECT * FROM TASK ");
            //Move to first row of cursor if not empty
            this.cursor.moveToFirst();
            this.taskAdapter.setCursor(cursor);
            //Set the adapter in the global recyclerView
            this.recyclerView.setAdapter(taskAdapter);
            //Set the OnCheckedChangedListener for the task adapter (even though a new taskAdapter has not been created, sometimes the listener is lost)
            taskAdapter.setOnItemCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    //Toast.makeText(MainActivity.this,"Check box checked or unchecked",Toast.LENGTH_LONG).show();
                    int adapterPosition = recyclerView.getChildAdapterPosition((View) buttonView.getParent());
                    cursor.moveToPosition(adapterPosition);
                    Task task = db.extractTask(cursor);
                    taskAdapter.updateItemIsSelected(adapterPosition,isChecked);
                    db.updateIsSelected(currentCategory.getName().toString(),task.getId(),isChecked);
                    //Update the data set (cursor object) with most up to date data from database
                    cursor = db.runQuery("SELECT * FROM TASK");
                    //Move to first row of cursor if not empty
                    cursor.moveToFirst();
                    //Refresh the new data set on the grocery adapter
                    taskAdapter.setCursor(cursor);
                }//End of onCheckedChanged method

            });//End of setOnItemCheckedChange listener method

        } else if (id == R.id.nav_grocery) {
            // Handle the the groceries task category list menu option
            //Set the current category to be Groceries
            this.currentCategory=findCategoryByName("Groceries");
            //Run sql query to retrieve all the groceries  in the GROCERIES table
            cursor = db.runQuery("SELECT * FROM GROCERIES ORDER BY TypeOfGrocery ASC");
            //Move to first row of cursor if not empty
            cursor.moveToFirst();
            //RecycleView settings
            //Instantiate a new GroceryAdapter and pass in the updated cursor
            groceryAdapter = new GroceryAdapter(this,db,cursor);
            //Set the adapter in the global recyclerView
            recyclerView.setAdapter(groceryAdapter);
            //Set the OncheckedChangedListener for the groceryAdapter
            groceryAdapter.setOnItemCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    //Toast.makeText(MainActivity.this,"Grocery box checked or unchecked",Toast.LENGTH_SHORT).show();
                    int adapterPosition = recyclerView.getChildAdapterPosition((View) buttonView.getParent());
                    cursor.moveToPosition(adapterPosition);
                    Grocery grocery = db.extractGrocery(cursor);
                    groceryAdapter.updateItemIsSelected(adapterPosition,isChecked);
                    db.updateIsSelected(currentCategory.getName().toString(),grocery.getId(),isChecked);
                    //Update the data set (cursor object) with most up to date data from database
                    cursor = db.runQuery("SELECT * FROM GROCERIES ORDER BY TypeOfGrocery ASC");
                    //Move to first row of cursor if not empty
                    cursor.moveToFirst();
                    //Refresh the new data set on the grocery adapter
                    groceryAdapter.setCursor(cursor);
                }//End of onCheckedChanged method

            });//End of setOnItemCheckedChangeListner

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }//End of if else statement chain to check menu option that has been selected

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        Log.d("Ext_onNavigationSel","Exit onNavigationItemSelected method in MainActivity class.");
        return true;
    }//End of onNavigationItemSelected method


    //Method to filter task or groceries by description content
    public void search(){
        Log.d("Ent_serach","Enter the search method in the MainActivity class");
        //Declare and instantiate a new EditText object
        final EditText input= new EditText(this);
        ////Set text to empty text
        input.setText("");
        //Declare a string to hold a sql query which will vary depending on the type of Category
        final String query;
        //Check the current category object... Depending on if it is Groceries or any other the actions will change
        if (currentCategory.equals(this.findCategoryByName("Groceries"))){
            //In case the current category is Groceries
            query = "SELECT * FROM GROCERIES WHERE Name LIKE '%";
            taskAdapter = new TaskAdapter(MainActivity.this,db,cursor);
            //Create new dialog box to ask the user to input the filter text
            //groceryAdapter = new GroceryAdapter(MainActivity.this,db,cursor);
            //this.displayDialogueBox(R.string.searchGrocery,R.string.hingSearchGrocery,R.string.searchExplanation,"SELECT * FROM GROCERIES WHERE Name LIKE '%",groceryAdapter);
            //Set the hint message to be displayed
            input.setHint(R.string.hingSearchGrocery);
            new AlertDialog.Builder(this)
                    .setTitle(R.string.searchGrocery)
                    .setMessage(R.string.searchExplanation)
                    .setView(input)
                    .setPositiveButton("Ok",new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog,int whichButton){
                            //Run the sql query and retrieve data
                            cursor = db.runQuery(query+input.getText().toString()+"%'");
                            //Move cursor to first row
                            cursor.moveToFirst();
                            //Set the new cursor in the groceryAdapter
                            groceryAdapter.setCursor(cursor);
                            //Set the new adapter for the recyclerview
                            recyclerView.setAdapter(groceryAdapter);
                        }//End of Onclick method
                    })
                    .setNegativeButton(R.string.cancel,null)
                    .show();
        }else{
            //Otherwise, all the task can be handled in a similar  way
            //Set the hint message to be displayed
            input.setHint(R.string.hintSearchTask);
            //Create new dialog box to ask the user to input the filter text
            new AlertDialog.Builder(this)
                    .setTitle(R.string.searchTask)
                    .setMessage(R.string.searchExplanation)
                    .setView(input)
                    .setPositiveButton("Ok",new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog,int whichButton){
                            //Declare and instantiate as null a string object to hold the sql query to run. Depending on the current category, different query will be run
                            String sql="";
                            //First, Check the current property is All otherwise the query must include the specific category that has been selected
                            if(currentCategory.equals(findCategoryByName("All"))){
                                //Define sql query to retrieve all the task categories
                                sql = "SELECT * FROM TASK WHERE description LIKE '%"+input.getText().toString()+"%'";
                            }else{
                                //Otherwise, define sql query to retrieve tasks with the search text and the current category
                                sql = "SELECT * FROM TASK WHERE Category = " + currentCategory.getId() + " AND description LIKE '%"+input.getText().toString()+"%'";
                            }
                            //Run the sql query and retrieve data
                            cursor = db.runQuery(sql);
                            //Move cursor to first row
                            cursor.moveToFirst();
                            //Set the  new cursor for the already existing taskAdapter object
                            taskAdapter.setCursor(cursor);
                            //Set the adapter for the already existing recyclerView
                            recyclerView.setAdapter(taskAdapter);
                        }//End of Onclick method
                    })
                    .setNegativeButton(R.string.cancel,null)
                    .show();
        }//End of if else statements to check the currentCategory object
        Log.d("Ext_serach","Exit the search method in the MainActivity class");
    }//End of search method

    //Method to create and personalize a dialogue box
    /*public void displayDialogueBox(int title, int hint, int msg, final String query, final TaskAdapter adapter){
        //Declare and instantiate a new EditText object
        final EditText input= new EditText(this);
        //Set text to empty text
        input.setText("");
        //Set the hint message to be displayed
        input.setHint(hint);
        if(adapter instanceof GroceryAdapter){
            new AlertDialog.Builder(this)
                    .setTitle(title)
                    .setMessage(msg)
                    .setView(input)
                    .setPositiveButton("Ok",new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog,int whichButton){
                            cursor = db.runQuery(query+input.getText().toString()+"%'");
                            cursor.moveToFirst();
                            //taskAdapter = new TaskAdapter(MainActivity.this,db,cursor);
                            //groceryAdapter = new GroceryAdapter(MainActivity.this,db,cursor);
                            recyclerView.setAdapter(adapter);
                        }//End of Onclick method
                    })
                    .setNegativeButton(R.string.cancel,null)
                    .show();
        }else{
            new AlertDialog.Builder(this)
                    .setTitle(title)
                    .setMessage(msg)
                    .setView(input)
                    .setPositiveButton("Ok",new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog,int whichButton){
                            cursor = db.runQuery(query+input.getText().toString()+"%'");
                            cursor.moveToFirst();
                            //taskAdapter = new TaskAdapter(MainActivity.this,db,cursor);
                            recyclerView.setAdapter(adapter);
                        }//End of Onclick method
                    })
                    .setNegativeButton(R.string.cancel,null)
                    .show();
        }

    }
*/
    //Method to filter selected items

    //Method to filter only the items that has been selected
    public void filterCheckedOnly(){
        Log.d("Ent_filterCheckedOnly","Enter the filterCheckedOnlyt method in the MainActivity class");
        //Check the current category object... Depending on if it is Groceries or any other the actions will change
        if (currentCategory.equals(this.findCategoryByName("Groceries"))){
            //Run the sql query to retrieve data from Groceries table where is selected is 1
            this.cursor = db.runQuery("SELECT * FROM GROCERIES WHERE isSelected = 1");
            //Move to first row in cursor if available
            this.cursor.moveToFirst();
            //Set the  new cursor for the already existing taskAdapter object
            this.groceryAdapter.setCursor(this.cursor);
            //Set the  adapter for the already existing recyclerView object
            this.recyclerView.setAdapter(groceryAdapter);
        }else{
            //Check if the All category has been selected
            if(currentCategory.equals(findCategoryByName("All"))){
                //Run sql query to retrieve data from task table where isSelected is 1
                this.cursor = db.runQuery("SELECT * FROM TASK WHERE isSelected = 1");
            }else{
                //Otherwise, run a query to retrieve data from task table where isSelected is 1 and Category mateches the selected one
                this.cursor = db.runQuery("SELECT * FROM TASK WHERE Category = " + currentCategory.getId() + " AND isSelected = 1");
            }//End of if else statement to check if All category has been selected
            //Move cursor to the first row if avaialable
            this.cursor.moveToFirst();
            //Set the  new cursor for the already existing taskAdapter object
            this.taskAdapter.setCursor(cursor);
            //Set the  adapter for the already existing recyclerView object
            this.recyclerView.setAdapter(taskAdapter);
        }//End of if else statement to check category is groceries

        Log.d("Ext_filterCheckedOnly","Exit the unfilterCheckedOnlyt method in the MainActivity class");
    }//End of filterCheckedOnly

    //Method to filter only the items that has been selected
    public void unfilterCheckedOnly(){
        Log.d("Ent_filterCheckedOnly","Enter the unfilterCheckedOnlyt method in the MainActivity class");
        //Check the current category object... Depending on if it is Groceries or any other the actions will change
        if (currentCategory.equals(this.findCategoryByName("Groceries"))){
            //Run query to retrieve data from the Groceries table
            this.cursor = db.runQuery("SELECT * FROM GROCERIES");
            //Move cursor to the first row if avaialable
            this.cursor.moveToFirst();
            //Set the  new cursor for the already existing groceryAdapter object
            this.groceryAdapter.setCursor(this.cursor);
            //Set the  adapter for the already existing recyclerView object
            this.recyclerView.setAdapter(groceryAdapter);
        }else{
            //Check if the All category has been selected
            if(currentCategory.equals(findCategoryByName("All"))){
                //Run query to retrieve data from the task table
                this.cursor = db.runQuery("SELECT * FROM TASK");
            }else{
                //Otherwise, run query to retrieve data from the Task table where category matches the current one
                this.cursor = db.runQuery("SELECT * FROM TASK WHERE Category = " + currentCategory.getId());
            }//End of if else statement to check if All category has been selected
            //Move cursor to the first row if avaialable
            this.cursor.moveToFirst();
            //Set the  new cursor for the already existing taskAdapter object
            this.taskAdapter.setCursor(cursor);
            //Set the  adapter for the already existing recyclerView object
            this.recyclerView.setAdapter(taskAdapter);
        }//End of if else statement to check category is groceries

        Log.d("Ext_unfilterCheckedOnly","Exit the unfilterCheckedOnlyt method in the MainActivity class");
    }//End of filterCheckedOnly

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}//End of the MainActivity class
