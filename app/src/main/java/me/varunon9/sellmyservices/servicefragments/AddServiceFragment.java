package me.varunon9.sellmyservices.servicefragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.google.android.gms.maps.model.LatLng;

import me.varunon9.sellmyservices.R;
import me.varunon9.sellmyservices.ServiceActivity;
import me.varunon9.sellmyservices.constants.AppConstants;
import me.varunon9.sellmyservices.db.models.Service;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddServiceFragment extends Fragment implements View.OnClickListener {

    private ServiceActivity serviceActivity;
    private String TAG = "AddServiceFragment";
    private Service service;

    private EditText serviceNameEditText;
    private EditText serviceDescriptionEditText;
    private EditText serviceTagsEditText;
    private Button addServiceButton;

    public AddServiceFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =
                inflater.inflate(R.layout.fragment_add_service, container, false);

        serviceActivity = ((ServiceActivity) getActivity());
        service = new Service();

        serviceNameEditText = rootView.findViewById(R.id.serviceNameEditText);
        serviceDescriptionEditText = rootView.findViewById(R.id.serviceDescriptionEditText);
        serviceTagsEditText = rootView.findViewById(R.id.serviceTagsEditText);
        addServiceButton = rootView.findViewById(R.id.addServiceButton);

        addServiceButton.setOnClickListener(this);

        SupportPlaceAutocompleteFragment placeAutocompleteFragment = (SupportPlaceAutocompleteFragment)
                getChildFragmentManager()
                        .findFragmentById(R.id.place_autocomplete_fragment);
        placeAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                String placeAddress = place.getAddress().toString();
                LatLng latlng = place.getLatLng();
                Log.i(TAG, "Place: " + placeAddress);
                service.setLocation(placeAddress);
                service.setLatitude(latlng.latitude);
                service.setLongitude(latlng.longitude);
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
                serviceActivity.showMessage(
                        AppConstants.GENERIC_ERROR_MESSAGE + " Status: " + status
                );
            }
        });
        placeAutocompleteFragment.setHint(AppConstants.CHOOSE_LOCATION);

        // changing icon
        ImageView searchIcon = (ImageView)((LinearLayout)placeAutocompleteFragment
                .getView()).getChildAt(0);
        searchIcon.setImageResource(R.drawable.search_text_icon);
        return rootView;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.addServiceButton: {
                String serviceName = serviceNameEditText.getText().toString();
                String serviceDescription = serviceDescriptionEditText
                        .getText().toString();
                String serviceTags = serviceTagsEditText.getText().toString();

                if (service.getLatitude() == 0) {
                    serviceActivity.showMessage("Location is mandatory");
                    return;
                }
                if (serviceName.equals("")) {
                    serviceActivity.showMessage("Name is mandatory");
                    return;
                }
                service.setName(serviceName);
                service.setDescription(serviceDescription);
                service.setTags(serviceTags);

                serviceActivity.addService(service);
                break;
            }
        }
    }
}
