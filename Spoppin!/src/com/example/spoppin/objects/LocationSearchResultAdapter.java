package com.example.spoppin.objects;

import java.util.List;

import com.example.spoppin.R;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

//adapter for geocoder address results
public class LocationSearchResultAdapter extends ArrayAdapter<Address>{
	
	 private Context context;
	 private int layoutResourceId;
	 
	 public LocationSearchResultAdapter(Context context, int layoutResourceId, List<Address> address){
	    super(context, layoutResourceId,  address);
	 	this.context = context;
	 	this.layoutResourceId = layoutResourceId;
	 }

 @Override
 public View getView(int position, View convertView, ViewGroup parent) {
     // set address to the address at the current the current position
     Address address = getItem(position);

     // init the view wrapper
     LocationAddressResultViewWrapper wrapper = null;

     // set row to current view
     View row = convertView;

     // check to see if the row doesn't exist
     if (row == null) {
         // inflate a row layout
    	 LayoutInflater inflater = ((Activity)context).getLayoutInflater();
    	 
         // set the row to the inflated layout
         row = inflater.inflate(R.layout.easy_geo_location_row, parent, false);

         // create a new wrapper
         wrapper = new LocationAddressResultViewWrapper(row);

         // set the rows tag to the new wrapper
         row.setTag(wrapper);
     } else {
         // if the row does exist populated it
         wrapper = (LocationAddressResultViewWrapper) row.getTag();
     }

     // get part one of the address
     String addressLineOne = address.getAddressLine(0) == null ? "" : address.getAddressLine(0);

     // get part two of the address
     String addressLineTwo = address.getAddressLine(1) == null ? "" : address.getAddressLine(1);

     // set address text
     String addressText =  addressLineOne + " " + addressLineTwo;

     // set textview of address for the current row
     wrapper.getName().setText(addressText);

     return (row);
 }
}

