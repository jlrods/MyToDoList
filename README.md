# MyToDoList

## Project AIM
The aim of this project was to develop an Android Application that would be able to handle and manage personal tasks and appointments and be offered as an alternative tool for the user to manage and organize their to-do things and ideas.
## Decription
This projects was born from my wife's necessity of taking down her ideas and to-do things in a list, so she could review it afterwards and plan her time as well as check what is left to be done. So this is an attempt to fulfil her needs with an Android app that will be available for her wherever she goes and ready to add a new task or mark it as done within her customized lists.
This app offers the option of creating tasks lists and assign that category to each task so they can be filtered and grouped by category. Lists can be added and removed from the Navigation Menu, tasks can be considered as an appointment so date and time can be recorded, notes can be added at any time and finally, tasks can be marked as done (highlighted), archived or even completely removed from the app.
In addition, a built in category is available to manage the grocery shops. This list cannot be deleted, however, the actual groceries can be renamed, reassign to a different  type of grocery or add a complete new grocery.
A search functionality and a IsChecked filter are available for both tasks and groceries. 
 
## Installation
### Minimun requirements
- Android device running Ice Cream Sandwich - Version 4.01 - AP1 14 or higher.
### How to install
Instructions only valid for Android device:
1. Go to this [link](https://jlrods.github.io/Downloads.html)
2. Look for MyToDoList software in the list and click on it to start the download.
3. Open the MyToDOList1.3a.zip file by tapping the file.
4. Follow the on screen instructions and click on Install.
5. That's all! you can now test the MyToDoList software and create, delete and manage your own tasks.

## Lessons Learnt
- Full management of the RecyclerView class.
- Deal with different type of data adapters (i.e. the RecyclerView.Adapter) to bind data and the UI elements.
- Use of DrawerLayout and Navegation view classes to set up a Navigation Menu Drawer.
- Dynamically build a menu at runtime by retrieving data from the DB.
- Dynamically build SQL queries to extract data and update the RecyclerView object and other UI elements.
- Save app state on the DB, so the app can start at the same point where it was left, even when the app goes to an end in its life cycle.
- Use and implementation of DatePickerDialog and TimePickerDialog classes for displaying calendar and clock, respectively.
- Change the app themes via app preferences and recall the onCreate method so the new theme is selected.
- Use the string.xml and include the Locale qualifier so all text hardcoded on the app can be traslated to a different language (Spanish in this case).
- Set user name (or message) and profile picture (via use of third party dependency: de.hdodenhof.circleimageview.CircleImageView).
- Implementation of TabHost container in the AboutActivity.
