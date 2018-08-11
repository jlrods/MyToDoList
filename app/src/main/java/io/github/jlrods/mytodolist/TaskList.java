package io.github.jlrods.mytodolist;

import java.util.ArrayList;

import io.github.jlrods.mytodolist.Task;

/**
 * Created by rodjose1 on 08/08/2018.
 */

public class TaskList {
    private ArrayList<Task> tasks;
    private TasksDB db;
    
    public TaskList(String query){
        tasks = db.getTaskList(query);
    }
}
