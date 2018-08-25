package me.varunon9.sellmyservices;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import me.varunon9.sellmyservices.constants.AppConstants;

/**
 * Created by varunkumar on 24/7/18.
 */

public class Singleton {
    private static Singleton singleton;
    private RequestQueue requestQueue;
    private Context context;
    private LocationManager locationManager;
    private JSONObject loginDetails;
    private String LOGIN_DETAILS = "loginDetails";
    private JSONObject profileDetails;
    private String PROFILE_DETAILS = "profileDetails";
    private String TAG = "Singleton";

    // this will be called only one time to sync server database to SQLite
    private boolean fetchServicesFromServer = true;

    private Singleton(Context context) {
        this.context = context;
        requestQueue = getRequestQueue();
    }

    public static synchronized Singleton getInstance(Context context) {
        if (singleton == null) {
            singleton = new Singleton(context);
        }
        return singleton;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }
        return requestQueue;
    }

    public Location getCurrentLocation()
            throws SecurityException {
        if (locationManager == null) {
            locationManager =
                    (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
        }
        Criteria criteria = new Criteria();
        Location location = locationManager.getLastKnownLocation(
                locationManager.getBestProvider(criteria, false)
        );
        return location;
    }

    /**
     * This method will check if authToken and other login details are present
     * in shared preferences
     * @return null if user is not signed-in else return login details as json
     */
    public JSONObject getLoginDetails() {
        if (loginDetails == null) {
            SharedPreferences sharedPreferences = getLoginSharedPreferences();
            String loginData = sharedPreferences.getString(LOGIN_DETAILS, null);
            if (loginData != null) {
                try {
                    loginDetails = new JSONObject(loginData);
                    Long expiryTime = loginDetails.getLong(AppConstants.LoginDetails.EXPIRY_TIME); // in s
                    Long currentTimestamp = System.currentTimeMillis() / 1000; // in secs
                    if (currentTimestamp > expiryTime) {
                        loginDetails = null;
                        Log.d(TAG, "token is expired. Need to login again");
                    }
                } catch(Exception e) {
                    loginDetails = null;
                    e.printStackTrace();
                }
            }
        }
        return loginDetails;
    }

    public void setLoginDetails(JSONObject loginDetails) {
        /**
         * ideally all LoginDetails params should be available (except mobile and profilePic)
         * Still checking for all keys for robustness
         */
        try {
            if (!loginDetails.has(AppConstants.LoginDetails.ID)) {
                loginDetails.put(AppConstants.LoginDetails.ID, 0);
            }
            if (!loginDetails.has(AppConstants.LoginDetails.FIRST_NAME)) {
                loginDetails.put(AppConstants.LoginDetails.FIRST_NAME, "");
            }
            if (!loginDetails.has(AppConstants.LoginDetails.EMAIL)) {
                loginDetails.put(AppConstants.LoginDetails.EMAIL, "");
            }
            if (!loginDetails.has(AppConstants.LoginDetails.MOBILE)) {
                loginDetails.put(AppConstants.LoginDetails.MOBILE, "");
            }
            if (!loginDetails.has(AppConstants.LoginDetails.PROFILE_PIC)) {
                loginDetails.put(AppConstants.LoginDetails.PROFILE_PIC, "");
            }
            if (!loginDetails.has(AppConstants.LoginDetails.AUTH_TOKEN)) {
                loginDetails.put(AppConstants.LoginDetails.AUTH_TOKEN, "");
            }
            if (!loginDetails.has(AppConstants.LoginDetails.EXPIRY_TIME)) {
                loginDetails.put(AppConstants.LoginDetails.EXPIRY_TIME, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.loginDetails = loginDetails;

        SharedPreferences sharedPreferences = getLoginSharedPreferences();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String loginData = loginDetails.toString();
        editor.putString(LOGIN_DETAILS, loginData);
        editor.apply(); // we don't care about return value so not using commit
    }

    public void logout() {
        // clearing login data
        SharedPreferences sharedPreferences = getLoginSharedPreferences();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear().commit();

        // clearing profile data
        sharedPreferences = getProfileDetailsSharedPreferences();
        editor = sharedPreferences.edit();
        editor.clear().commit();

        this.loginDetails = null;
        this.profileDetails = null;
    }

    private SharedPreferences getLoginSharedPreferences() {
        String sharedPreferencesFileName = AppConstants.SHARED_PREFERENCES_PREFIX
                + LOGIN_DETAILS;
        SharedPreferences sharedPreferences =
                context.getSharedPreferences(sharedPreferencesFileName, Context.MODE_PRIVATE);
        return sharedPreferences;
    }

    public JSONObject getProfileDetails() {
        if (profileDetails != null) {
            SharedPreferences sharedPreferences = getProfileDetailsSharedPreferences();
            String profileDetailsData = sharedPreferences.getString(PROFILE_DETAILS, null);

            if (profileDetailsData != null) {
                try {
                    profileDetails = new JSONObject(profileDetailsData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return profileDetails;
    }

    public void setProfileDetails(JSONObject profileDetails) {
        /**
         * Ideally profileDetails should have all keys of AppConstant.UserProfile
         * Still checking for robustness
         */
        try {
            if (!profileDetails.has(AppConstants.UserProfile.ID)) {
                profileDetails.put(AppConstants.UserProfile.ID, 0);
            }
            if (!profileDetails.has(AppConstants.UserProfile.MOBILE)) {
                profileDetails.put(AppConstants.UserProfile.MOBILE, "");
            }
            if (!profileDetails.has(AppConstants.UserProfile.FIRST_NAME)) {
                profileDetails.put(AppConstants.UserProfile.FIRST_NAME, "");
            }
            if (!profileDetails.has(AppConstants.UserProfile.LAST_NAME)) {
                profileDetails.put(AppConstants.UserProfile.LAST_NAME, "");
            }
            if (!profileDetails.has(AppConstants.UserProfile.GENDER)) {
                profileDetails.put(AppConstants.UserProfile.GENDER, "male");
            }
            if (!profileDetails.has(AppConstants.UserProfile.EMAIL)) {
                profileDetails.put(AppConstants.UserProfile.EMAIL, "");
            }
            if (!profileDetails.has(AppConstants.UserProfile.PROFILE_PIC)) {
                profileDetails.put(AppConstants.UserProfile.PROFILE_PIC, "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.profileDetails = profileDetails;

        SharedPreferences sharedPreferences = getProfileDetailsSharedPreferences();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String profileDetailsData = profileDetails.toString();
        editor.putString(PROFILE_DETAILS, profileDetailsData);
        editor.apply();
    }

    private SharedPreferences getProfileDetailsSharedPreferences() {
        String sharedPreferencesFileName = AppConstants.SHARED_PREFERENCES_PREFIX
                + PROFILE_DETAILS;
        SharedPreferences sharedPreferences =
                context.getSharedPreferences(sharedPreferencesFileName, Context.MODE_PRIVATE);
        return sharedPreferences;
    }

    // after first time value will be false
    public boolean isFetchServicesFromServer() {
        boolean requiredValue = fetchServicesFromServer;
        fetchServicesFromServer = false;
        return requiredValue;
    }
}
