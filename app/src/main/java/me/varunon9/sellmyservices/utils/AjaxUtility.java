package me.varunon9.sellmyservices.utils;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
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
        final String requestBody = body.toString();
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d(LOG, response);
                    ajaxCallback.onSuccess(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(LOG, error.toString());
                    ajaxCallback.onError(error);
                }
            }){
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return requestBody == null ?
                                null : requestBody.getBytes("utf-8");
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            };
            singleton.getRequestQueue().add(stringRequest);
        } catch(Exception e) {
            Log.d(LOG, "Exception makePostRequest");
            e.printStackTrace();
        }
    }
}
