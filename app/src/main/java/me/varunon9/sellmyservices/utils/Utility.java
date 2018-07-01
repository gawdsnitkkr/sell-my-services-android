package me.varunon9.sellmyservices.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by varunkumar on 30/6/18.
 */

public class Utility {
    private Context context;

    public Utility(Context context) {
        this.context = context;
    }

    public boolean isPermissionGranted(String permission) {
        int result = context.checkCallingOrSelfPermission(permission);
        return (result == PackageManager.PERMISSION_GRANTED);
    }

    public boolean isBuildVersionGreaterEqualToMarshmallow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return true;
        }
        return false;
    }

    public Location getCurrentLocation(LocationManager locationManager)
            throws SecurityException {
        Criteria criteria = new Criteria();
        Location location = locationManager.getLastKnownLocation(
                locationManager.getBestProvider(criteria, false)
        );
        return location;
    }

    public void showLocationOnMap(GoogleMap googleMap, Location location,
                                  String marker, boolean moveCamera) {
        // default location: Bangalore
        LatLng currentLocation = new LatLng(12.97, 77.6);
        float zoomLevel = 15; // streets view

        if (location != null) {
            currentLocation = new LatLng(location.getLatitude(),
                    location.getLongitude());
        }
        googleMap.addMarker(new MarkerOptions().position(currentLocation)
                .title(marker));

        if (moveCamera) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    currentLocation, zoomLevel)
            );
        }
    }

    public boolean isConnectedToNetwork() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }
}
