package com.example.spoppin.objects;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.location.Address;

public class Venue {
	
	private int venueId;
	public int VenueId(){
		return venueId;
	}
	
	private String name;
	public String getName(){
		return name;
	}
	
	private Date dateAdded;
	public Date getDateAdded(){
		return dateAdded;
	}
	
	private Date lastUpdated;
	public Date getLastUpdated(){
		return lastUpdated;
	}
	
	private VenueScoreInfo score;
	public VenueScoreInfo Score(){
		return score;
	}
	
	private Address address;
	public Address getAddress(){
		return address;
	}
	
	public static Venue loadFromJson(JSONObject data) throws JSONException, ParseException{
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);

		Venue venue = new Venue();
		venue.venueId = data.getInt("venue_id");
		venue.name = data.getString("name");
		
		// dates
		venue.dateAdded = (Date) formatter.parse(data.getString("date_added"));
		venue.lastUpdated = (Date) formatter.parse(data.getString("venue_last_updated"));
		
		// address
		venue.address = new Address(null);
		venue.address.setAddressLine(0, data.getString("street"));
		venue.address.setLocality(data.getString("city"));
		venue.address.setAdminArea(data.getString("state"));
		venue.address.setPostalCode(data.getString("zip"));
		venue.address.setCountryName(data.getString("country"));
		venue.address.setLatitude(Double.parseDouble(data.getString("latitude")));
		venue.address.setLongitude(Double.parseDouble(data.getString("longitude")));
		
		// score
		venue.score = new VenueScoreInfo();
		venue.score.setDrinks(Double.parseDouble(data.getString("drinks_score")));
		venue.score.setMusic(Double.parseDouble(data.getString("music_score")));
		venue.score.setGirls(Double.parseDouble(data.getString("girls_score")));
		venue.score.setGuys(Double.parseDouble(data.getString("guys_score")));
		
		return venue;
	}
}
