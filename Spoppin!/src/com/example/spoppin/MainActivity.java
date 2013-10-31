package com.example.spoppin;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
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
import android.widget.ToggleButton;

import com.example.spoppin.objects.Venue;
import com.example.spoppin.requests.GetVenueListRequest;
import com.example.spoppin.requests.GetVenueListResponse;
import com.example.spoppin.requests.VenueRankRequest;
import com.example.spoppin.requests.VenueRankResponse;
import com.example.spoppin.utilities.LocationUtils;
import com.example.spoppin.utilities.UIUtils;

public class MainActivity extends BaseSpoppinActivity implements IGPSActivity{
	
	private GPS gps;
	private double latitude;
	private double longitude;
	private String selectedVenue;
	private boolean isSpoppin;
	
	ArrayList<BarRank> venueList = null;
	BarRankAdapter adapter;
	
	// requests
	GetVenueListRequest vlr = null;
	VenueRankRequest vrr = null;
	
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
        		SpopPrompt(venueList.get(position).venueId, venueList.get(position).name);
        	}     
        });
        
        int delay = 60000;// in ms 

        Timer timer = new Timer();

        timer.schedule( new TimerTask(){
           public void run() { 
        	   mHandler.sendMessage(new Message()); // set msg.obj = "" if you need to update some text
            }
         }, delay, delay);
        
    }
	

    private void SpopPrompt(final int venueId, final String venueName){
    	selectedVenue = venueName;
    	
    	// Inflate the venue_score_items layout as a view and use it in the dialog
    	LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	final View layout = inflater.inflate(R.layout.venue_score_items, (ViewGroup) findViewById(R.id.toggleDrinks));
    	
    	final ToggleButton tbDrinks = (ToggleButton)layout.findViewById(R.id.toggleDrinks);
    	final ToggleButton tbMusic = (ToggleButton)layout.findViewById(R.id.toggleMusic);
    	final ToggleButton tbGirls = (ToggleButton)layout.findViewById(R.id.toggleGirls);
    	final ToggleButton tbGuys = (ToggleButton)layout.findViewById(R.id.toggleGuys);
    	
    	AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
    	builder.setMessage(String.format(getString(R.string.spop_prompt_message), venueName));
    	builder.setTitle(R.string.spop_prompt_title);
    	builder.setView(layout);
    	builder.setPositiveButton(R.string.spoppin, new DialogInterface.OnClickListener() {
			
			@Override
			// spops
			public void onClick(DialogInterface dialog, int which) {
				RankVenue(venueId
						, new int[]{tbDrinks.isChecked()?1:0,tbMusic.isChecked()?1:0,tbGirls.isChecked()?1:0,tbGuys.isChecked()?1:0}
						, true);	
			}
		});
    	builder.setNegativeButton(R.string.sucks, new DialogInterface.OnClickListener() {
			
			@Override
			// sucks
			public void onClick(DialogInterface dialog, int which) {
				RankVenue(venueId
						, new int[]{tbDrinks.isChecked()?1:0,tbMusic.isChecked()?1:0,tbGirls.isChecked()?1:0,tbGuys.isChecked()?1:0}
						, false);
			}
		});
    	AlertDialog dialog = builder.create();
    	dialog.show();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        getMenuInflater().inflate(R.menu.main_activity_actions, menu); // ActionBar menu items
        
        //menu.add(0, MENU_REQUEST_VENUE, Menu.NONE, R.string.menu_venue_request);
        //menu.add(Menu.NONE, MENU_DELETE, Menu.NONE, "Delete");
        return super.onCreateOptionsMenu(menu);
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
            case R.id.action_search:
            	return true;
            case R.id.action_new:
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
    
	
	private void RankVenue(int venueId, int[] items, boolean isSpoppin){
		this.isSpoppin = isSpoppin;
    	
    	vrr = new VenueRankRequest(this);
    	String sClassName = "com.example.spoppin.MainActivity";
    	Class<?> c;
    	try{
    		c = Class.forName(sClassName);
			vrr.setResponseHandler(c.getMethod("VenueRank_ResponseHandler"));
			
			// Request parameters
			List<RequestParameter> params = new java.util.ArrayList<RequestParameter>();
			params.add(new RequestParameter("venue_id", String.valueOf(venueId)));
			params.add(new RequestParameter("drinks", String.valueOf(items[0])));
			params.add(new RequestParameter("music", String.valueOf(items[1])));
			params.add(new RequestParameter("girls",String.valueOf(items[2])));
			params.add(new RequestParameter("guys", String.valueOf(items[3])));
			params.add(new RequestParameter("spoppin", (isSpoppin? "1" : "0")));
			
			vrr.buildRequest(params);						
			vrr.sendRequest();
						
    	} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
    }
	
	Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
        	RefreshNearbyVenues();
        	return true;
        }
	});

	public void RefreshNearbyVenues(){
		this.RequestNearbyVenues(this.latitude, this.longitude);
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
			params.add(new RequestParameter("radius", "10")); //TODO: Allow user to change radius from Settings page
			params.add(new RequestParameter("authorized", "0"));
			
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
						venueList.add(new BarRank(v.getVenueId(), (i == 0? R.drawable.star : -1), v.getName(), v.Score(), i+1));
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
	
	public void VenueRank_ResponseHandler(){
		if (vrr != null){
			VenueRankResponse resval = vrr.getResponse();
			if (resval == null)
				return;
			this.PreProcessServerResponse(resval.result);
			RequestCompleted();
			if (resval.success){
				this.RefreshNearbyVenues();
				Toast.makeText(this, selectedVenue + (this.isSpoppin? " 'spoppin!" : " sucks!"), Toast.LENGTH_LONG).show();
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
		
		//if (newloc.distanceTo(currloc) < 10)
			//return;
		
		// Compare the two
		if ((this.latitude == 0 && this.longitude == 0) ||
				LocationUtils.isBetterLocation(newloc, currloc)){
			Log.d(context.getLogKey()
				, String.format("Found better location. Old:(lat:%s, lon:%s), New:(lat:%s, lon:%s)"
						, this.latitude, this.longitude, latitude, longitude));
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
				Log.d(context.getLogKey(), "Location address found: " + address.getLocality() + "," + address.getAdminArea());
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
