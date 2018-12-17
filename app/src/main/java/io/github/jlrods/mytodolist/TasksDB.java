package io.github.jlrods.mytodolist;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by rodjose1 on 30/07/2018.
 * Class to manage all the database management and database interaction
 */

public class TasksDB extends SQLiteOpenHelper {
    private Context context;
    //Default constructor
    public TasksDB(Context context){
        super(context, "Task Database",null, 1);
        this.context = context;
    }//End of default constructor

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("Ent_DBOncreate","Enter onCreate method in TasksDB class.");
        //Create table to store app state
        db.execSQL("CREATE TABLE APP (_id INTEGER PRIMARY KEY AUTOINCREMENT,currentCategory INTEGER, isSearchFilter INTEGER, isChecked INTEGER," +
                "lastSearchTask TEXT,lastSearchGrocery TEXT);");
        //Populate default state of app
        db.execSQL("INSERT INTO APP VALUES(null,1,0,0,'','')");
        //Create User table
        db.execSQL("CREATE TABLE USER (_id INTEGER PRIMARY KEY AUTOINCREMENT,UserName TEXT, UserMessage TEXT, Photo TEXT);");
        //Populate the user table with unique user
        db.execSQL("INSERT INTO USER VALUES(null,'MyToDoList','example@gmail.com','')");

        //Create CATEGORY table
        db.execSQL("CREATE TABLE CATEGORY (_id INTEGER PRIMARY KEY AUTOINCREMENT,Name TEXT);");
        //Populate the CATEGORY table with different categories for the task lists
        db.execSQL("INSERT INTO CATEGORY VALUES(null, 'All');");
        db.execSQL("INSERT INTO CATEGORY VALUES(null, 'Home');");
        db.execSQL("INSERT INTO CATEGORY VALUES(null, 'Work');");
        db.execSQL("INSERT INTO CATEGORY VALUES(null, 'Social');");
        db.execSQL("INSERT INTO CATEGORY VALUES(null, 'Health');");
        db.execSQL("INSERT INTO CATEGORY VALUES(null, 'College');");
        db.execSQL("INSERT INTO CATEGORY VALUES(null, 'Groceries');");
        db.execSQL("INSERT INTO CATEGORY VALUES(null, 'Travel');");

        //Create the PRIORITY table
        db.execSQL("CREATE TABLE PRIORITY (_id INTEGER PRIMARY KEY AUTOINCREMENT,Name TEXT);");
        //Populate the PRIORITY table
        db.execSQL("INSERT INTO PRIORITY VALUES(null, '"+Priority.NONE.toString()+"');");
        db.execSQL("INSERT INTO PRIORITY VALUES (null, '"+Priority.LOW.toString()+"');");
        db.execSQL("INSERT INTO PRIORITY VALUES (null, '"+Priority.MEDIUM.toString()+"');");
        db.execSQL("INSERT INTO PRIORITY VALUES (null, '"+Priority.HIGH.toString()+"');");
        db.execSQL("INSERT INTO PRIORITY VALUES (null, '"+Priority.URGENT.toString()+"');");

        //Create the GROCERY_TYPE TABLE
        db.execSQL("CREATE TABLE GROCERY_TYPE(_id INTEGER PRIMARY KEY AUTOINCREMENT,Name TEXT, IsInFilter INTEGER);");
        db.execSQL("INSERT INTO GROCERY_TYPE VALUES(null, 'Meats',0);");
        db.execSQL("INSERT INTO GROCERY_TYPE VALUES(null, 'Dairy',0);");
        db.execSQL("INSERT INTO GROCERY_TYPE VALUES(null, 'Fruits',0);");
        db.execSQL("INSERT INTO GROCERY_TYPE VALUES(null, 'Vegetables',0);");
        db.execSQL("INSERT INTO GROCERY_TYPE VALUES(null, 'Bakery',0);");
        db.execSQL("INSERT INTO GROCERY_TYPE VALUES(null, 'Deli',0);");
        db.execSQL("INSERT INTO GROCERY_TYPE VALUES(null, 'Canned',0);");
        db.execSQL("INSERT INTO GROCERY_TYPE VALUES(null, 'Sebas',0);");
        db.execSQL("INSERT INTO GROCERY_TYPE VALUES(null, 'Toiletries',0);");
        db.execSQL("INSERT INTO GROCERY_TYPE VALUES(null, 'Cleaning',0);");
        db.execSQL("INSERT INTO GROCERY_TYPE VALUES(null, 'General',0);");

