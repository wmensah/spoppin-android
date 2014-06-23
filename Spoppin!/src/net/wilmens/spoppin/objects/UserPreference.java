package net.wilmens.spoppin.objects;

public class UserPreference {
	
	private int refreshInterval;
	public int getRefreshInterval(){
		return this.refreshInterval;
	}
	public void setRefreshInterval(int interval){
		this.refreshInterval = interval;
	}
	
	int searchRadius;
	public int getSearchRadius(){
		return this.searchRadius;
	}
	public void setSearchRadius(int radius){
		this.searchRadius = radius;
	}
	
	boolean rememberSearchedLocation;
	public boolean getRememberSearchedLocation(){
		return this.rememberSearchedLocation;
	}
	
	public void setRememberSearchedLocation(boolean remember){
		this.rememberSearchedLocation = remember;
	}
	
	boolean showStatistics;
	public void setShowStatistics(boolean show){
		this.showStatistics = show;
	}
	public boolean getShowStatistics(){
		return this.showStatistics;
	}
	
	public UserPreference(int refreshInterval, int searchRadius, boolean rememberSearchedLocation,
			boolean showStatistics){
		this.refreshInterval = refreshInterval;
		this.searchRadius = searchRadius;
		this.rememberSearchedLocation = rememberSearchedLocation;
		this.showStatistics = showStatistics;
	}

}
