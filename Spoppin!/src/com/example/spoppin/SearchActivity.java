package com.example.spoppin;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.spoppin.objects.LocationSearchResultAdapter;

public class SearchActivity extends ListActivity {
	
	private Context mContext;

    // geocoder to process queries
    private Geocoder geocoder;

    private EditText mLocationSearchEditText;

    private Button mLocationSearchButton;

    private final int DIALOG_FIND_ADDRESS = 0;
    
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	
	    setContentView(R.layout.activity_search);
	   

        mContext = this;

        // create new geocoder
        geocoder = new Geocoder(this, Locale.getDefault());

        mLocationSearchEditText = (EditText) findViewById(R.id.location_search_edit_text);

        mLocationSearchButton = (Button) findViewById(R.id.location_search_button);

        mLocationSearchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // retrieve search query from search edit text
                String searchQuery = mLocationSearchEditText.getText().toString();

                // execute geo coding async task
                new EasyGeoCodeTask().execute(searchQuery);
            }
        });
        
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
        	@SuppressLint("NewApi")
			@Override
            public void onItemClick(AdapterView<?> parent, final View view,
                int position, long id) {
        		Address address = (Address) parent.getItemAtPosition(position);
        		Intent i = new Intent(mContext, MainActivity.class);
    			i.putExtra("lat", address.getLatitude());
    			i.putExtra("lon", address.getLongitude());
    			mContext.startActivity(i);
            }
		});
	}
	
	
    private class EasyGeoCodeTask extends AsyncTask<String, Void, Boolean> {
        // list of addresses to be stored by the geocoder query response
        private List<Address> addressList;

        // adapter for list that is populated by the geocoder query results
        private LocationSearchResultAdapter locationResultAdapter;

        // geocoder specific settings
        private final Integer RESPONSE_LIMIT = 3;
        private final Integer SEARCH_RESULTS = 5;

        @Override
        protected void onPreExecute() {
            //showDialog(DIALOG_FIND_ADDRESS);
        }

        protected Boolean doInBackground(String... searchQuery) {

            // counter for the loop below
            int responseCount = 0;

            // geocoder retry loop when google has issues
            while (addressList == null && responseCount <= RESPONSE_LIMIT) {
                try {
                    // populate address list from query and return 
                    addressList = geocoder.getFromLocationName(searchQuery[0], SEARCH_RESULTS);
                } catch (SocketTimeoutException e) {
                    addressList = null;
                } catch (IOException e) {
                    addressList = null;
                }

                // add to the response count until the response limit has been hit
                if (responseCount == RESPONSE_LIMIT) {
                    return false;
                } else {
                    responseCount++;
                }
            }

            return true;
        }

        protected void onPostExecute(Boolean result) {
            try {
                // if everything worked out ok
                if (result) {
                    // display a toast if the address list is empty
                    if (addressList.isEmpty()) {
                        ToastIt("Could not find that location, check the address and try again.");
                    } else {
                        // create an adapter using the address results
                        locationResultAdapter = new LocationSearchResultAdapter(SearchActivity.this, R.layout.easy_geo_location_row, addressList);

                        //set the activities adapter to the new one we just created
                        getListView().setAdapter(locationResultAdapter);
                    }
                } else {
                    // display a toast if everything didn't work out ok
                    ToastIt("There was an issue with your request, please try again.");
                }
            } catch (Exception e) {
                // display a toast if there is an exception
                ToastIt("There was an issue with your request, please try again.");
            }

            // we are done so let's dismiss the progress dialog
            //dismissDialog(DIALOG_FIND_ADDRESS);
        }
    }

    // easy toast method
    private void ToastIt(String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }

}
