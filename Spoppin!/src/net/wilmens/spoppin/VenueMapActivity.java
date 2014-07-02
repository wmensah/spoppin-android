package net.wilmens.spoppin;

import java.util.ArrayList;
import java.util.Locale;

import net.wilmens.spoppin.objects.VenueColor;
import net.wilmens.spoppin.objects.VenueMarker;
import net.wilmens.spoppin.utilities.UIUtils;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
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

public class VenueMapActivity extends ActionBarActivity {
	
	private GoogleMap mMap;
	LatLngBounds.Builder builder;

	/** Called when the activity is first created. */
	@SuppressWarnings("unchecked")
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
	    mMap.setMyLocationEnabled(true); // show current location
	    builder = new LatLngBounds.Builder();
	    
	    // get markers from intent
	    final Intent i = this.getIntent();
	    if (i.getExtras() != null && i.getSerializableExtra("venues") != null){
	    	ArrayList<VenueMarker> venueList = (ArrayList<VenueMarker>) i.getSerializableExtra("venues");	    	
	    	BindMarkers(venueList);
	    }
	    
	    // Can't zoom in on the markers until the map has been completely loaded, so this listener will notify us when that happens
	    mMap.setOnCameraChangeListener(new OnCameraChangeListener() {

			@Override
			public void onCameraChange(CameraPosition position) {
				if (i.getExtras() != null && ((ArrayList<VenueMarker>) i.getSerializableExtra("venues")).size() > 0){
		        	mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 50));
				}else{
					// No markers to load so default the map to the user's current location
					ZoomToCurrentLocation();
				}
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
	
	/*
	 * Places the markers on the map
	 * @param venueList - list of venues to be displayed as markers on the map
	 * @returns void
	 */
	private void BindMarkers(ArrayList<VenueMarker> venueList){
		if (venueList.size() == 0)
			return;
		
		mMap.clear();
		int i = 0;
		for (VenueMarker v : venueList){
			LatLng pos = new LatLng(v.getLatitutde(), v.getLongitude());
			VenueColor color = UIUtils.getColorArray()[i % (UIUtils.getColorArray().length-1)];
			mMap.addMarker(new MarkerOptions().position(pos).title(v.getVenueName())
					.alpha(1f)
					.icon(BitmapDescriptorFactory.defaultMarker(color.bitmapValue)));
			builder.include(pos);			
			i++;
		}		
	}
	
	// Zoom the map to the user's current location
	private void ZoomToCurrentLocation(){		
		 LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
         Criteria criteria = new Criteria();

         Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
         if (location != null)
         {
         	mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                     new LatLng(location.getLatitude(), location.getLongitude()), 13));

             CameraPosition cameraPosition = new CameraPosition.Builder()
             .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
             .zoom(17)                   // Sets the zoom
             .build();                   // Creates a CameraPosition from the builder
             mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
         }
	}
}
