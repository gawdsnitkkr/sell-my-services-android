package me.varunon9.sellmyservices.uifragments;


import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONObject;

import me.varunon9.sellmyservices.R;
import me.varunon9.sellmyservices.UiFragmentActivity;
import me.varunon9.sellmyservices.constants.AppConstants;
import me.varunon9.sellmyservices.utils.AjaxCallback;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignupFragment extends Fragment {

    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText passwordConfirmEditText;
    private Button signupButton;
    private String TAG = "SignupFragment";

    private UiFragmentActivity uiFragmentActivity;

    public SignupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_signup, container, false);

        uiFragmentActivity = ((UiFragmentActivity) getActivity());

        emailEditText = (EditText) rootView.findViewById(R.id.emailSellerSignup);
        passwordEditText = (EditText) rootView.findViewById(R.id.passwordSellerSignup);
        passwordConfirmEditText
                = (EditText) rootView.findViewById(R.id.passwordConfirmSellerSignup);
        signupButton = (Button) rootView.findViewById(R.id.signupButton);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String passwordConfirm = passwordConfirmEditText.getText().toString();

                if (email.equals("") || password.equals("")) {
                    showMessage("All fields are mandatory");
                    return;
                }

                if (!password.equals(passwordConfirm)) {
                    showMessage("Password mismatch");
                    return;
                }

                signup(email, password);
            }
        });
        return rootView;
    }

    private void signup(String email, String password) {
        JSONObject body = new JSONObject();
        String url = AppConstants.Urls.SIGNUP;
        Location location = uiFragmentActivity.singleton.getCurrentLocation();
        try {
            body.put("latitude", location.getLatitude());
            body.put("longitude", location.getLongitude());
            body.put("email", email);
            body.put("password", password);
            uiFragmentActivity.showProgressDialog("Signing Up", "Please wait", false);
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
