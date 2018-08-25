package me.varunon9.sellmyservices;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.json.JSONObject;

import me.varunon9.sellmyservices.constants.AppConstants;
import me.varunon9.sellmyservices.db.DbHelper;
import me.varunon9.sellmyservices.db.models.Service;
import me.varunon9.sellmyservices.db.services.ServiceService;
import me.varunon9.sellmyservices.servicefragments.AddServiceFragment;
import me.varunon9.sellmyservices.servicefragments.EditServiceFragment;
import me.varunon9.sellmyservices.servicefragments.ServiceDetailsFragment;
import me.varunon9.sellmyservices.utils.AjaxCallback;
import me.varunon9.sellmyservices.utils.AjaxUtility;

public class ServiceActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    // this object will be used in details and edit fragments
    public Service service;
    public Singleton singleton;
    private String TAG = "ServiceActivity";
    private ProgressDialog progressDialog;
    public AjaxUtility ajaxUtility;
    public DbHelper dbHelper;
    private ServiceService serviceService;

    private static final int SERVICE_DETAILS_TAB = 0;
    private static final int ADD_SERVICE_TAB = 1;
    private static final int EDIT_SERVICE_TAB = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // display back button in action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.serviceContainer);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        service = (Service) getIntent().getSerializableExtra("service");
        if (service == null) {
            // switch to add service fragment
            mViewPager.setCurrentItem(ADD_SERVICE_TAB, true);
        }

        ajaxUtility = new AjaxUtility(getApplicationContext());
        dbHelper = new DbHelper(getApplicationContext());
        serviceService = new ServiceService(dbHelper);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
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

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            Fragment fragment = null;
            switch (position) {
                case SERVICE_DETAILS_TAB:
                    fragment = new ServiceDetailsFragment();
                    break;
                case ADD_SERVICE_TAB:
                    fragment = new AddServiceFragment();
                    break;
                case EDIT_SERVICE_TAB:
                    fragment = new EditServiceFragment();
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
    }

    public void showProgressDialog(String title, String message, boolean isCancellable) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(ServiceActivity.this);
        }
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(isCancellable);
        progressDialog.show();
    }

    public void dismissProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    public void showMessage(String message) {
        View parentLayout = findViewById(R.id.ServiceActivityContent);
        Snackbar.make(parentLayout, message, Snackbar.LENGTH_LONG).show();
    }

    public void addService(Service newService) {
        JSONObject serviceObject = new JSONObject();
        showProgressDialog("Creating Service", "Please wait", false);
        try {
            serviceObject.put(AppConstants.Service.NAME, newService.getName());
            serviceObject.put(AppConstants.Service.DESCRIPTION, newService.getDescription());
            serviceObject.put(AppConstants.Service.TAGS, newService.getTags());
            serviceObject.put(AppConstants.Service.LATITUDE, newService.getLatitude());
            serviceObject.put(AppConstants.Service.LONGITUDE, newService.getLongitude());
            serviceObject.put(AppConstants.Service.LOCATION, newService.getLocation());

            String url = AppConstants.Urls.SERVICES;

            ajaxUtility.makeHttpRequest(url, "POST", serviceObject, new AjaxCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    try {
                        JSONObject createdServiceObject = response.getJSONObject("result");

                        // updating ServiceActivity.service with newly created service
                        service = getServiceFromJsonObject(createdServiceObject);
                        serviceService.createService(service);

                        // switching to serviceDetails view which will display newly created service
                        //mViewPager.setCurrentItem(SERVICE_DETAILS_TAB);
                        //showMessage("Service added successfully");

                        goToUiFragmentActivity(); // bad idea to switch?
                    } catch (Exception e) {
                        e.printStackTrace();
                        showMessage("Parsing error");
                    }
                }

                @Override
                public void onError(JSONObject response) {
                    try {
                        String message = response.getString("message");
                        int statusCode = response.getInt("statusCode");
                        showMessage(statusCode + ": " + message);
                    } catch (Exception e) {
                        e.printStackTrace();
                        showMessage(AppConstants.GENERIC_ERROR_MESSAGE);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            showMessage(AppConstants.GENERIC_ERROR_MESSAGE);
        } finally {
            dismissProgressDialog();
        }
    }

    public void updateService(Service updatedService) {
        JSONObject serviceObject = new JSONObject();
        showProgressDialog("Saving details", "Please wait", false);
        try {
            serviceObject.put(AppConstants.Service.ID, updatedService.getId());
            serviceObject.put(AppConstants.Service.NAME, updatedService.getName());
            serviceObject.put(AppConstants.Service.DESCRIPTION, updatedService.getDescription());
            serviceObject.put(AppConstants.Service.TAGS, updatedService.getTags());
            serviceObject.put(AppConstants.Service.LATITUDE, updatedService.getLatitude());
            serviceObject.put(AppConstants.Service.LONGITUDE, updatedService.getLongitude());
            serviceObject.put(AppConstants.Service.LOCATION, updatedService.getLocation());

            String url = AppConstants.Urls.SERVICES;

            ajaxUtility.makeHttpRequest(url, "PUT", serviceObject, new AjaxCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    try {
                        JSONObject updatedServiceObject = response.getJSONObject("result");

                        // updating ServiceActivity.service with newly updated service
                        service = getServiceFromJsonObject(updatedServiceObject);
                        serviceService.updateService(service);

                        // switching to serviceDetails view which will display newly created service
                        //mViewPager.setCurrentItem(SERVICE_DETAILS_TAB);
                        //showMessage("Service updated successfully");

                        goToUiFragmentActivity(); // bad idea to switch?
                    } catch (Exception e) {
                        e.printStackTrace();
                        showMessage("Parsing error");
                    }
                }

                @Override
                public void onError(JSONObject response) {
                    try {
                        String message = response.getString("message");
                        int statusCode = response.getInt("statusCode");
                        showMessage(statusCode + ": " + message);
                    } catch (Exception e) {
                        e.printStackTrace();
                        showMessage(AppConstants.GENERIC_ERROR_MESSAGE);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            showMessage(AppConstants.GENERIC_ERROR_MESSAGE);
        } finally {
            dismissProgressDialog();
        }
    }

    private Service getServiceFromJsonObject(JSONObject serviceObject) {
        Service service = new Service();
        try {
            service.setId(serviceObject.getInt(AppConstants.Service.ID));
            service.setName(serviceObject.getString(AppConstants.Service.NAME));
            service.setDescription(serviceObject.getString(AppConstants.Service.DESCRIPTION));
            service.setTags(serviceObject.getString(AppConstants.Service.TAGS));
            service.setRating(serviceObject.getDouble(AppConstants.Service.RATING));
            service.setRatingCount(serviceObject.getInt(AppConstants.Service.RATING_COUNT));
            service.setLatitude(serviceObject.getDouble(AppConstants.Service.LATITUDE));
            service.setLongitude(serviceObject.getDouble(AppConstants.Service.LONGITUDE));
            service.setLocation(serviceObject.getString(AppConstants.Service.LOCATION));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return service;
    }

    private void goToUiFragmentActivity() {
        Intent intent = new Intent(ServiceActivity.this, UiFragmentActivity.class);
        Bundle args = new Bundle();
        args.putInt(AppConstants.NAVIGATION_ITEM, R.id.navSellerServices);

        // clear history stack so that back button does no lead to ServiceActivity
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtras(args);
        startActivity(intent);
    }
}
