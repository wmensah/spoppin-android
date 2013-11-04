package com.example.spoppin.objects;

public class UserPreference {
	
	private int refreshInterval;
	public int getRefreshInterval(){
		return this.refreshInterval;
	}
	public void setRefreshInterval(int refreshInterval){
		this.refreshInterval = refreshInterval;
	}
	
	public UserPreference(int refreshInterval){
		this.refreshInterval = refreshInterval;
	}

}
