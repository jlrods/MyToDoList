package io.github.jlrods.mytodolist;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.Preference;
import android.util.Log;

/**
 * Created by rodjose1 on 18/07/2018.
 */

//Activity to handle the About app info
public class PreferencesActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Get default current property from preferences
        //SharedPreferences pref =  PreferenceManager.getDefaultSharedPreferences(this);
        //String preferedThemeID = pref.getString("appTheme","0");
        /*int themeId;
        if(preferedThemeID.equals("1")){
            themeId = R.style.AppTheme1;
        }else if(preferedThemeID.equals("2")){
            themeId = R.style.AppTheme2;
        }else{
            themeId = R.style.AppTheme;
        }
        setTheme(themeId);*/
        setTheme(MainActivity.setAppTheme(this));
        //Call super method
        super.onCreate(savedInstanceState);
        //set fragment for preferences
        getSupportFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, new PreferencesFragment())
                .commit();
        /*getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new PreferencesFragment())
                .commit();*/
    }// End of constructor method


}//End of PreferencesActivity class