package me.varunon9.sellmyservices.utils;

import com.android.volley.VolleyError;

/**
 * Created by varunkumar on 24/7/18.
 */

public interface AjaxCallback {
    void onSuccess(String response);
    void onError(VolleyError error);
}
