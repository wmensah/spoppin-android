package com.example.spoppin;

import java.util.List;
import java.util.Map.Entry;

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
	private double latitude;
	private double longitude;
	
	private GPS gps;
	
	// requests
	NewVenueRequest nvr = null;
	
	TextView txtName;
	TextView txtStreet;
	TextView txtCity;
	TextView txtState;
	TextView txtZip;
	TextView txtCountry;

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
	    
	    txtName = (TextView)findViewById(R.id.txtName);
	    txtStreet = (TextView)findViewById(R.id.txtStreet);
	    txtCity = (TextView)findViewById(R.id.txtCity);
	    txtState = (TextView)findViewById(R.id.txtState);
	    txtZip = (TextView)findViewById(R.id.txtZip);
	    txtCountry = (TextView)findViewById(R.id.txtCountry);
	    
	    nvr = new NewVenueRequest(this);
	    Button btnSubmitRequest = (Button)findViewById(R.id.btnSubmitRequest);
	    if (btnSubmitRequest != null){
	    	btnSubmitRequest.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					String sClassName = "com.example.spoppin.VenueRequestActivity";   
				    Class<?> c;
					try {
						//TODO: Validate input
						c = Class.forName(sClassName);
						nvr.setResponseHandler(c.getMethod("NewVenueRequest_ResponseHandler"));
						
						// Request paramters
						List<RequestParameter> params = new java.util.ArrayList<RequestParameter>();
						params.add(new RequestParameter("name", txtName.getText().toString()));
						params.add(new RequestParameter("street", txtStreet.getText().toString()));
						params.add(new RequestParameter("city", txtCity.getText().toString()));
						params.add(new RequestParameter("state", txtState.getText().toString()));
						params.add(new RequestParameter("zip", txtZip.getText().toString()));
						params.add(new RequestParameter("country", txtCountry.getText().toString()));
						params.add(new RequestParameter("latitude", Double.toString(latitude)));
						params.add(new RequestParameter("longitude", Double.toString(longitude)));
						
						nvr.buildRequest(params);						
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
		
		if(txtStreet != null && address.getAddressLine(0) != null)
			txtStreet.setText(address.getAddressLine(0));
		
		if (txtCity != null && address.getLocale() != null)
			txtCity.setText(address.getLocality());
		
		if (txtState != null)
			txtState.setText(address.getAdminArea());
		
		if (txtZip != null &&  address.getPostalCode() != null)
			txtZip.setText(address.getPostalCode());
		
		if (txtCountry != null && address.getAddressLine(2) != null)
			txtCountry.setText(address.getAddressLine(2));
	}
	


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
