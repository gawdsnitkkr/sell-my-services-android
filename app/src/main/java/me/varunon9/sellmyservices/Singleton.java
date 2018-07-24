package me.varunon9.sellmyservices;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by varunkumar on 24/7/18.
 */

public class Singleton {
    private static Singleton singleton;
    private RequestQueue requestQueue;
    private Context context;

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
}
