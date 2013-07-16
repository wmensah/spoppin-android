package com.example.spoppin;

import java.util.ArrayList;
import java.util.List;

import SpoppinObjects.Venue;
import Utilities.LocationUtils;
import Utilities.UIUtils;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.spoppin.RequestsAndResponses.GetVenueListRequest;
import com.example.spoppin.RequestsAndResponses.GetVenueListResponse;

public class MainActivity extends BaseSpoppinActivity implements IGPSActivity{
	
	private GPS gps;
	private double latitude;
	private double longitude;
	
	ArrayList<BarRank> venueList = null;
	BarRankAdapter adapter;
	
	// requests
	GetVenueListRequest vlr = null;
	Boolean requestPending = false;
	
	// controls
	private TextView lblCurrentLocation;
	private ListView lv;

	@SuppressLint("NewApi")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_main);
        
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        
        lblCurrentLocation = (TextView)findViewById(R.id.lblCurrentLocation);
        
        gps = new GPS(this);
        
        // Get last known location
        Location lastLocation = gps.getLastKnownLocation();
        locationChanged(lastLocation.getLongitude(), lastLocation.getLatitude());
        
        venueList = new ArrayList<BarRank>();
        
        adapter = new BarRankAdapter(this, R.layout.list_item, venueList);
        lv = (ListView)findViewById(R.id.lstBars);
        lv.setAdapter(adapter);
        lv.setClickable(true);
        lv.setOnItemClickListener(new OnItemClickListener(){
        	
        	public void onItemClick(AdapterView<?> parent, View view, int position, long id){
        		SpopPrompt(position, venueList.get(position).name);
        	}     
        });
    }

    private void SpopPrompt(int venueId, final String venueName){
    	// Inflate the venue_score_items layout as a view and use it in the dialog
    	LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	View layout = inflater.inflate(R.layout.venue_score_items, (ViewGroup) findViewById(R.id.toggleDrinks));
    	
    	AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
    	builder.setMessage(String.format(getString(R.string.spop_prompt_message), venueName));
    	builder.setTitle(R.string.spop_prompt_title);
    	builder.setView(layout);
    	builder.setPositiveButton(R.string.spoppin, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				ShowToast(venueName + " " + getString(R.string.spoppin).toLowerCase()+"!", true);
			}
		});
    	builder.setNegativeButton(R.string.sucks, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				ShowToast(venueName + " " + getString(R.string.sucks).toLowerCase()+"!", true);
			}
		});
    	AlertDialog dialog = builder.create();
    	dialog.show();
    }
    
    private void ShowToast(String message, Boolean lng){
    	Toast.makeText(MainActivity.this, message, lng? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        //menu.add(0, MENU_REQUEST_VENUE, Menu.NONE, R.string.menu_venue_request);
        //menu.add(Menu.NONE, MENU_DELETE, Menu.NONE, "Delete");
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
    	Intent i = null;
        switch(item.getItemId())
        {
        	case R.id.menu_refresh:
        		super.init();
        		this.SetProgressLabelText(getString(R.string.msg_loading), true);
        		gps.resumeGPS(); // onLocationChanged will set the venues
        		return true;
            case R.id.menu_settings:
			  // open settings page
			  //i = new Intent(this, SettingsActivity.class);
			  //this.startActivity(i);
			  return true;
            case R.id.menu_venue_request:
				// open venue request page
				i = new Intent(this, VenueRequestActivity.class);
				i.putExtra("lat", this.latitude);
				i.putExtra("lon", this.longitude);
				gps.stopGPS();
				this.startActivity(i);
				return true;
            default:
                  return super.onOptionsItemSelected(item);
        }
    }
	
	private void RequestNearbyVenues(double latitude, double longitude){
		if (requestPending)
			return; // a request has already been made
		
		// request venues
		requestPending = true;
        vlr = new GetVenueListRequest(this);
        String sClassName = "com.example.spoppin.MainActivity";   
	    Class<?> c;
		try {
			//TODO: Validate input
			this.SetProgressLabelText(getString(R.string.msg_updating_venues), true);
			c = Class.forName(sClassName);
			vlr.setResponseHandler(c.getMethod("GetVenueList_ResponseHandler"));
			
			// Request parameters
			List<RequestParameter> params = new java.util.ArrayList<RequestParameter>();
			params.add(new RequestParameter("latitude", Double.toString(latitude)));
			params.add(new RequestParameter("longitude", Double.toString(longitude)));
			params.add(new RequestParameter("radius", "10"));
			
			vlr.buildRequest(params);						
			vlr.sendRequest();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}
	
	public void GetVenueList_ResponseHandler(){
		if (vlr != null && this.latitude != 0 && this.longitude != 0){
			GetVenueListResponse resval = vlr.getResponse();
			if (resval == null)
				return; 
			this.PreProcessServerResponse(resval.result);
			RequestCompleted();
			venueList.clear();
			if (resval.success){
				if (resval.venues.size() > 0){
					for(int i = 0; i < resval.venues.size(); i++){
						Venue v = resval.venues.get(i);
						venueList.add(new BarRank((i == 0? R.drawable.ic_launcher : -1), v.getName(), (int)v.Score().getDrinks(), i+1));
					}
					adapter.notifyDataSetChanged();	
				}else{
					if (this.latitude != 0 && this.longitude != 0){
						Toast.makeText(this, R.string.msg_no_venues_found, Toast.LENGTH_LONG).show();
					}
				}
			}
		}
	}
	
	private void RequestCompleted(){
		requestPending = false;
		progressView.setVisibility(View.INVISIBLE);
		gps.stopGPS();
	}

	@Override
	public void locationChanged(double longitude, double latitude) {
		// New location
		Location newloc = new Location("newlocprovider");
		newloc.setLatitude(latitude);
		newloc.setLongitude(longitude);
		
		// Current location
		Location currloc = new Location("oldlocprovider");
		currloc.setLatitude(this.latitude);
		currloc.setLongitude(this.longitude);
		
		// Compare the two
		if ((this.latitude == 0 && this.longitude == 0) ||
				LocationUtils.isBetterLocation(newloc, currloc)){
			this.latitude = latitude;
			this.longitude = longitude;
		}
		
		// Make request
		if (this.latitude != 0 && this.longitude != 0){
			RequestNearbyVenues(this.latitude, this.longitude);
			
			// Get the current address and display the city and state
			Address address = UIUtils.GeocodeCoordinates(MainActivity.this
					, this.latitude
					, this.longitude, 1);
			if (address != null){
				this.lblCurrentLocation.setVisibility(View.VISIBLE);
				this.lblCurrentLocation.setText(address.getLocality() + ", "
						+ address.getAdminArea());
			}else{
				this.lblCurrentLocation.setVisibility(View.GONE);
			}
		}
	}


	@Override
	public void gpsDisabled() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void gpsEnabled() {
		// TODO Auto-generated method stub
		
	}
    
}
