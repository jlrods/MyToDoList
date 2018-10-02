package io.github.jlrods.mytodolist;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.media.Image;
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
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    //Define global variables and constants
    //Declare a TaskDB object to create the database and manage different db operations
    public static TasksDB db;
    //A cursor object to hold data retrieved from database queries
    private static Cursor cursor;
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
    public static TaskAdapter taskAdapter;
    public static GroceryAdapter groceryAdapter;
    private RecyclerView.LayoutManager layoutManager;
    //Declare variable to define the current list that is selected
    private static Category currentCategory;
    //Declare global variables to handle UI icon menu
    private TextView tvCurrentList;
    private ImageView imgCurrentList;
    private CheckBox cbOnlyChecked;
    private TextView tvOnlyChecked;
    private ImageView imgHighlightFilter;
    private TextView tvHighlightFilter;
    //Declare global variable list to hold the current categories selected in the filter menu. This data will come from the DB
    private static ArrayList selectedTypes = new ArrayList();
    private static boolean[] selectedTypesListPosition ;
    //Constants
    private static final String groceryCategory = "Groceries";
    private static final String allCategory ="All";
    private static String dateFormat ="MMM dd yyyy";
    private boolean isSearchFilter = false;
    private String[] lastSearchText ={"",""};
    private static int highlightColor = R.color.colorAccent;
    private static int primaryTextColor = R.color.colorPrimaryText;
    private static String doneColor ="green";
    private static String doneHighlighter = "#FF4081";
    private static String whiteBackground ="#FAFAFA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Ent_onCreateMain","Enter onCreate method in MainActivity class.");
        //Instantiate the database manager object
        this.db = new TasksDB(this);
        //Populate the list of Categories
        this.categories = db.getCategoryList();
        //Populate the list of Grocery types
        this.groceryTypes = db.getGroceryTypeList();
        //Populate list of groceries
        //groceries = db.getGroceryList("SELECT * FROM GROCERIES ORDER BY TypeOfGrocery ASC");
        //Populate the Taksk list
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
        this.cursor = db.runQuery("SELECT * FROM TASK ORDER BY Category ASC");
        //Move cursor to the first position. The runQuery method returns a cursor with no position set yet
        this.cursor.moveToFirst();
        //RecycleView settings
        this.recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        this.taskAdapter = new TaskAdapter(this,db,cursor);
        this.taskAdapter.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = recyclerView.getChildAdapterPosition(v);
                //move the cursor to the task position in the adapter
                cursor.moveToPosition(adapterPosition);
                //Extract the task object from the cursor row
                Task task = db.extractTask(cursor);
                Intent i = new Intent(MainActivity.this, EditTaskActivity.class);
                i.putExtra("id", task.getId());
                i.putExtra("category",currentCategory.toString());
                startActivity(i);
            }//End of onClick method
        });//End of OnSetItemClickListener method
        this.recyclerView.setAdapter(taskAdapter);
        this.layoutManager = new LinearLayoutManager(this);
        this.recyclerView.setLayoutManager(layoutManager);
        //Set up onCheckedChangeListener handler for the task items
        this.taskAdapter.setOnItemCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //Find the position of parent recyclerview item in the adapter and store it in an int variable
                int adapterPosition = recyclerView.getChildAdapterPosition((View) buttonView.getParent());
                //move the cursor to the task position in the adapter
                cursor.moveToPosition(adapterPosition);
                //Extract the task object from the cursor row
                Task task = db.extractTask(cursor);
                //Check the current task isSelected attribute has changed or not
                if(task.isSelected()!=isChecked){
                    //Update the isSelected list within the adapter used to track the actua isSelected status of each task
                    taskAdapter.updateItemIsSelected(adapterPosition,isChecked);
                    //Declare and initialize a string to hold the sql query to update the cursor
                    String sql="";
                    //Update the data set (cursor object) with most up to date data from database
                    if(isSearchFilter){
                        //Check if any filter has been selected
                        if(cbOnlyChecked.isChecked()){
                            //If the global check box is checked, filter the selected items only
                            sql = lastSearchText[0] + " AND IsSelected = 1 ORDER BY Category ASC";
                        }else{
                            //otherwise, retrieve everything from Task table
                            sql =lastSearchText[0] + " ORDER BY Category ASC";
                        }//End of if else statement
                    }else{
                        //Check if any filter has been selected
                        if(cbOnlyChecked.isChecked()){
                            //If the global check box is checked, filter the selected items only
                            sql = "SELECT * FROM TASK WHERE IsSelected = 1 ORDER BY Category ASC";
                        }else{
                            //otherwise, retrieve everything from Task table
                            sql ="SELECT * FROM TASK ORDER BY Category ASC";
                        }//End of if else statement
                    }//End of if statement to check the isSearchFilter attribute state
                    //Update the isSelected attribute (un)checked task
                    db.updateBoolAttribute(currentCategory.getName().toString(),"IsSelected",task.getId(),isChecked);
                    //Call method to update the adapter and the recyclerView
                    updateRecyclerViewData(sql);
                }//End of if statement to check the current task actually changed isSelected stated (otherwise is the recyclerview recycling a  View)
            }//End of onCheckedChanged method
        });//End of setOnCheckedChangeListener method

        //Instantiate variables to handle the MainActiviy UI icons (List selected, actions: highligh, filter)
        this.tvCurrentList = this.findViewById(R.id.tvCurrentList);
        this.tvCurrentList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFullListNoFilter();
            }
        });
        this.imgCurrentList = this.findViewById(R.id.imgListIcon);
        this.imgCurrentList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFullListNoFilter();
            }
        });
        this.tvOnlyChecked = this.findViewById(R.id.tvOnlyChecked);
        this.tvHighlightFilter = this.findViewById(R.id.tvHighlightFilter);
        this.imgHighlightFilter = this.findViewById(R.id.imgHighlightFilter);
        this.imgHighlightFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check the current category matches the Groceries category
                if(!currentCategory.getName().equals(groceryCategory)){
                    //Call method to highlight the selected tasks
                    highlightSelectedTask();
                }else{
                    //Call method to filter by grocery type
                    filterByType();
                }//End of if else statement to check the current category
            }//End of onClick method
        });//End of setOnclickListener method
        this.tvHighlightFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check the current category matches the Groceries category
                if(!currentCategory.getName().equals(groceryCategory)){
                    //Call method to highlight the selected tasks
                    highlightSelectedTask();
                }else{
                    //Call method to filter by grocery type
                    filterByType();
                }//End of if else statement to check the current category
            }
        });

        //Check the currentCategory selected and display the proper name
        //this.tvCurrentList.setText(this.currentCategory.getName().toString());
        //Update the top menu text and images
        this.updateTopMenuUI();
        //Populate selected grocery types array list
        selectedTypes = this.findSelectedTypes();
        //Populate the list of seleted grocery types
        selectedTypesListPosition = this.findSelectedTypesPosition();
        //Tool bar creation and functionality set up
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Floating button creation and functionality set up
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                throwAddTaskActivity(null);
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
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
        }else if(id == R.id.delete){
            return this.delete();
        }

        return super.onOptionsItemSelected(item);
    }//End of onOptionsItemSelected method

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
            }while(!found && i < categories.size());
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
            }while(!found && i < groceryTypes.size());
        }//End of if statement to check GroceryType Array List is not null or empty
        Log.d("Ext_FindGroceryTypeById","Exit the findGroceryTypeById method in the MainActivity class.");
        return type;
    }//End of the findGroceryTypeById method

    //Method to find a GroceryType in the GroceryType Array List by passing in id number
    public static GroceryType findGroceryTypeByName(String name){
        Log.d("Ent_FindGroceryTByName","Enter the findGroceryTypeByName method in the MainActivity class.");
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
                if(groceryTypes.get(i).getName().toLowerCase().equals(name.toLowerCase())){
                    type = groceryTypes.get(i);
                    found = true;
                }else{
                    i++;
                }//End of if else statement to check current item in the list
            }while(!found && i < groceryTypes.size());
        }//End of if statement to check GroceryType Array List is not null or empty
        Log.d("Ext_FindGroceryTByName","Exit the findGroceryTypeByName method in the MainActivity class.");
        return type;
    }//End of the findGroceryTypeById method

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Log.d("Ent_onNavigationSel","Enter onNavigationItemSelected method in MainActivity class.");
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        //Check the item selected on the nav menu
        if (id == R.id.nav_all) {
            // Handle the the All task list menu option
            //Set the current category to be All
            this.currentCategory = findCategoryByName(allCategory);
            //Check if global checkbox state
            if(this.cbOnlyChecked.isChecked()){
                this.cbOnlyChecked.setChecked(false);
            }//End of if statement to check the check box state
            //Change background in case previous list was the groceries list (selectedTypes size >0)
            tvHighlightFilter.setTextColor(getResources().getColor(R.color.colorSecondayText));
            this.recyclerView.setAdapter(taskAdapter);
            //Call method to update the RecyclerView data set and update ui
            this.updateRecyclerViewData("SELECT * FROM TASK ORDER BY Category ASC");
            //Set the OnCheckedChangedListener for the task adapter (even though a new taskAdapter has not been created, sometimes the listener is lost)
            this.taskAdapter.setOnItemCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    //Find the position in the adapter  of parent recyclerView item where the selected checkbox is and store it in an int variable
                    int adapterPosition = recyclerView.getChildAdapterPosition((View) buttonView.getParent());
                    //Move the cursor to the task position in the adapter
                    cursor.moveToPosition(adapterPosition);
                    //Extract the task object from the cursor row
                    Task task = db.extractTask(cursor);
                    //Check the current task isSelected attribute has changed or not
                    if(task.isSelected()!=isChecked){
                        //Update the isSelected list within the adapter used to track the actual isSelected status of each task
                        taskAdapter.updateItemIsSelected(adapterPosition,isChecked);
                        //Declare and initialize a string to hold the sql query to update the cursor
                        String sql="";
                        if(isSearchFilter){
                            //Check if any filter has been selected
                            if(cbOnlyChecked.isChecked()){
                                //If the global check box is checked, filter the selected items only
                                sql = lastSearchText[0] + " AND IsSelected = 1 ORDER BY Category ASC";
                            }else{
                                //otherwise, retrieve everything from Task table
                                sql =lastSearchText[0] + " ORDER BY Category ASC";
                            }//End of if else statement
                        }else{
                            //Check if any filter has been selected
                            if(cbOnlyChecked.isChecked()){
                                //If the global ccheck box is checked, filter the selected items only
                                sql = "SELECT * FROM TASK WHERE IsSelected = 1 ORDER BY Category ASC";
                            }else{
                                //otherwise, retrieve everything from Task table
                                sql ="SELECT * FROM TASK ORDER BY Category ASC";
                            }//End of if else statement
                        }
                        //Update database with the isSelected attribute of current task which checkbox was toggled
                        db.updateBoolAttribute(currentCategory.getName().toString(),"IsSelected",task.getId(),isChecked);
                        //Call method to update the adapter and the recyclerView
                        updateRecyclerViewData("SELECT * FROM TASK ORDER BY Category ASC");
                    }//End of if statement to check the current task actually changed isSelected stated (otherwise is the recyclerview recycling a  View)
                }//End of onCheckedChanged method
            });//End of setOnItemCheckedChange listener method
            //Update the top menu text and images
            this.updateTopMenuUI();
        } else if (id == R.id.nav_grocery) {
            // Handle the the groceries task category list menu option
            if(this.cbOnlyChecked.isChecked()){
                this.cbOnlyChecked.setChecked(false);
            }//End of if statement to check the check box state
            //Set the current category to be Groceries
            this.currentCategory=findCategoryByName(groceryCategory);
            //Declare and initiallize a string to hold the sql query to update the cursor
            String sql="";
            //Check if the selectedTypes list is not empty. If that is the case, change the filter background color to the accent color
            if(this.selectedTypes.size()>0){
                tvHighlightFilter.setTextColor(getResources().getColor(R.color.colorAccent));
                //Call method to dynamically depending on the grocery types selected
                sql = listGroceriesFiltered();
            }else{
                //Otherwise, select everything from GROCERIES table ordered by type
                sql ="SELECT * FROM GROCERIES ORDER BY TypeOfGrocery ASC";
            }// End of if else statement
            //Update cursor data by queriying database
            cursor = db.runQuery(sql);
            //Move to first row in cursor (if any)
            cursor.moveToFirst();
            //Instantiate the groceryAdapter for first time, pass in MainActivity context, current DB manager class and the cursor with grocery data
            groceryAdapter = new GroceryAdapter(this,db,cursor);
            //Set the adapter in the global recyclerView
            recyclerView.setAdapter(groceryAdapter);
            groceryAdapter.setOnItemClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int adapterPosition = recyclerView.getChildAdapterPosition(v);
                    //move the cursor to the task position in the adapter
                    cursor.moveToPosition(adapterPosition);
                    //Extract the task object from the cursor row
                    Grocery grocery = db.extractGrocery(cursor);
                    Intent i = new Intent(MainActivity.this, EditTaskActivity.class);
                    i.putExtra("id", grocery.getId());
                    i.putExtra("category",currentCategory.toString());
                    startActivity(i);
                }//End of onClick method
            });//End of OnSetItemClickListener method
            //Set the OncheckedChangedListener for the groceryAdapter
            groceryAdapter.setOnItemCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    //Find the position in the adapter  of parent recyclerView item where the selected checkbox is and store it in an int variable
                    int adapterPosition = recyclerView.getChildAdapterPosition((View) buttonView.getParent());
                    //Move the cursor to the task position in the adapter
                    cursor.moveToPosition(adapterPosition);
                    //Extract the grocery object from the cursor row
                    Grocery grocery = db.extractGrocery(cursor);
                    //Check the current task isSelected attribute has changed or not
                    if(grocery.isSelected()!=isChecked){
                        //Update the isSelected list within the grocery adapter used to track the actual isSelected status of each task
                        groceryAdapter.updateItemIsSelected(adapterPosition,isChecked);
                        //Declare and initialize a string to hold the sql query to update the cursor
                        String sql="";
                        if(isSearchFilter){
                            //Check if any filter has been selected
                            if(cbOnlyChecked.isChecked()){
                                //If the global check box is checked, filter the selected items only
                                sql = lastSearchText[1] + " AND IsSelected = 1 ORDER BY TypeOfGrocery ASC";
                            }else{
                                //otherwise, retrieve everything from Task table
                                sql =lastSearchText[1] + " ORDER BY TypeOfGrocery ASC";
                            }//End of if else statement
                        }else{
                            //Check if any filter has been selected
                            if(cbOnlyChecked.isChecked()){
                                //If the global check box is checked, filter the selected items only
                                sql = "SELECT * FROM GROCERIES WHERE IsSelected = 1 ORDER BY TypeOfGrocery ASC";
                            }else if(selectedTypes.size()>0){
                                //Check if grocery type filter is in place (selectedTypes list is not empty)
                                sql = listGroceriesFiltered();
                            }else{
                                //otherwise, retrieve everything from groceries table
                                sql ="SELECT * FROM GROCERIES ORDER BY TypeOfGrocery ASC";
                            }//End of if else statements to check if the only checked filter has been applied
                        }//End of if else statement to check the isSearchFilter is true or false
                        //Update database with the isSelected attribute of current grocery which checkbox was toggled
                        db.updateBoolAttribute(currentCategory.getName().toString(),"IsSelected",grocery.getId(),isChecked);
                        //Call method to update the adapter and the recyclerView
                        updateRecyclerViewData(sql);
                    }// End of if statement to check the current task actually changed isSelected stated (otherwise is the recyclerview recycling a  View)
                }//End of onCheckedChanged method
            });//End of setOnItemCheckedChangeListner
            //Update the top menu text and images
            this.updateTopMenuUI();
        //Check if the xx list was selected
        } else if (id == R.id.nav_slideshow) {
        //Check if the xx list was selected
        } else if (id == R.id.nav_manage) {
        //Check if the xx list was selected
        } else if (id == R.id.nav_share) {
        //Check if the xx list was selected
        } else if (id == R.id.nav_send) {
            //Check if the xx list was selected
        }//End of if else statement chain to check menu option that has been selected

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        Log.d("Ext_onNavigationSel","Exit onNavigationItemSelected method in MainActivity class.");
        return true;
    }//End of onNavigationItemSelected method

    //Method to update the data set in the RecyclerView adapter
    public static void updateRecyclerViewData(String sql){
        Log.d("Ent_updateRecViewData","Enter the updateRecyclerViewData method in the MainActivity class.");
        cursor = db.runQuery(sql);
        //Move to first row of cursor if not empty
        cursor.moveToFirst();
        //Check what type of adapter has to be used TaskAdapter or GroceryAdapter
        if(currentCategory.equals(findCategoryByName(groceryCategory))){
            //Refresh the new data set on the grocery adapter
            groceryAdapter.setCursor(cursor);
            groceryAdapter.notifyDataSetChanged();
        }else{
            //Refresh the new data set on the task adapter
            taskAdapter.setCursor(cursor);
            taskAdapter.notifyDataSetChanged();
        }//End of if else statement
        Log.d("Ext_updateRecViewData","Exit the updateRecyclerViewData method in the MainActivity class.");
    }//End of updateRecyclerViewData method

    //Method to filter task or groceries by description content
    public void search(){
        Log.d("Ent_serach","Enter the search method in the MainActivity class");
        //Declare and instantiate a new EditText object
        final EditText input= new EditText(this);
        //Set text to empty text
        input.setText("");
        //Check the current category object... Depending on if it is Groceries or any other the actions will change
        if (currentCategory.equals(this.findCategoryByName(this.groceryCategory))){
            //In case the current category is Groceries
            //Clear selection of grocery type filters
            this.clearTypeFilter();
            //Create new dialog box to ask the user to input the filter text
            //Set the hint message to be displayed
            input.setHint(R.string.hingSearchGrocery);
            new AlertDialog.Builder(this)
                    .setTitle(R.string.searchGrocery)
                    .setMessage(R.string.searchExplanation)
                    .setView(input)
                    .setPositiveButton("Ok",new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog,int whichButton){
                            //Set isSearchFilter boolean to true
                            isSearchFilter = true;
                            //Declare and instantiate as null a string object to hold the sql query to run. Depending on the current category, different query will be run
                            String sql="SELECT * FROM GROCERIES WHERE Name LIKE '%";
                            //Store the search sql for future use
                            lastSearchText[1] = sql+input.getText().toString()+"%'";
                            //Call method to update the adapter and the recyclerView
                            updateRecyclerViewData(lastSearchText[1]);
                        }//End of Onclick method
                    })//End of setPossitiveButton method
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
                            if(currentCategory.equals(findCategoryByName(allCategory))){
                                //Define sql query to retrieve all the task categories
                                sql = "SELECT * FROM TASK WHERE description LIKE '%"+input.getText().toString()+"%'";
                            }else{
                                //Otherwise, define sql query to retrieve tasks with the search text and the current category
                                sql = "SELECT * FROM TASK WHERE Category = " + currentCategory.getId() + " AND description LIKE '%"+input.getText().toString()+"%'";
                            }//End of if else statement to  check the current category object
                            //Set isSearchFilter boolean to true
                            isSearchFilter = true;
                            //Store the search sql for future use
                            lastSearchText[0] = sql;
                            //Call method to update the adapter and the recyclerView
                            updateRecyclerViewData(sql);
                        }//End of Onclick method
                    })//End of setPositiveButton melthod
                    .setNegativeButton(R.string.cancel,null)
                    .show();
        }//End of if else statements to check the currentCategory object
        //Unchecked the only checked task (grocery) check box (if checked)
        if(this.cbOnlyChecked.isChecked()){
            this.cbOnlyChecked.setChecked(false);
        }//End of if statement to check the check box state
        Log.d("Ext_serach","Exit the search method in the MainActivity class");
    }//End of search method

    //Method to filter only the items that has been selected
    public void filterCheckedOnly(){
        Log.d("Ent_filterCheckedOnly","Enter the filterCheckedOnlyt method in the MainActivity class");
        //If the search filter was applied set boolean flag to false
        if(this.isSearchFilter){
            this.isSearchFilter = false;
        }//End of if statement to check the isSearchFilter attribute state
        //Check the current category object... Depending on if it is Groceries or any other the actions will change
        String sql ="";
        if (currentCategory.equals(this.findCategoryByName("Groceries"))){
            //Clear selection of grocery type filters
            this.clearTypeFilter();
            sql ="SELECT * FROM GROCERIES WHERE isSelected = 1 ORDER BY TypeOfGrocery ASC";
            //Call method to update the adapter and the recyclerView
            updateRecyclerViewData(sql);
        }else{
            //Otherwise, check if the All category has been selected
            if(currentCategory.equals(findCategoryByName("All"))){
                //Define sql query to retrieve data from task table where isSelected is 1
                sql = "SELECT * FROM TASK WHERE isSelected = 1 ORDER BY Category ASC";
            }else{
                //Otherwise, define a query to retrieve data from task table where isSelected is 1 and Category mateches the selected one
                sql = "SELECT * FROM TASK WHERE Category = " + currentCategory.getId() + " AND isSelected = 1";
            }//End of if else statement to check if All category has been selected
            //Call method to update the adapter and the recyclerView
            this.updateRecyclerViewData(sql);
        }//End of if else statement to check category is groceries
        Log.d("Ext_filterCheckedOnly","Exit the filterCheckedOnly method in the MainActivity class");
    }//End of filterCheckedOnly

    //Method to filter only the items that has been selected
    public void unfilterCheckedOnly(){
        Log.d("Ent_filterCheckedOnly","Enter the unfilterCheckedOnlyt method in the MainActivity class");
        String sql ="";

        //Check the current category object... Depending on if it is Groceries or any other the actions will change
        if (currentCategory.equals(this.findCategoryByName(groceryCategory))){
            //Define sql query to retrieve data from groceries table
            sql = "SELECT * FROM GROCERIES ORDER BY TypeOfGrocery ASC";
            //Get rid of fitler by type
            if(this.selectedTypes.size()>0){
                this.clearTypeFilter();
            }//End of if statement to clear the gorcery type filter

        }else{
            //Check if the All category has been selected
            if(currentCategory.equals(findCategoryByName(allCategory))){
                //Define query to retrieve data from the task table
                sql = "SELECT * FROM TASK ORDER BY Category ASC";
            }else{
                //Otherwise, define query to retrieve data from the Task table where category matches the current one
                sql ="SELECT * FROM TASK WHERE Category = " + currentCategory.getId();
            }//End of if else statement to check if All category has been selected
        }//End of if else statement to check category is groceries
        //Call method to update the adapter and the recyclerView
        this.updateRecyclerViewData(sql);
        Log.d("Ext_unfilterCheckedOnly","Exit the unfilterCheckedOnlyt method in the MainActivity class");
    }//End of filterCheckedOnly

    //Method to update MainActivity UI top menu (No  the action bar)
    public void updateTopMenuUI(){
        Log.d("Ent_updateUIMA","Enter updateTopeMenuUI in MainActivity class.");
        //Check current category and select the correct actions to update the top menu
        if(this.currentCategory.getName().toLowerCase().equals("groceries")){
            this.tvOnlyChecked.setText(R.string.checkedGrocery);
            this.imgHighlightFilter.setImageResource(R.drawable.filter_icon);
            this.tvHighlightFilter.setText(R.string.filterByType);
        }else{
            this.tvOnlyChecked.setText(R.string.checkedTask);
            this.imgHighlightFilter.setImageResource(R.drawable.done_icon);
            this.tvHighlightFilter.setText(R.string.markDone);
        }
        //Display the proper name
        this.tvCurrentList.setText(this.currentCategory.getName().toString());
        Log.d("Ext_updateUIMA","Exit updateTopeMenuUI in MainActivity class.");
    }//End of updateTopMenuUI method

    //Method to filter the grocery list by grocery type
    public void filterByType(){
        Log.d("Ent_filterByType","Enter filterByType method in the MainActivity class.");
        //Retrieve from DB the current list of grocery types
        final CharSequence[] types = new CharSequence[groceryTypes.size()];
        //For loop to populate the char-sequence array with the grocery type names coming from GroceryType objects constructed with data from DB
        for(int i=0;i<types.length;i++){
            //For each item in the groceryType list, extract name and save it in the string array
            types[i]= groceryTypes.get(i).getName();
        }//End of for loop
        if(this.cbOnlyChecked.isChecked()){
            this.cbOnlyChecked.setChecked(false);
        }//End of if statement to check the check box state
        //Set search boolean varible to false
        if(this.isSearchFilter){
            this.isSearchFilter = false;
        }//End of if statement to check isSearchFilter
        //Create a dialog box to display the grocery types
        new AlertDialog.Builder(MainActivity.this)
                .setTitle(R.string.filterByType)
                .setMultiChoiceItems(types, selectedTypesListPosition, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which,
                                        boolean isChecked) {
                        int groceryTypeID= findGroceryTypeByName(types[which].toString()).getId();
                        if (isChecked) {
                            if (!selectedTypes.contains(groceryTypeID)){
                                // If the user checked the item, add it to the selected items
                                selectedTypes.add(groceryTypeID);
                            }
                        } else if (selectedTypes.contains(groceryTypeID)) {
                            // Else, if the item is already in the array, remove it
                            selectedTypes.remove((Object)groceryTypeID);
                        }//End of if else estatements
                    }//End of onClick method
                })//End of setMultichoiceItems method
                .setPositiveButton("Ok",new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog,int whichButton){
                        //Declare and initialize a string variable to hold the sql query
                        String sql="";
                        //Check if at least one grocery type has been selected to filter
                        if (selectedTypes.size()>0){
                            //Call method to dynamically depending on the grocery types selected
                            sql=listGroceriesFiltered();
                            //Change the filter background colour to the accent color
                            tvHighlightFilter.setTextColor(getResources().getColor(R.color.colorAccent));
                        }else{
                            //Otherwise the full grocery list will be displayed
                            sql ="SELECT * FROM GROCERIES ORDER BY TypeOfGrocery ASC";
                            //Change the filter background colour to white
                            tvHighlightFilter.setTextColor(getResources().getColor(R.color.colorSecondayText));
                        }//End of if else statement to check there is one selection
                        //Update the database via for loop to iterate the selectedTypesListPositoin array
                        for(int i=0;i<selectedTypesListPosition.length;i++){
                            //Create a grocery type object to hold the data
                            GroceryType type = findGroceryTypeByName(types[i].toString());
                            //call the database method to update the GROCERY_TYPE table and set the IsInFilter attribute
                            db.updateIsInFilter(type.getId(),selectedTypesListPosition[i]);
                        }//End of for loop
                        //Call method to update the adapter and the recyclerView
                        updateRecyclerViewData(sql);
                    }//End of Onclick method
                })
                .setNegativeButton(R.string.cancel,null)
                .create()
                .show();
        Log.d("Ext_filterByType","Exit filterByType method in the MainActivity class.");
    }//End of filterByType method

    //Method to dynamically create a sql query based on the grocery type used to filter the list
    public String listGroceriesFiltered(){
        Log.d("Ent_listGrocFilt","Enter listGroceriesFiltered method in MainActivity class.");
        String sql = "";
        //Assign to sql string the query which will be constructed
        sql = "SELECT * FROM GROCERIES WHERE TypeOfGrocery IN (";
        //For loop to iterate through the list of selected grocery types and include its id in the sql query
        for(int i=0;i<this.selectedTypes.size();i++){
            //for each item in the list, concatenate its id to the sql string
            sql += (this.selectedTypes.get(i));
            //Check if the current item is the lastone in the list
            if(i+1 == this.selectedTypes.size()){
                //If it is the last one, exit the  loop
                break;
            }else{
                //Otherwise, append a comma and a blank space and continue iterating
                sql += ", ";
            }//End of if else statement
        }//End of for loop
        //Close the sql string by appending a closing round bracket
        sql+= " ) ORDER BY TypeOfGrocery ASC";
        Log.d("Ext_listGrocFilt","Exit listGroceriesFiltered method in MainActivity class.");
        return sql;
    }//End of listGroceriesFiltered

    //Method to populate the GroceryType lilst that holds the types selected in the filter menu
    public ArrayList findSelectedTypes(){
        Log.d("Ent_findSelectedTypes","Enter findSelectedTypes method in the MainActivity class.");
        //Declare and instantiate a new ArrayList
        ArrayList selectedItemsID = new ArrayList();
        //For loop to iterate through the groceryTypes list (which comes from onCreate method and gets data from DB)
        for(int i=0;i<groceryTypes.size();i++){
            //If the grocery has isInFilter attribute equal  to true, add the type into the selecteditems Array list
            if(groceryTypes.get(i).isInFilter()){
                selectedItemsID.add(groceryTypes.get(i).getId());
            }else{
                //Otherwise, check if the list contains the item already. In that case, remove it
                if(selectedItemsID.contains(groceryTypes.get(i).getId())){
                    //Remove the grocery type id from the selected list
                    selectedItemsID.remove(groceryTypes.get(i).getId());
                }//End of if statement
            }//End of if else statement
        }//End of for loop
        Log.d("Ext_findSelectedTypes","Exit findSelectedTypes method in the MainActivity class.");
        return selectedItemsID;
    }//End of findSelectedTypes method

    //Method to populate boolean array of selected grocery types
    public boolean[] findSelectedTypesPosition(){
        Log.d("Ent_findSelectTypesPos","Enter the findSelectedTypesPostion method in the MainActivity class.");
        //Declare and instantiate a new boolean array, which sie will be equal to the grocery type list
        boolean[] selectedItems = new boolean[groceryTypes.size()];
        //For loop to iterate through the grocery type object array list and assign the boolean value within each grocery type object
        for(int i=0;i<groceryTypes.size();i++){
            selectedItems[i] = groceryTypes.get(i).isInFilter();
        }//End of for loop
        Log.d("Ext_findSelectTypesPos","Ext the findSelectedTypesPostion method in the MainActivity class.");
        //Return the boolean array
        return selectedItems;
    }//End of findSelectedTypes method

    //Method to clear the grocery type filter selection
    public void clearTypeFilter(){
        Log.d("Ent_clearType","Enter clearTypeFilter method in the MainActivity class.");
        //Check the selecte type list is not empty
        if(this.selectedTypes.size()>0){
            //if it's not empty, declare a variable to hold the grocery type id
            int groceryTypeID;
            //For loop to iterate through the selected types list and update the current values of isInFilter attribute
            for(int i=0; i<this.selectedTypes.size();i++){
                //Extract the id form the list bu looking at the positon i in the list
                groceryTypeID = (int)selectedTypes.get(i);
                //Set the boolean value of position groceryTypeID to false
                this.selectedTypesListPosition[groceryTypeID-1] = false;
                //Update de DB for each type in the GroceryType table
                this.db.updateIsInFilter(groceryTypeID,this.selectedTypesListPosition[groceryTypeID-1]);
                //selectedTypes.remove(i);
            }//End of for loop
            //Clear the whole selectedTypes list
            this.selectedTypes.clear();
            //Remove accent color from Filter by type text
            this.tvHighlightFilter.setTextColor(getResources().getColor(R.color.colorSecondayText));
        } //End of if statement to check slected types list size
        Log.d("Ext_clearType","Exit clearTypeFilter method in the MainActivity class.");
    }//End of clearTypeFilter method#

    //Method to highlight as done the selected tasks
    private void highlightSelectedTask(){
        Log.d("Ent_highlightTask","Enter the highligtSelectedTask method in the MainActivity class.");
        //Get the selected tasks
        //Declare and instantiate as null a temporary cursor to hold a list of selected tasks to be highlighted
        Cursor tempCursor = null;
        String sql ="SELECT * FROM TASK WHERE IsSelected = 1";
        //Check the checkedOnly filter is active
        if(this.cbOnlyChecked.isChecked()){
            //Refer the temp cursor to the cursor populating the recycleView
            tempCursor = this.cursor;
        }else{
            //Check if search filter is being used so the data set returned only returns the last search filtering
            if(this.isSearchFilter){
                sql = lastSearchText[0];
            }else{
                //Check the current task category
                if(currentCategory.equals(findCategoryByName(allCategory))){
                    sql ="SELECT * FROM TASK";
                }else{
                    //To be tested!!!!
                    sql = "SELECT * FROM TASK WHERE Category = "+currentCategory.getId();
                }//End of if else statement to check the current category variable
            }
            //Run query to get only the checked tasks
            tempCursor = db.runQuery(sql);
        }//End of if else statement to check the checked only filter
        //Check the temp cursor is not empty, otherwise send an error message
        if(tempCursor != null && tempCursor.getCount()>0){
            //Declare and initialize a list of pair values to hold the task id and the IsSelected property when it's set to true
            SparseBooleanArray checkedItems = this.getCheckedItems(tempCursor);
            //Check the list is not empty, otherwise display an error message
            if(checkedItems.size() > 0){
                //Move the current cursor to the -1 position so the moveToNext method used below works properly
                tempCursor.moveToFirst();
                tempCursor.moveToPrevious();
                //For each task in the temp cursor
                while(tempCursor.moveToNext()){
                    //Update the isDone attribute in the DB
                    boolean isDone = false;
                    if(db.toBoolean(tempCursor.getInt(8))){
                        //Check the task is marked as not done
                        if(!db.toBoolean(tempCursor.getInt(4))){
                            //Change current attribute to opposite value
                            isDone = true;
                        }//End of if statement to check the task is done  (Column 4 in DB)
                    }//End of if statement to check the task is selected (Column 8 in DB)
                    db.updateBoolAttribute(currentCategory.getName().toString(),"IsDone",tempCursor.getInt(0),isDone);
                }//End of while loop to iterate through the temp cursor
                //Update the list of tasks
                updateRecyclerViewData(sql);
            }else{
                Toast.makeText(this,"No task selected to be highlighted.",Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this,"No task selected to be highlighted.",Toast.LENGTH_SHORT).show();
        }//End of if else statement to check the temp cursor is not empty
        Log.d("Ext_highlightTask","Exit the highligtSelectedTask method in the MainActivity class.");
    }//End of highlightSelectedTask method

    //Method to get a boolean list of checked tasks
    private SparseBooleanArray getCheckedItems(Cursor c){
        Log.d("EntgetChkItems","Enter getCheckedItems method in MainActivity class.");
        //Declare and instantiate the list of boolean values to be returned
        SparseBooleanArray checkedItems = new SparseBooleanArray();
        //Move the cursor to first position in case it has been used before
        c.moveToFirst();
        //Declare and initialize a boolean variable to hold the IsSelected property of each item
        boolean isChecked = false;
        //Do while loop to iterate through the cursor
        do{
            //Get the IsSelected property
            isChecked = db.toBoolean(c.getInt(8));
            //If it is set to true (1)
            if(isChecked){
                //Include in the list
                checkedItems.put(c.getInt(0),isChecked);
            }//End of if statement to check the isChecked variable
        }while(c.moveToNext());
        Log.d("ExtgetChkItems","Exit getCheckedItems method in MainActivity class.");
        return checkedItems;
    }//End of getCheckedItems method


    //Method to throw new AddTaskActivity
    private void throwAddTaskActivity(View view){
        Log.d("Ent_throwAddTask","Enter throwAddTaskActivity method in the MainActivity class.");
        //Declare and instantiate a new intent object
        Intent i= new Intent(MainActivity.this,AddTaskActivity.class);
        //Add extras to the intent object, specifically the current category where the add button was pressed from
        i.putExtra("category",this.currentCategory.toString());
        //Start the addTaskActivity class
        startActivity(i);
        Log.d("Ext_throwAddTask","Exit throwAddTaskActivity method in the MainActivity class.");
    }//End of throwAddTaskActivity method

    //Method to delete a task or a grocery
    private boolean delete(){
        Log.d("Ent_delete","Enter delete method in the MainActivity class.");
        //Declare and instantiate a boolean  varuable which will return if the delete method was successful or not
        boolean result = false;
        //Declare a cursor object to query the database and hold the data
        final Cursor c;
        //Declare a String object to hold the message to be displayed on the  dialog box that will prompt the user the tasks or groceries to be deleted
        String dialogMessage;
        //Check current property (is it groceries or any other?)
        if(currentCategory.equals(findCategoryByName(getGroceryCategory()))){
            //if the current category is the Groceries
            //Run query to filter all the selected items in the Groceries table
            c= db.runQuery("SELECT * FROM GROCERIES WHERE isSelected = 1 ORDER BY TypeOfGrocery ASC");
            //Initialize the dialogMessage variable.
            dialogMessage = "";
            //Check the cursor is not empty an d has more than one row
            if(c.getCount()>1){
                //Set the dialog box message to refer to several groceries
                dialogMessage = "Are you sure you want to delete the following groceries?: ";
                //Use a while loop to iterate through the cursor and obtain the name of each grocery selected to be deleted
                while(c.moveToNext()){
                    //Add the grocery name to the dialogMessage string
                    dialogMessage += "\n- "+c.getString(1);
                }//End of while loop
                //If the cursor has less than 2 row, check is not empty and has at least one row
            }else if(c != null && c.getCount()>0){
                    //Move to first position of cursor.
                    c.moveToFirst();
                    //Set the dialog box message to be singular and refer to only one grocery and add its name (extracted from cursor)
                    dialogMessage = "Are you sure you want to delete the following grocery: " +c.getString(1)+"?";
            }else{
                //If cursor did not comply previous conditions, means the cursor is empty or null. Display error message for that
                dialogMessage ="No grocery is selected to be deleted.";
            }//End of if else statement to check the number of groceries to be deleted
            //Display a warning message asking user if he/she wants to delete the selected items
            new AlertDialog.Builder(this)
                    .setTitle(R.string.deleteGroceryTitle)//Set title
                    .setMessage(dialogMessage)// Set the message as per previous dynamic selection
                    .setPositiveButton("Ok",new DialogInterface.OnClickListener(){//Define the positive button
                        public void onClick(DialogInterface dialog,int whichButton){
                            //Check the cursor is not empty and is not null
                            if(c != null && c.getCount()>0){
                                //Move cursor to first position
                                c.moveToFirst();
                                //do while loop to iterate through the cursor and delete each item selected
                                do {
                                    //Declare and instantiate a grocery object from data in the current row of cursor
                                    Grocery grocery = db.extractGrocery(c);
                                    //Call the deleteItem method and pass in the grocery object to be deleted
                                    boolean result = db.deleteItem(grocery);
                                }while(c.moveToNext());//End of while loop to delete the selected groceries
                                //Declare and instantiate as null a string object to hold the sql query to run. Depending on the current category, different query will be run
                                String sql="SELECT * FROM GROCERIES ORDER BY TypeOfGrocery ASC";
                                //Call method to update the adapter and the recyclerView
                                updateRecyclerViewData(sql);
                            }//End of if statement to check cursor is not null or empty
                        }//End of Onclick method
                    })//End of setPositiveButton method
                    .setNegativeButton(R.string.cancel,null)
                    .show();
            //Return true if method is successful
            result = true;
        }else{
            //For any other category
            //Declare and initialize an empty string object to hold the sql query that will be run to retrive data from database. Depending on current category different sql query will be required
            String sql ="";
            //Check if the current category is All tasks or a specific category has been selected
            if(currentCategory.equals(findCategoryByName(allCategory))){
                //Define sql query that includes all the categories
                sql = "SELECT * FROM TASK WHERE isSelected = 1 ORDER BY Category ASC" ;
            }else{
                //Define a sql query for a specific category. Category id comes from the current category object
                sql = "SELECT * FROM TASK WHERE isSelected = 1 AND Category = "+ currentCategory.getId();
            }//End of if else statement
            //Retrieve data from DB by running sql query defined above
            c= db.runQuery(sql);
            //Initialize the dialogMessage string to be empty
            dialogMessage = "";
            //Check the cursor is not empty an d has more than one row
            if(c.getCount()>1){
                //Set the dialog box message to refer to several tasks
                dialogMessage = "Are you sure you want to delete the following tasks?: ";
                //Use a while loop to iterate through the cursor and obtain the description of each task selected to be deleted
                while(c.moveToNext()){
                    dialogMessage += "\n- "+c.getString(1);
                }//End of while loop
                //If the cursor has less than 2 rows, check is not empty and has at least one row
            }else if(c != null && c.getCount()>0){
                //Move to first position of cursor.
                c.moveToFirst();
                //Set the dialog box message to be singular and refer to only one task and add its descriotion (extracted from cursor)
                dialogMessage = "Are you sure you want to delete the following task: " +c.getString(1)+"?";
            }else{
                //If cursor did not comply previous conditions, means the cursor is empty or null. Display error message for that
                dialogMessage ="No task is selected to be deleted.";
            }//End of if else statement to check the number of groceries to be deleted
            //Display a warning message asking user if he/she wants to delete the selected items
            new AlertDialog.Builder(this)
                    .setTitle(R.string.deleteTaskTitle)//Set title
                    .setMessage(dialogMessage)// Set the message as per previous dynamic selection
                    .setPositiveButton("Ok",new DialogInterface.OnClickListener(){//Define the positive button
                        public void onClick(DialogInterface dialog,int whichButton){
                            //Check the cursor is not empty and is not null
                            if(c != null && c.getCount()>0){
                                //Move cursor to first position
                                c.moveToFirst();
                                //do while loop to iterate through the cursor and delete each item selected
                                do {
                                    //Declare and instantiate a task object from data in the current row of cursor
                                    Task task= db.extractTask(c);
                                    //Call the deleteItem method and pass in the grocery object to be deleted
                                    boolean result = db.deleteItem(task);
                                }while(c.moveToNext());//End of while loop to delete the selected groceries
                                //Declare and instantiate as null a string object to hold the sql query to run. Depending on the current category, different query will be run
                                String sql="";
                                //Check the current category to delete the tasks from
                                if(currentCategory.equals(findCategoryByName(allCategory))){
                                    //Define a sql for all the task categories
                                    sql = "SELECT * FROM TASK ORDER BY Category ASC";
                                }else{
                                    //Define a sql query for specific category, coming from the current category id
                                    sql = "SELECT * FROM TASK WHERE Category = "+currentCategory.getId()+ "ORDER BY Category ASC";
                                }
                                //Call method to update the adapter and the recyclerView
                                updateRecyclerViewData(sql);
                            }//End of if statement to check cursor is not null or empty
                        }//End of Onclick method
                    })//End of setPositiveButton method
                    .setNegativeButton(R.string.cancel,null)
                    .show();
        }
        Log.d("Ext_delete","Exit delete method in the MainActivity class.");
        //Return result whihc should be true if the whole method was completed
        return result;
    }// End of delete method

    //Method to retrieve the entire list of tasks or groceries when the list icon is clicked on
    private void getFullListNoFilter(){
        Log.d("Ent_getFullListNF","Enter getFullListNoFilter method in the MainActivity class.");
        //Clear all the generic filters
        if(this.cbOnlyChecked.isChecked()){
            this.cbOnlyChecked.setChecked(false);
        }else{
            if(this.isSearchFilter){
                this.isSearchFilter = false;
            }//End of if statement to check the isSearchFilter applied
            //Run the query and update the recyclerView
            this.unfilterCheckedOnly();
        }//End of if that checks the only checked filter
        Log.d("Ext_getFullListNF","Exit getFullListNoFilter method in the MainActivity class.");
    }//End of getFullList method

    //Method to get full list of tasks or groceries
    private void getFullList(){
        Log.d("Ent_getFullList","Enter getFullList method in the MainActivity class.");
        String sql ="";
        //Check the current category variable
        if (currentCategory.equals(this.findCategoryByName(groceryCategory))){
            //Define sql query to retrieve data from groceries table
            sql = "SELECT * FROM GROCERIES ORDER BY TypeOfGrocery ASC";
        }else{
            //Check if the All category has been selected
            if(currentCategory.equals(findCategoryByName(allCategory))){
                //Define query to retrieve data from the task table
                sql = "SELECT * FROM TASK ORDER BY Category ASC";
            }else{
                //Otherwise, define query to retrieve data from the Task table where category matches the current one
                sql ="SELECT * FROM TASK WHERE Category = " + currentCategory.getId();
            }//End of if else statement to check if All category has been selected
        }//End of if else statement to check category is groceries
        //Call method to update the adapter and the recyclerView
        this.updateRecyclerViewData(sql);
        Log.d("Ext_getFullList","Exit getFullList method in the MainActivity class.");
    }

    public static String getGroceryCategory(){
        return groceryCategory;
    }

    public static String getAllCategory(){
        return allCategory;
    }

    public static String getDateFormat(){
        return MainActivity.dateFormat;
    }

    public static void setDateFormat(String dateFormat){
        MainActivity.dateFormat = dateFormat;
    }

    public static Category getCurrentCategory(){return MainActivity.currentCategory;}

    public static ArrayList getSelectedTypes(){return MainActivity.selectedTypes;}

    public static boolean[] getSelectedTypesListPosition(){return MainActivity.selectedTypesListPosition;}

    public static int getHighlightColor(){return highlightColor;}

    public static int getPrimaryTextColor(){return primaryTextColor;}

    public static String getDoneColor(){return doneColor;}

    public static String getDoneHighlighter(){return doneHighlighter;}

    public static  String getWhiteBackground(){return whiteBackground;}

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }//End of if else statement
    }// End of onBackPressed method
}//End of the MainActivity class
