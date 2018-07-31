package me.varunon9.sellmyservices.utils;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import me.varunon9.sellmyservices.Singleton;

/**
 * Created by varunkumar on 24/7/18.
 */

public class AjaxUtility {
    private static final String LOG = "AjaxUtility";
    Singleton singleton;
    Context context;

    public AjaxUtility(Context context) {
        this.context = context;
        singleton = Singleton.getInstance(context);
    }

    public void makePostRequest(String url, JSONObject body, final AjaxCallback ajaxCallback) {
        try {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                    url, body, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    Log.d(LOG, response.toString());
                    ajaxCallback.onSuccess(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(LOG, error.toString());
                    ajaxCallback.onError(error);
                }
            });
            singleton.getRequestQueue().add(jsonObjectRequest);
        } catch(Exception e) {
            Log.d(LOG, "Exception makePostRequest");
            e.printStackTrace();
        }
    }
}
