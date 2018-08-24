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
    public static final String GENERIC_ERROR_MESSAGE = "Something went wrong";
    public static final String LOGIN_ERROR_MESSAGE = "Error: Please login again";
    public static final String SERVICES = "services";
    public static final String NAVIGATION_ITEM = "navigationItem";
    public static final String YOUR_PROFILE = "Your Profile";
    public static final String YOUR_SERVICES = "Your Services";
    public static final String LOGIN = "Login";
    public static final String SIGNUP = "Signup";
    public static final String SETTINGS = "Settings";
    public static final String ABOUT_US = "About Us";

    public static class Urls {
        private Urls() {
        }

        private static final String DOMAIN = "http://192.168.43.147:4000";
        //private static final String DOMAIN = "https://sellmyservices.in";
        public static final String SEARCH_SERVICES =
                DOMAIN + "/search/services/";
        public static final String GOOGLE_TOKEN_SIGNIN =
                DOMAIN + "/google-token-signin/";
        public static final String LOGIN = DOMAIN + "/login/";
        public static final String SIGNUP = DOMAIN + "/signup/";
        public static final String USER_PROFILE = DOMAIN + "/users/:id";
        public static final String SERVICES = DOMAIN + "/auth/services";
    }

    public static final String SHARED_PREFERENCES_PREFIX = "me.varunon9.sellmyservices";

    public static class LoginDetails {
        private LoginDetails() {
        }

        public static final String ID = "id";
        public static final String MOBILE = "mobile";
        public static final String FIRST_NAME = "firstName";
        public static final String EMAIL = "email";
        public static final String AUTH_TOKEN = "authToken";
        public static final String EXPIRY_TIME = "expiryTime";
        public static final String PROFILE_PIC = "profilePic";
    }

    public static final String shareAppSubject = "SellMyServices";
    public static final String shareAppBody = "Search and sell your services to nearby locations."
            + "\nDownload the app now"
            + "\nhttps://play.google.com/store/apps/details?id=me.varunon9.sellmyservices";

    public static class UserProfile {
        private UserProfile() {
        }

        public static final String ID = "id";
        public static final String MOBILE = "mobile";
        public static final String FIRST_NAME = "firstName";
        public static final String LAST_NAME = "lastName";
        public static final String GENDER = "gender";
        public static final String EMAIL = "email";
        public static final String PROFILE_PIC = "profilePic";
    }

    public static final String CHOOSE_LOCATION = "Choose location";

    public static class Service {
        private Service() {
        }

        public static final String NAME = "name";
        public static final String DESCRIPTION = "description";
        public static final String TAGS = "tags";
        public static final String LOCATION = "location";
        public static final String LATITUDE = "latitude";
        public static final String LONGITUDE = "longitude";
    }
}