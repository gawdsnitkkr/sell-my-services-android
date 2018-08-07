package me.varunon9.sellmyservices;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.json.JSONObject;

import me.varunon9.sellmyservices.constants.AppConstants;
import me.varunon9.sellmyservices.uifragments.AboutUsFragment;
import me.varunon9.sellmyservices.uifragments.LoginFragment;
import me.varunon9.sellmyservices.uifragments.SellerServicesFragment;
import me.varunon9.sellmyservices.uifragments.SignupFragment;

/**
 * This activity  displays various UI fragments when called from navigation drawer
 */
public class UiFragmentActivity extends AppCompatActivity {

    private static final String LOG = "UiFragmentActivity";
    private Singleton singleton;
    private GoogleSignInClient mGoogleSignInClient;
    private  int RC_SIGN_IN = 0;
    private String TAG = "UiFragmentActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG, "onCreate called");
        setContentView(R.layout.activity_fragment);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // display back button in action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        singleton = Singleton.getInstance(getApplicationContext());

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            int navigationLink = bundle.getInt(AppConstants.NAVIGATION_ITEM);
            Fragment fragment = getSelectedFragment(navigationLink);
            if (fragment != null) {
                FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                        .beginTransaction();
                fragmentTransaction.add(R.id.frameLayout, fragment);
                fragmentTransaction.commit();
            } else {
                Log.e(LOG, "Null Fragment to display");
            }
        }

        // login using google implementation
        JSONObject loginDetails = singleton.getLoginDetails();
        if (loginDetails == null) {
            // Configure sign-in to request the user's ID, email address, and basic
            // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();

            // Build a GoogleSignInClient with the options specified by gso.
            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(LOG, "onResume called");
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private Fragment getSelectedFragment(int id) {
        Fragment fragment = null;
        String title = "";
        if (id == R.id.nav_user_profile) {
            title = AppConstants.YOUR_PROFILE;
        } else if (id == R.id.nav_seller_services) {
            title = AppConstants.YOUR_SERVICES;
            fragment = new SellerServicesFragment();
        } else if (id == R.id.nav_user_login) {
            title = AppConstants.LOGIN;
            fragment = new LoginFragment();
        } else if (id == R.id.nav_user_signup) {
            title = AppConstants.SIGNUP;
            fragment = new SignupFragment();
        } else if (id == R.id.nav_about_us) {
            title = AppConstants.ABOUT_US;
            fragment = new AboutUsFragment();
        }
        updateActionBarTitle(title);
        return fragment;
    }

    private void updateActionBarTitle(String title) {
        if (title != null) {
            getSupportActionBar().setTitle(title);
        }
    }

    // calling from XML
    public void goToSignupFragment(View view) {
        Fragment fragment = new SignupFragment();
        String title = AppConstants.SIGNUP;
        updateActionBarTitle(title);
        replaceOldFragment(fragment);
    }

    public void goToLoginFragment(View view) {
        Fragment fragment = new LoginFragment();
        String title = AppConstants.LOGIN;
        updateActionBarTitle(title);
        replaceOldFragment(fragment);
    }

    private void replaceOldFragment(Fragment newFragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                .beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, newFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            String idToken = account.getIdToken();

            // TODO(developer): send ID Token to server and validate
            // Signed in successfully, show authenticated UI.
            //updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            //updateUI(null);
        }
    }

}
