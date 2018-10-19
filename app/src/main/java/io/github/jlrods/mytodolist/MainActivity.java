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
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.EventLogTags;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.SubMenu;
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
    private static NavigationView navigationView;
    private static DrawerLayout drawer;
    private boolean isSearchFilter = false;
    private String[] lastSearchText ={"",""};
    private static int highlightColor = R.color.colorAccent;
    private static int primaryTextColor = R.color.colorPrimaryText;
    private static String doneColor ="green";
    private static String doneHighlighter = "#FF4081";
    private static String whiteBackground ="#FAFAFA";
    private static String dateFormat ="MMM dd yyyy";
    private static int indexToGetLastTaskListItem =2;
    //Constants
    private static final String groceryCategory = "Groceries";
    private static final String allCategory ="All";



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
                    String sql= getSQLForRecyclerView();
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
        //Populate the list of selected grocery types
        selectedTypesListPosition = this.findSelectedTypesPosition();
        //Tool bar creation and functionality set up
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        /*ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.list_icon);*/

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
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        this.updateNavMenu(this.navigationView.getMenu());
        navigationView.setNavigationItemSelectedListener(this);
        Log.d("Ent_onCreateMain","Enter onCreate method in MainActivity class.");
    }//End of onCreate Method

    //Method to return sql string to be used to update the REcyclerViewer object
    private String getSQLForRecyclerView(){
        Log.d("Ent_getSQLRecView","Enter getSQLForRecyclerView method in MainActivity class.");
        //Declare and initialize a String to be returned by this method
        String sql="";
        //Check if current category is Groceries and the type filter is applied (selectedTypes list is not empty)
        if(currentCategory.equals(findCategoryByName(groceryCategory)) && selectedTypes.size()>0){
            //If that is the case, call the method that returns sql to list the groceries to be filtered
            sql = listGroceriesFiltered();
        }else{
            //If that is not the case, declare and initialize strings to dynamically build final sql
            String select = "SELECT * FROM ";
            String table =" TASK ";
            String where = " WHERE ";
            String condition1 = ""; //To be used for the search filter
            String and =" AND ";
            String condition2 ="";// To be used for the only checked items filter
            String condition3 = "";// To be used for specific categories other that all and groceries categories
            String orderBy = " ORDER BY ";
            String column1 = " Description ";
            String column2 = " Category ";
            String direction =" ASC";
            int i =0;// inteer to define position to search text in the lastSearchText String array
            //Check if current category is the groceries category
            if(currentCategory.equals(findCategoryByName(groceryCategory))){
                //If that is the case, do some changes for this specific category
                table = "GROCERIES";
                i=1;
                column1 = "Name";
                column2 = "TypeOfGrocery";
                //If it isn't groceries category, check it is not the All category
            }else if(!currentCategory.equals(findCategoryByName(allCategory))){
                //If that is the case, condition 3 must match the current category id
                condition3 = column2+" = "+currentCategory.getId();
            }//End of if else statements to check the current category
            //Check if the search filter has been applied
            if(isSearchFilter){
                //If it's applied, condition 1 mus include the last search text
                condition1 = column1+" LIKE '%"+lastSearchText[i];
            }//End of if statement to check the search filter
            //Check the only checked items filter is applied
            if(cbOnlyChecked.isChecked()){
                //if it is applied, the condition2 must indicate the isSelected property set to 1
                condition2 +=  " IsSelected = 1 ";
            }//End of if statement to check the only checked items
            //Logic to build the sql query dynamically based on the different category and filters
            //Check the three conditions are empty
            if(condition1.equals("")&& condition2.equals("") && condition3.equals("")){
                //If they are empty, the sql must not contain any condition in it
                sql = select+table+orderBy+column2+direction;
            }else{
                //Otherwise, check the differnt combinations of conditions
                //If the third condition is blank, means it is not a specific task (It is either All or Groceries)
                if(condition3.equals("")){
                    //column2 = "_id";
                    //Check the condition 1 is not empty
                    if(!condition1.equals("")){
                        //In that case, check if condition2 is not empty either, which means more than one condition is required
                        //As per current code, this option will never be used, however is left here intentionally in case is needed in future
                        if(!condition2.equals("")){
                            sql = select+table+where+condition1+and+condition2+orderBy+column2+direction;
                        }else{
                            //if it doesn't have the and word, means only the condition 1 is applied
                            sql = select+table+where+condition1+orderBy+column2+direction;
                        }//End of if else statement to check condition1 is not empty and condition2 is not empty
                        //If condition1 is blank, check the condition2 is not empty
                    }else if(!condition2.equals("")){
                        //If the condition1 is blank, means no search filter is applied and the checked only items is the only filter applied
                        sql = select+table+where+condition2+orderBy+column2+direction;
                    }/*else{
                        //If condition2 is empty too, no co
                        sql = select+table+where+condition3;
                    }*/
                    //If condition3 is not empty, a specific category filter must be required
                }else{
                    //Check condition1 is not empty (at least two conditions required)
                    if(!condition1.equals("")){
                        //Include condition1 and condition3 in the sql query
                        if(!condition2.equals("")){
                            //SQL for three filters applied
                            sql = select+table+condition1+and+condition2+and+condition3+orderBy+column2+direction;
                        }else{
                            //otherwise, only condition 1 and 3 applied
                            sql = select+table+where+condition1+and+condition3+orderBy+column2+direction;
                        }//End of if else statement to chec condition 2 is empty
                        //If condition1 is empty, check condition2
                    }else if(!condition2.equals("")) {
                        //This means only condition 2 and 3 will be required in the sql query
                        sql = select+table+where+condition2+and+condition3+orderBy+column2+direction;
                    }else{
                        //Finally, query if only condition 3 is required (specific category filter)
                        sql = select+table+where+condition3+orderBy+column2+direction;
                    }//End of if else statement to check condition1 and 2 are not empty
                }//End of if else statement to check condition3 is empty
            }//End if else statement to check three conditions are empty
        }//End if else statement to check current category is groceries and filter type is applied
        Log.d("Ext_getSQLRecView","Exit getSQLForRecyclerView method in MainActivity class.");
        return sql;
    }//End of getSQLForRecyclerView method

    //Method to update the Nav Menu items when new task list are created or deleted. Used to populate the menu on onCreate method too
    public static void updateNavMenu(Menu navMenu){
        Log.d("Ent_UpdateNaveMenu","Enter the updateNavMenu method in MainActivity class.");
        //Declare and initialize a string to get category list from DB
        String sql = "";
        if(navMenu.size()>indexToGetLastTaskListItem+1){
            //Initialize a string to get the Category with MAX id from category list (The last category added into DB)
            sql = "SELECT * FROM CATEGORY  WHERE _id= (SELECT MAX(_id) FROM CATEGORY)";

        }else{
            //Initialize a string to get category list from DB that does not include All and Groceries
            sql = "SELECT * FROM CATEGORY WHERE _id NOT IN("+findCategoryByName(allCategory).getId()+", "+findCategoryByName(groceryCategory).getId()+")";
            //Make the All Category the default selected item
            navMenu.getItem(0).setChecked(true);
        }
        //Declare and initialize a cursor object to retrieve the list task categories in the DB
        Cursor c = db.runQuery(sql);
        int order =0;
        //Last menu item
        //MenuItem lastItem = navMenu.getItem(navMenu.size()-1);
        //While loop to iterate through the cursor and include the item in the Task list menu
        while(c.moveToNext()){
            order = navMenu.getItem(navMenu.size()-indexToGetLastTaskListItem).getOrder()+1;
            //MenuItem previousItem = navMenu.getItem(navMenu.size()-1)
            navMenu.add(R.id.categoryListMenu,c.getInt(0),order,c.getString(1));
            MenuItem newItem = navMenu.getItem(navMenu.size()-indexToGetLastTaskListItem);
            newItem.setIcon(R.drawable.list_icon);
        }//End of while loop
        Log.d("Ext_UpdateNaveMenu","Exit the updateNavMenu method in MainActivity class.");
    }//End of updateNavMenu method

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }//End of onCreaeOptionsMenu method

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
            //Update the top menu text and images
            this.updateTopMenuUI();
            drawer.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_grocery) {
            // Handle the the groceries task category list menu option
            if(this.cbOnlyChecked.isChecked()){
                this.cbOnlyChecked.setChecked(false);
            }//End of if statement to check the check box state
            //Set the current category to be Groceries
            this.currentCategory=findCategoryByName(groceryCategory);
            //Declare and initialize a string to hold the sql query to update the cursor
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
                        String sql=getSQLForRecyclerView();
                        //Update database with the isSelected attribute of current grocery which checkbox was toggled
                        db.updateBoolAttribute(currentCategory.getName().toString(),"IsSelected",grocery.getId(),isChecked);
                        //Call method to update the adapter and the recyclerView
                        updateRecyclerViewData(sql);
                    }// End of if statement to check the current task actually changed isSelected stated (otherwise is the recyclerview recycling a  View)
                }//End of onCheckedChanged method
            });//End of setOnItemCheckedChangeListner
            //Update the top menu text and images
            this.updateTopMenuUI();
            drawer.closeDrawer(GravityCompat.START);
        //Check if the Add list item was selected
        } else if (id == R.id.nav_addList) {
            //Declare and instantiate a new EditText object
            final EditText input= new EditText(this);
            //Display a Dialog to ask for the List name (New Category)
            new AlertDialog.Builder(this)
                    .setTitle(R.string.addTaskList)//Set title
                    .setMessage(R.string.addTaskListExp)// Set the message that clarifyes the requested action
                    .setView(input)
                    .setPositiveButton("Ok",new DialogInterface.OnClickListener(){//Define the positive button
                        public void onClick(DialogInterface dialog,int whichButton){
                            //Read from the user input the list name
                            String newListName = input.getText().toString();
                            //Check the new list's name is not empty
                            if(newListName.equals("")){
                                //Display error message via toast
                                Toast.makeText(MainActivity.this,R.string.listNameEmpty,Toast.LENGTH_SHORT).show();
                            }else{
                                //Check the name is not already in the categories list
                                Cursor c = db.runQuery("SELECT * FROM CATEGORY");
                                boolean found = false;

                                while(c.moveToNext() && !found){
                                    if(newListName.toLowerCase().equals(c.getString(1).toLowerCase())){
                                        found = true;
                                    }//End of if statement to check the category name is not in the current category table in DB
                                }//End of while loop to iterate through category list
                                //Check the boolean flag
                                if(found){
                                   //Display error message that reads the name is already in use
                                    Toast.makeText(MainActivity.this,R.string.listNameDuplicate,Toast.LENGTH_SHORT).show();
                                }else{
                                    //Include the new category in the DB
                                    c = db.runQuery("SELECT MAX(_id) FROM CATEGORY");
                                    int temId = -1;
                                    int actualId = -1;
                                    if(c.moveToNext()) {
                                        temId = c.getInt(0);
                                        Category newList = new Category(temId,newListName);
                                        actualId = db.addItem(newList);
                                    }
                                    if(actualId ==-1){
                                        //Display error message
                                        Toast.makeText(MainActivity.this,R.string.addListFail,Toast.LENGTH_SHORT).show();
                                    }else{
                                        //Update the list of categories in the current MainActivity instance
                                        MainActivity.categories = db.getCategoryList();
                                        //Include the new list in the Navigation menu
                                        MainActivity.updateNavMenu(MainActivity.getNavigationView().getMenu());
                                        //Select the new MenuItem and displayed as selected
                                        navigationView.getMenu().getItem(navigationView.getMenu().size()-indexToGetLastTaskListItem).setCheckable(true).setChecked(true);
                                        Toast.makeText(MainActivity.this,R.string.addListSuccess,Toast.LENGTH_SHORT).show();
                                    }//End of if else statement that check retrieved id is not -1
                                }//End of if else statement to check the new list name was not found in DB already
                            }//End of if else statement to check the new list name is not empty
                        }//End of Onclick method
                    })//End of setPositiveButton method
                    .setNegativeButton(R.string.cancel,null)
                    .show();
        //Check if the delete  list icon was selected
        } else if (id == R.id.nav_deleteList) {
            //Get list of task lists where the all and grocery lists are not included
            Cursor c = db.runQuery("SELECT * FROM CATEGORY WHERE _id NOT IN ("+findCategoryByName(allCategory).getId()+", "+findCategoryByName(groceryCategory).getId()+")");
            //Check the cursor is not empty
            if(c.moveToNext()){
                //Declare and initialize two variables to be used in the AlertDialog with MultiChoice input
                //One to hold a the task lists or category names
                final CharSequence[] taskList = new CharSequence[c.getCount()];
                //another one to hold the isChecked attribute
                final boolean[] deletableCategories = new boolean[c.getCount()];
                //For loop to populate the char-sequence array with the category names coming from cursor objects constructed with data from DB
                for(int i=0;i<taskList.length;i++){
                    //For each item in the groceryType list, extract name and save it in the string array
                    //Get the name from the cursor
                    String listName = c.getString(1);
                    //Save the name into the array to be passed into the AlertDialog constructor
                    taskList[i]= listName;
                    //Set the isChecked to false for all the categories
                    deletableCategories[i]= false;
                    //Move to next item
                    c.moveToNext();
                }//End of for loop to populate the taskList array
                //Create a dialog box to display the grocery types
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(R.string.delTaskList)
                        .setMultiChoiceItems(taskList, deletableCategories, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which,
                                                boolean isChecked) {
                                //When a list is selected, save it in the boolean array
                                deletableCategories[which] = isChecked;
                            }//End of onClick method
                        })//End of setMultichoiceItems method)
                        .setPositiveButton("Ok",new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog,int whichButton){
                                //Declare boolean flag to check if list with items to delete is empty or not
                                boolean notEmpty = false;
                                //Check the taskList is not empty
                                if(taskList.length>0){
                                    //Declare and initialize an empty array list to hold the categories to be deleted
                                    final ArrayList<Category> categoriesToBeDeleted= new ArrayList<Category>();
                                    //If not empty  get the name of list to be deleted
                                    for(int i=0;i<taskList.length;i++){
                                        //Check the category was selected to be deleted
                                        if(deletableCategories[i]){
                                            Category category = findCategoryByName(taskList[i].toString());
                                            categoriesToBeDeleted.add(category);
                                            //positionsToBeDeleted.add(i+indexToGetLastTaskListItem);
                                            notEmpty = true;
                                        }///End of for loop to go through the deletableTasks list
                                    }//End of for loop to iterate through the list of Categories
                                    //Check at least one list was selected for deletion, otherwise display an error message
                                    if(notEmpty){
                                        //Declare and instantiate a string object to dynamically include the names of lists to be deleted in message
                                        String deleteConfirmationMessage = "Are you sure you want to delete the following Task List";
                                        if(categoriesToBeDeleted.size()>1){
                                            //Make the text plural if more than one category will be deleted
                                            deleteConfirmationMessage += "s: \n\t* ";
                                        }else{
                                            //Make the text singular if only one category will be deleted
                                            deleteConfirmationMessage += ": \n\t* ";
                                        }//End of if else statement fo selected the proper warning message to display
                                        //For loop to go through the list of categories to be deleted and add every list's name into the warning message
                                        for(int i=0;i<categoriesToBeDeleted.size();i++){
                                            //Add the current list name to the text
                                            deleteConfirmationMessage += categoriesToBeDeleted.get(i).getName();
                                            //Check this is not the last item in the list
                                            if(i+1<categoriesToBeDeleted.size()){
                                                //If it is not the last one, add an extra line and bullet
                                                deleteConfirmationMessage += "\n\t* ";
                                            }//End of if statement to check if it's the last one item in the list
                                        }//End of for loop to include the list names to be deleted
                                        //Display a final warning message summarizing  all the lists to be deleted and informing all the tasks in that lis will be deleted
                                        new AlertDialog.Builder(MainActivity.this)
                                                .setTitle(R.string.deleteTaskList)
                                                .setMessage(deleteConfirmationMessage)
                                                .setPositiveButton("Ok",new DialogInterface.OnClickListener(){
                                                    public void onClick(DialogInterface dialog,int whichButton){
                                                        //If clicked Ok, delete the tasks within the selected category
                                                        //Declare and instantiate a string to construct dynamically sql to look for all the tasks in the categories to be deleted
                                                        String sql ="SELECT * FROM TASK WHERE Category IN (";
                                                        //For loop to construct sql dynamically
                                                        for(int i=0;i<categoriesToBeDeleted.size();i++){
                                                            //Add current category id to the sql text
                                                            sql+= categoriesToBeDeleted.get(i).getId();
                                                            //Check this is not the last item in list
                                                            if(i+1<categoriesToBeDeleted.size()){
                                                                //if it is not the last one, add a comma
                                                                sql+=",";
                                                            }else{
                                                                //Otherwise, close the sql text with a closing bracket
                                                                sql+=")";
                                                            }//End of if else statement to check the last item in list
                                                        }//End of for loop to construct sql
                                                        //Run the sql query and retrieve the tasks
                                                        Cursor tasksToDelete = db.runQuery(sql);
                                                        //Delete all tasks in the selected categories from the Task table in DB
                                                        while (tasksToDelete.moveToNext()){
                                                            Task task = db.extractTask(tasksToDelete);
                                                            db.deleteItem(task);
                                                            Log.d("TaskDeleted","The task with _id "+task.getId()+" has been deleted from then TASK table in the DB.");
                                                        }//End of while loop
                                                        //For loop to iterate through the list of categories to be deleted and execute the removal on DB
                                                        for(int i=0;i<categoriesToBeDeleted.size();i++){
                                                            //Delete the category from the CATEGORY table in DB
                                                            db.deleteItem(categoriesToBeDeleted.get(i));
                                                            //Remove the category from the Navigation Menu
                                                            navigationView.getMenu().removeItem(categoriesToBeDeleted.get(i).getId());
                                                            categories.remove(categoriesToBeDeleted.get(i));
                                                            //Select the All category by default + Update RecyclerViewer in the background???
                                                        }//End of for loop to delete the selected categories
                                                        //Move the item selection to the All list and refresh the RecyclerView in background
                                                        navigationView.getMenu().getItem(0).setChecked(true);
                                                        currentCategory = findCategoryByName(getAllCategory());
                                                        updateRecyclerViewData(getSQLForRecyclerView());
                                                    }//End of Onclick method
                                                })//End of setPossitiveButton method
                                                .setNegativeButton(R.string.cancel,null)
                                                .show();
                                    }else{
                                        Toast.makeText(MainActivity.this,"Error, no task list selected",Toast.LENGTH_SHORT).show();
                                    }// End of if else statement to check the list of categories is not empty
                                }else{
                                    //Display an error message
                                    Toast.makeText(MainActivity.this,"Error,No task available for deletion ",Toast.LENGTH_SHORT).show();
                                }
                            }//End of Onclick method
                        })
                        .setNegativeButton(R.string.cancel,null)
                        .create()
                        .show();
            }//End of if statement to check the cursor is not empty
        }else{
            //Any other menu item added programmatically will fall in this section
            //Get the item id, which matches the Category _id attribute in the DB
            String sql ="SELECT * FROM CATEGORY WHERE _id NOT IN ("+findCategoryByName(allCategory).getId()+", "+findCategoryByName(groceryCategory).getId()+")";
            Cursor c = db.runQuery(sql);
            boolean found = false;
            while(c.moveToNext() && !found ){
                if(id == c.getInt(0)){
                    //Set the current category to be All
                    this.currentCategory = findCategoryById(id);
                    //Check if global checkbox state
                    if(this.cbOnlyChecked.isChecked()){
                        this.cbOnlyChecked.setChecked(false);
                    }//End of if statement to check the check box state
                    //Change background in case previous list was the groceries list (selectedTypes size >0)
                    tvHighlightFilter.setTextColor(getResources().getColor(R.color.colorSecondayText));
                    this.updateTopMenuUI();
                    //MenuItem item = navigationView.getMenu().getItem(id);
                    item.setCheckable(true);
                    item.setChecked(true);
                    this.recyclerView.setAdapter(taskAdapter);
                    //Call method to update the RecyclerView data set and update ui
                    this.updateRecyclerViewData("SELECT * FROM TASK WHERE Category = "+id+" ORDER BY _id");
                    found = true;
                }//End of if statement to extract data from cursor
            }//End of while loop to iterate through the cursor
            drawer.closeDrawer(GravityCompat.START);
            /*DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);*/
        }//End of if else statement chain to check menu option that has been selected

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
                            //lastSearchText[1] = sql+input.getText().toString()+"%'";
                            lastSearchText[1] = input.getText().toString()+"%'";
                            //Call method to update the adapter and the recyclerView
                            updateRecyclerViewData(sql+lastSearchText[1]);
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
                            //lastSearchText[0] = sql;
                            lastSearchText[0] = input.getText().toString()+"%'";
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
        //Clear selection of grocery type filters
        this.clearTypeFilter();
        //Check the current category object... Depending on if it is Groceries or any other the actions will change
        String sql = this.getSQLForRecyclerView();
        this.updateRecyclerViewData(sql);
        Log.d("Ext_filterCheckedOnly","Exit the filterCheckedOnly method in the MainActivity class");
    }//End of filterCheckedOnly

    //Method to filter only the items that has been selected
    public void unfilterCheckedOnly(){
        Log.d("Ent_filterCheckedOnly","Enter the unfilterCheckedOnlyt method in the MainActivity class");
        //Call method to get sql based on current filters and method to update the adapter and the recyclerView
        String sql =this.getSQLForRecyclerView();
        this.updateRecyclerViewData(sql);
        Log.d("Ext_unfilterCheckedOnly","Exit the unfilterCheckedOnly method in the MainActivity class");
    }//End of filterCheckedOnly

    //Method to update MainActivity UI top menu (No  the action bar)
    public void updateTopMenuUI(){
        Log.d("Ent_updateUIMA","Enter updateTopeMenuUI in MainActivity class.");
        String currentCategoryName = this.currentCategory.getName();
        //Check current category and select the correct actions to update the top menu
        if(currentCategoryName.toLowerCase().equals(groceryCategory.toLowerCase())){
            this.tvOnlyChecked.setText(R.string.checkedGrocery);
            this.imgCurrentList.setImageResource(R.drawable.groceries_icon);
            this.imgHighlightFilter.setImageResource(R.drawable.filter_icon);
            this.tvHighlightFilter.setText(R.string.filterByType);
        }else{
            this.tvOnlyChecked.setText(R.string.checkedTask);
            this.imgHighlightFilter.setImageResource(R.drawable.done_icon);
            this.tvHighlightFilter.setText(R.string.markDone);
            if(currentCategoryName.toLowerCase().equals(allCategory.toLowerCase())){
                this.imgCurrentList.setImageResource(R.drawable.all_icon);
            }else{
                this.imgCurrentList.setImageResource(R.drawable.list_icon);
            }
        }//End of if else statement to check the current category name
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
                        }//End of if else statements
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
        //Get the proper sql query based on the different filters applied and the category selected
        String sql =this.getSQLForRecyclerView();
        //Check the checkedOnly filter is active
        if(this.cbOnlyChecked.isChecked()){
            //Refer the temp cursor to the cursor populating the recycleView
            tempCursor = this.cursor;
        }else{
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
                        db.updateBoolAttribute(currentCategory.getName().toString(),"IsDone",tempCursor.getInt(0),isDone);
                    }//End of if statement to check the task is selected (Column 8 in DB)
                }//End of while loop to iterate through the temp cursor
                //Update the list of tasks
                updateRecyclerViewData(sql);
            }else{
                Toast.makeText(this,"No task selected to be highlighted.",Toast.LENGTH_SHORT).show();
            }//End of if else statement to check there are checked items
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

    public static NavigationView getNavigationView(){return  navigationView;}

    public static DrawerLayout getDrawer(){return drawer;}

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
