package io.github.jlrods.mytodolist;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TabHost;

/**
 * Created by rodjose1 on 21/01/2019.
 */

public class AboutActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("Ent_onCreateAbout","Enter the onCreate method method on AboutActivity.");
        setTheme(MainActivity.setAppTheme(this));
        super.onCreate(savedInstanceState);
        //Set layout for main activity
        setContentView(R.layout.activity_about);
        TabHost host = (TabHost) findViewById(R.id.tabHost);
        host.setup();

        //Tab 1
        TabHost.TabSpec spec = host.newTabSpec(getResources().getString(R.string.aboutMain));
        spec.setContent(R.id.aboutMain);
        spec.setIndicator(getResources().getString(R.string.aboutMain));
        host.addTab(spec);

        //Tab 2
        spec = host.newTabSpec(getResources().getString(R.string.aboutAdd));
        spec.setContent(R.id.aboutAdd);
        spec.setIndicator(getResources().getString(R.string.aboutAdd));
        host.addTab(spec);

        //Tab 3
        spec = host.newTabSpec(getResources().getString(R.string.aboutNav));
        spec.setContent(R.id.aboutNav);
        spec.setIndicator(getResources().getString(R.string.aboutNav));
        host.addTab(spec);
        Log.d("Ext_onCreateAbout","Exit the onCreate method method on AboutActivity.");
    }// End of onCreate method

    @Override
    protected void onSaveInstanceState(Bundle saveState) {
        //Call super method
        super.onSaveInstanceState(saveState);
        Log.d("Ent_onSaveInstance","Enter the overridden section of onSaveInstanceSate method on AboutActivity.");
        //Save the current tab
        TabHost host = (TabHost) findViewById(R.id.tabHost);
        int currentTab = host.getCurrentTab();
        saveState.putInt("currentTab",currentTab);
        Log.d("Ext_onSaveInstance","Exit the overridden section of onSaveInstanceSate method on AboutActivity.");
    }//End of onSaveInstanceState method

    @Override
    protected void onRestoreInstanceState(Bundle restoreState) {
        //Call the super method
        super.onRestoreInstanceState(restoreState);
        Log.d("Ent_onRestoreInstance","Enter the orverriden section of onRestroeInstanceState method on AboutActivity.");
        if (restoreState != null){
            int currentTab = restoreState.getInt("currentTab");
            TabHost host = (TabHost) findViewById(R.id.tabHost);
            host.setCurrentTab(currentTab);
        }
        Log.d("Ext_onRestoreInstance","Exit the orverriden section of onRestroeInstanceState method on AboutActivity.");
    }//End of onRestoreInstanceState method
}
