package net.wilmens.spoppin;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Locale;

import net.wilmens.spoppin.objects.LocationSearchResultAdapter;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import net.wilmens.spoppin.R;

public class SearchActivity extends ActionBarListActivity {
	
	private Context mContext;

    // geocoder to process queries
    private Geocoder geocoder;

    private EditText mLocationSearchEditText;

    private final int DIALOG_FIND_ADDRESS = 0;
    
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	
	    setContentView(R.layout.activity_search);
	   
	    // allow navigating up with the app icon
	    ActionBar actionBar = getSupportActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);
	    actionBar.setTitle("Search");

        mContext = this;

        // create new geocoder
        geocoder = new Geocoder(this, Locale.getDefault());

        mLocationSearchEditText = (EditText) findViewById(R.id.location_search_edit_text);
        
        mLocationSearchEditText.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                    (keyCode == KeyEvent.KEYCODE_ENTER)) {
                  // Perform action on key press
                	String searchQuery = mLocationSearchEditText.getText().toString();

                    // execute geo coding async task
                    new EasyGeoCodeTask().execute(searchQuery);
                    return true;
                }
                return false;
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
    			i.putExtra("loc_option", 1);
    			setResult(RESULT_OK, i);
    			finish();
            }
		});
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_activity_actions, menu); // ActionBar menu items
        return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
    	Intent i = null;
        if (item.getItemId() == R.id.action_use_my_location) {
        	// Navigate to MainActivity without setting lat/lon so user's current location is used
			i = new Intent(this, MainActivity.class);
			i.putExtra("loc_option", 2);
			setResult(RESULT_OK, i);
			finish();
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
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
