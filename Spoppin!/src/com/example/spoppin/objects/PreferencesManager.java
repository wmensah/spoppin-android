package com.example.spoppin.objects;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;

public class PreferencesManager {
	
	private SharedPreferences preferences = null;
	private SharedPreferences.Editor editor = null;
	
	public PreferencesManager(Context c){
		preferences = PreferenceManager.getDefaultSharedPreferences(c);
	}
	
	public void setLocation(double latitude, double longitude){
		editor = preferences.edit();
		editor.putString("latitude", String.valueOf(latitude));
		editor.putString("longitude", String.valueOf(longitude));
		editor.commit();
	}
	
	public void setLocation(Location loc){
		setLocation(loc.getLatitude(), loc.getLongitude());
	}
	
	public Location getLocation(){
		Location loc = new Location("newlocprovider");
		loc.setLatitude(Double.parseDouble(preferences.getString("latitude", "0.0")));
		loc.setLongitude(Double.parseDouble(preferences.getString("longitude", "0.0")));
		if (loc.getLatitude() == 0 || loc.getLongitude() == 0)
			return null;
		return loc;
	}
	
	public double getLatitude(){
		return Double.parseDouble(preferences.getString("latitude", "0.0"));
	}
	
	public double getLongitude(){
		return Double.parseDouble(preferences.getString("longitude", "0.0"));
	}
	
	
	public void resetLocation(){
		editor = preferences.edit();
		editor.remove("latitude");
		editor.remove("longitude");
		editor.commit();
	}
}
