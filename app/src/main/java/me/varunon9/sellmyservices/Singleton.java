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
    private SharedPreferences sharedPreferences;
    private String LOGIN_DETAILS = "loginDetails";
    private String TAG = "Singleton";

    private Singleton(Context context) {
        this.context = context;
        requestQueue = getRequestQueue();
        String sharedPreferencesFileName = AppConstants.SHARED_PREFERENCES_PREFIX
                + LOGIN_DETAILS;
        sharedPreferences = context.getSharedPreferences(sharedPreferencesFileName,
                Context.MODE_PRIVATE);
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
     * @return null if user is not signed in else login details as json
     */
    public JSONObject getLoginDetails() {
        return loginDetails;
    }

    public void setLoginDetails(JSONObject loginDetails) {
        this.loginDetails = loginDetails;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String loginData = loginDetails.toString();
        editor.putString(LOGIN_DETAILS, loginData);
        editor.apply(); // we don't care about return value so not using commit
    }

    public void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear().commit();
        this.loginDetails = null;
    }
}
