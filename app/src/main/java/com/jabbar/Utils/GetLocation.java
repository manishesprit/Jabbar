package com.jabbar.Utils;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import static android.content.Context.LOCATION_SERVICE;


public class GetLocation implements LocationListener {

    public Context context;
    public MyLocationListener myLocationListener;
    public LocationManager locationManager;
    private Location location;

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

    public GetLocation(Context context, MyLocationListener myLocationListener) {
        this.context = context;
        this.myLocationListener = myLocationListener;
    }

    public void UpdateLocation() {
        locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isGPSEnabled && !isNetworkEnabled) {
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                Log.print("====location getLastKnownLocation======" + location.getLatitude() + "===" + location.getLongitude());
                Pref.setValue(context, Config.PREF_LOCATION, location.getLatitude() + "," + location.getLongitude());
                myLocationListener.getLoc(true);
            } else {
                Log.print("====location getLastKnownLocation NULL======");
                myLocationListener.getLoc(false);
            }

        } else {
            if (isNetworkEnabled) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                Log.print("Network" + "Network");
                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location != null) {
                        Log.print("====isNetworkEnabled======" + location.getLatitude() + "===" + location.getLongitude());
                        Pref.setValue(context, Config.PREF_LOCATION, location.getLatitude() + "," + location.getLongitude());
                        myLocationListener.getLoc(true);
                    } else {
                        Log.print("====isNetworkEnabled NULL======");
                        myLocationListener.getLoc(false);
                    }
                }
            }

            // if GPS Enabled get lat/long using GPS Services
            if (isGPSEnabled) {
                if (location == null) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.print("GPS Enabled " + " GPS Enabled");
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (location != null) {
                            Log.print("====isGPSEnabled======" + location.getLatitude() + "===" + location.getLongitude());
                            Pref.setValue(context, Config.PREF_LOCATION, location.getLatitude() + "," + location.getLongitude());
                            myLocationListener.getLoc(true);
                        } else {
                            Log.print("====isGPSEnabled NULL======");
                            myLocationListener.getLoc(false);
                        }
                    }
                }
            }

        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.print("====location======" + location.getLatitude() + "===" + location.getLongitude());
        Pref.setValue(context, Config.PREF_LOCATION, location.getLatitude() + "," + location.getLongitude());
//        myLocationListener.getLoc(true);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    public interface MyLocationListener {
        public void getLoc(boolean isUpdate);
    }
}
