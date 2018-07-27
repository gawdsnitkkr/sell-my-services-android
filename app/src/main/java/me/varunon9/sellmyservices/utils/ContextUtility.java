package me.varunon9.sellmyservices.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

/**
 * Created by varunkumar on 30/6/18.
 *
 * This Utility class needs context
 */

public class ContextUtility {
    private Context context;

    public ContextUtility(Context context) {
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

    // todo: deprecate this method here instead use from singleton class
    // currently being used in MainActivity
    public Location getCurrentLocation(LocationManager locationManager)
            throws SecurityException {
        Criteria criteria = new Criteria();
        Location location = locationManager.getLastKnownLocation(
                locationManager.getBestProvider(criteria, false)
        );
        return location;
    }

    /**
     * zoom level range-
     *
     * 1: World
     * 5: Landmass/continent
     * 10: City
     * 15: Streets
     * 20: Buildings
     */
    public void showLocationOnMap(GoogleMap googleMap, Location location,
                                  String marker, boolean moveCamera, float zoomLevel) {
        // default location: Bangalore
        LatLng currentLocation = new LatLng(12.97, 77.6);

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

    public void populateListView(ListView listView, ArrayList<String> list) {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                context,
                android.R.layout.simple_list_item_1,
                list
        );
        listView.setAdapter(arrayAdapter);
    }
}
