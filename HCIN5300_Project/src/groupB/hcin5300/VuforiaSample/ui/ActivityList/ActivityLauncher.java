/*==============================================================================
 Copyright (c) 2012-2013 Qualcomm Connected Experiences, Inc.
 All Rights Reserved.
 ==============================================================================*/

package groupB.hcin5300.VuforiaSample.ui.ActivityList;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import groupB.hcin5300.VuforiaSample.R;


// This activity starts activities which demonstrate the Vuforia features
public class ActivityLauncher extends ListActivity
{
    
    private String mActivities[] = {"AR Periodic Table"};
    
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
            R.layout.activities_list_text_view, mActivities);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(R.layout.activities_list);
        setListAdapter(adapter);
    }
    
    
    @Override
    public void onListItemClick(ListView l, View v, int position, long id)
    {
        
        Intent intent = new Intent(this, AboutScreen.class);
        intent.putExtra("ABOUT_TEXT_TITLE", mActivities[position]);
        
        switch (position)
        {          
            case 0:
                intent.putExtra("ACTIVITY_TO_LAUNCH",
                    "app.VirtualButtons.VirtualButtons");
                intent.putExtra("ABOUT_TEXT", "VirtualButtons/VB_about.html");
                break;
        }
        
        startActivity(intent);
    }
}
