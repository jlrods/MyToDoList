package io.github.jlrods.mytodolist;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    //Define global variables and constants
    //Declare a TaskDB object to create the database and manage different db operations
    public static TasksDB db;
    //A cursor object to hold data retrieved from database queries
    private static Cursor cursor;
    //Declare globals list to hold the current task objects
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
    private static String doneColor;
    private static String doneHighlighter ;
    private static String whiteBackground;
    private static String dateFormat;
    private static boolean isArchivedSelected = false;
    private enum sortOrientation {DESC,ASC}
    private sortOrientation orientation = sortOrientation.DESC;
    private ColorStateList colorStateList1;
    private static boolean dateFormatChanged = false;
    //Constants
    private static int INDEX_TO_GET_LAST_TASK_LIST_ITEM = 3;
    private static final String groceryCategory = "Groceries";//Const Name should go in capital letters
    private static final String allCategory ="All"; //Const Name should go in capital letters
    private static final int MAX_TASK_LIST_NAME = 11;
    private static final int CAMERA_ACCESS_REQUEST = 0;
    private static final int GALLERY_ACCESS_REQUEST = 0;
    final static int RESULT_PROFILE_IMAGE_CAMERA= 0;
    final static int RESULT_PROFILE_IMAGE_GALLERY= 1;
    private Uri uriProfileImage;
    private ImageView imgUserProfile;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Get default current app theme from preferences
        int appThemeSelected = setAppTheme(this);
        //Set the theme by passing theme id number coming from preferences
        setTheme(appThemeSelected);
        //Call  parent onCreate method
        super.onCreate(savedInstanceState);
        Log.d("Ent_onCreateMain","Enter onCreate method in MainActivity class.");
        //Call method to setup language based on app preferences
        this.setAppLanguage();
        //Instantiate the database manager object
        this.db = new TasksDB(this);
        //Populate the list of Categories
        this.categories = db.getCategoryList();
        //Populate the list of Grocery types
        this.groceryTypes = db.getGroceryTypeList();
        //Set layout for main activity
        setContentView(R.layout.activity_main);
        //Set proper color for the divider line on the tabMenu
        //Find the view on the layout
        LinearLayout tabMenu = findViewById(R.id.tabMenu);
        //Check the version number as setBackground method only works for Jelly Bean and onwards
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            //Check the app theme selected, coming from the shared references is the Red-Green theme
            if(appThemeSelected == R.style.AppTheme1){
                //Set the divider line color to red
                tabMenu.setBackground(getResources().getDrawable(R.drawable.topmenu_linear_layout_background1));
                //Check the app theme selected, coming from the shared references is the Green theme
            }else if(appThemeSelected == R.style.AppTheme2){
                //Set the divider line color to green
                tabMenu.setBackground(getResources().getDrawable(R.drawable.topmenu_linear_layout_background2));
            }else{
                //In case the default theme is selected, set the divider line colour to pink
                tabMenu.setBackground(getResources().getDrawable(R.drawable.topmenu_linear_layout_background));
            }//End of if else statement to check the app theme selected in preferences
        }//End of if statement to check the sw version

        //Set the white background color as per Resources
        whiteBackground = getResources().getString(R.color.whiteBackground);



        //Set the date format as per Shared preferences by calling setDateFormat method
        this.setDateFormat();
        //Find the checkBox in the layout and set the onCheckedChangeListener handler
        this.cbOnlyChecked = this.findViewById(R.id.cbOnlyChecked);

        //RecycleView settings
        //Set app state variables
        Cursor appState = db.runQuery("SELECT * FROM APP");
        this.recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        boolean isChecked =false;
        int categoryMenuItemId=0;

        //Update App state variables by extracting data from DB
        if(appState.moveToNext()){
            this.currentCategory = findCategoryById(appState.getInt(1));
            this.isSearchFilter = db.toBoolean(appState.getInt(2));
            isChecked = db.toBoolean(appState.getInt(3));
            this.cbOnlyChecked.setChecked(isChecked);
            this.lastSearchText[0] = appState.getString(4);
            this.lastSearchText[1] = appState.getString(5);
        }//End of if statement to check the appState cursor is not empty

        //Populate selected grocery types array list
        selectedTypes = this.findSelectedTypes();
        //Populate the list of selected grocery types
        selectedTypesListPosition = this.findSelectedTypesPosition();

        //Instantiate variables to handle the MainActivity UI icons (List selected, actions: highlight, filter)
        this.tvCurrentList = this.findViewById(R.id.tvCurrentList);
        this.tvCurrentList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check the Archived task list is not selected, if it is no action will be performed
                if(!isArchivedSelected){
                    getFullListNoFilter();
                }//End of if statement
            }
        });//End of setOnClickListener method
        this.imgCurrentList = this.findViewById(R.id.imgListIcon);
        this.imgCurrentList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check the Archived task list is not selected, if it is no action will be performed
                if(!isArchivedSelected){
                    getFullListNoFilter();
                }//End of if statement
            }
        });//End of setOnClickListener method
        this.tvOnlyChecked = this.findViewById(R.id.tvOnlyChecked);
        this.tvHighlightFilter = this.findViewById(R.id.tvHighlightFilter);
        this.imgHighlightFilter = this.findViewById(R.id.imgHighlightFilter);
        this.imgHighlightFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check the Archived task list is not selected, if it is call the method for sorting archived tasks
                if(isArchivedSelected){
                    //Call method to sort archived task
                    sortArchivedTasks();
                }else {
                    //Otherwise, check the current category matches the Groceries category
                    if (!currentCategory.getName().equals(groceryCategory)) {
                        //Call method to highlight the selected tasks
                        highlightSelectedTask();
                    } else {
                        //Call method to filter by grocery type
                        filterByType();
                    }//End of if else statement to check the current category
                }//End of if else statement to check the isArchivedSelected state
            }//End of onClick method
        });//End of setOnclickListener method
        this.tvHighlightFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check the Archived task list is not selected, if it is call the method for sorting archived tasks
                if(isArchivedSelected){
                    //Call method to sort archived task
                    sortArchivedTasks();
                }else {
                    //Check the current category matches the Groceries category
                    if (!currentCategory.getName().equals(groceryCategory)) {
                        //Call method to highlight the selected tasks
                        highlightSelectedTask();
                    } else {
                        //Call method to filter by grocery type
                        filterByType();
                    }//End of if else statement to check the current category
                }//End of if else statement to check the isArchivedSelected state
            }
        });// End of setOnClickListener method

        //Check if current category is the Groceries cat  egory (this one requires different treatment)
        if(currentCategory.equals(findCategoryByName(groceryCategory))){
            //If it tis the groceries category, first save the id to select the correct item in the Nav menu
            categoryMenuItemId = R.id.nav_grocery;
            //Call method that will set up the grocery adapter into the recyclerView
            this.setGroceryAdapter();
        }else{
            //Otherwise, call method that will set up the task adapter into the recyclerView
            this.setTaskAdapter();
            //Check the current category is the All category
            if(currentCategory.equals(findCategoryByName(allCategory))){
                //Assign Id for the All Category Menu Item
                categoryMenuItemId = R.id.nav_all;
            }else{
                //Otherwise assigng the id given programatically to the Menu Item
                categoryMenuItemId = currentCategory.getId();
            }//End of if else statement to check the current category is the All Category
        }// End of if else statement that checks the current category is Groceries Category
        //Set up the layout manager and the recyclerView
        this.layoutManager = new LinearLayoutManager(this);
        this.recyclerView.setLayoutManager(layoutManager);
        //Update the data set for the recyclerView
        updateRecyclerViewData(this.getSQLForRecyclerView());
        //Set the onCheckedChagne listener for the glogal check box filter
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

        //Update the top menu text and images
        this.updateTopMenuUI();
        //Tool bar creation and functionality set up
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary1));
        setSupportActionBar(toolbar);

        //Floating button creation and functionality set up
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Call method to throw the Add tas Activity
                throwAddTaskActivity(null);
            }//End of onClick method for the floating button
        });//End of setOnClickListener method
        //Side Navigation Menu set up
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        //Find the navigation view on the layout by using its id
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        //Check the selected theme coming from shared preferences and set the text and icon color when items is selected
        if(appThemeSelected == R.style.AppTheme1){
            //If them is the Red-Green theme, set the color state object to dark green when selected
            colorStateList1 = getResources().getColorStateList(R.drawable.drawer_item_color1);
        }else if(appThemeSelected == R.style.AppTheme2){
            //If them is the Red-Green theme, set the color state object to lime green when selected
            colorStateList1 = getResources().getColorStateList(R.drawable.drawer_item_color2);
        }
        else{
            ////If them is the default theme, set the color state object to pink when selected
            colorStateList1 = getResources().getColorStateList(R.drawable.drawer_item_color);
        }//End of if else statement to check the theme selected in preferences
        //Set the item text and icon colour in navigation menu as peer color state object
        navigationView.setItemTextColor(colorStateList1);
        navigationView.setItemIconTintList(colorStateList1);

        //Get the menu in the navigation view
        Menu navMenu = this.navigationView.getMenu();
        //Call method to populate the menu programmatically
        this.updateNavMenu(navMenu);
        //Set the current category item in nav menu as the selected item when the nav menu is open
        navMenu.findItem(categoryMenuItemId).setCheckable(true).setChecked(true);
        //Set item selected listener
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        //Declare and initialize the user avatar image
        imgUserProfile = (ImageView) headerView.findViewById(R.id.imgUserProfile);
        imgUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUserProfileImage();
            }
        });
        Cursor user = db.runQuery("SELECT * FROM USER");
        if(user.moveToNext()){
            if(!user.getString(3).equals("")){
                imgUserProfile.setImageURI(Uri.parse(user.getString(3)));
            }
            TextView tvUserName = (TextView) headerView.findViewById(R.id.tvUserName);
            tvUserName.setText(user.getString(1));
            tvUserName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setUserProfileName();
                }
            });
            TextView tvUserMessage = (TextView) headerView.findViewById(R.id.tvUserMessage);
            tvUserMessage.setText(user.getString(2));
            tvUserMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setUserProfileMessage();
                }
            });
        }//End of if statement to check user cursor is not empty

        Log.d("Ent_onCreateMain","Enter onCreate method in MainActivity class.");
    }//End of onCreate Method

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Ent_onResumeMain","Enter onResume method in MainActivity class.");
        if(dateFormatChanged){
            //Set the date format as per Shared preferences by calling setDateFormat method
            this.setDateFormat();
            //Update recyclerView
            updateRecyclerViewData(getSQLForRecyclerView());
        }
        Log.d("Ext_onResumeMain","Exit onResume method in MainActivity class.");
    }//End of onResume method

    /*@Override
    protected void onSaveInstanceState(Bundle saveState) {
        //Call super method
        super.onSaveInstanceState(saveState);
        Log.d("Ent_onSaveInstance","Enter the overridden section of onSaveInstanceSate method on MainActivity.");
        //Save the current task list selected by user
        //Declare and instantiate an int to hold the current category id
        int category = currentCategory.getId();
        //Store the current category id in the bundle object
        saveState.putInt("category",category);
        //Save filters applied so the can be set up again
        // Declare and initialize an int to hold the only checked items filter state (0 false, 1 true)
        int onlyChecked = db.toInt(cbOnlyChecked.isChecked());
        //Store the checked filter state in the app state
        saveState.putInt("checkedFilter",onlyChecked);
        // Declare and initialize an int to hold the only search filter state (0 false, 1 true)
        int isSearchFilter = db.toInt(this.isSearchFilter);
        //Store the checked filter state in the app state
        saveState.putInt("isSearchFiler",isSearchFilter);
        //If the search filer is applied save the last search text array in the app state
        if(this.isSearchFilter){
            //Last search for the TASK table
            saveState.putString("searchFilterText0",lastSearchText[0]);
            //Last search for the GROCERIES table
            saveState.putString("searchFilterText1",lastSearchText[1]);
        }//End of if statement to check the search filter is applied
        //Check the current category is the groceries category
        if(category == findCategoryByName(groceryCategory).getId()){
            //If it is the groceries category, check if the type filter is being used
            //Declare and initialize an int to hold the selected types array list size
            int selectedTypesQty = selectedTypes.size();
            //Save the size in the app state
            saveState.putInt("selectedTypesQty",selectedTypesQty);

        }//End of if statement to check the current category
        Log.d("Ext_onSaveInstance","Exit the orverriden section of onSaveInstanceSate method on MainActivity.");
    }// End of onSaveInstanceState method

    @Override
    protected void onRestoreInstanceState(Bundle restoreState) {
        //Call the super method
        super.onRestoreInstanceState(restoreState);
        Log.d("Ent_onRestoreInstance","Enter the orverriden section of onRestroeInstanceState method on MainActivity.");
        //Check the bundle object is not empty
        if (restoreState != null){
            //Retrieve the current category ID stored in the app state
            int category = restoreState.getInt("category");
            //Assign the currentCategory variable the category that matches the previous ID
            currentCategory = findCategoryById(category);
            //Set the current category list the one selected on the NavigationMenu
            //navigationView.getMenu().findItem(currentCategory.getId()).setCheckable(true).setChecked(true);
            //Assign to isSearchFiler and onlyChecked filter flags the value stored on the app state
            this.isSearchFilter = db.toBoolean(restoreState.getInt("isSearchFiler"));
            this.cbOnlyChecked.setChecked(db.toBoolean(restoreState.getInt("checkedFilter")));
            if(this.isSearchFilter){
                //Save the last search text for the TASK table
                this.lastSearchText[0] = restoreState.getString("searchFilterText0");
            }//End of if statement to check the search filter is applied
            //Check the current category is the GROCERIES category
            if(currentCategory.equals(findCategoryByName(groceryCategory))){
                //Check the isSearchFilter applied
                if(this.isSearchFilter){
                    //Save the last search text for the groceries table
                    this.lastSearchText[1] = restoreState.getString("searchFilterText1");
                }//End of if statement to check the search filter is applied
                //Check the type filter is applied
                int selectedTypesQty = restoreState.getInt("selectedTypesQty");
                if(selectedTypesQty>0){
                    //Look for the grocery types to be filtered
                    selectedTypes = this.findSelectedTypes();
                }//End of if statement to check the type filer is not empty
                //Assign the groceryAdapter in the recyclerView object, otherwise it will use the taskAdapter
                recyclerView.setAdapter(groceryAdapter);
            }//End of if statement to check the current category value
            //Update the top menu
            this.updateTopMenuUI();
            //Refresh the recycler viewer with proper data set based on app state parameters
            updateRecyclerViewData(getSQLForRecyclerView());
        }//End of if statement to check the restore item is not null
        Log.d("Ext_onRestoreInstance","Exit the orverriden section of onRestroeInstanceState method on MainActivity.");
    }// End of onRestoreInstanceState method*/

    //Method to return sql string to be used to update the REcyclerViewer object
    public String getSQLForRecyclerView(){
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
            String condition3 = "";// To be used for specific categories other that all and groceries category
            String isNotArchived = "  IsArchived = 0 ";
            String orderBy = " ORDER BY ";
            String column1 = " Description ";
            String column2 = " Category ";
            String direction =" ASC";
            int i =0;// integer to define position to search text in the lastSearchText String array
            boolean isGrocery = currentCategory.equals(findCategoryByName(groceryCategory));
            //Check if current category is the groceries category
            if(isGrocery){
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
                condition1 = column1+" LIKE '%"+lastSearchText[i]+"'";
            }//End of if statement to check the search filter
            //Check the only checked items filter is applied
            if(cbOnlyChecked.isChecked()){
                //if it is applied, the condition2 must indicate the isSelected property set to 1
                condition2 +=  " IsSelected = 1 ";
            }//End of if statement to check the only checked items
            //Logic to build the sql query dynamically based on the different category and filters
            //Check the three conditions are empty
            if(condition1.equals("")&& condition2.equals("") && condition3.equals("")){
                if(isGrocery){
                    //If they are empty, the sql must not contain any condition in it
                    sql = select+table+orderBy+column2+direction;
                }else{
                    //If they are empty, the sql must not contain any condition in it
                    sql = select+table+where+isNotArchived+orderBy+column2+direction;
                }

            }else{
                //Otherwise, check the different combinations of conditions
                //If the third condition is blank, means it is not a specific task (It is either All or Groceries)
                if(condition3.equals("")){
                    //column2 = "_id";
                    //Check the condition 1 is not empty
                    if(!condition1.equals("")){
                        //In that case, check if condition2 is not empty either, which means more than one condition is required
                        //As per current code, this option will never be used, however is left here intentionally in case is needed in future
                        if(!condition2.equals("")){
                            if(isGrocery){
                                //If they are empty, the sql must not contain any condition in it
                                sql = select+table+where+condition1+and+condition2+orderBy+column2+direction;
                            }else{
                                //If they are empty, the sql must not contain any condition in it
                                sql = select+table+where+isNotArchived+and+condition1+and+condition2+orderBy+column2+direction;
                            }

                        }else{
                            if(isGrocery){
                                //if it doesn't have the and word, means only the condition 1 is applied
                                sql = select+table+where+condition1+orderBy+column2+direction;
                            }else{
                                //if it doesn't have the and word, means only the condition 1 is applied
                                sql = select+table+where+isNotArchived+and+condition1+orderBy+column2+direction;
                            }

                        }//End of if else statement to check condition1 is not empty and condition2 is not empty
                        //If condition1 is blank, check the condition2 is not empty
                    }else if(!condition2.equals("")){
                        if(isGrocery){
                            //If the condition1 is blank, means no search filter is applied and the checked only items is the only filter applied
                            sql = select+table+where+condition2+orderBy+column2+direction;
                        }else{
                            //If the condition1 is blank, means no search filter is applied and the checked only items is the only filter applied
                            sql = select+table+where+isNotArchived+and+condition2+orderBy+column2+direction;
                        }

                    }
                    //If condition3 is not empty, a specific category filter must be required
                }else{
                    //Check condition1 is not empty (at least two conditions required)
                    if(!condition1.equals("")){
                        //Include condition1 and condition3 in the sql query
                        if(!condition2.equals("")){
                            if(isGrocery){
                                //SQL for three filters applied
                                sql = select+table+where+condition1+and+condition2+and+condition3+orderBy+column2+direction;
                            }else{
                                //SQL for three filters applied
                                sql = select+table+where+isNotArchived+and+condition1+and+condition2+and+condition3+orderBy+column2+direction;
                            }

                        }else{
                            if(isGrocery){
                                //otherwise, only condition 1 and 3 applied
                                sql = select+table+where+condition1+and+condition3+orderBy+column2+direction;
                            }else{
                                //otherwise, only condition 1 and 3 applied
                                sql = select+table+where+isNotArchived+and+condition1+and+condition3+orderBy+column2+direction;
                            }

                        }//End of if else statement to chec condition 2 is empty
                        //If condition1 is empty, check condition2
                    }else if(!condition2.equals("")) {
                        if(isGrocery){
                            //This means only condition 2 and 3 will be required in the sql query
                            sql = select+table+where+condition2+and+condition3+orderBy+column2+direction;
                        }else{
                            //This means only condition 2 and 3 will be required in the sql query
                            sql = select+table+where+isNotArchived+and+condition2+and+condition3+orderBy+column2+direction;
                        }

                    }else{
                        if(isGrocery){
                            //Finally, query if only condition 3 is required (specific category filter)
                            sql = select+table+where+condition3+orderBy+column2+direction;
                        }else{
                            //Finally, query if only condition 3 is required (specific category filter)
                            sql = select+table+where+isNotArchived+and+condition3+orderBy+column2+direction;
                        }

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
        if(navMenu.size()>INDEX_TO_GET_LAST_TASK_LIST_ITEM+1){
            //Initialize a string to get the Category with MAX id from category list (The last category added into DB)
            sql = "SELECT * FROM CATEGORY  WHERE _id= (SELECT MAX(_id) FROM CATEGORY)";

        }else{
            //Initialize a string to get category list from DB that does not include All and Groceries
            sql = "SELECT * FROM CATEGORY WHERE _id NOT IN("+findCategoryByName(allCategory).getId()+", "+findCategoryByName(groceryCategory).getId()+")";
            //Make the All Category the default selected item
            navMenu.getItem(0).setChecked(true);
        }//End of if else statement to check the nav menu size is greater than the number of pre-existent menu items
        //Declare and initialize a cursor object to retrieve the list task categories in the DB
        Cursor c = db.runQuery(sql);
        int order =0;
        //While loop to iterate through the cursor and include the item in the Task list menu
        while(c.moveToNext()){
            order = navMenu.getItem(navMenu.size()-INDEX_TO_GET_LAST_TASK_LIST_ITEM).getOrder()+1;
            //MenuItem previousItem = navMenu.getItem(navMenu.size()-1)
            navMenu.add(R.id.categoryListMenu,c.getInt(0),order,c.getString(1));
            MenuItem newItem = navMenu.getItem(navMenu.size()-INDEX_TO_GET_LAST_TASK_LIST_ITEM);
            newItem.setIcon(R.drawable.list_icon);
        }//End of while loop
        Log.d("Ext_UpdateNaveMenu","Exit the updateNavMenu method in MainActivity class.");
    }//End of updateNavMenu method

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        //When menu is invalidated, find the search and archive menu items
        MenuItem search = menu.findItem(R.id.search);
        MenuItem archive = menu.findItem(R.id.archive);
        //Check the Archived task list is active or not
        if(isArchivedSelected){
            //If it is active, set search and archive menus to be not visible
            search.setVisible(false);
            archive.setVisible(false);
        }else{
            //Otherwise, check the current category is the Groceries Category
            if(currentCategory.equals(findCategoryByName(groceryCategory))){
                //If that is the case, only the  archive will be hidden
                archive.setVisible(false);
            }//End of if statement to check the current category
        }//End of if else statement to check the isArchiveSelected state
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
            //Call the method to start the preferences activity
            this.callPrefernces(null);
            return true;
        }else if(id==R.id.search){
            this.search();
            return true;
        }else if(id == R.id.delete){
            return this.delete();
        }else if(id == R.id.archive){
            return this.archive();
        }else if(id == R.id.about){
            this.throwAboutActivity(null);
            return true;
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
            if(this.isArchivedSelected){
                this.isArchivedSelected = false;
                FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
                //fab.setEnabled(true);
                fab.setVisibility(View.VISIBLE);
            }
            // Handle the the All task list menu option
            //Set the current category to be All
            this.currentCategory = findCategoryByName(allCategory);
            //Check if global checkbox state
            if(this.cbOnlyChecked.isChecked()){
                this.cbOnlyChecked.setChecked(false);
            }//End of if statement to check the check box state
            //Check if global isSearch filer variable is set to true
            if(this.isSearchFilter){
                this.isSearchFilter = false;
            }//End of if statement to check isSearchFilter state
            //Change background in case previous list was the groceries list (selectedTypes size >0)
            tvHighlightFilter.setTextColor(getResources().getColor(R.color.colorSecondayText));
            if(this.taskAdapter == null){
                //If the task adapter is null, call method to set up the adapter into the recyclerView
                this.setTaskAdapter();
            }else{
                //Otherwise, set the adapter directly
                this.recyclerView.setAdapter(taskAdapter);
            }//End of if statement that check the taskAdapter is not null
            //Call method to update the RecyclerView data set and update ui
            this.updateRecyclerViewData(getSQLForRecyclerView());
            invalidateOptionsMenu();
            //Update the top menu text and images
            this.updateTopMenuUI();
            this.db.updateAppState(currentCategory.getId(),db.toInt(isSearchFilter),db.toInt(cbOnlyChecked.isChecked()),lastSearchText[0]+"'",lastSearchText[1]+"'");
            drawer.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_grocery) {
            //Check the Archived tasks list is selected or was selected
            if(this.isArchivedSelected){
                //If that is the case, set to false, so new category is in use
                this.isArchivedSelected = false;
                //Make the fab button visible again, as it was hidden by the Arvhive Task menu
                FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
                fab.setVisibility(View.VISIBLE);
            }//End of if statement to check the isArchivedSelected state
            // Handle the the groceries task category list menu option
            if(this.cbOnlyChecked.isChecked()){
                this.cbOnlyChecked.setChecked(false);
            }//End of if statement to check the check box state
            //Check if global isSearch filer variable is set to true
            if(this.isSearchFilter){
                this.isSearchFilter = false;
            }//End of if statement to check isSearchFilter state
            //Set the current category to be Groceries
            this.currentCategory=findCategoryByName(groceryCategory);
            //If the grocery adapter is null, call method to set up the adapter into the recyclerView
            if(groceryAdapter == null){
                //Instantiate the groceryAdapter for first time, pass in MainActivity context, current DB manager class and the cursor with grocery data
                this.setGroceryAdapter();
            }else{
                //Set the adapter in the global recyclerView
                this.recyclerView.setAdapter(groceryAdapter);
            }//End of if statement to check the groceryAdapter is null
            //Invalidate menu setup and set up proper menu items
            invalidateOptionsMenu();
            //Update the top menu text and images
            this.updateTopMenuUI();
            updateRecyclerViewData(this.getSQLForRecyclerView());
            this.db.updateAppState(currentCategory.getId(),db.toInt(isSearchFilter),db.toInt(cbOnlyChecked.isChecked()),lastSearchText[0]+"'",lastSearchText[1]+"'");
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
                    .setPositiveButton(R.string.ok,new DialogInterface.OnClickListener(){//Define the positive button
                        public void onClick(DialogInterface dialog,int whichButton){
                            //Read from the user input the list name
                            String newListName = input.getText().toString();
                            //Check the new list's name is not empty
                            if(newListName.equals("")){
                                //Display error message via toast
                                Toast.makeText(MainActivity.this,R.string.listNameEmpty,Toast.LENGTH_SHORT).show();
                            }else if(newListName.length()>MAX_TASK_LIST_NAME){
                                Toast.makeText(MainActivity.this, R.string.listNameTooLong,Toast.LENGTH_SHORT).show();
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
                                        navigationView.getMenu().getItem(navigationView.getMenu().size()-INDEX_TO_GET_LAST_TASK_LIST_ITEM).setCheckable(true).setChecked(true);
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
                        .setPositiveButton(R.string.ok,new DialogInterface.OnClickListener(){
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
                                            //positionsToBeDeleted.add(i+INDEX_TO_GET_LAST_TASK_LIST_ITEM);
                                            notEmpty = true;
                                        }///End of for loop to go through the deletableTasks list
                                    }//End of for loop to iterate through the list of Categories
                                    //Check at least one list was selected for deletion, otherwise display an error message
                                    if(notEmpty){
                                        //Declare and instantiate a string object to dynamically include the names of lists to be deleted in message
                                        String deleteConfirmationMessage = getResources().getString(R.string.wantToDeleteList);
                                        if(categoriesToBeDeleted.size()>1){
                                            //Make the text plural if more than one category will be deleted
                                            deleteConfirmationMessage += "s: \n\t- ";
                                        }else{
                                            //Make the text singular if only one category will be deleted
                                            deleteConfirmationMessage += ": \n\t- ";
                                        }//End of if else statement fo selected the proper warning message to display
                                        //For loop to go through the list of categories to be deleted and add every list's name into the warning message
                                        for(int i=0;i<categoriesToBeDeleted.size();i++){
                                            //Add the current list name to the text
                                            deleteConfirmationMessage += categoriesToBeDeleted.get(i).getName();
                                            //Check this is not the last item in the list
                                            if(i+1<categoriesToBeDeleted.size()){
                                                //If it is not the last one, add an extra line and bullet
                                                deleteConfirmationMessage += "\n\t- ";
                                            }//End of if statement to check if it's the last one item in the list
                                        }//End of for loop to include the list names to be deleted
                                        //Display a final warning message summarizing  all the lists to be deleted and informing all the tasks in that lis will be deleted
                                        new AlertDialog.Builder(MainActivity.this)
                                                .setTitle(R.string.deleteTaskList)
                                                .setMessage(deleteConfirmationMessage)
                                                .setPositiveButton(R.string.ok,new DialogInterface.OnClickListener(){
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
                                        Toast.makeText(MainActivity.this, R.string.noTaskListSelected,Toast.LENGTH_SHORT).show();
                                    }// End of if else statement to check the list of categories is not empty
                                }else{
                                    //Display an error message
                                    Toast.makeText(MainActivity.this, R.string.noTaskListAvailable,Toast.LENGTH_SHORT).show();
                                }
                            }//End of Onclick method
                        })
                        .setNegativeButton(R.string.cancel,null)
                        .create()
                        .show();
            }//End of if statement to check the cursor is not empty
        }else if(id == R.id.nav_archive){
            //Set the Archived tasks list to be selected
            this.isArchivedSelected = true;
            //Floating button creation and functionality set up (make it invisible)
            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setVisibility(View.GONE);
            //If the task adapter is null, call method to set up the adapter into the recyclerView
            if(this.taskAdapter == null){
                this.setTaskAdapter();
            }else{
                //Otherwise, setup the adapter into the recyclerViuew
                this.recyclerView.setAdapter(taskAdapter);
            }//End of if else statement to check the task adapter is null or not
            //Set the proper string for the sql questy that will retrieve the list of archived tasks only
            String sql ="SELECT * FROM TASK WHERE IsArchived = 1 ORDER BY DateClosed DESC";
            //Update data set for the RecyclerView
            updateRecyclerViewData(sql);
            //Invalidate menu and set up proper menu items to be visible
            invalidateOptionsMenu();
            //Update the top menu to display correct images and menu names
            this.updateTopMenuUI();
            //Close the drawer
            drawer.closeDrawer(GravityCompat.START);
        }else{
            //Check the Archived tasks list is selected or was selected
            if(this.isArchivedSelected){
                //If that is the case, set to false, so new category is in use
                this.isArchivedSelected = false;
                //Make the fab button visible again, as it was hidden by the Arvhive Task menu
                FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
                fab.setVisibility(View.VISIBLE);
            }//End of if statement to check the isArchivedSelected state

            //Any other menu item added programmatically will fall in this section
            //Declare and instantiate a string to hold the sql query that will retrieve all the Category items in the DB except the All and Groceries categories
            String sql ="SELECT * FROM CATEGORY WHERE _id NOT IN ("+findCategoryByName(allCategory).getId()+", "+findCategoryByName(groceryCategory).getId()+")";
            //Declare and instantiate a cursor that will hold all the categories coming from the DB
            Cursor c = db.runQuery(sql);
            //Declare an initialize a boolean flag to prompt when a specific Category has been found in the Cursor c
            boolean found = false;
            //Check
            if(this.taskAdapter == null){
               this.setTaskAdapter();
            }else{
                this.recyclerView.setAdapter(taskAdapter);
            }//End of if else statement to check the task adapter is null or not
            //While loop to iterate through the cursor and find the new current category by passing in the Category id coming from DB, which matches the Menu item id
            while(c.moveToNext() && !found ){
                //Check the Menu Item id from item selected by user against the _id attribute of items in the cursor
                if(id == c.getInt(0)){
                    //Set the current category to be All
                    this.currentCategory = findCategoryById(id);
                    //Check if global checkbox state
                    if(this.cbOnlyChecked.isChecked()){
                        this.cbOnlyChecked.setChecked(false);
                    }//End of if statement to check the check box state
                    //Change background in case previous list was the groceries list (selectedTypes size >0)
                    tvHighlightFilter.setTextColor(getResources().getColor(R.color.colorSecondayText));
                    //Reset main menu and display items properly
                    invalidateOptionsMenu();
                    //Update the Top menu images and texts
                    this.updateTopMenuUI();
                    //Set the selected item as checkable and set it to check on the Navigation Menu, so it can be highlighted on the UI
                    item.setCheckable(true);
                    item.setChecked(true);
                    //Set the booblean flag to true to exit the loop
                    found = true;
                }//End of if statement to extract data from cursor
            }//End of while loop to iterate through the cursor
            //Call method to update the RecyclerView data set and update ui
            this.updateRecyclerViewData("SELECT * FROM TASK WHERE Category = "+id+" AND IsArchived = 0 ORDER BY _id");
            //Save the App state in the DB
            this.db.updateAppState(currentCategory.getId(),db.toInt(isSearchFilter),db.toInt(cbOnlyChecked.isChecked()),lastSearchText[0]+"'",lastSearchText[1]+"'");
            //Close the Navigation Menu drawer
            drawer.closeDrawer(GravityCompat.START);
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
        if(currentCategory.equals(findCategoryByName(groceryCategory))&& !isArchivedSelected){
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
                    .setPositiveButton(R.string.ok,new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog,int whichButton){
                            //Set isSearchFilter boolean to true
                            isSearchFilter = true;
                            //Declare and instantiate as null a string object to hold the sql query to run. Depending on the current category, different query will be run
                            String sql="SELECT * FROM GROCERIES WHERE Name LIKE '%";
                            //Store the search sql for future use
                            //lastSearchText[1] = sql+input.getText().toString()+"%'";
                            lastSearchText[1] = input.getText().toString()+"%";
                            //Update app state in DB
                            db.updateAppState(currentCategory.getId(),db.toInt(isSearchFilter),db.toInt(cbOnlyChecked.isChecked()),lastSearchText[0]+"'",lastSearchText[1]+"'");
                            //Call method to update the adapter and the recyclerView
                            updateRecyclerViewData(sql+lastSearchText[1]+"'");
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
                    .setPositiveButton(R.string.ok,new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog,int whichButton){
                            //Declare and instantiate as null a string object to hold the sql query to run. Depending on the current category, different query will be run
                            String sql="";
                            //First, Check the current property is All otherwise the query must include the specific category that has been selected
                            if(currentCategory.equals(findCategoryByName(allCategory))){
                                //Define sql query to retrieve all the task categories
                                sql = "SELECT * FROM TASK WHERE IsArchived = 0 AND description LIKE '%"+input.getText().toString()+"%'";
                            }else{
                                //Otherwise, define sql query to retrieve tasks with the search text and the current category
                                sql = "SELECT * FROM TASK WHERE IsArchived = 0 AND Category = " + currentCategory.getId() + " AND description LIKE '%"+input.getText().toString()+"%'";
                            }//End of if else statement to  check the current category object
                            //Set isSearchFilter boolean to true
                            isSearchFilter = true;
                            //Store the search sql for future use
                            //lastSearchText[0] = sql;
                            lastSearchText[0] = input.getText().toString()+"%";
                            //Update app state in DB
                            db.updateAppState(currentCategory.getId(),db.toInt(isSearchFilter),db.toInt(cbOnlyChecked.isChecked()),lastSearchText[0]+"'",lastSearchText[1]+"'");
                            //Call method to update the adapter and the recyclerView
                            updateRecyclerViewData(sql);
                        }//End of Onclick method
                    })//End of setPositiveButton method
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
        this.db.updateAppState(currentCategory.getId(),db.toInt(isSearchFilter),db.toInt(cbOnlyChecked.isChecked()),lastSearchText[0]+"'",lastSearchText[1]+"'");
        String sql = this.getSQLForRecyclerView();
        this.updateRecyclerViewData(sql);
        Log.d("Ext_filterCheckedOnly","Exit the filterCheckedOnly method in the MainActivity class");
    }//End of filterCheckedOnly

    //Method to filter only the items that has been selected
    public void unfilterCheckedOnly(){
        Log.d("Ent_filterCheckedOnly","Enter the unfilterCheckedOnlyt method in the MainActivity class");
        //Call method to get sql based on current filters and method to update the adapter and the recyclerView
        this.db.updateAppState(currentCategory.getId(),db.toInt(isSearchFilter),db.toInt(cbOnlyChecked.isChecked()),lastSearchText[0]+"'",lastSearchText[1]+"'");
        String sql =this.getSQLForRecyclerView();
        this.updateRecyclerViewData(sql);
        Log.d("Ext_unfilterCheckedOnly","Exit the unfilterCheckedOnly method in the MainActivity class");
    }//End of filterCheckedOnly

    //Method to update MainActivity UI top menu (No  the action bar)
    public void updateTopMenuUI(){
        Log.d("Ent_updateUIMA","Enter updateTopeMenuUI in MainActivity class.");
        //Check the Archive menu was selected before the UI update
        if(this.isArchivedSelected){
            //If that is the case, set the archive image
            this.imgCurrentList.setImageResource(R.drawable.archive_icon);
            //Set the archive text
            this.tvCurrentList.setText(R.string.archived);
            //Change color of icon for archive list
            Drawable drawable = getResources().getDrawable(android.R.drawable.ic_menu_sort_by_size);
            drawable = DrawableCompat.wrap(drawable);
            DrawableCompat.setTint(drawable,Color.BLACK);
            //Set the image in the imageView
            this.imgHighlightFilter.setImageDrawable(drawable);
            //Toggle text for the sort orientation, based ont he current orientation
            switch(orientation){
                //If it it Descending order, change to ascending
                case DESC:
                    this.tvHighlightFilter.setText(sortOrientation.DESC.toString());
                    break;
                //If it is ascending, change it to descending
                case ASC:
                    this.tvHighlightFilter.setText(sortOrientation.ASC.toString());
                    break;
            }//End of switch statement
            //this.tvHighlightFilter.setText("DESC");
            this.cbOnlyChecked.setVisibility(View.GONE);
            this.tvOnlyChecked.setVisibility(View.GONE);
        }else{
            this.cbOnlyChecked.setVisibility(View.VISIBLE);
            this.tvOnlyChecked.setVisibility(View.VISIBLE);
            //this.imgHighlightFilter.setVisibility(View.VISIBLE);
            //this.tvHighlightFilter.setVisibility(View.VISIBLE);
            //Get the current category name and store it in a String variable
            String currentCategoryName = this.currentCategory.getName();
            this.tvOnlyChecked.setText(R.string.checked);
            //Check current category and select the correct actions to update the top menu
            if(currentCategoryName.toLowerCase().equals(groceryCategory.toLowerCase())){
                //this.tvOnlyChecked.setText(R.string.checkedGrocery);
                this.imgCurrentList.setImageResource(R.drawable.groceries_icon);
                this.imgHighlightFilter.setImageResource(R.drawable.filter_icon);
                this.tvHighlightFilter.setText(R.string.filterByType);
                //Check the type filter is applied, if that is the case, change text color
                if(selectedTypes.size()>0){
                    tvHighlightFilter.setTextColor(getResources().getColor(R.color.colorAccent));
                }//End of if statement to check the type filter is applied
            }else{
                //For all other task lists
                this.imgHighlightFilter.setImageResource(R.drawable.done_icon);
                this.tvHighlightFilter.setText(R.string.markDone);
                if(currentCategoryName.toLowerCase().equals(allCategory.toLowerCase())){
                    this.imgCurrentList.setImageResource(R.drawable.all_icon);
                }else{
                    this.imgCurrentList.setImageResource(R.drawable.list_icon);
                }//End of if else statement to check the current task list is the all category
            }//End of if else statement to check the current category name
            //Display the proper name
            this.tvCurrentList.setText(this.currentCategory.getName().toString());
        }//End of if else statement to check the isArchiveSelected

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
        //Set search boolean variable to false
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
                .setPositiveButton(R.string.ok,new DialogInterface.OnClickListener(){
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
        //Check the selected type list is not empty
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
    }//End of clearTypeFilter method

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
                        //Uncheck the task
                        if(db.toBoolean(tempCursor.getInt(8))){
                            //Change current attribute to opposite value
                            db.updateBoolAttribute("TASK","IsSelected",tempCursor.getInt(0),false);
                        }//End of if statement to check the task is done  (Column 4 in DB)
                        db.updateBoolAttribute(currentCategory.getName().toString(),"IsDone",tempCursor.getInt(0),isDone);
                    }//End of if statement to check the task is selected (Column 8 in DB)
                }//End of while loop to iterate through the temp cursor
                //Update the list of tasks
                updateRecyclerViewData(sql);
            }else{
                Toast.makeText(this,R.string.noTaskToHighlight,Toast.LENGTH_SHORT).show();
            }//End of if else statement to check there are checked items
        }else{
            Toast.makeText(this,R.string.noTaskToHighlight,Toast.LENGTH_SHORT).show();
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
        i.putExtra("sql",this.getSQLForRecyclerView());
        //Start the addTaskActivity class
        startActivity(i);
        Log.d("Ext_throwAddTask","Exit throwAddTaskActivity method in the MainActivity class.");
    }//End of throwAddTaskActivity method

    //Method to throw new throwAboutActivity
    private void throwAboutActivity(View view){
        Log.d("Ent_throwAbout","Enter throwAboutActivity method in the MainActivity class.");
        //Declare and instantiate a new intent object
        Intent i= new Intent(MainActivity.this,AboutActivity.class);
        //Add extras to the intent object, specifically the current category where the add button was pressed from
        //i.putExtra("category",this.currentCategory.toString());
        //i.putExtra("sql",this.getSQLForRecyclerView());
        //Start the addTaskActivity class
        startActivity(i);
        Log.d("Ext_throwAbout","Exit throwAboutActivity method in the MainActivity class.");
    }//End of throwAboutActivity method

    private void throwEditTaskActivity(int id){
        Log.d("Ent_throwEditTask","Enter throwEditTaskActivity method in the MainActivity class.");
        //Declare and instantiate a new intent object
        Intent i= new Intent(MainActivity.this,EditTaskActivity.class);
        //Add extras to the intent object, specifically the current category where the add button was pressed from
        i.putExtra("category",this.currentCategory.toString());
        i.putExtra("id",id);
        i.putExtra("sql",this.getSQLForRecyclerView());
        //Start the addTaskActivity class
        startActivity(i);
        Log.d("Ext_throwEditTask","Exit throwEditTaskActivity method in the MainActivity class.");
    }//End of throwAddTaskActivity method

    //Method to delete a task or a grocery
    private boolean delete(){
        Log.d("Ent_delete","Enter delete method in the MainActivity class.");
        //Declare and instantiate a boolean  variable which will return if the delete method was successful or not
        boolean result = false;
        //Declare a cursor object to query the database and hold the data
        final Cursor c;
        //Declare a String object to hold the message to be displayed on the  dialog box that will prompt the user the tasks or groceries to be deleted
        String dialogMessage;
        //Check current property (is it groceries or any other?)
        if(currentCategory.equals(findCategoryByName(getGroceryCategory())) && !isArchivedSelected){
            //if the current category is the Groceries
            //Run query to filter all the selected items in the Groceries table
            c= db.runQuery("SELECT * FROM GROCERIES WHERE isSelected = 1 ORDER BY TypeOfGrocery ASC");
            //Initialize the dialogMessage variable.
            dialogMessage = "";
            //Check the cursor is not empty an d has more than one row
            if(c.getCount()>1){
                //Set the dialog box message to refer to several groceries
                dialogMessage = getResources().getString(R.string.deleteGroceries);
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
                    dialogMessage = getResources().getString(R.string.delete1Grocery)+ " " + c.getString(1)+"?";
            }else{
                //If cursor did not comply previous conditions, means the cursor is empty or null. Display error message for that
                dialogMessage =getResources().getString(R.string.noGroceryDeleted);
            }//End of if else statement to check the number of groceries to be deleted
            //Display a warning message asking user if he/she wants to delete the selected items
            new AlertDialog.Builder(this)
                    .setTitle(R.string.deleteGroceryTitle)//Set title
                    .setMessage(dialogMessage)// Set the message as per previous dynamic selection
                    .setPositiveButton(R.string.ok,new DialogInterface.OnClickListener(){//Define the positive button
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
            if(isArchivedSelected){
                sql = "SELECT * FROM TASK WHERE IsSelected = 1 AND IsArchived = 1 ORDER BY DateClosed "+ tvHighlightFilter.getText().toString();
            }else{
                //Check if the current category is All tasks or a specific category has been selected
                if(currentCategory.equals(findCategoryByName(allCategory))){
                    //Define sql query that includes all the categories
                    sql = "SELECT * FROM TASK WHERE IsSelected = 1 AND IsArchived = 0 ORDER BY Category ASC" ;
                }else{
                    //Define a sql query for a specific category. Category id comes from the current category object
                    sql = "SELECT * FROM TASK WHERE IsSelected = 1 AND IsArchived = 0 AND Category = "+ currentCategory.getId();
                }//End of if else statement
            }
            //Retrieve data from DB by running sql query defined above
            c= db.runQuery(sql);
            //Initialize the dialogMessage string to be empty
            dialogMessage = "";
            //Check the cursor is not empty an d has more than one row
            if(c.getCount()>1){
                //Set the dialog box message to refer to several tasks
                dialogMessage = getResources().getString(R.string.deleteTasks);
                //Use a while loop to iterate through the cursor and obtain the description of each task selected to be deleted
                while(c.moveToNext()){
                    dialogMessage += "\n- "+c.getString(1);
                }//End of while loop
                //If the cursor has less than 2 rows, check is not empty and has at least one row
            }else if(c != null && c.getCount()>0){
                //Move to first position of cursor.
                c.moveToFirst();
                //Set the dialog box message to be singular and refer to only one task and add its descriotion (extracted from cursor)
                dialogMessage = getResources().getString(R.string.delete1Task)+ " " + c.getString(1)+"?";
            }else{
                //If cursor did not comply previous conditions, means the cursor is empty or null. Display error message for that
                dialogMessage =getResources().getString(R.string.noTaskDeleted);
            }//End of if else statement to check the number of groceries to be deleted
            //Display a warning message asking user if he/she wants to delete the selected items
            new AlertDialog.Builder(this)
                    .setTitle(R.string.deleteTaskTitle)//Set title
                    .setMessage(dialogMessage)// Set the message as per previous dynamic selection
                    .setPositiveButton(R.string.ok,new DialogInterface.OnClickListener(){//Define the positive button
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
                                if(isArchivedSelected){
                                    sql = "SELECT * FROM TASK WHERE IsArchived = 1 ORDER BY DateClosed "+ tvHighlightFilter.getText().toString();
                                }else{
                                    //Check if the current category is All tasks or a specific category has been selected
                                    if(currentCategory.equals(findCategoryByName(allCategory))){
                                        //Define sql query that includes all the categories
                                        sql = "SELECT * FROM TASK WHERE IsArchived = 0 ORDER BY Category ASC" ;
                                    }else{
                                        //Define a sql query for a specific category. Category id comes from the current category object
                                        sql = "SELECT * FROM TASK WHERE IsArchived = 0 AND Category = "+ currentCategory.getId();
                                    }//End of if else statement
                                }
                                //Call method to update the adapter and the recyclerView
                                updateRecyclerViewData(sql);
                            }//End of if statement to check cursor is not null or empty
                        }//End of Onclick method
                    })//End of setPositiveButton method
                    .setNegativeButton(R.string.cancel,null)
                    .show();
        }//End if else statement to check is  the grocery category and the isArchviveSelected is false
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
        if(currentCategory.equals(findCategoryByName(groceryCategory))){
            if(selectedTypes.size()>0){
                //Reset the filter type
                this.clearTypeFilter();
                //Run the query and update the recyclerView
                this.unfilterCheckedOnly();
            }//End of if statement to check the filter type is applied
        }//End of if statement to check the the current category is the groceries category
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
    }//End of method to getFullList

    //Method to update User Profile Name
    private void setUserProfileName(){
        Log.d("Ent_setProfName","Enter setUserProfileName method in the MainActivity class.");
        //Declare and instantiate a new EditText object
        final EditText input= new EditText(this);
        //Set text to empty text
        //input.setText("");
        //Populate current name in the input text and get focus
        View headerView = navigationView.getHeaderView(0);
        TextView tvUserName = (TextView) headerView.findViewById(R.id.tvUserName);
        input.setText(tvUserName.getText());
        input.requestFocus();
        //Display a Dialog to ask for the List name (New Category)
        new AlertDialog.Builder(this)
                .setTitle(R.string.setUserName)//Set title
                .setMessage(R.string.inputUserName)// Set the message that clarifyes the requested action
                .setView(input)
                .setPositiveButton(R.string.ok,new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog,int whichButton){
                        //Check the input field is not empty
                        if(!input.getText().toString().trim().equals("")){
                            View headerView = navigationView.getHeaderView(0);
                            TextView tvUserName = (TextView) headerView.findViewById(R.id.tvUserName);
                            //Try to update user name on the DB and check result from DB
                            if(db.updateUser("UserName",input.getText().toString())){
                                tvUserName.setText(input.getText());
                            }else{
                                //Display error message if the boolean received from DB is false
                                Toast.makeText(MainActivity.this,R.string.unableUpdateUserName,Toast.LENGTH_SHORT).show();
                            }//End of if else statement to update the user data and receive result of that DB action
                        }else{
                            //If input fiel is empty, display an error message
                            Toast.makeText(MainActivity.this,R.string.blankUserName,Toast.LENGTH_SHORT).show();
                            //input.requestFocus();
                        }//End of if else statement to check the input field is not left blank
                    }//Define the positive button
                })//End of AlerDialog Builder
                .setNegativeButton(R.string.cancel,null)
                .create()
                .show();
        Log.d("Ext_setProfName","Exit setUserProfileName method in the MainActivity class.");
    }//End of setUserProfileName method

    //Method to update user profile message
    private void setUserProfileMessage(){
        Log.d("Ent_setProfMesg","Enter setUserProfileMessage method in the MainActivity class.");
        //Declare and instantiate a new EditText object
        final EditText input= new EditText(this);
        //Set text to current message text
        //input.setText("");
        View headerView = navigationView.getHeaderView(0);
        TextView tvUserMessage = (TextView) headerView.findViewById(R.id.tvUserMessage);
        input.setText(tvUserMessage.getText());
        input.requestFocus();
        //Display a Dialog to ask for the List name (New Category)
        new AlertDialog.Builder(this)
                .setTitle(R.string.setUserMsg)//Set title
                .setMessage(R.string.inputUserMsg)// Set the message that clarifyes the requested action
                .setView(input)
                .setPositiveButton(R.string.ok,new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog,int whichButton){
                        //Check the input field is not empty
                        if(!input.getText().toString().trim().equals("")){
                            View headerView = navigationView.getHeaderView(0);
                            TextView tvUserMessage = (TextView) headerView.findViewById(R.id.tvUserMessage);
                            //Try to update user message on the DB and check result from DB
                            if(db.updateUser("UserMessage",input.getText().toString())){
                                tvUserMessage.setText(input.getText());
                            }else{
                                //Display error message if the boolean received from DB is false
                                Toast.makeText(MainActivity.this,R.string.unableUpdateUserMsg,Toast.LENGTH_SHORT);
                            }//End of if else statement to update the user data and receive result of that DB action
                        }else{
                            //If input field is empty, display an error message
                            Toast.makeText(MainActivity.this,R.string.unableUpdateUserMsg,Toast.LENGTH_SHORT).show();
                            //input.requestFocus();
                        }//End of if else statement to check the input field is not left blank
                    }//Define the positive button
                })//End of AlertDialog Builder
                .setNegativeButton(R.string.cancel,null)
                .create()
                .show();
        Log.d("Ext_setProfMessage","Exit setUserProfileMessage method in the MainActivity class.");
    }//End of setUserProfileMessage method

    //Method to update user profile picture
    private void setUserProfileImage(){
        Log.d("Ent_setProfPict","Enter setUserProfileImage method in the MainActivity class.");
        //CharSequence sources[] = new CharSequence[]{"Camera","Gallery"};
        new AlertDialog.Builder(MainActivity.this)
                .setTitle(R.string.setUserImg)
                //.setMessage("Select an option to load a profile picture:")
                .setSingleChoiceItems(R.array.profileImageSources,0, null)//End of setSingleChoice method
                .setPositiveButton(R.string.ok,new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog,int whichButton){
                        //Check the option selected by user
                        int selectedPosition = ((AlertDialog)dialog).getListView().getCheckedItemPosition();
                        if(selectedPosition == 0){
                            if (ContextCompat.checkSelfPermission(MainActivity.this,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                    == PackageManager.PERMISSION_GRANTED) {
                                //If permit has been granted Call method to take image via the camera
                                takePicture(null);
                            } else {
                                //Otherwise, call method to display justification for this permit and request access to it
                                permissionRequest(Manifest.permission.WRITE_EXTERNAL_STORAGE, "Without this permit"+
                                                " the app won't be able to take pictures with the camera.",
                                        CAMERA_ACCESS_REQUEST, MainActivity.this);
                            }//End of if else statement to check the Camera access rights has been granted or not
                        }else if(selectedPosition==1){
                            if (ContextCompat.checkSelfPermission(MainActivity.this,
                                    Manifest.permission.READ_EXTERNAL_STORAGE)
                                    == PackageManager.PERMISSION_GRANTED) {
                                //If permit has been granted Call method to get access to gallery app via new intent
                                loadPicture(null);
                            } else {
                                //Otherwise, call method to display justification for this permit and request access to it
                                permissionRequest(Manifest.permission.READ_EXTERNAL_STORAGE, "Without this permit"+
                                                " the app won't be able to load pictures from your selected gallery.",
                                        GALLERY_ACCESS_REQUEST, MainActivity.this);
                            }//end of if else statement to check the read storage access rights has been granted or not
                        }else{
                            finish();
                        }//End of if else statement to check the selectedPosition value
                    }//End of Onclick method
                })
                .setNegativeButton(R.string.cancel,null)
                .create()
                .show();
        Log.d("Ext_setProfPict","Exit setUserProfileImage method in the MainActivity class.");
    }// End of setUserProfileImage method

    //Method to display alert dialog to request permission for access rights
    public static void permissionRequest(final String permit, String justify,final int requestCode, final Activity activity) {
        Log.d("Ent_permitReq","Enter permissionRequest method in the MainActivity class.");
        //Check the permission request needs formal explanation
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                permit)){
            //Display alert with justification about why permit is necessary
            new AlertDialog.Builder(activity)
                    .setTitle(R.string.generalPermitRqst)
                    .setMessage(justify)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            //Call method to request permission
                            ActivityCompat.requestPermissions(activity,
                                    new String[]{permit}, requestCode);
                        }})
                    .show();
        } else {
            //Otherwise, proceed to request permission
            ActivityCompat.requestPermissions(activity,
                    new String[]{permit}, requestCode);
        }//End of if else statement to check the permission request must be displyed
        Log.d("Ext_permitReq","Exit permissionRequest method in the MainActivity class.");
    }//End of permissionRequest method

    //Method To take a picture via intent
    public void takePicture(View view) {
        Log.d("Ent_TakePicture","Enter takePicture method in the MainActivity class.");
        //Declare and initialize a new Intent object to call camera app
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //Check the PackageManager is not null
        if (intent.resolveActivity(getPackageManager()) != null) {
            ContentValues values = new ContentValues(1);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
            uriProfileImage = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uriProfileImage);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            startActivityForResult(intent, RESULT_PROFILE_IMAGE_CAMERA);
        } else {
                Toast.makeText(this, R.string.errCamera, Toast.LENGTH_LONG).show();
        }
        Log.d("Ext_TakePicture","Exit takePicture method in the MainActivity class.");
    }// End of takePicture method

    //Method to load ad picture from gallery app
    public void loadPicture(View view) {
        Log.d("Ent_LoadPicture","Enter loadPicture method in the MainActivity class.");
        //Declare a new intent
        Intent intent;
        //Check SDK version
        if (Build.VERSION.SDK_INT < 19){
            //Log the current verison
            Log.i("Build.VERSION", "< 19");
            //Initiallize the intent object and set it up for calling the Gallery app
            intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, RESULT_PROFILE_IMAGE_GALLERY);
        } else {
            //Log the current verison
            Log.i("Build.VERSION", ">= 19");
            //Initiallize the intent object and set it up for calling the Gallery app
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, RESULT_PROFILE_IMAGE_GALLERY);
        }//End of if else statement that checks the SDK version
        Log.d("Ext_LoadPicture","Exit loadPicture method in the MainActivity class.");
    }//End of loadPicture method

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent
            data) {
        Log.d("Ent_onActResult","Enter onActivityResult method in the MainActivity class.");
        //Check if result code matches the result from image taken from camera action
        if(requestCode == RESULT_PROFILE_IMAGE_CAMERA){
            //If action is the image taken from camera, check the result is good and the variable to hold the image location is not empty
            if(resultCode == Activity.RESULT_OK && !uriProfileImage.equals("")){
                //Call method to update the DB, and check the result received form DB is successful
                if(db.updateUser("Photo",uriProfileImage.toString())){
                    //Set the image as per uri from camera
                    imgUserProfile.setImageURI(uriProfileImage);
                }else{
                    //Otherwise, display error message
                }
            }else{
                //Otherwise, display error message
            }//Check if request code comes from the Gallery image action
        }else if(requestCode == RESULT_PROFILE_IMAGE_GALLERY){
            //If it comes from the gallery action, check the result code is OK
            if(resultCode == Activity.RESULT_OK ){
                //Set the image as per path coming from the intent. The data can be parsed as an uri
                String uri = data.getDataString();
                //Call method to update the DB, and check the result received form DB is successful
                if(db.updateUser("Photo",uri)){
                    imgUserProfile.setImageURI(Uri.parse(uri));
                }//End of if statment
            }//End of if statemnt that checks the resultCode is OK
        }//End of if else statement to check the request code and define the proper actinos to be taken
        Log.d("Ext_onActResult","Exit onActivityResult method in the MainActivity class.");
    }//End of onActivityResult method

    private void setGroceryAdapter(){
        Log.d("Ent_setGroAdapt","Enter setGroceryAdapter method in the MainActivity class.");
        this.cursor = null;
        //Instantiate the groceryAdapter for first time, pass in MainActivity context, current DB manager class and the cursor with grocery data
        groceryAdapter = new GroceryAdapter(this,db,cursor);
        groceryAdapter.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = recyclerView.getChildAdapterPosition(v);
                //move the cursor to the task position in the adapter
                cursor.moveToPosition(adapterPosition);
                //Extract the task object from the cursor row
                Grocery grocery = db.extractGrocery(cursor);
                throwEditTaskActivity(grocery.getId());
            }//End of onClick method
        });//End of OnSetItemClickListener method
        //Set the onCheckedChangedListener for the groceryAdapter
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
        });//End of setOnItemCheckedChangeListener
        this.recyclerView.setAdapter(groceryAdapter);
        Log.d("Ext_setGroAdapt","Exit setGroceryAdapter method in the MainActivity class.");
    }//End of setGroceryAdapter method

    private void setTaskAdapter(){
        Log.d("Ent_setTaskAdapt","Enter setTaskAdapter method in the MainActivity class.");
        this.cursor = null;
        this.taskAdapter = new TaskAdapter(this,db,cursor);
        this.taskAdapter.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = recyclerView.getChildAdapterPosition(v);
                //move the cursor to the task position in the adapter
                cursor.moveToPosition(adapterPosition);
                //Extract the task object from the cursor row
                Task task = db.extractTask(cursor);
                throwEditTaskActivity(task.getId());
            }//End of onClick method
        });//End of OnSetItemClickListener method
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
                    String sql= "";
                    if(isArchivedSelected){
                        sql="SELECT * FROM TASK WHERE IsArchived = 1 ORDER BY DateClosed " + tvHighlightFilter.getText().toString();
                    }else{
                        sql= getSQLForRecyclerView();
                    }
                    //Update the isSelected attribute (un)checked task
                    db.updateBoolAttribute(currentCategory.getName().toString(),"IsSelected",task.getId(),isChecked);
                    //Call method to update the adapter and the recyclerView
                    updateRecyclerViewData(sql);
                }//End of if statement to check the current task actually changed isSelected stated (otherwise is the recyclerview recycling a  View)
            }//End of onCheckedChanged method
        });//End of setOnCheckedChangeListener method
        this.recyclerView.setAdapter(taskAdapter);
        Log.d("Ext_setTaskAdapt","Exit setTaskAdapter method in the MainActivity class.");
    }//End of setTaskAdapter method

    private boolean archive(){
        Log.d("Ent_archive","Enter the archive method in the MainActivity class.");
        boolean result = false;
        //Declare a cursor object to query the database and hold the data
        final Cursor c;
        String   dialogMessage = " ";
        c= db.runQuery("SELECT * FROM TASK WHERE IsSelected = 1");
        //Check the cursor is not empty an d has more than one row
        if(c.getCount()>1){
            //Set the dialog box message to refer to several tasks
            dialogMessage = getResources().getString(R.string.archiveTasks);
            //Use a while loop to iterate through the cursor and obtain the description of each task selected to be deleted
            while(c.moveToNext()){
                dialogMessage += "\n- "+c.getString(1);
            }//End of while loop
            //If the cursor has less than 2 rows, check is not empty and has at least one row
        }else if(c != null && c.getCount()>0){
            //Move to first position of cursor.
            c.moveToFirst();
            //Set the dialog box message to be singular and refer to only one task and add its description (extracted from cursor)
            dialogMessage = getResources().getString(R.string.archive1Task)+" "+c.getString(1)+"?";
        }else{
            //If cursor did not comply previous conditions, means the cursor is empty or null. Display error message for that
            dialogMessage = getResources().getString(R.string.noTaskArchived);
        }//End of if else statement to check the number of groceries to be deleted
        // Display a warning message asking user if he/she wants to delete the selected items
        new AlertDialog.Builder(this)
                    .setTitle(R.string.archiveTaskTitle)//Set title
                    .setMessage(dialogMessage)// Set the message as per previous dynamic selection
                    .setPositiveButton(R.string.ok,new DialogInterface.OnClickListener(){//Define the positive button
                        public void onClick(DialogInterface dialog,int whichButton){
                            //Check the cursor is not empty and is not null
                            if(c != null && c.getCount()>0){
                                //Move cursor to first position
                                c.moveToFirst();
                                //do while loop to iterate through the cursor and delete each item selected
                                do {
                                    //Declare and instantiate a task object from data in the current row of cursor
                                    Task task= db.extractTask(c);
                                    task.setSelected(false);
                                    task.setArchived(true);
                                    task.setDateClosed(Calendar.getInstance().getTimeInMillis());
                                    //Call the deleteItem method and pass in the grocery object to be deleted
                                    db.updateItem(task);
                                    //boolean result = db.updateBoolAttribute("TASK","IsArchived",task.getId(),true);
                                }while(c.moveToNext());//End of while loop to delete the selected groceries
                                //Declare and instantiate as null a string object to hold the sql query to run. Depending on the current category, different query will be run
                                String sql= getSQLForRecyclerView();
                                //Call method to update the adapter and the recyclerView
                                updateRecyclerViewData(sql);
                            }//End of if statement to check cursor is not null or empty
                        }//End of Onclick method
                    })//End of setPositiveButton method
                    .setNegativeButton(R.string.cancel,null)
                    .show();
        Log.d("Ext_archive","Exit the archive method in the MainActivity class.");
        return result;
    }//End of archive method

    private void sortArchivedTasks(){
        Log.d("Ent_sortTask","Enter the sortArchivedTasks method in the MainActivity class.");
        //Declare and initialize empty string to hold the orientation to be used to order the result data set from DB
        String sort = "";
        //Check the orientation global variable
        switch (orientation){
            case DESC:
                //If it is descending order, set the new one to ascending
                orientation = sortOrientation.ASC;
                //Save the new orientation as a string
                sort = sortOrientation.ASC.toString();
                //Display in the menu
                tvHighlightFilter.setText(sort);
                break;
            case ASC:
                //If it is ascending order, set the new one to descending
                orientation = sortOrientation.DESC;
                //Save the new orientation as a string
                sort = sortOrientation.DESC.toString();
                //Display in the menu
                tvHighlightFilter.setText(sort);
                break;
        }//End of switch statement
        //Update recyclerViewer data for all archived tasks in DB
        updateRecyclerViewData("SELECT * FROM TASK WHERE IsArchived = 1 ORDER BY DateClosed "+sort);
        Log.d("Ext_sortTask","Exit the sortArchivedTasks method in the MainActivity class.");
    }//End of sortArchivedTasks method

    //Method to call the Preferences screen
    private void callPrefernces(View view){
        Log.d("Ent_callPrefernce","Enter the callPreferences method in MainActivity.");
        //Declare and instantiate a new Intent object, passing the PreferencesActivity class as argument
        Intent i = new Intent(this, PreferencesActivity.class);
        //Start the activity by passin in the intent
        startActivity(i);
        Log.d("Ext_callPrefernce","Exit the callPreferences method in MainActivity.");
    }// End of callPreferences method

    @SuppressLint("ResourceType")
    public static int setAppTheme(Context context){
        Log.d("Ent_setAppTheme","Enter setAppTheme method in MainActivity class.");
        SharedPreferences pref =  PreferenceManager.getDefaultSharedPreferences(context);
        String preferedThemeID = pref.getString("appTheme","0");
        int themeId;
        if(preferedThemeID.equals("1")){
            themeId = R.style.AppTheme1;
            doneHighlighter = context.getResources().getString(R.color.colorAccent1);
        }else if(preferedThemeID.equals("2")){
            themeId = R.style.AppTheme2;
            doneHighlighter = context.getResources().getString(R.color.colorAccent2);
        }else{
            themeId = R.style.AppTheme;
            doneHighlighter = context.getResources().getString(R.color.colorAccent);
        }
        Log.d("Ext_setAppTheme","Exit setAppTheme method in MainActivity class.");
        return themeId;
    }//End of setAppTheme method

    public void setAppLanguage(){
        Log.d("Ent_setAppLang","Enter setAppLanguage method in MainActivity class.");
        SharedPreferences pref =  PreferenceManager.getDefaultSharedPreferences(this);
        String languageValue = pref.getString("languages","0");
        String language;
        if(languageValue.equals("0")){
            language = "en";
        }else{
            language = "es";
        }
        // Change locale settings in the app.
        Resources res = this.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            conf.setLocale(new Locale(language.toLowerCase())); // API 17+ only.
        }
        // Use conf.locale = new Locale(...) if targeting lower versions
        res.updateConfiguration(conf, dm);
        Log.d("Ext_setAppLang","Exit setAppLanguage method in MainActivity class.");
    }//End of setAppTheme method

    private void setDateFormat(){
        Log.d("Ent_setDateFormat","Enter setDateFormat method in MainActivity class.");
        //Get shared references info
        SharedPreferences pref =  PreferenceManager.getDefaultSharedPreferences(this);
        //Get the preference selected for date format
        String preferredDateFormat = pref.getString("dateFormat","0");
        //Assign the preferred value to the global variable
        dateFormat = getResources().getStringArray(R.array.dateFormats)[Integer.parseInt(preferredDateFormat)];
        Log.d("Ext_setDateFormat","Exit setDateFormat method in MainActivity class.");
    }//End of setDateFormat method

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

    public static boolean isDateFormatChanged() {return dateFormatChanged;}

    public static void setDateFormatChanged(boolean newValue) {
        dateFormatChanged = newValue;
    }

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
