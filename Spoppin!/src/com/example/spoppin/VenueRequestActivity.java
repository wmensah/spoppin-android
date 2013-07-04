package com.example.spoppin;

import java.util.List;
import java.util.Map.Entry;

import com.example.spoppin.RequestsAndResponses.NewVenueRequest;
import com.example.spoppin.RequestsAndResponses.NewVenueResponse;

import Utilities.UIUtils;
import android.app.Activity;
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
import android.widget.Toast;

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
	ProgressView progressView;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	
	    setContentView(R.layout.activity_venue_request);
	    
	    gps = new GPS(this);
	    //Toast.makeText(this, R.string.toast_finding_location, Toast.LENGTH_SHORT).show();
	    progressView = (ProgressView)findViewById(R.id.pvVenueRequest);
	    if (progressView != null)
	    	progressView.setLabelText("Finding your location...");
	    
	    Button btnLocation = (Button)findViewById(R.id.btnGetLocation);
	    if (btnLocation != null){
		    btnLocation.setOnClickListener(new OnClickListener(){
	
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					progressView.setVisibility(View.VISIBLE);
					Address address = UIUtils.GeocodeCoordinates(VenueRequestActivity.this
							, latitude
							, longitude, 1);
					if (address != null){
						PopulateAddressFields(address);
					}else{
						//Toast.makeText(VenueRequestActivity.this, "Address not found", Toast.LENGTH_SHORT).show();
						SpopPrompt("Find location", "Address not found. Please try again later.");
						progressView.setVisibility(View.INVISIBLE);
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
			ShowSubmitResultPrompt(resval.success, this);
		}
	}
	
    private void ShowSubmitResultPrompt(final boolean success, final Activity sender){
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setMessage(success? R.string.venue_request_submitted : R.string.venue_request_failed);
    	builder.setTitle(R.string.dialog_venue_request_title);
    	builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// if success, return to the MainActivity
				if (success){
					sender.finish();
				}
			}
		});
    	AlertDialog dialog = builder.create();
    	dialog.show();
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
		
			//Geocode 
			Address address = UIUtils.GeocodeCoordinates(VenueRequestActivity.this
					, latitude
					, longitude, 1);
			if (address != null){
				PopulateAddressFields(address);
			}else{
				Toast.makeText(VenueRequestActivity.this, "Address not found", Toast.LENGTH_SHORT).show();
				progressView.setVisibility(View.INVISIBLE);
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
	
	private void SpopPrompt(String title, String message){
    	AlertDialog.Builder builder = new AlertDialog.Builder(VenueRequestActivity.this);
    	builder.setTitle(title);
    	builder.setMessage(message);
    	builder.setCancelable(true);
        builder.setNeutralButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

    	
    	AlertDialog dialog = builder.create();
    	dialog.show();
    }

}
