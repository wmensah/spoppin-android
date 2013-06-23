package com.example.spoppin;

import com.example.spoppin.RequestsAndResponses.NewVenueRequest;
import com.example.spoppin.RequestsAndResponses.NewVenueResponse;

import Utilities.UIUtils;
import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class VenueRequestActivity extends BaseSpoppinActivity implements IGPSActivity{
	String lat, lon;
	private GPS gps;
	
	// requests
	NewVenueRequest nvr = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	
	    setContentView(R.layout.activity_venue_request);
	    
	    gps = new GPS(this);
	    UIUtils.Toast(this, "Finding your location...");
	    
	    Button btnLocation = (Button)findViewById(R.id.btnGetLocation);
	    if (btnLocation != null){
		    btnLocation.setOnClickListener(new OnClickListener(){
	
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Address address = UIUtils.GeocodeCoordinates(VenueRequestActivity.this
							, latitude
							, longitude, 1);
					if (address != null){
						PopulateAddressFields(address);
					}else{
						UIUtils.Toast(VenueRequestActivity.this, "Address not found");
					}
				}	    	
		    });
	    }
	    
	    nvr = new NewVenueRequest(this);
	    Button btnSubmitRequest = (Button)findViewById(R.id.btnSubmitRequest);
	    if (btnSubmitRequest != null){
	    	btnSubmitRequest.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					String sClassName = "com.example.spoppin.VenueRequestActivity";   
				    Class<?> c;
					try {
						c = Class.forName(sClassName);
						nvr.setResponseHandler(c.getMethod("NewVenueRequest_ResponseHandler"));
						nvr.sendRequest();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					} catch (NoSuchMethodException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 

				}
	    		
	    	});
	    	
	    }
	}
	
	public void NewVenueRequest_ResponseHandler(){
		if (nvr != null){
			NewVenueResponse resval = nvr.getResponse();
			UIUtils.Toast(this, (resval.success? "Request has been submitted" : "Request submission failed"));
		}
	}
	
	private void PopulateAddressFields(Address address){
		if (address == null)
			return;
		
		TextView street = (TextView)findViewById(R.id.txtStreet);
		if(street != null && address.getAddressLine(0) != null)
			street.setText(address.getAddressLine(0));
		
		TextView city = (TextView)findViewById(R.id.txtCity);
		if (city != null && address.getLocale() != null)
			city.setText(address.getLocality());
		
		TextView state = (TextView)findViewById(R.id.txtState);
		if (state != null)
			state.setText(address.getAdminArea());
		
		TextView zip = (TextView)findViewById(R.id.txtZip);
		if (zip != null &&  address.getPostalCode() != null)
			zip.setText(address.getPostalCode());
		
		TextView country = (TextView)findViewById(R.id.txtCountry);
		if (country != null && address.getAddressLine(2) != null)
			country.setText(address.getAddressLine(2));
	}
	
	private double latitude;
	private double longitude;

	@Override
	public void locationChanged(double longitude, double latitude) {
		if (this.latitude == 0 && this.longitude == 0){
		this.latitude = latitude;
		this.longitude = longitude;
		
			UIUtils.Toast(this, latitude + "--" + longitude);
			//Geocode 
					Address address = UIUtils.GeocodeCoordinates(VenueRequestActivity.this
							, latitude
							, longitude, 1);
					if (address != null){
						PopulateAddressFields(address);
					}else{
						UIUtils.Toast(VenueRequestActivity.this, "Address not found");
					}
		}
	}

	@Override
	public void gpsDisabled() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
	}
	
	@Override
	public void gpsEnabled(){

	}

}
