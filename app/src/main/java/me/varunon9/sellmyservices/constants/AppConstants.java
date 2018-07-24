package me.varunon9.sellmyservices.constants;

/**
 * Created by varunkumar on 1/7/18.
 */

public final class AppConstants {
    // can't instantiate
    private AppConstants() {
    }

    public static final int ACCESS_LOCATION_REQUEST_CODE = 1;
    public static final String ACCESS_LOCATION_TOAST_MESSAGE =
            "Please grant location permission to use this service";
    public static final String CURRENT_LOCATION_MARKER = "You are here";
    public static final String INTERNET_CONNECTION_IS_MANDATORY =
            "Internet connection is mandatory.";
    public static final String DATABASE_NAME = "sellMyServices.db";

    public static class Urls {
        private Urls() {
        }

        private static final String DOMAIN = "http://192.168.43.147:4000";
        //private static final String DOMAIN = "https://sellmyservices.in";
        public static final String SEARCH_SELLERS =
                DOMAIN + "/search/sellers";
    }
}