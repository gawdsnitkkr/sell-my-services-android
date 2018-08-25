package me.varunon9.sellmyservices;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import me.varunon9.sellmyservices.constants.AppConstants;
import me.varunon9.sellmyservices.db.models.Service;
import me.varunon9.sellmyservices.utils.AjaxCallback;
import me.varunon9.sellmyservices.utils.AjaxUtility;

public class ServiceResultActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private AjaxUtility ajaxUtility;

    private String TAG = "ServiceResultActivity";
    private TextView serviceDescriptionTextView;
    private TextView serviceLocationTextView;
    private TextView serviceTagsTextView;
    private TextView serviceRatingTextView;
    private TextView sellerNameTextView;
    private TextView sellerEmailTextView;
    private TextView sellerMobileTextView;
    private EditText feedbackEditText; // todo: add feedback and rating for seeker

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_result);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ajaxUtility = new AjaxUtility(getApplicationContext());
        serviceDescriptionTextView = (TextView) findViewById(R.id.serviceDescriptionTextView);
        serviceLocationTextView = (TextView) findViewById(R.id.serviceLocationTextView);
        serviceRatingTextView = (TextView) findViewById(R.id.serviceRatingTextView);
        serviceTagsTextView = (TextView) findViewById(R.id.serviceTagsTextView);
        sellerNameTextView = (TextView) findViewById(R.id.sellerNameTextView);
        sellerEmailTextView = (TextView) findViewById(R.id.sellerEmailTextView);
        sellerMobileTextView = (TextView) findViewById(R.id.sellerMobileTextView);

        Intent intent = getIntent();
        try {
            JSONObject serviceObject = new JSONObject(intent.getStringExtra("service"));
            updateUi(serviceObject);
        } catch (Exception e) {
            e.printStackTrace();
            showMessage(AppConstants.GENERIC_ERROR_MESSAGE);
        }

    }

    private void showProgressDialog(String title, String message, boolean isCancellable) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(ServiceResultActivity.this);
        }
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(isCancellable);
        progressDialog.show();
    }

    private void dismissProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    private void showMessage(String message) {
        View parentLayout = findViewById(R.id.serviceResultActivityContent);
        Snackbar.make(parentLayout, message, Snackbar.LENGTH_LONG).show();
    }

    private void updateUi(JSONObject serviceObject) {
        Log.d(TAG, serviceObject.toString());
        try {
            getSupportActionBar().setTitle(serviceObject.get(AppConstants.Service.NAME).toString());
            serviceDescriptionTextView
                    .setText(serviceObject.getString(AppConstants.Service.DESCRIPTION));
            serviceLocationTextView
                    .setText(serviceObject.getString(AppConstants.Service.LOCATION));
            serviceRatingTextView
                    .setText(serviceObject.getString(AppConstants.Service.RATING));
            serviceTagsTextView
                    .setText(serviceObject.getString(AppConstants.Service.TAGS));

            JSONObject sellerObject = serviceObject.getJSONObject("user");
            sellerNameTextView.setText(sellerObject.getString(AppConstants.UserProfile.FIRST_NAME));
            sellerEmailTextView.setText(sellerObject.getString(AppConstants.UserProfile.EMAIL));
            sellerMobileTextView.setText(sellerObject.getString(AppConstants.UserProfile.MOBILE));
        } catch (Exception e) {
            e.printStackTrace();
            showMessage("Error updating UI");
        }
    }
}
