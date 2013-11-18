package net.wilmens.spoppin;

import java.util.ArrayList;

import net.wilmens.spoppin.objects.VenueMarker;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

public class VenueMapActivity extends ActionBarActivity {
	
	private GoogleMap mMap;
	LatLngBounds.Builder builder;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	
	    setContentView(R.layout.activity_map);
	    
	    // allow navigating up with the app icon
	    ActionBar actionBar = getSupportActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);
	    
	    SupportMapFragment mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
	    mMap = mapFrag.getMap();
	    builder = new LatLngBounds.Builder();
	    
	    // get markers from intent
	    Intent i = this.getIntent();
	    if (i.getExtras() != null && i.getSerializableExtra("venues") != null){
	    	@SuppressWarnings("unchecked")
			ArrayList<VenueMarker> venueList = (ArrayList<VenueMarker>) i.getSerializableExtra("venues");	    	
	    	BindMarkers(venueList);
	    }
	    
	    // Can't zoom in on the markers until the map has been completely loaded, so this listener will notify us when that happens
	    mMap.setOnCameraChangeListener(new OnCameraChangeListener() {

			@Override
			public void onCameraChange(CameraPosition position) {
				// TODO Auto-generated method stub
	        	mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 50));
	            // Remove listener to prevent position reset on camera move.
	        	mMap.setOnCameraChangeListener(null);
			}
	    });
	}
	
	private void BindMarkers(ArrayList<VenueMarker> venueList){
		if (venueList.size() == 0)
			return;
		
		mMap.clear();

		for (VenueMarker v : venueList){
			LatLng pos = new LatLng(v.getLatitutde(), v.getLongitude());
			mMap.addMarker(new MarkerOptions().position(pos).title(v.getVenueName()));
			builder.include(pos);
		}		
	}

}
