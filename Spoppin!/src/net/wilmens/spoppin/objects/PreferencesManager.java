package net.wilmens.spoppin.objects;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;

public class PreferencesManager {
	
	private SharedPreferences preferences = null;
	private SharedPreferences.Editor editor = null;
	private String PREFKEY_LATITUDE = "latitude";
	private String PREFKEY_LONGITUDE = "longitude";
	private String PREFKEY_SEARCH_RADIUS = "search_radius";
	
	
	public PreferencesManager(Context c){
		preferences = PreferenceManager.getDefaultSharedPreferences(c);
	}
	
	// Preference: Location
	public void setLocation(double latitude, double longitude){
		editor = preferences.edit();
		editor.putString(PREFKEY_LATITUDE, String.valueOf(latitude));
		editor.putString(PREFKEY_LONGITUDE, String.valueOf(longitude));
		editor.commit();
	}
	
	public void setLocation(Location loc){
		setLocation(loc.getLatitude(), loc.getLongitude());
	}
	
	public Location getLocation(){
		Location loc = new Location("newlocprovider");
		loc.setLatitude(Double.parseDouble(preferences.getString(PREFKEY_LATITUDE, "0.0")));
		loc.setLongitude(Double.parseDouble(preferences.getString(PREFKEY_LONGITUDE, "0.0")));
		if (loc.getLatitude() == 0 || loc.getLongitude() == 0)
			return null;
		return loc;
	}
	
	public double getLatitude(){
		return Double.parseDouble(preferences.getString(PREFKEY_LATITUDE, "0.0"));
	}
	
	public double getLongitude(){
		return Double.parseDouble(preferences.getString(PREFKEY_LONGITUDE, "0.0"));
	}
	
	
	public void resetLocation(){
		editor = preferences.edit();
		editor.remove(PREFKEY_LATITUDE);
		editor.remove(PREFKEY_LONGITUDE);
		editor.commit();
	}
	
	//Preference: Search Radius
	public void setSearchRadius(int radius){
		editor = preferences.edit();
		editor.putString(PREFKEY_SEARCH_RADIUS, String.valueOf(radius));
		editor.commit();
	}
	
	public int getSearchRadius(){
		return Integer.parseInt(preferences.getString(PREFKEY_SEARCH_RADIUS, "5"));
	}
	
	public void saveUserPreferences(UserPreference pref){
		editor = preferences.edit();
		editor.putInt("refresh_interval", pref.getRefreshInterval());
		editor.putInt("search_radius", pref.getSearchRadius());
		editor.putBoolean("remember_searched_location", pref.getRememberSearchedLocation());
		editor.putBoolean("show_statistics", pref.getShowStatistics());
		editor.commit();
	}
	
	public UserPreference getUserPreferences(){
		return new UserPreference(preferences.getInt("refresh_interval", 5)
				, preferences.getInt("search_radius", 5)
				, preferences.getBoolean("remember_searched_location", true)
				, preferences.getBoolean("show_statistics", true));
	}
}
