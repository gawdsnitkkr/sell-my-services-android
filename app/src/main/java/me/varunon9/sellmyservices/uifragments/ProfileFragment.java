package me.varunon9.sellmyservices.uifragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import org.json.JSONObject;

import me.varunon9.sellmyservices.R;
import me.varunon9.sellmyservices.UiFragmentActivity;
import me.varunon9.sellmyservices.constants.AppConstants;
import me.varunon9.sellmyservices.utils.AjaxCallback;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener {

    private EditText emailEditText;
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText mobileEditText;
    private EditText passwordEditText;
    private Button updateProfileButton;
    private RadioGroup genderRadioGroup;
    private RadioButton maleRadioButton;
    private RadioButton femaleRadioButton;
    private String TAG = "ProfileFragment";

    private UiFragmentActivity uiFragmentActivity;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        uiFragmentActivity = ((UiFragmentActivity) getActivity());

        emailEditText = rootView.findViewById(R.id.profileEmailEditText);
        firstNameEditText = rootView.findViewById(R.id.profileFirstNameEditText);
        lastNameEditText = rootView.findViewById(R.id.profileLastNameEditText);
        mobileEditText = rootView.findViewById(R.id.profileMobileEditText);
        passwordEditText = rootView.findViewById(R.id.profilePasswordEditText);
        updateProfileButton = rootView.findViewById(R.id.updateProfileButton);
        genderRadioGroup = rootView.findViewById(R.id.genderRadioGroup);
        maleRadioButton = rootView.findViewById(R.id.maleRadioButton);
        femaleRadioButton = rootView.findViewById(R.id.femaleRadioButton);

        displayProfile(null); // will read from sharedPreferences

        updateProfileButton.setOnClickListener(this);
        return rootView;
    }

    private void showMessage(String message) {
        uiFragmentActivity.showMessage(message);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.updateProfileButton: {
                String firstName = firstNameEditText.getText().toString();
                String lastName = lastNameEditText.getText().toString();
                String mobile = mobileEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String gender = maleRadioButton.getText().toString().toLowerCase();

                int selectedGenderButtonId = genderRadioGroup.getCheckedRadioButtonId();
                if (selectedGenderButtonId == R.id.femaleRadioButton) {
                    gender = femaleRadioButton.getText().toString().toLowerCase();
                }
                JSONObject profileDetails = new JSONObject();
                try {
                    profileDetails.put(AppConstants.UserProfile.FIRST_NAME, firstName);
                    profileDetails.put(AppConstants.UserProfile.LAST_NAME, lastName);
                    profileDetails.put(AppConstants.UserProfile.MOBILE, mobile);
                    profileDetails.put(AppConstants.UserProfile.GENDER, gender);

                    // password is a special case
                    if (!password.matches("")) {
                        profileDetails.put("password", password);
                    }
                    updateProfileDetails(profileDetails);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    private void displayProfile(JSONObject profileDetails) {
        Log.d(TAG, "displayProfile called");
        if (profileDetails == null) {
            profileDetails = uiFragmentActivity.singleton.getProfileDetails();
        }

        // if still null, hit server to get profileDetails
        if (profileDetails == null) {
            getProfileDetailsFromServer();
            return;
        }
        try {
            String email = profileDetails.getString(AppConstants.UserProfile.EMAIL);
            String firstName = profileDetails.getString(AppConstants.UserProfile.FIRST_NAME);
            String lastName = profileDetails.getString(AppConstants.UserProfile.LAST_NAME);
            String mobile = profileDetails.getString(AppConstants.UserProfile.MOBILE);
            String gender = profileDetails.getString(AppConstants.UserProfile.GENDER);

            emailEditText.setText(email);
            firstNameEditText.setText(firstName);
            lastNameEditText.setText(lastName);
            mobileEditText.setText(mobile);

            if (gender.toLowerCase().equals("male")) {
                maleRadioButton.setChecked(true);
            } else {
                femaleRadioButton.setChecked(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getProfileDetailsFromServer() {
        JSONObject loginDetails = uiFragmentActivity.singleton.getLoginDetails();
        int userId;
        try {
            userId = loginDetails.getInt(AppConstants.LoginDetails.ID);
            String url = AppConstants.Urls.USER_PROFILE.replace(":id", String.valueOf(userId));
            try {
                uiFragmentActivity.showProgressDialog("Getting Profile Details",
                        "Please wait", false);
                uiFragmentActivity.ajaxUtility.makeHttpRequest(url, "GET", null,
                        new AjaxCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        uiFragmentActivity.dismissProgressDialog();
                        try {
                            JSONObject profileDetails = response.getJSONObject("result");
                            uiFragmentActivity.singleton.setProfileDetails(profileDetails);

                            /**
                             * deliberately passing null
                             * singleton will initialize params with blank if they are missing
                             */
                            displayProfile(null);
                            Log.d(TAG, profileDetails.toString());
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
                Log.e(TAG, "get profile request failed");
                showMessage(AppConstants.GENERIC_ERROR_MESSAGE);
                e.printStackTrace();
            }
        } catch (Exception e) {
            Log.e(TAG, "get userId from loginDetails failed");
            showMessage(AppConstants.LOGIN_ERROR_MESSAGE);
        }
    }

    private void updateProfileDetails(JSONObject profileDetails) {
        JSONObject loginDetails = uiFragmentActivity.singleton.getLoginDetails();
        int userId;
        try {
            userId = loginDetails.getInt(AppConstants.LoginDetails.ID);
            String url = AppConstants.Urls.USER_PROFILE.replace(":id", String.valueOf(userId));
            try {
                uiFragmentActivity.showProgressDialog("Updating Profile Details",
                        "Please wait", false);
                uiFragmentActivity.ajaxUtility.makeHttpRequest(url, "PUT", profileDetails,
                        new AjaxCallback() {
                            @Override
                            public void onSuccess(JSONObject response) {
                                uiFragmentActivity.dismissProgressDialog();
                                try {
                                    JSONObject profileDetails = response.getJSONObject("result");
                                    uiFragmentActivity.singleton.setProfileDetails(profileDetails);

                                    /**
                                     * deliberately passing null
                                     * singleton will initialize params with blank if they are missing
                                     */
                                    displayProfile(null);
                                    Log.d(TAG, profileDetails.toString());
                                    showMessage("Success");
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
                Log.e(TAG, "put profile request failed");
                showMessage(AppConstants.GENERIC_ERROR_MESSAGE);
                e.printStackTrace();
            }
        } catch (Exception e) {
            Log.e(TAG, "get userId from loginDetails failed");
            showMessage(AppConstants.LOGIN_ERROR_MESSAGE);
        }
    }
}
