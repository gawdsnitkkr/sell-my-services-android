package me.varunon9.sellmyservices.utils;

import com.android.volley.VolleyError;

import org.json.JSONObject;

/**
 * Created by varunkumar on 24/7/18.
 */

public interface AjaxCallback {
    void onSuccess(JSONObject response);
    void onError(VolleyError error);
}
