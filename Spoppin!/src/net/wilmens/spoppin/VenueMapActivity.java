package net.wilmens.spoppin;

import java.util.ArrayList;
import java.util.Locale;

import net.wilmens.spoppin.objects.VenueMarker;
import net.wilmens.spoppin.utilities.UIUtils;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

//import com.google.android.gms.maps.CameraUpdateFactory;
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
//import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
//import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
//import com.google.android.gms.maps.SupportMapFragment;
//import com.google.android.gms.maps.model.CameraPosition;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.LatLngBounds;
//import com.google.android.gms.maps.model.Marker;
//import com.google.android.gms.maps.model.MarkerOptions;

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
	    actionBar.setTitle("Map");
	    
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
	    
	    mMap.setInfoWindowAdapter(new InfoWindowAdapter(){

			@Override
			public View getInfoContents(Marker args) {
			View v = getLayoutInflater().inflate(R.layout.info_window_layout, null);
				
				TextView txtMarkerTitle = (TextView)v.findViewById(R.id.txtMarkerTitle);
				txtMarkerTitle.setText(args.getTitle() + " >");
				
				mMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener(){

					@Override
					public void onInfoWindowClick(Marker marker) {
						String uri = String.format(Locale.ENGLISH, "google.navigation:q=%f,%f", marker.getPosition().latitude, marker.getPosition().longitude);
						Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
						startActivity(intent);
						
					}
					
				});
				return v;
				
			}

			@Override
			public View getInfoWindow(Marker arg0) {
				// TODO Auto-generated method stub
				return null;
			}
	    	
	    });
	}
	
	private void BindMarkers(ArrayList<VenueMarker> venueList){
		if (venueList.size() == 0)
			return;
		
		mMap.clear();
		int i = 0;
		for (VenueMarker v : venueList){
			LatLng pos = new LatLng(v.getLatitutde(), v.getLongitude());
			Float color = (Float) UIUtils.getColorArray().values().toArray()[i];
			Log.d("map", "i = " + i + ", color=" + color);
			mMap.addMarker(new MarkerOptions().position(pos).title(v.getVenueName())
					.alpha(1f)
					.icon(BitmapDescriptorFactory.defaultMarker(color)));
			builder.include(pos);
			
			i++;
		}		
	}

}
