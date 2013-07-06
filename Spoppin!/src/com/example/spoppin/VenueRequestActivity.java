package com.example.spoppin;

import java.util.List;
import com.example.spoppin.RequestsAndResponses.NewVenueRequest;
import com.example.spoppin.RequestsAndResponses.NewVenueResponse;

import Utilities.StringUtils;
import Utilities.UIUtils;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
	    
	    SetProgressLabelText("Finding your location...", true);
	    
	    Button btnLocation = (Button)findViewById(R.id.btnGetLocation);
	    if (btnLocation != null){
		    btnLocation.setOnClickListener(new OnClickListener(){
	
				@Override
				public void onClick(View arg0) {
					gps.resumeGPS();
					HandleLocationChanged(latitude, longitude);
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
					// check Internet connection
					if (!(VenueRequestActivity.this.CheckInternet())){
						ShowOkDialog("Connectivity", "No internet connection", null);
						return;
					}
					
					// validate input
					if (StringUtils.isNullOrEmpty(txtName.getText().toString()) ||
						StringUtils.isNullOrEmpty(txtStreet.getText().toString()) ||
						StringUtils.isNullOrEmpty(txtCity.getText().toString()) ||
						StringUtils.isNullOrEmpty(txtState.getText().toString()) ||
						StringUtils.isNullOrEmpty(txtZip.getText().toString()) ||
						StringUtils.isNullOrEmpty(txtCountry.getText().toString())){
						ShowOkDialog("Error", "All fields are required.", null);
						return;
					}
							
					SetProgressLabelText("Submitting request...", true);
					
					String sClassName = "com.example.spoppin.VenueRequestActivity";   
				    Class<?> c;
					try {
						//TODO: Validate input
						c = Class.forName(sClassName);
						nvr.setResponseHandler(c.getMethod("NewVenueRequest_ResponseHandler"));
						
						// Request parameters
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
						e.printStackTrace();
					} 

				}
	    		
	    	});
	    	
	    }
	}
	
	public void NewVenueRequest_ResponseHandler(){
		progressView.setVisibility(View.INVISIBLE);
		if (nvr != null){
			NewVenueResponse resval = nvr.getResponse();
			ShowOkDialog(this.getString(R.string.dialog_venue_request_title)
					, this.getString(resval.success? R.string.venue_request_submitted : R.string.venue_request_failed)
					, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							VenueRequestActivity.this.finish();
						}
					});
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
		}
		HandleLocationChanged(latitude, longitude);
	}
	
	private void HandleLocationChanged(double latitude, double longitude){
		//Geocode 
		progressView.setVisibility(View.VISIBLE);
		Address address = UIUtils.GeocodeCoordinates(VenueRequestActivity.this
				, latitude
				, longitude, 1);
		progressView.setVisibility(View.INVISIBLE);
		if (address != null){
			PopulateAddressFields(address);
		}else{
			ShowOkDialog(getString(R.string.loc_find_location), getString(R.string.msg_address_not_found), null);
		}
		gps.stopGPS();
	}

	@Override
	public void gpsDisabled() {
		Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
	}
	
	@Override
	public void gpsEnabled(){

	}

}
