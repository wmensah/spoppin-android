package com.example.spoppin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.example.spoppin.objects.UserPreference;

public class SettingsActivity extends BaseSpoppinActivity implements OnSeekBarChangeListener{

	SeekBar skbRefreshInterval;
	TextView txtRefreshInterval;
	UserPreference userPrefs;
	int refreshIntervalProgress;
    Boolean saveSettings;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	
	    setContentView(R.layout.activity_settings);
	    
	    // allow navigating up with the app icon
	    ActionBar actionBar = getSupportActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);
	    
	    userPrefs = pm.getUserPreferences();
	    
	    skbRefreshInterval = (SeekBar)findViewById(R.id.skbRefresh);
	    skbRefreshInterval.setOnSeekBarChangeListener(this);
	    skbRefreshInterval.incrementProgressBy(5);
	    skbRefreshInterval.setMax(30);
	    skbRefreshInterval.setProgress(userPrefs.getRefreshInterval());
	    
	    txtRefreshInterval = (TextView)findViewById(R.id.txtRefreshInterval);
	    txtRefreshInterval.setText(String.valueOf(userPrefs.getRefreshInterval()) + " minutes");
	    
	    saveSettings = true;
	}
	
	@Override
    public void onProgressChanged(SeekBar seekBar, int progress,
    		boolean fromUser) {
    	if (progress < 5){
    		progress = 5;
    		skbRefreshInterval.setProgress(5);
    	}
		refreshIntervalProgress = progress;
		if (txtRefreshInterval != null){
			txtRefreshInterval.setText(progress + " minutes");	  
		}
    }

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_activity_actions, menu); // ActionBar menu items
        return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.action_cancel_settings) {
        	saveSettings = false;
        	finish();
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
    }
    
    @Override
    protected void onStop(){
    	if (saveSettings){
    		SaveSettings();
    	}else{
    		Log.d("spoppin", "settings canceled");
    	}
    	super.onStop();
    }
    
    private void SaveSettings(){
    	userPrefs.setRefreshInterval(refreshIntervalProgress);
    	pm.saveUserPreferences(userPrefs);
    	Log.d("spoppin", "settings saved");
    }

}
