package io.github.jlrods.mytodolist;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by rodjose1 on 30/07/2018.
 * Class to manage all the database management and database interaction
 */

public class TasksDB extends SQLiteOpenHelper {
    private Context context;
    //Default constructor
    public TasksDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
        this.context = context;
    }//End of default constructor

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("Ent_DBOncreate","Enter onCreate method in TasksDB class.");
        //Create CATEGORY table
        db.execSQL("CREATE TABLE CATEGORY (_id INTEGER PRIMARY KEY AUTOINCREMENT,Name TEXT);");
        //Populate the CATEGORY table with different categories for the task lists
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
        db.execSQL("INSERT INTO PRIORITY VVALUES (null, '"+Priority.URGENT.toString()+"');");

        //Create the GROCERY_TYPE TABLE
        db.execSQL("CREATE TABLE GROCERY_TYPE(_id INTEGER PRIMARY KEY AUTOINCREMENT,Name TEXT);");
        //Populate the GROCERY_TYPE table
        db.execSQL("INSERT INTO GROCERY_TYPE VALUES(null, 'Meats');");
        db.execSQL("INSERT INTO GROCERY_TYPE VALUES(null, 'Dairy');");
        db.execSQL("INSERT INTO GROCERY_TYPE VALUES(null, 'Fruits');");
        db.execSQL("INSERT INTO GROCERY_TYPE VALUES(null, 'Vegetables');");
        db.execSQL("INSERT INTO GROCERY_TYPE VALUES(null, 'Bakery');");
        db.execSQL("INSERT INTO GROCERY_TYPE VALUES(null, 'Deli');");
        db.execSQL("INSERT INTO GROCERY_TYPE VALUES(null, 'Canned');");
        db.execSQL("INSERT INTO GROCERY_TYPE VALUES(null, 'Sebas');");
        db.execSQL("INSERT INTO GROCERY_TYPE VALUES(null, 'Toiletries');");
        db.execSQL("INSERT INTO GROCERY_TYPE VALUES(null, 'Cleaning');");
        db.execSQL("INSERT INTO GROCERY_TYPE VALUES(null, 'General');");

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
                "DateCreater BIGINT, DateClosed BIGINT);");




        Log.d("Ext_DBOncreate","Exit onCreate method in TasksDB class.");
    }//End of OnCreate method

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }// End of onUpgrade method
}//End of TaskDB class
