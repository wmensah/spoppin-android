package net.wilmens.spoppin;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import net.wilmens.spoppin.objects.ServerResponseEnum;
import net.wilmens.spoppin.objects.Venue;
import net.wilmens.spoppin.requests.GetVenueListRequest;
import net.wilmens.spoppin.requests.GetVenueListResponse;
import net.wilmens.spoppin.requests.VenueRankRequest;
import net.wilmens.spoppin.requests.VenueRankResponse;
import net.wilmens.spoppin.utilities.ConnectionUtils;
import net.wilmens.spoppin.utilities.LocationUtils;
import net.wilmens.spoppin.utilities.StringUtils;
import net.wilmens.spoppin.utilities.UIUtils;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends BaseSpoppinActivity implements IGPSActivity, INavigationMenu{
	
	private GPS gps;
	private String selectedVenue;
	private boolean isSpoppin;
	
	public static final int REQUEST_CODE_LOCATION_SEARCH = 1;
	public static final int RESULT_CODE_USE_GIVEN_LOCATION = 1;
	public static final int RESULT_CODE_USE_USER_LOCATION = 2;
	
	ArrayList<BarRank> venueList = null;
	BarRankAdapter adapter;
	
	// requests
	GetVenueListRequest vlr = null;
	VenueRankRequest vrr = null;
	
	Boolean requestPending = false;
	
	// controls
	private TextView lblCurrentLocation;
	private TextView txtNoVenueFound;
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
        
	    // allow navigating up with the app icon
	    ActionBar actionBar = getSupportActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);
	 
        lblCurrentLocation = (TextView)findViewById(R.id.lblCurrentLocation);
        txtNoVenueFound = (TextView)findViewById(R.id.txtVenueNotFound);
        
        gps = new GPS(this);
        
        GetVenuesBasedOnIntentLocation(this.getIntent());
        
        int refInt = pm.getUserPreferences().getRefreshInterval();
        int delay = (refInt == 0)? 5 : refInt * 60000;// mins to ms

        Timer timer = new Timer();

        timer.schedule( new TimerTask(){
           public void run() { 
        	   mHandler.sendMessage(new Message());
            }
         }, delay, delay);
    }
	
	/*
	 * Sometimes coordinates (current or last known location) may be passed via the intent 
	 * so this method allows us to get venues based on that.
	 * @param i - Intent containing a set of coordinates as Extras (ie. lat, lon)
	 */
	private void GetVenuesBasedOnIntentLocation(Intent i){
        Location loc = new Location("newlocprovider");
        
        Boolean useCurrentLoc = true;
        
        if (i.getExtras() != null){
        	double lat = i.getExtras().getDouble("lat", 0);
        	double lon = i.getExtras().getDouble("lon", 0);
        	
        	if (lat != 0 && lon != 0){
        		useCurrentLoc = false;
        		
	        	if (pm.getUserPreferences().getRememberSearchedLocation()){
	        		// If searched location is to be remembered, save it and use it
	        		Log.d("spoplog", "Saving searched location");
		        	pm.setLocation(lat, lon);
		        	loc = pm.getLocation();
	        	}else{
	        		loc.setLatitude(lat);
	        		loc.setLongitude(lon);
	        	}
        	}
        }
        if (useCurrentLoc){
        	// see if there was location saved in preferences
        	loc = pm.getLocation();
        	if (loc == null){
        		loc = gps.getLastKnownLocation();
        		if (loc != null)
        			pm.setLocation(loc);
        	}
        }
        if (loc == null)
        	Toast.makeText(this, R.string.msg_location_not_found, Toast.LENGTH_SHORT).show();
        else
        	locationChanged(loc.getLongitude(), loc.getLatitude());
	    
	    venueList = new ArrayList<BarRank>();
        
        adapter = new BarRankAdapter(this, R.layout.list_item, venueList);
        lv = (ListView)findViewById(R.id.lstBars);
        lv.setAdapter(adapter);
        lv.setClickable(true);
        lv.setOnItemClickListener(new OnItemClickListener(){
        	
        	public void onItemClick(AdapterView<?> parent, View view, int position, long id){
        		SpopPrompt(venueList.get(position).venue.getVenueId(), venueList.get(position).venue.getName());
        	}     
        });
        
        // If you press and hold on a venue, you get a navigation prompt
        lv.setOnItemLongClickListener(new OnItemLongClickListener(){

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					final int position, long id) {
				
				// TODO: use resource strings
				Dialog dialog = UIUtils.CreateDialog("Navigate to " + venueList.get(position).venue.getName() + "?"
						, "Ok"
						, "Cancel"
						, new DialogInterface.OnClickListener() {
			                   public void onClick(DialogInterface dialog, int id) {
			                       
			                	   // proceed to navigate
			                	   String uri = String.format(Locale.ENGLISH, "google.navigation:q=%f,%f"
			                			   , venueList.get(position).venue.getAddress().getLatitude()
			                			   , venueList.get(position).venue.getAddress().getLongitude());
									Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
									startActivity(intent);
									
			                   }
							}
		                   , new DialogInterface.OnClickListener() {
			                   public void onClick(DialogInterface dialog, int id) {
			                       // User cancelled the dialog
			                	   dialog.cancel();
			                   }
		                   }
		                   , parent.getContext());
				
				if (dialog != null){
					dialog.show();
					return true;
				}
				return false;
			}
        	
        });
	}
	
	@Override
	public void onPause()
	{
		if (gps.isRunning()){
			gps.stopGPS();
		}
	    super.onPause();
	}
	
	@Override
	public void onResume()
	{
		if (!gps.isRunning()){
			gps.resumeGPS();
		}

		// User may have toggled hide/show stats, so rebind adapter
        lv = (ListView)findViewById(R.id.lstBars);
        lv.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		
		super.onResume();
	}
	

	/*
	 * Displays a prompt for voting allowing the user to rank different categories for a venue.
	 * @param venueId - Id of the venue being ranked
	 * @param venueName - Name of the venue being ranked
	 */
    @SuppressWarnings("deprecation")
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
    	final AlertDialog dialog = builder.create();
    	dialog.show();
    	dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
    	dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setEnabled(false);
    	
       	tbDrinks.setOnCheckedChangeListener(new OnCheckedChangeListener(){

    			@Override
    			public void onCheckedChanged(CompoundButton buttonView,
    					boolean isChecked) {
    				if (isChecked || AnyCategorySelected(tbDrinks, tbMusic, tbGirls, tbGuys)){
    					dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
    			    	dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setEnabled(true);
    				}else{
    					dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
    			    	dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setEnabled(false);
    				}
    			}
        	});
        	
        	tbMusic.setOnCheckedChangeListener(new OnCheckedChangeListener(){

    			@Override
    			public void onCheckedChanged(CompoundButton buttonView,
    					boolean isChecked) {
    				if (isChecked || AnyCategorySelected(tbDrinks, tbMusic, tbGirls, tbGuys)){
    					dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
    			    	dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setEnabled(true);
    				}else{
    					dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
    			    	dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setEnabled(false);
    				}
    			}
        	});
        	
        	tbGirls.setOnCheckedChangeListener(new OnCheckedChangeListener(){

    			@Override
    			public void onCheckedChanged(CompoundButton buttonView,
    					boolean isChecked) {
    				if (isChecked || AnyCategorySelected(tbDrinks, tbMusic, tbGirls, tbGuys)){
    					dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
    			    	dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setEnabled(true);
    				}else{
    					dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
    			    	dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setEnabled(false);
    				}
    			}
        	});
        	
        	tbGuys.setOnCheckedChangeListener(new OnCheckedChangeListener(){

    			@Override
    			public void onCheckedChanged(CompoundButton buttonView,
    					boolean isChecked) {
    				if (isChecked || AnyCategorySelected(tbDrinks, tbMusic, tbGirls, tbGuys)){
    					dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
    			    	dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setEnabled(true);
    				}else{
    					dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
    			    	dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setEnabled(false);
    				}			
    			}
        	});
        	
    	
    }
    
    private Boolean AnyCategorySelected(ToggleButton... args){
    	for(int i = 0; i < args.length; i++){
    		if (args[i].isChecked()){
    			return true;
    		}
    	}
    	return false;    	
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
    	if (!ConnectionUtils.isConnected(this)){
    		Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
    		return true;
    	}
    	Intent i = null;
    	if (item.getItemId() == R.id.action_refresh) {
			super.init();
			this.SetProgressLabelText(getString(R.string.msg_loading), true);
			gps.resumeGPS(); // onLocationChanged will set the venues
			return true;
		} else if (item.getItemId() == R.id.action_search) {
			// open venue request page
			i = new Intent(this, SearchActivity.class);
			this.startActivityForResult(i, REQUEST_CODE_LOCATION_SEARCH);
			return true;
		} else if (item.getItemId() == R.id.action_new) {
			// open venue request page
			i = new Intent(this, VenueRequestActivity.class);
			i.putExtra("lat", pm.getLatitude());
			i.putExtra("lon", pm.getLongitude());
			gps.stopGPS();
			this.startActivity(i);
			return true;
		} else {
			if (menu != null) {
				// if the menu is showing, hide it, otherwise, show it
				if (menu.isMenuShowing()){
					menu.showContent(true);
				}else{
					menu.showMenu();
				}
    	        return true;
    	    }
			return super.onOptionsItemSelected(item);
		}
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent i)
    {
    	if (requestCode == REQUEST_CODE_LOCATION_SEARCH && resultCode == RESULT_OK && i != null){
	        if (i.getExtras() != null) {
	        	if (i.getIntExtra("loc_option", 2) == 1){
	        		// use given location
	        		GetVenuesBasedOnIntentLocation(i);
	        		return;
	        	}
	        	else if (i.getIntExtra("loc_option", 1) == 2){
	        		// use user's current location
	        		pm.resetLocation(); // so cached location is not used.
		            Intent it = new Intent(this, MainActivity.class); 
		            GetVenuesBasedOnIntentLocation(it);
	        	}	        	
	        }
    	}
    }
    
	/*
	 * Calls the webservice method to rank a Venue based on the categories associated with it.
	 * @param venueId - Venue to be ranked
	 * @param items - Categories associated with the Venue to be ranked
	 * @param isSpoppin - If true, the Venue gets a thumb-up (spoppin) for the categories, or thumbs-down (sucks) otherwise
	 */
	private void RankVenue(int venueId, int[] items, boolean isSpoppin){
		// get current location
		if (!gps.isRunning()){
			gps.resumeGPS();
		}
		Location loc = gps.getLastKnownLocation();
		if (loc == null){
			Dialog dialog = UIUtils.CreateDialog(getString(R.string.msg_unable_to_determine_location)
					, R.string.ok
					, new DialogInterface.OnClickListener(){

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
						
					}
					, MainActivity.this);
			if (dialog != null){
				dialog.show();
			}
			return;
		}
		
		this.isSpoppin = isSpoppin;
    	
    	vrr = new VenueRankRequest(this);
    	String sClassName = "net.wilmens.spoppin.MainActivity";
    	Class<?> c;
    	try{
    		c = Class.forName(sClassName);
			vrr.setResponseHandler(c.getMethod("VenueRank_ResponseHandler"));
			
			// Request parameters
			List<RequestParameter> params = new java.util.ArrayList<RequestParameter>();
			params.add(new RequestParameter("latitude", String.valueOf(loc.getLatitude())));
			params.add(new RequestParameter("longitude", String.valueOf(loc.getLongitude())));
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
		this.RequestNearbyVenues(pm.getLatitude(), pm.getLongitude());
	}
	
	/*
	 * Calls the webservice to obtain a list of Venues at a given coordinate
	 * @param latitude - latitude of coordinate
	 * @param longitude - longitude of coordinate
	 */
	private void RequestNearbyVenues(double latitude, double longitude){
		if (requestPending)
			return; // a request has already been made
		
		requestPending = true;
        vlr = new GetVenueListRequest(this);
        String sClassName = "net.wilmens.spoppin.MainActivity";   
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
			params.add(new RequestParameter("radius", Integer.toString(pm.getUserPreferences().getSearchRadius())));
			params.add(new RequestParameter("authorized", "1"));
			
			vlr.buildRequest(params);						
			vlr.sendRequest();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}
	
	public void GetVenueList_ResponseHandler(){
		if (vlr != null && pm.getLocation() != null){
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
						venueList.add(new BarRank(v, (i == 0? R.drawable.star : -1), v.Score(), i+1));
					}
					adapter.notifyDataSetChanged();	
					txtNoVenueFound.setVisibility(View.INVISIBLE);
				}else{
					txtNoVenueFound.setVisibility(View.VISIBLE);
				}
			}
		}
	}
	
	@SuppressLint("NewApi")
	public void VenueRank_ResponseHandler(){
		if (vrr != null){
			VenueRankResponse response = vrr.getResponse();
			if (response == null)
				return;
			this.PreProcessServerResponse(response.result);
			RequestCompleted();
			if (response.success){
				Log.d(context.getLogKey(), "venue rank successful");
				this.RefreshNearbyVenues();
				Toast.makeText(MainActivity.this, selectedVenue + (this.isSpoppin? " 'spoppin!" : " sucks!"), Toast.LENGTH_LONG).show();
			}else{
				// check error message for error code
				if (!StringUtils.isNullOrEmpty(response.errorMessage)){
					int errorCode = Integer.parseInt(response.errorMessage);
					Log.d(context.getLogKey(), "errorCode = " + errorCode);
					
					if (errorCode == ServerResponseEnum.NotNearVenue.Value()){
						// get venue name
						String venueName = null;
						String venueId = vrr.getParameterValue("venue_id");
						if (!StringUtils.isNullOrEmpty(venueId)){
							Venue venue = this.getVenueById(Integer.parseInt(venueId));
							if (venue != null){
								venueName = venue.getName();
							}
						}
						
						// If we know the venue name, let the user know they are not near the venue
						// Otherwise, just let them know they are not in range
						String notInRangeMsg = getString(R.string.msg_not_in_range_venue);
						if (!StringUtils.isNullOrEmpty(venueName)){
							notInRangeMsg = String.format(getString(R.string.msg_not_in_range_venue), venueName);
						}
						Dialog dialog = UIUtils.CreateDialog(notInRangeMsg
								, R.string.ok
								, new DialogInterface.OnClickListener() {
									
									@Override
									public void onClick(DialogInterface dialog, int which) {
										dialog.cancel();										
									}
								}, MainActivity.this);
						if (dialog != null){
							dialog.show();
						}else{
							Log.e(context.getLogKey(), "Error creating dialog for user not in range");
						}
					}else if (errorCode == ServerResponseEnum.VoteIntervalError.Value()){
						Dialog dialog = UIUtils.CreateDialog(R.string.msg_spopped_not_long_ago
								, R.string.msg_request_failed
								, R.string.ok
								, new DialogInterface.OnClickListener() {
									
									@Override
									public void onClick(DialogInterface dialog, int which) {
										dialog.cancel();										
									}
								}, MainActivity.this);
						if (dialog != null){
							dialog.show();
						}else{
							Log.e(context.getLogKey(), "Error creating dialog for vote interval error");
						}
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
		Location currloc = pm.getLocation();
		
		//if (newloc.distanceTo(currloc) < 10)
			//return;
		
		// Compare the two
		if ((pm.getLocation() == null) ||
				LocationUtils.isBetterLocation(newloc, currloc)){
			Log.d(context.getLogKey()
				, String.format("Found better location. Old:(lat:%s, lon:%s), New:(lat:%s, lon:%s)"
						, pm.getLatitude(), pm.getLongitude(), latitude, longitude));
			pm.setLocation(newloc);
		}
		
		UpdateVenuesAtCurrentLocation(); //TODO: check if refresh interval has elapsed
	}
	
	private void UpdateVenuesAtCurrentLocation(){
		if (ConnectionUtils.isConnected(this)){
			if (pm.getLocation() != null){
				RequestNearbyVenues(pm.getLatitude(), pm.getLongitude());
				
				// Get the current address and display the city and state
				Address address = UIUtils.GeocodeCoordinates(MainActivity.this
						, pm.getLatitude()
						, pm.getLongitude(), 1);
				if (address != null){
					this.lblCurrentLocation.setVisibility(View.VISIBLE);
					this.lblCurrentLocation.setText(address.getLocality() + ", "
							+ address.getAdminArea());
				}else{
					this.lblCurrentLocation.setVisibility(View.GONE);
				}
			}
		}
		else{
			gps.stopGPS();
			this.lblCurrentLocation.setVisibility(View.GONE);
			this.SetProgressLabelText(null, false);
			Toast.makeText(this, R.string.msg_no_internet_connection, Toast.LENGTH_SHORT).show();
		}
	}
	
	private Venue getVenueById(int venueId){
		if (venueList == null || venueList.size() == 0){
			return null;
		}
		for (BarRank br:venueList){
			if (br.venue.getVenueId() == venueId){
				return br.venue;
			}
		}
		return null;
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