        //Populate the GROCERY_TYPE table
        //Create the GROCERIES TABLE
        db.execSQL("CREATE TABLE GROCERIES(_id INTEGER PRIMARY KEY AUTOINCREMENT,Name TEXT, TypeOfGrocery INTEGER, IsSelected INTEGER);");
        //Populate the GROCERIES table with an initial set of common grocery products
        //Meats
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Beef steaks',1,0);");//1
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Mince beef',1,0);");//2
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Chicken breast',1,0);");//3
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Pork chops',1,0);");//4
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Mince pork',1,0);");//5
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Turkey breast',1,0);");//6
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Salmon',1,0);");//7
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Cod',1,0);");//8
        //Dairy
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Milk',2,0);");//9
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Almond milk',2,0);");//10
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Butter',2,0);");//11
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Baking butter',2,0);");//12
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Eggs',2,0);");//13
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Yogurt',2,0);");//14
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Feta cheese',2,0);");//15
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Mozzarella cheese',2,0);");//16
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Yellow chesse',2,0);");//17
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Grated cheese',2,0);");//18
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Parmesan cheese',2,0);");//19
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Cream cheese',2,0);");//20
        //Fruits
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Apples',3,0);");//21
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Bananas',3,0);");//22
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Oranges',3,0);");//23
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Clementines',3,0);");//24
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Strawberries',3,0);");//25
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Raspberries',3,0);");//26
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Blueberries',3,0);");//27
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Melon',3,0);");//28
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Watermelon',3,0);");//29
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Pineapple',3,0);");//30
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Lemons',3,0);");//31
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Limes',3,0);");//32
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Red grapes',3,0);");//33
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Green grapes',3,0);");//34
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Pears',3,0);");//35
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Mango',3,0);");//36
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Peaches',3,0);");//37
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Kiwi',3,0);");//38
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Plums',3,0);");//39
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Avocado',3,0);");//40
        //Vegetables
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Onions',4,0);");//41
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Red onions',4,0);");//42
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Tomatoes',4,0);");//43
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Cherry tomatoes',4,0);");//44
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Potatoes',4,0);");//45
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Green peppers',4,0);");//46
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Yellow peppers',4,0);");//47
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Red peppers',4,0);");//48
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Lettuce',4,0);");//49
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Rocket',4,0);");//50
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Parsley',4,0);");//51
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Coriander',4,0);");//52
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Spring onions',4,0);");//53
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Spinach',4,0);");//54
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Mushrooms',4,0);");//55
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Chestnut mushrooms',4,0);");//56
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Portobello mushrooms',4,0);");//57
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Radish',4,0);");//58
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Celery',4,0);");//59
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Beetroots',4,0);");//60
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Leek',4,0);");//61
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Butternut squash',4,0);");//62
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Courgettes',4,0);");//63
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Broccoli',4,0);");//64
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Cucumber',4,0);");//65
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Garlic',4,0);");//66
        //Bakery
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Bread',5,0);");//67
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Hotdog buns',5,0);");//68
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Burger buns',5,0);");//69
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Bagels',5,0);");//70
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Irish soda bread',5,0);");//71
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Baggette',5,0);");//72
        //Deli
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Ham',6,0);");//73
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Fuet',6,0);");//74
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Salami',6,0);");//75
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Pate',6,0);");//76
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Chorizo',6,0);");//77
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Serrano ham',6,0);");//78
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Roast beef',6,0);");//79
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Turkey breast',6,0);");//80
        //Canned
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Tuna',7,0);");//81
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Mackerel',7,0);");//82
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Tomato paste',7,0);");//83
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Chopped tomatoes',7,0);");//84
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'White beens',7,0);");//85
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Cheak beans',7,0);");//86
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Kidney beans',7,0);");//87
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Sweet corn',7,0);");//88
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Peas',7,0);");//89
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Mixed fruits',7,0);");//90
        //Sebas
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Granola',8,0);");//91
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Doritos',8,0);");//92
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Pringles',8,0);");//93
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Popcorn',8,0);");//94
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Cereal',8,0);");//95
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Ice cream',8,0);");//96
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Pirulin',8,0);");//97
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Digestives',8,0);");//98
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Oreo',8,0);");//99
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Energy bars',8,0);");//100
        //Toileteries
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Soap',9,0);");//101
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Shampoo',9,0);");//102
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Conditioner',9,0);");//103
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Shower gel',9,0);");//104
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Tooth paste',9,0);");//105
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Tooth brush Sebas',9,0);");//106
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Tooth brush Gise',9,0);");//107
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Tooth brush Jose',9,0);");//108
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Cotton buds',9,0);");//109
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Toilet paper',9,0);");//110
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Baby wipes',9,0);");//111
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Man shaver',9,0);");//112
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Woman shaver',9,0);");//113
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Shaving foam/gel',9,0);");//114
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Baby powder',9,0);");//115
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Deodorant Sebas',9,0);");//116
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Deodorant Gise',9,0);");//117
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Deodorant Jose',9,0);");//118
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Toilet spray',9,0);");//119
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Cotton',9,0);");//120
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Makeup wipes',9,0);");//121
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Baby cream',9,0);");//122
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Hand wash',9,0);");//123
        //Cleaning
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Bleach',10,0);");//124
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Floor cleaner',10,0);");//125
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Black bags',10,0);");//126
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'White bags',10,0);");//127
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Compost bags',10,0);");//128
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Washing powder',10,0);");//129
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Whashing up liquid',10,0);");//130
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Carpet cleaning foam',10,0);");//131
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Cleaning wipes',10,0);");//132
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Brazzo',10,0);");//133
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Pride',10,0);");//134
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Softener liquid',10,0);");//135
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Multipurpose cleaner',10,0);");//136
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Kitchen cleaner',10,0);");//137
        //General
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'American mustard',11,0);");//138
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'English mustard',11,0);");//139
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Honey mustard',11,0);");//140
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Mustard powder',11,0);");//141
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Garlic sauce',11,0);");//142
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Horseradish sauce',11,0);");//143
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Soy sauce',11,0);");//144
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Sweet chili sauce',11,0);");//145
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Tabasco sauce',11,0);");//146
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Worcestershire sauce',11,0);");//147
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Mayoneese',11,0);");//148
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Holland sauce',11,0);");//149
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Ketchup',11,0);");//150
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'BBQ sauce',11,0);");//151
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Jelly',11,0);");//152
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Peanut butter',11,0);");//153
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Salt',11,0);");//154
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Pepper',11,0);");//155
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Coffee',11,0);");//156
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Tea',11,0);");//157
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Water',11,0);");//158
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Coke',11,0);");//159
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Orange juice',11,0);");//160
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Apple juice',11,0);");//161
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Pasta',11,0);");//162
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Frozen fries',11,0);");//163
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Oatmeal',11,0);");//164
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Crackers',11,0);");//165
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Baking powder',11,0);");//166
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Flour',11,0);");//167
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Beer',11,0);");//168
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Wine',11,0);");//169
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Frozen pizza',11,0);");//170
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Ice cubes',11,0);");//171
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Sunflower oi',11,0);");//172
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Olive oil',11,0);");//173
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Coconut oil',11,0);");//174
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Nutella',11,0);");//175
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Dog food',11,0);");//176
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Dog treats',11,0);");//177
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Candles',11,0);");//178
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Matches',11,0);");//179
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Stove logs',11,0);");//180
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Brown sugar',11,0);");//181
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'White sugar',11,0);");//182
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Shoe spray',11,0);");//183
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Rice',11,0);");//184
        db.execSQL("INSERT INTO GROCERIES VALUES(null, 'Cuscus',11,0);");//185

        //Create TASKS table to hold all the differnt taks input by the user. This table is not populated from the begining
        db.execSQL("CREATE TABLE TASK (_id INTEGER PRIMARY KEY AUTOINCREMENT, \n" +
                "Description TEXT, Category INTEGER, Priority INTEGER, IsDone INTEGER, \n" +
                "IsAppointment INTEGER, DueDate BIGINT, IsArchived INTEGER, IsSelected INTEGER, Notes TEXT, \n" +
                "DateCreated BIGINT, DateClosed BIGINT);");

        db.execSQL("INSERT INTO TASK VALUES(null, 'Task1: This is a test 1',1,1,0,0,-1,0,0,'This is the first task to be done...',1533829500000,-1);");
        db.execSQL("INSERT INTO TASK VALUES(null, 'Task2: This is a test 2',2,2,0,0,-1,0,0,'This is the first task to be done...',1534829500000,-1);");
        db.execSQL("INSERT INTO TASK VALUES(null, 'Task3: This is a test 3',2,3,0,0,-1,0,1,'This is the first task to be done...',1535829500000,-1);");
        db.execSQL("INSERT INTO TASK VALUES(null, 'Task4: This is a test 4',4,4,0,0,1533829500000,0,1,'This is the first task to be done...',1536829500000,-1);");

        Log.d("Ext_DBOncreate","Exit onCreate method in TasksDB class.");
    }//End of OnCreate method

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }// End of onUpgrade method

    //Method to add a new task within the database
        public int addItem(Object item) {
        Log.d("Ent_addItem","Enter addItem method in TasksDB class.");
        //Declare and instantiate a new database object to handle the database operations
        SQLiteDatabase db = getWritableDatabase();
            //Declare and initialize a query string variables
            String insertInto = "INSERT INTO ";
            String values = " VALUES(null, ";
            String closeBracket =")";
            String table ="";
            String fields ="";
            String sql = "SELECT MAX(_id) FROM ";
            //Declare and initialize int variable to hold the task id to be returned. Default value is -1
            int id =-1;
            //Declare and initialize variables to be used in the SQL statement (Values a got from task object parameter)
            String description;
            int category;
            int priority;
            int isDone;
            int isAppointment;
            long dueDate;
            int isArchived;
            int isSelected;
            String notes;
            long dateCreated;
            long dateClosed ;
            //If else statements to check the class each object is from
            if(item instanceof GroceryType){
               //if item is a GroceryType object, update the Task table where the id corresponds
               table= "GROCERY_TYPE";
               fields="Name = '"+((GroceryType) item).getName()+"'";
                sql+= table;
                Log.d("addGroceryType","GroceryType to be added in the addItem method in TasksDB class.");
            }else if(item instanceof Category){
                //if item is a Category object, update the Task table where the id corresponds
                table = "CATEGORY";
                fields="'"+((Category) item).getName()+"'";
                sql+= table;
                Log.d("addCategory","Catgory to be added in the addItem method in TasksDB class.");
            }else if(item instanceof Grocery){
                //if item is a Grocery object, update the Task table where the id corresponds
                description = ((Task)item).getDescription();
                table = "GROCERIES";
                fields=" '"+((Grocery) item).getDescription()+
                            "', "+((Grocery)item).getType().getId()+
                            ", "+toInt(((Grocery)item).isSelected());
                sql+=" "+table;
                Log.d("addGrocery","Grocery to be added in the addItem method in TasksDB class.");
            }else if(item instanceof Task){
                table ="TASK";
                description = ((Task)item).getDescription();
                category = ((Task)item).getCategory().getId();
                priority = ((Task)item).getPriority().increaseOrdinal();
                isDone = toInt(((Task)item).isDone());
                isAppointment = toInt(((Task)item).isAppointment());
                dueDate = ((Task)item).getDueDate();
                isArchived = toInt(((Task)item).isArchived());
                isSelected = toInt(((Task)item).isSelected());
                notes = ((Task)item).getNotes();
                dateCreated = ((Task)item).getDateCreated();
                dateClosed = ((Task)item).getDateClosed();
                fields = "'"+description+"', "+ category+", "+priority+", "+isDone+", "+isAppointment+", "+
                        dueDate+", "+isArchived+", "+isSelected+", '"+notes+"', "+dateCreated+", "+dateClosed;
                sql ="SELECT _id FROM TASK WHERE DateCreated = "+ ((Task) item).getDateCreated();
                Log.d("addTask","Task to be added in the addItem method in TasksDB class.");
            }//End of if else statements
            //Execute the sql command to update corresponding table
            String insertSql = insertInto+table+values+fields+closeBracket;
            db.execSQL(insertSql);
            //Declare and isntantiate a cursor object to hold the id of task just added into the TASK table
            Cursor c =db.rawQuery(sql,null);
            //Check the cursor is not empty and move to next row
            if (c.moveToNext()){
                //Make the id equal to the _id field in the database
                id = c.getInt(0);
            }//End of if condition
            //Close both  database and cursor
            c.close();
            db.close();
            Log.d("Ext_addTask","Exit addTask method in TasksDB class.");
            //Return id of item just added into database
            return id;
    }//End of addTask method

    //Method to delete a task within the database
    public boolean deleteItem(Object item) {
        Log.d("Ent_deleteItem","Enter deleteItem method in TasksDB class.");
        boolean result = false;
        int id=-1;
        //Declare and instantiate a new database object to handle the database operations
        SQLiteDatabase db = getWritableDatabase();
        //Declare and initialize a query string
        String deleteFrom = "DELETE FROM ";
        String whereID =" WHERE _id = ";
        String table="";
        if(item instanceof GroceryType){
            table = "GROCERY_TYPE";
            //Delete all items from GROCERIES table where type id is equal to id
           // db.execSQL(deleteFrom+"GOCERIES WHERE TypeOfGroceries = "+ ((GroceryType) item).getId());
            id= ((GroceryType) item).getId();
            Log.d("deleteGROCERY_TYPE","GROCERY_TYPE to be deleted.");
        }else if(item instanceof Category){
            table ="CATEGORY";
            //Delete all items from TASK table where Category ID = id
           // db.execSQL(deleteFrom+"TASK WHERE Category = "+ ((Category) item).getId());
            id = ((Category) item).getId();
            Log.d("deleteCATEGORY","CATEGORY to be deleted.");
        }else if(item instanceof Grocery){
            table = "GROCERIES";
            id = ((Grocery) item).getId();
            Log.d("deleteGROCERY","GROCERY to be deleted.");
        }else if(item instanceof Task){
            table = "TASK";
            id = ((Task) item).getId();
            Log.d("deleteTASK","TASK to be deleted.");
        }//End of if else statements

        //Run SQL statement to delete the task with id x from the TASK table
        db.execSQL(deleteFrom+ table +whereID+ id);
        db.close();
        result = true;
        Log.d("Ext_deleteItem","Exit deleteItem method in TasksDB class.");
        return result;
    }//End of deleteTask method

    //Method to update an existing Task
    public void updateItem(Object item) {
        Log.d("Ent_UpdateItem","Enter updateItem method in TasksDB class.");
        //Declare and instantiate a new database object to handle the database operations
        SQLiteDatabase bd = getWritableDatabase();
        //Declare and initialize a query string variables
        String update = "UPDATE ";
        String set = " SET ";
        String where =" WHERE _id = ";
        String table ="";
        String fields ="";
        int id=-1;
        //If else statements to check the class each object is from
        if(item instanceof GroceryType){
            //if item is a GroceryType object, update the Task table where the id corresponds
            id = ((GroceryType) item).getId();
            table= "GROCERY_TYPE";
            fields="Name = '"+((GroceryType) item).getName()+'"';
        }else if(item instanceof Category){
            id = ((Category) item).getId();
            //if item is a Category object, update the Task table where the id corresponds
            table = "CATEGORY";
            fields="Name ='"+ ((Category) item).getName()+"'";
        }else if(item instanceof Grocery){
            id = ((Grocery) item).getId();
            //if item is a Grocery object, update the Task table where the id corresponds
            table = "GROCERIES";
            fields="Name = '"+((Grocery) item).getDescription()+
                    "', TypeOfGrocery = "+((Grocery)item).getType().getId()+
                    ", IsSelected = "+toInt(((Grocery)item).isSelected());
        }else if(item instanceof Task){
            id = ((Task) item).getId();
            //if item is a Task object, update the Task table where the id corresponds
            table ="TASK";
            fields = "Description = '"+((Task) item).getDescription()+
                    "', Category = "+ ((Task) item).getCategory().getId()+
                    ", Priority = "+ ((Task) item).getPriority().increaseOrdinal()+
                    ", IsDone = "+ toInt(((Task) item).isDone())+
                    ", IsAppointment = "+toInt(((Task) item).isAppointment())+
                    ", DueDate = "+((Task) item).getDueDate()+
                    ", IsArchived = "+toInt(((Task) item).isArchived())+
                    ", IsSelected = "+toInt(((Task)item).isSelected())+
                    ", Notes = '"+ ((Task) item).getNotes()+
                    "', DateCreated = "+((Task) item).getDateCreated()+
                    ", DateClosed = "+((Task) item).getDateClosed();
            Log.d("UpdateTask","Task item to be updated in database.");
        }//End of if else statements
        //Execute the sql command to update corresponding table
        String sql = update+table+set+fields+where+id;
        bd.execSQL(sql);
        //Close the database connection
        bd.close();
        Log.d("Ext_UpdateTable","Exit updateItem method in TasksDB class.");
    }//End of UpdateTable method

    //Method to internally convert a boolean into a int number 1 or  0
    public int toInt(boolean bool){
        Log.d("Ent_toInt","Enter toInt method in TasksDB class.");
        if(bool){
            Log.d("Ext_toInt","Exit toInt method in TasksDB class (Returned value 1 ).");
            return 1;
        }else{
            Log.d("Ext_toInt","Exit toInt method in TasksDB class (Returned value 0).");
            return 0;
        }//End of if else statement
    }//End of toInt

    //Method to internally convert an int into a boolean true or false. Any value different from 0 will be true
    public boolean toBoolean (int valueToConvert){
        Log.d("Ent_toBool","Enter toBoolean method in the TaskDB class.");
        boolean bool;
        if(valueToConvert==0){
            bool = false;
        }else{
            bool = true;
        }//End of if else statement
        Log.d("Ext_toBool","Exit toBoolean method in the TaskDB class.");
        return bool;
    }//End of toBoolean method

    //Method to extract a Category from a cursor object
    public Category extractCategory(Cursor c){
        Log.d("Ent_ExtractCategory","Enter extractCategory method in the TaskDB class.");
        //Declare and initialize a null category object, the one to be returned by the method
        Category category =null;
        //Declare an int variable to hold the Category id retrieved from the cursor
        int id;
        //Declare a string object to hold the name attribute retrieved from the cursor
        String name="";
        //Retrieve the id value from the cursor object
        id = c.getInt(0);
        //Retrieve the name value from the cursor object
        name = c.getString(1);
        //Create a new Category object by using the full constructor
        category = new Category(id,name);
        Log.d("Ext_ExtractCategory","Exit extractCategory method in the TaskDB class.");
        //Return the category object
        return category;
    }//End of extractCategory method

    //Method to extract a GroceryType from a cursor object
    public GroceryType extractGroceryType(Cursor c){
        Log.d("Ent_ExtractGroceryType","Enter extractGroceryType method in the TaskDB class.");
        //Declare and initialize a null GroceryType object, the one to be returned by the method
        GroceryType type=null;
        //Declare an int variable to hold the Category id retrieved from the cursor
        int id;
        //Declare a string object to hold the name attribute retrieved from the cursor
        String name="";
        //Declare a boolean to hold the isInFilter attribute
        int isInFilter = 0;
        //Retrieve the id value from the cursor object
        id = c.getInt(0);
        name = c.getString(1);
        isInFilter = c.getInt(2);
        type = new GroceryType(id,name,toBoolean(isInFilter));
        Log.d("Ext_ExtractGroceryType","Exit extractGroceryType method in the TaskDB class.");
        return type;
    }//End of extractGroceryType method

    //Method to extract an unit form a cursor object
    public Task extractTask(Cursor c){
        Log.d("Ent_ExtractTask","Enter extractTask method in the TaskDB class.");
        //Declare null Task object to be returned by method
        Task task = null;
        //Declare variables to hold data coming from cursor and to be used to call Task constructor
        int id;
        String description ="";
        Category category;
        Priority priority;
        boolean isDone;
        boolean isAppointment;
        long dueDate;
        boolean isArchived;
        String notes;
        long dateCreated;
        long dateClosed;
        boolean isSelected;
        //Populate variables with values coming from the Cursor object
        id = c.getInt(0);
        description = c.getString(1);
        category = this.findCategoryById(c.getInt(2));
        priority = Priority.findPriorityById(c.getInt(3)-1);//Decrease the returned value by one as ordinal starts at 0 while DB start counting at 1
        isDone = toBoolean(c.getInt(4));
        isAppointment = toBoolean(c.getInt(5));
        dueDate = c.getLong(6);
        isArchived = toBoolean(c.getInt(7));
        isSelected = toBoolean(c.getInt(8));
        notes = c.getString(9);
        dateCreated = c.getLong (10);
        dateClosed = c.getLong(11);
        //Create the Task object
        task = new Task(id,description,category,priority,isDone,isAppointment,dueDate,isArchived,
                notes,isSelected,dateCreated,dateClosed);
        Log.d("Ext_ExtractTask","Exit extractTask method in the TaskDB class.");
        return task;
    }//End of extractTask method

    public Task extractTask(Cursor c,int id){
        //Declare null Task object to be returned by method
        Task task = null;
        boolean found = false;
        //int i =1;
        while(c.moveToNext()){
            if(c.getInt(0)== id){
                task = extractTask(c);
                found = true;
            }
        }
        return task;
    }//End of extractTask method

    public Grocery extractGrocery(Cursor c){
        Log.d("Ent_ExtractGrocery","Enter extractGrocery method in the TaskDB class.");
        //Declare null Grocery object to be returned by method
        Grocery grocery = null;
        //Declare variable to hold data coming from cursor and to be used to call Grocery constructor
        int id;
        String groceryName ="";
        GroceryType type;
        boolean isSelected;
        //Populate variables with values coming from the Cursor object
        id = c.getInt(0);
        groceryName = c.getString(1);
        type = this.findGroceryTypeById(c.getInt(2));
        isSelected = toBoolean(c.getInt(3));
        Category category = this.findCategoryByName("Groceries");
        //Create the Grocery object
        grocery = new Grocery(id,groceryName,category,type,isSelected);
        Log.d("Ext_ExtractGrocery","Exit extractGrocery method in the TaskDB class.");
        return grocery;
    }//End of extractGrocery method

    //Method to retrieve the list of categories stored on the database
    public ArrayList<Category> getCategoryList(){
        Log.d("Ent_getCategoryList","Enter getCategoryList method in the TaskDB class.");
        //Declare and instantiate Array list of Category objects
        ArrayList<Category> list = new ArrayList<Category>();
        //Define a string to hold the sql query
        String query = "SELECT * FROM CATEGORY ";
        //Declare a category object to hold temporarily the Category objects to be created
        Category item;
        //Declare and instantiate a cursor object to hold data retrieved from sql query
        Cursor c = runQuery(query);
        //Check the cursor is not null or empty
        if(c !=null && c.getCount()>0){
            //Loop through the cursor and extract each row as a Category object
            while(c.moveToNext()){
                //Call method to extract the category
                item = extractCategory(c);
                //Add category to the Array list
                list.add(item);
            }//End of while loop
        }//End of if statement to check cursor is not null or empty
        Log.d("Ext_getCategoryList","Exit getCategoryList method in the TaskDB class.");
        return list;
    }//End of getGroceryList method

    //Method to retrieve the list of categories stored on the database
    public Cursor getCategoryList(String query){
        Log.d("Ent_getCategoryList","Enter getCategoryList method in the TaskDB class.");
        //Declare and instantiate Array list of Category objects
        Cursor list = null;
        //Define a string to hold the sql query
        //String query = "SELECT * FROM CATEGORY ";
        //Declare a category object to hold temporarily the Category objects to be created
        //Category item;
        //Declare and instantiate a cursor object to hold data retrieved from sql query
        Cursor c = runQuery(query);
        //Check the cursor is not null or empty
        Log.d("Ext_getCategoryList","Exit getCategoryList method in the TaskDB class.");
        return list;
    }//End of getGroceryList method


    //Method to retrieve the list of grocery types stored on the database
    public ArrayList<GroceryType> getGroceryTypeList(){
        Log.d("Ent_getGroceryTypeList","Enter getGroceryTypeList method in the TaskDB class.");
        //Declare and instantiate Array list of GroceryType objects
        ArrayList<GroceryType> list = new ArrayList<GroceryType>();
        //Define a string to hold the sql query
        String query = "SELECT * FROM GROCERY_TYPE ";
        //Declare a GroceryType object to hold temporarily the Category objects to be created
        GroceryType item;
        //Declare and instantiate a cursor object to hold data retrieved from sql query
        Cursor c = runQuery(query);
        //Check the cursor is not null or empty
        if(c !=null && c.getCount()>0){
            //Loop through the cursor and extract each row as a GroceryType object
            while(c.moveToNext()){
                //Call method to extract the grocery
                item = extractGroceryType(c);
                //Add grocery to the Array list
                list.add(item);
            }//End of while loop
        }//End of if statement to check cursor is not null or empty
        Log.d("Ext_getGroceryTypeList","Exit getGroceryTypeList method in the TaskDB class.");
        return list;
    }//End of getGroceryList method

    //Method to retrieve a list of groceries stored on the database
    public ArrayList<Grocery> getGroceryList(String query){
        Log.d("Ent_getGroceryList","Enter getGroceryList method in the TaskDB class.");
        //Declare and instantiate Array list of Grocery objects
        ArrayList<Grocery> list = new ArrayList<Grocery>();
        //Define a string to hold the sql query
        //String query = "SELECT * FROM GROCERIES ORDER BY TypeOfGrocery";
        //Declare a Grocery object to hold temporarily the Category objects to be created
        Grocery item;
        //Declare and instantiate a cursor object to hold data retrieved from sql query
        Cursor c = runQuery(query);
        //Check the cursor is not null or empty
        //Loop through the cursor and extract each row as a Grocery object
        if(c !=null && c.getCount()>0){
            while(c.moveToNext()){
                //Call method to extract the grocery
                item = extractGrocery(c);
                //Add grocery to the Array list
                list.add(item);
            }//End of while loop
        }//End of if statement to check cursor is not null or empty
        Log.d("Ext_getGroceryList","Exit getGroceryList method in the TaskDB class.");
        return list;
    }//End of getGroceryList method

    //Method to retrieve a list of groceries stored on the database
    public ArrayList<Task> getTaskList(String query){
        Log.d("Ent_getTaskList","Enter getTaskList method in the TaskDB class.");
        //Declare and instantiate Array list of Grocery objects
        ArrayList<Task> list = new ArrayList<Task>();
        //Define a string to hold the sql query
        //String query = "SELECT * FROM TASK";
        //Declare a Grocery object to hold temporarily the Category objects to be created
        Task item;
        //Declare and instantiate a cursor object to hold data retrieved from sql query
        Cursor c = runQuery(query);
        //Check the cursor is not null or empty
        if(c !=null && c.getCount()>0){
            //Loop through the cursor and extract each row as a Task object
            while(c.moveToNext()){
                //Call method to extract the grocery
                item = extractTask(c);
                //Add Task to the Array list
                list.add(item);
            }//End of while loop
        }//End of if statement to check cursor is not null or empty
        Log.d("Ext_getTaskList","Exit getTaskList method in the TaskDB class.");
        return list;
    }//End of getGroceryList method

    //Method to create a database object, a cursorl, run the sql query and return the result cursor
    public Cursor runQuery(String query){
        Log.d("Ent_runQuery","Enter runQuery method.");
        Cursor cursor = null;
        SQLiteDatabase db = getReadableDatabase();
        cursor = db.rawQuery(query,null);
        //cursor.moveToFirst();
        Log.d("Ext_runQuery","Exit runQuery method.");
        return cursor;
    }//End of runQuery method

    //Method to find a category by passing in id number
    public Category findCategoryByName(String name){
        Log.d("Ent_FindCatByName","Enter the findCategoryByName method in the TasksDB class.");
        //Declare and instantiate a new cursor object to hold the list of categories by running a sql query
        Cursor categories = runQuery("SELECT * FROM CATEGORY");
        //Declare and instantiate a null Category object
        Category category = null;
        //Declare and set to false a boolean flag to prompt when the category it's being looked for is found
        Boolean found = false;
        //While loop to iterate through the cursor (list of categories)
        while(categories.moveToNext() && !found){
            //Check the name of each category by accessing the corresponding column in the cursor
            if(categories.getString(1).toLowerCase().equals(name.toLowerCase())){
                //if the names are the same, populate the category object by extracting the category object from the cursor
                category = extractCategory(categories);
                //Set boolean flag to true
                found = true;
            }//End of if statement to check the current item name
        }//End of while loop to iterate
        Log.d("Ent_FindCatByName","Enter the findCategoryByName method in the TasksDB class.");
        return category;
    }//End of findCategoryById method

    //Method to find a category by passing in id number
    public Category findCategoryById(int id){
        Log.d("Ent_FindCatById","Enter the findCategoryById method in the TaskDB class.");
        //Declare and instantiate a new cursor object to hold the list of categories by running a sql query
        Cursor categories = runQuery("SELECT * FROM CATEGORY");
        //Declare and instantiate a null Category object
        Category category = null;
        //Declare and set to false a boolean flag to prompt when the category it's being looked for is found
        Boolean found = false;
        //While loop to iterate through the cursor (list of categories)
        while(categories.moveToNext() && !found){
            //Check the id of each category by accessing the corresponding column in the cursor
            if(categories.getInt(0)== id){
                //if the names are the same, populate the category object by extracting the category object from the cursor
                category = extractCategory(categories);
                //Set boolean flag to true
                found = true;
            }//End of if statement to check the current item name
        }//End of while loop to iterate
        Log.d("Ent_FindCatById","Enter the findCategoryById method in the TaskDB class.");
        return category;
    }//End of findCategoryById method

    //Method to find a category by passing in id number
    public GroceryType findGroceryTypeById(int id){
        Log.d("Ent_FindCatById","Enter the findCategoryById method in the TaskDB class.");
        //Declare and instantiate a new cursor object to hold the list of grocery types by running a sql query
        Cursor groceryTypes = runQuery("SELECT * FROM GROCERY_TYPE");
        //Declare and instantiate a null groceryType object
        GroceryType type = null;
        //Declare and set to false a boolean flag to prompt when the grocery type it's being looked for is found
        Boolean found = false;
        //While loop to iterate through the cursor (list of grocery types)
        while(groceryTypes.moveToNext() && !found){
            //if the names are the same, populate the category object by extracting the category object from the cursor
            if(groceryTypes.getInt(0)== id){
                type = extractGroceryType(groceryTypes);
                //Set boolean flag to true
                found = true;
            }//End of if statement to check the current item name
        }//End of while loop to iterate
        Log.d("Ent_FindCatById","Enter the findCategoryById method in the TaskDB class.");
        return type;
    }//End of findCategoryById method

    //Method to Update the isSelected attribure of a Task or Grocery
    public boolean updateBoolAttribute(String table,String atrtibute ,int id,boolean boolValue){
        Log.d("Ent_UpdateBoolAttr","Enter the updateBoolAttribute method in the TaskDB class.");
        //Declare and instantiate a new database object to handle the database operations
        SQLiteDatabase bd = getWritableDatabase();
        //Declare and initialize a query string variables
        boolean success=false;
        String update = "UPDATE ";
        String set =" SET";
        String where = " WHERE _id = ";
        //Check the current category is the Groceries Category
        if(table.equals(MainActivity.getGroceryCategory())){
            //If that is the case set the table name to GROCERIES and attribute to update the IsSelected (the only boolean attribute)
            table= table.toUpperCase();
            set += " IsSelected = ";
        }else{
            //Otherwise, set table to be TASK and check the attribute passed in as a parameter
            table = "TASK";
            //Check the attribute text and include it  into the sql statement
            switch(atrtibute){
                case "IsSelected":
                    set += " IsSelected = ";
                    break;
                case "IsDone":
                    set += " IsDone = ";
                    break;
                case "IsAppointment":
                    set += " IsAppointment = ";
                    break;
                case "IsArchived":
                    set += " IsArchived =  ";
                    break;
                default:
                    set += " IsSelected = ";
                    break;
            }//End of switch statement
        }//End of if else statement to check the current category
        //Construct the complete sql query
        String sql = update+table+set+toInt(boolValue)+where+id;
        //Try Catch block to execute the sql command to update corresponding table
        try{
            //Run the query and change success to true if no issues
            bd.execSQL(sql);
            success = true;
        }catch (Exception e) {
            //Log the exception message
            Log.d("DataBaseException",e.toString());
        }//End of try and catch block
        Log.d("Ext_UpdateBoolAttr","Exit the updateBoolAttribute method in the TaskDB class.");
        return success;
    }//End of updateBoolAttribute method

    //Method to update the isInFilter attribute of the Grocery Type table
    public boolean updateIsInFilter(int id, boolean isInFilter){
        Log.d("Ent_UpdateIsInFilter","Enter the updateIsInFiler method in the TaskDB class.");
        //Declare and instantiate a new database object to handle the database operations
        SQLiteDatabase bd = getWritableDatabase();
        //Declare and initialize a query string variables
        boolean success=false;
        String sql = "UPDATE GROCERY_TYPE SET IsInFilter = "+toInt(isInFilter)+" WHERE _id = "+id;
        //Try Catch block to execute the sql command to update corresponding table
        try{
            //Run the query and change success to true if no issues
            bd.execSQL(sql);
            success = true;
        }catch (Exception e) {
            //Log the exception message
            Log.d("DataBaseException",e.toString());
        }//End of try catch block
        Log.d("Ext_UpdateIsInFiler","Exit the updateIsInFiler method in the TaskDB class.");
        return success;
    }//End of updateIsInFilter method

    public boolean updateUser(String column,String value){
        Log.d("Ent_UpdateUser","Enter the updateUser method in the TaskDB class.");
        boolean success = false;
        Cursor userData;
        //Declare and instantiate a new database object to handle the database operations
        SQLiteDatabase bd = getWritableDatabase();
        userData = this.runQuery("SELECT * FROM USER");
        String updateUser ="UPDATE USER SET ";
        String whereId = " WHERE _id = ";
        String sql = "";
        if(userData.moveToNext()){

            sql = updateUser+column+" = '"+value+"'"+ whereId+userData.getInt(0);
        }
        //Try Catch block to execute the sql command to update corresponding table
        try{
            //Run the query and change success to true if no issues
            bd.execSQL(sql);
            success = true;
        }catch (Exception e) {
            //Log the exception message
            Log.d("DataBaseException",e.toString());
        }//End of try and catch block
        Log.d("Ext_UpdateUser","Exit the updateUser method in the TaskDB class.");
        return success;
    }//End of updateUser method

    public boolean updateAppState(int currentCategory,int isSearchFilter, int isChecked, String lastSearchTask, String lastSearchGrocery){
        Log.d("Ent_UpdateState","Enter the updateAppState method in the TaskDB class.");
        boolean success = false;
        Cursor appState;
        //Declare and instantiate a new database object to handle the database operations
        SQLiteDatabase db = getWritableDatabase();
        appState = this.runQuery("SELECT * FROM APP");
        String updateState ="UPDATE APP SET ";
        String fields = " currentCategory = " + currentCategory + ","+
                        " isSearchFilter = " + isSearchFilter + ","+
                        " isChecked = "+ isChecked + ","+
                        " lastSearchTask = '" + lastSearchTask + ","+
                        " lastSearchGrocery = '" + lastSearchGrocery;
        String whereId = " WHERE _id = ";
        String sql = "";
        if(appState.moveToNext()){
            sql = updateState+fields+ whereId+appState.getInt(0);
        }
        //Try Catch block to execute the sql command to update corresponding table
        try{
            //Run the query and change success to true if no issues
            db.execSQL(sql);
            success = true;
        }catch (Exception e) {
            //Log the exception message
            Log.d("DataBaseException",e.toString());
        }//End of try and catch block
        Log.d("Ext_UpdateState","Exit the updateAppState method in the TaskDB class.");
        return success;
    }//End of updateAppState






}//End of TaskDB class