package me.varunon9.sellmyservices.uifragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;

import org.json.JSONObject;

import me.varunon9.sellmyservices.R;
import me.varunon9.sellmyservices.UiFragmentActivity;
import me.varunon9.sellmyservices.constants.AppConstants;
import me.varunon9.sellmyservices.utils.AjaxCallback;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment implements View.OnClickListener {

    private GoogleSignInClient mGoogleSignInClient;
    private  int SIGN_IN_REQUEST_CODE = 0;

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private String TAG = "LoginFragment";

    private UiFragmentActivity uiFragmentActivity;

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        uiFragmentActivity = ((UiFragmentActivity) getActivity());

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

        SignInButton signInUsingGoogleButton = rootView.findViewById(R.id.signInUsingGoogleButton);
        signInUsingGoogleButton.setOnClickListener(this);

        emailEditText = (EditText) rootView.findViewById(R.id.loginEmailEditText);
        passwordEditText = (EditText) rootView.findViewById(R.id.loginPasswordEditText);
        loginButton = (Button) rootView.findViewById(R.id.loginButton);

        loginButton.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.signInUsingGoogleButton:
                signInUsingGoogle(null);
                break;
            case R.id.loginButton: {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                if (email.equals("") || password.equals("")) {
                    showMessage("All fields are mandatory");
                } else {
                    login(email, password);
                }
                break;
            }
        }
    }

    private void signInUsingGoogle(View v) {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        getActivity().startActivityForResult(signInIntent, SIGN_IN_REQUEST_CODE);
    }

    private void login(String email, String password) {
        JSONObject body = new JSONObject();
        String url = AppConstants.Urls.LOGIN;
        try {
            body.put("email", email);
            body.put("password", password);
            uiFragmentActivity.showProgressDialog("Signing You In", "Please wait", false);
            uiFragmentActivity.ajaxUtility.makeHttpRequest(url, "POST", body, new AjaxCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    uiFragmentActivity.dismissProgressDialog();
                    try {
                        JSONObject loginDetails = response.getJSONObject("result");
                        loginDetails.put(AppConstants.LoginDetails.AUTH_TOKEN,
                                response.getString(AppConstants.LoginDetails.AUTH_TOKEN));
                        Long expiresIn = response.getLong("expiresIn");
                        Long expiryTime = (System.currentTimeMillis() / 1000) + expiresIn; // in secs
                        loginDetails.put(AppConstants.LoginDetails.EXPIRY_TIME,
                                expiryTime);
                        uiFragmentActivity.singleton.setLoginDetails(loginDetails);
                        Log.d(TAG, loginDetails.toString());
                        uiFragmentActivity.goToMainActivity();
                    } catch (Exception e) {
                        e.printStackTrace();
                        showMessage("Parsing error");
                    }
                }

                @Override
                public void onError(JSONObject response) {
                    uiFragmentActivity.dismissProgressDialog();
                    try {
                        String message = response.getString("message");
                        int statusCode = response.getInt("statusCode");
                        showMessage(statusCode + ": " + message);
                    } catch (Exception e) {
                        e.printStackTrace();
                        showMessage(AppConstants.GENERIC_ERROR_MESSAGE);
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "signup request failed");
            showMessage(AppConstants.GENERIC_ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void showMessage(String message) {
        uiFragmentActivity.showMessage(message);
    }
}
