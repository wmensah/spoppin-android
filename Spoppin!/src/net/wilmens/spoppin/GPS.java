package net.wilmens.spoppin;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class GPS {

    private IGPSActivity main;

    // Helper for GPS-Position
    private LocationListener mlocListener;
    private LocationManager mlocManager;

    private boolean isRunning;

    public GPS(IGPSActivity main) {
        this.main = main;

        // GPS Position
        mlocManager = (LocationManager) ((Activity) this.main).getSystemService(Context.LOCATION_SERVICE);
        mlocListener = new MyLocationListener();
        
        if (mlocManager.getAllProviders().contains(LocationManager.NETWORK_PROVIDER))
        	mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mlocListener);

        if (mlocManager.getAllProviders().contains(LocationManager.GPS_PROVIDER))
        	mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mlocListener);

        // GPS Position END
        this.isRunning = true;
    }

    public void stopGPS() {
        if(isRunning) {
            mlocManager.removeUpdates(mlocListener);
            this.isRunning = false;
        }
    }

    public void resumeGPS() {
        if (mlocManager.getAllProviders().contains(LocationManager.NETWORK_PROVIDER))
        	mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mlocListener);

        if (mlocManager.getAllProviders().contains(LocationManager.GPS_PROVIDER))
        	mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mlocListener);
        
        this.isRunning = true;
    }

    public boolean isRunning() {
        return this.isRunning;
    }
    
    public Location getLastKnownLocation(){
    	String locationProvider = LocationManager.NETWORK_PROVIDER;
    	Location lastKnownLocation = mlocManager.getLastKnownLocation(locationProvider);
    	return lastKnownLocation;
    }

    public class MyLocationListener implements LocationListener {

        private final String TAG = MyLocationListener.class.getSimpleName();

        @Override
        public void onLocationChanged(Location loc) {
            GPS.this.main.locationChanged(loc.getLongitude(), loc.getLatitude());
        }

        @Override
        public void onProviderDisabled(String provider) {
            GPS.this.main.gpsDisabled();
        }

        @Override
        public void onProviderEnabled(String provider) {
    		GPS.this.main.gpsEnabled();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

    }

}