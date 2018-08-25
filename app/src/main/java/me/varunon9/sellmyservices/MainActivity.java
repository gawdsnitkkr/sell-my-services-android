package me.varunon9.sellmyservices;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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
    private static final String TAG = "MainActivity";
    private Singleton singleton;
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate called");
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
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }
            doubleBackToExitPressedOnce = true;
            showMessage("Please click BACK again to exit");

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
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
        if (id == R.id.navShareApp) {
            shareApp();
        } else if (id == R.id.navRateApp) {
            rateApp();
        } else if (id == R.id.navUserLogout) {
            singleton.logout();

            // refreshing MainActivity
            Intent intent = getIntent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            finish();
            startActivity(intent);
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
        Log.d(TAG, "onResume called");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady called");
        mMap = googleMap;
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mMap.setOnMarkerClickListener(this);
        if (bundle != null) {
            String servicesString = bundle.getString(AppConstants.SERVICES);
            if (servicesString != null) {
                showSellersOnMap(bundle, servicesString);
                return;
            }
        }
        Location location = null;
        if (contextUtility.isBuildVersionGreaterEqualToMarshmallow()) {
            if (contextUtility.isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
                // show the user his current location
                location = singleton.getCurrentLocation();
            } else {
                // request for location permission
                ActivityCompat.requestPermissions(this, new String[] {
                        Manifest.permission.ACCESS_FINE_LOCATION
                }, AppConstants.ACCESS_LOCATION_REQUEST_CODE);
            }
        } else {
            // show the user his current location
            location = singleton.getCurrentLocation();
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
                    Location location = singleton.getCurrentLocation();
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

    private void showSellersOnMap(Bundle bundle, String servicesString) {
        try {
            if (servicesString != null) {
                JSONArray services = new JSONArray(servicesString);

                // clearing previous sellers and showing latest
                mMap.clear();
                for (int i = 0; i < services.length(); i++) {
                    JSONObject service = services.getJSONObject(i);
                    double latitude = service.getDouble("latitude");
                    double longitude = service.getDouble("longitude");
                    String serviceName = service.getString("name");
                    LatLng sellerLatlng = new LatLng(latitude, longitude);
                    JSONObject seller = service.getJSONObject("user");
                    Marker marker = mMap.addMarker(new MarkerOptions().position(sellerLatlng)
                            .title(seller.getString("firstName"))
                            .snippet(serviceName)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.search_text_icon)));
                    marker.setTag(service);

                    // animate in last
                    if (i == (services.length() - 1)) {
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
        Log.d(TAG, marker.getTitle() + " clicked");
        JSONObject serviceObject = (JSONObject) marker.getTag();
        if (serviceObject != null) {
            // go to ServiceResultActivity and populate clicked service result
            Intent intent = new Intent(MainActivity.this, ServiceResultActivity.class);
            intent.putExtra("service", serviceObject.toString());
            startActivity(intent);
        }
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

            MenuItem profileMenuItem = navigationDrawerMenu.findItem(R.id.navUserProfile);
            MenuItem servicesMenuItem = navigationDrawerMenu.findItem(R.id.navSellerServices);
            MenuItem loginMenuItem = navigationDrawerMenu.findItem(R.id.navUserLogin);
            MenuItem signupMenuItem = navigationDrawerMenu.findItem(R.id.navUserSignup);
            MenuItem logoutMenuItem = navigationDrawerMenu.findItem(R.id.navUserLogout);

            // not loggedIn, update Menu
            if (loginDetails == null) {
                profileMenuItem.setVisible(false);
                servicesMenuItem.setVisible(false);
                logoutMenuItem.setVisible(false);
            } else {
                String userFirstName = loginDetails.getString(AppConstants.LoginDetails.FIRST_NAME);
                String userEmail = loginDetails.getString(AppConstants.LoginDetails.EMAIL);
                String profileUrl = loginDetails.getString(AppConstants.LoginDetails.PROFILE_PIC);

                loginMenuItem.setVisible(false);
                signupMenuItem.setVisible(false);

                navigationHeaderTitleTextView.setText(userFirstName);
                navigationHeaderSubTitleTextView.setText(userEmail);

                // todo set profile pic when loggedIn
                navigationHeaderImageView.setBackgroundResource(R.drawable.ic_user_profile);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void shareApp() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = AppConstants.shareAppBody;
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                AppConstants.shareAppSubject);
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    private void rateApp() {
        Uri uri = Uri.parse("market://details?id=" + this.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY
                | Intent.FLAG_ACTIVITY_NEW_DOCUMENT
                | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id="
                            + this.getPackageName())));
        }
    }

    private void showMessage(String message) {
        View parentLayout = findViewById(R.id.mainActivityContent);
        Snackbar.make(parentLayout, message, Snackbar.LENGTH_LONG).show();
    }
}
