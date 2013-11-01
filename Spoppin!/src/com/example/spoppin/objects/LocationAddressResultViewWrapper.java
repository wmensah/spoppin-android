package com.example.spoppin.objects;

import com.example.spoppin.R;

import android.view.View;
import android.widget.TextView;

//Wraps a view for easier inflation and manipulation
class LocationAddressResultViewWrapper {
 View base;

 TextView locationName = null;

 public LocationAddressResultViewWrapper(View base) {
     this.base = base;
 }

 TextView getName() {
     if (locationName == null) {
         locationName = (TextView) base.findViewById(R.id.easy_geo_location_name_text_view);
     }
     return (locationName);
 }
}

