package net.wilmens.spoppin;

import net.wilmens.spoppin.objects.UserPreference;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class SettingsActivity extends BaseSpoppinActivity{

	// controls
	SeekBar skbRefreshInterval;
	SeekBar skbSearchRadius;
	TextView txtRefreshInterval;
	TextView txtSearchRadius;
	CheckBox chkRememberSearchedLocation;
	CheckBox chkShowStatistics;
	
	UserPreference userPrefs;
	
	int refreshIntervalProgress;
	int searchRadiusProgress;
	boolean rememberSearchedLocationProgress;
	boolean showStatistics;
    Boolean saveSettings;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	
	    setContentView(R.layout.activity_settings);
	    
	    // allow navigating up with the app icon
	    ActionBar actionBar = getSupportActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);

	    saveSettings = false;
	    userPrefs = pm.getUserPreferences();
	    
	    // load default prefs
	    refreshIntervalProgress = userPrefs.getRefreshInterval();
	    searchRadiusProgress = userPrefs.getSearchRadius();
	    rememberSearchedLocationProgress = userPrefs.getRememberSearchedLocation();
	    showStatistics = userPrefs.getShowStatistics();
	    
	    // Refresh Interval
	    txtRefreshInterval = (TextView)findViewById(R.id.txtRefreshInterval);
	    txtRefreshInterval.setText(String.valueOf(userPrefs.getRefreshInterval()) + " minutes");
	    
	    skbRefreshInterval = (SeekBar)findViewById(R.id.skbRefreshInterval);
	    skbRefreshInterval.setMax(30);
	    skbRefreshInterval.setProgress(refreshIntervalProgress);
	    skbRefreshInterval.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				saveSettings = true;
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
	    });
	    

	    
	    
	    // Search Radius
	    txtSearchRadius = (TextView)findViewById(R.id.txtSearchRadius);
	    txtSearchRadius.setText(String.valueOf(userPrefs.getSearchRadius()) + " miles"); //TODO localize mi/km
	    
	    skbSearchRadius = (SeekBar)findViewById(R.id.skbSearchRadius);
	    skbSearchRadius.setMax(100);
	    skbSearchRadius.setProgress(searchRadiusProgress);
	    skbSearchRadius.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				saveSettings = true;
				searchRadiusProgress = progress;
				if (txtSearchRadius != null){
					txtSearchRadius.setText(progress + " miles");	  
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
	    }); 
	    
	    // Remember Searched Location
	    chkRememberSearchedLocation = (CheckBox)findViewById(R.id.chkRememberSearchedLocation);
	    chkRememberSearchedLocation.setChecked(rememberSearchedLocationProgress);
	    chkRememberSearchedLocation.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton checkbox, boolean isChecked) {
				saveSettings = true;
				rememberSearchedLocationProgress = isChecked;
			}
	    	
	    });
	    
	    // Show Statistics
	    chkShowStatistics = (CheckBox)findViewById(R.id.chkShowStatistics);
	    chkShowStatistics.setChecked(showStatistics);
	    chkShowStatistics.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				saveSettings = true;
				showStatistics = isChecked;				
			}	    	
	    });
	    
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
    	userPrefs.setSearchRadius(searchRadiusProgress);
    	userPrefs.setRememberSearchedLocation(rememberSearchedLocationProgress);
    	userPrefs.setShowStatistics(showStatistics);
    	
    	pm.saveUserPreferences(userPrefs);
    	Log.d("spoppin", "settings saved");
    }

}
