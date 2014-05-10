package net.wilmens.spoppin.objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import net.wilmens.spoppin.BarRank;

//Marker to be displayed on the map.
public class VenueMarker implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4757521158482509498L;
	String venueName;
	public String getVenueName(){
		return this.venueName;
	}
	
	double latitude;
	public double getLatitutde(){
		return this.latitude;
	}
	
	double longitude;
	public double getLongitude(){
		return this.longitude;
	}
	
	public VenueMarker(String name, double lat, double lon){
		super();
		this.venueName = name;
		this.latitude = lat;
		this.longitude = lon;
	}
	
	public VenueMarker(Venue v){
		super();
		this.LoadFromVenue(v);
	}
	
	public void LoadFromVenue(Venue venue){
		this.venueName = venue.getName();
		this.latitude = venue.getAddress().getLatitude();
		this.longitude = venue.getAddress().getLongitude();
	}
	
	/*
	 * Converts a list of Venues to a list of Marker objects that can be placed on the map
	 * @param - List of venues
	 * @return - List of VenueMarker objects
	 */
	public static ArrayList<VenueMarker> VenueListToVenueMarkerList(ArrayList<BarRank> venues){
		ArrayList<VenueMarker> retval = new ArrayList<VenueMarker>();
		Iterator<BarRank> i = venues.iterator();
		while(i.hasNext()){
			BarRank br = i.next();
			retval.add(new VenueMarker(br.venue));
		}
		return retval;
	}

}
