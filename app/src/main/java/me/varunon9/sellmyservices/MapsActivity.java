package me.varunon9.sellmyservices;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import me.varunon9.sellmyservices.constants.AppConstants;
import me.varunon9.sellmyservices.utils.Utility;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Utility utility;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        utility = new Utility(this);
        locationManager = (LocationManager) getSystemService(this.LOCATION_SERVICE);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Location location = null;
        if (utility.isBuildVersionGreaterEqualToMarshmallow()) {
            if (utility.isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
                // show the user his current location
                location = utility.getCurrentLocation(locationManager);
            } else {
                // request for location permission
                ActivityCompat.requestPermissions(this, new String[] {
                        Manifest.permission.ACCESS_FINE_LOCATION
                }, AppConstants.accessLocationRequestCode);
            }
        } else {
            // show the user his current location
            location = utility.getCurrentLocation(locationManager);
        }

        mMap = googleMap;
        utility.showLocationOnMap(mMap, location, AppConstants.currentLocationMarket,
                true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                          String permissions[],
                                          int[] grantResults) {
        switch (requestCode) {
            case AppConstants.accessLocationRequestCode: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, show user his current location
                    Location location = utility.getCurrentLocation(locationManager);
                    mMap.clear(); // clear initial marker
                    utility.showLocationOnMap(mMap, location, AppConstants.currentLocationMarket,
                            true);
                } else {
                    // permission denied, show user toast notification
                    Toast.makeText(this, AppConstants.accessLocationToastMessage,
                            Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

}
