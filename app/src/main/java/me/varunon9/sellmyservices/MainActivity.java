package me.varunon9.sellmyservices;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import me.varunon9.sellmyservices.constants.AppConstants;
import me.varunon9.sellmyservices.utils.ContextUtility;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private ContextUtility contextUtility;
    private LocationManager locationManager;
    private TextView searchTextView;
    private static final String LOG = "MainActivity";
    private Singleton singleton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG, "onCreate called");
        setContentView(R.layout.activity_main);

        singleton = Singleton.getInstance(getApplicationContext());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View mapView = findViewById(R.id.map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        contextUtility = new ContextUtility(this);
        locationManager = (LocationManager) getSystemService(this.LOCATION_SERVICE);

        // check if internet connection is available
        if (!contextUtility.isConnectedToNetwork()) {
            Snackbar.make(mapView, AppConstants.INTERNET_CONNECTION_IS_MANDATORY, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }

        searchTextView = (TextView) findViewById(R.id.searchTextView);

        checkLoginAndUpdateUi(navigationView);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_share) {
        } else if (id == R.id.nav_rate) {
        } else {
            Intent intent = new Intent(MainActivity.this, UiFragmentActivity.class);
            Bundle args = new Bundle();
            args.putInt(AppConstants.NAVIGATION_ITEM, id);
            intent.putExtras(args);
            startActivity(intent);
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(LOG, "onResume called");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(LOG, "onMapReady called");
        mMap = googleMap;
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mMap.setOnMarkerClickListener(this);
        if (bundle != null) {
            String sellersString = bundle.getString(AppConstants.SELLER);
            if (sellersString != null) {
                showSellersOnMap(bundle, sellersString);
                return;
            }
        }
        Location location = null;
        if (contextUtility.isBuildVersionGreaterEqualToMarshmallow()) {
            if (contextUtility.isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
                // show the user his current location
                location = contextUtility.getCurrentLocation(locationManager);
            } else {
                // request for location permission
                ActivityCompat.requestPermissions(this, new String[] {
                        Manifest.permission.ACCESS_FINE_LOCATION
                }, AppConstants.ACCESS_LOCATION_REQUEST_CODE);
            }
        } else {
            // show the user his current location
            location = contextUtility.getCurrentLocation(locationManager);
        }

        contextUtility.showLocationOnMap(mMap, location, AppConstants.CURRENT_LOCATION_MARKER,
                true, 15);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[],
                                           int[] grantResults) {
        switch (requestCode) {
            case AppConstants.ACCESS_LOCATION_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, show user his current location
                    Location location = contextUtility.getCurrentLocation(locationManager);
                    mMap.clear(); // clear initial marker
                    contextUtility.showLocationOnMap(mMap, location, AppConstants.CURRENT_LOCATION_MARKER,
                            true, 15);
                } else {
                    // permission denied, show user toast notification
                    Toast.makeText(this, AppConstants.ACCESS_LOCATION_TOAST_MESSAGE,
                            Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    // calling from XML hence public
    public void goToSearchActivity(View view) {
        Intent intent = new Intent(MainActivity.this, SearchActivity.class);
        startActivity(intent);
    }

    private void showSellersOnMap(Bundle bundle, String sellersString) {
        try {
            if (sellersString != null) {
                JSONArray sellers = new JSONArray(sellersString);

                // clearing previous sellers and showing latest
                mMap.clear();
                for (int i = 0; i < sellers.length(); i++) {
                    JSONObject seller = sellers.getJSONObject(i);
                    double latitude = seller.getDouble("latitude");
                    double longitude = seller.getDouble("longitude");
                    String name = seller.getString("name");
                    LatLng sellerLatlng = new LatLng(latitude, longitude);
                    JSONObject service = seller.getJSONArray("services").getJSONObject(0);
                    mMap.addMarker(new MarkerOptions().position(sellerLatlng)
                            .title(name)
                            .snippet(service.getString("name"))
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.search_text_icon)));

                    // animate in last
                    if (i == (sellers.length() - 1)) {
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sellerLatlng, 10));
                    }
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.d(LOG, marker.getTitle() + " clicked");
        return false;
    }

    private void checkLoginAndUpdateUi(NavigationView navigationView ) {
        try {
            JSONObject loginDetails = singleton.getLoginDetails();
            View navigationDrawerHeaderLayout = navigationView.getHeaderView(0);
            Menu navigationDrawerMenu = navigationView.getMenu();

            ImageView navigationHeaderImageView = navigationDrawerHeaderLayout
                    .findViewById(R.id.navigationHeaderImageView);
            TextView navigationHeaderTitleTextView = navigationDrawerHeaderLayout
                    .findViewById(R.id.navigationHeaderTitleTextView);
            TextView navigationHeaderSubTitleTextView = navigationDrawerHeaderLayout
                    .findViewById(R.id.navigationHeaderSubTitleTextView);

            MenuItem profileMenuItem = navigationDrawerMenu.findItem(R.id.nav_user_profile);
            MenuItem servicesMenuItem = navigationDrawerMenu.findItem(R.id.nav_seller_services);
            MenuItem loginMenuItem = navigationDrawerMenu.findItem(R.id.nav_user_login);
            MenuItem signupMenuItem = navigationDrawerMenu.findItem(R.id.nav_user_signup);

            // not loggedIn, update Menu
            if (loginDetails == null) {
                profileMenuItem.setVisible(false);
                servicesMenuItem.setVisible(false);
            } else {
                String userName = loginDetails.getString(AppConstants.LoginDetails.NAME);
                String userEmail = loginDetails.getString(AppConstants.LoginDetails.NAME);
                String profileUrl = loginDetails.getString(AppConstants.LoginDetails.PROFILE_URL);

                loginMenuItem.setVisible(false);
                signupMenuItem.setVisible(false);

                navigationHeaderTitleTextView.setText(userName);
                navigationHeaderSubTitleTextView.setText(userEmail);

                // todo set profile pic when loggedIn
                navigationHeaderImageView.setBackgroundResource(R.drawable.ic_user_profile);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
