package me.varunon9.sellmyservices.servicefragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import me.varunon9.sellmyservices.R;
import me.varunon9.sellmyservices.ServiceActivity;
import me.varunon9.sellmyservices.db.models.Service;

/**
 * A simple {@link Fragment} subclass.
 */
public class ServiceDetailsFragment extends Fragment {

    private ServiceActivity serviceActivity;
    private String TAG = "ServiceDetailsFragment";
    private TextView serviceNameTextView;
    private TextView serviceDescriptionTextView;
    private TextView serviceLocationTextView;
    private TextView serviceTagsTextView;
    private TextView serviceRatingTextView;

    public ServiceDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =
                inflater.inflate(R.layout.fragment_service_details, container, false);
        serviceActivity = ((ServiceActivity) getActivity());
        Service service = serviceActivity.service;

        serviceNameTextView = rootView.findViewById(R.id.serviceNameTextView);
        serviceRatingTextView = rootView.findViewById(R.id.serviceRatingTextView);
        serviceDescriptionTextView = rootView.findViewById(R.id.serviceDescriptionTextView);
        serviceTagsTextView = rootView.findViewById(R.id.serviceTagsTextView);
        serviceLocationTextView = rootView.findViewById(R.id.serviceLocationTextView);

        serviceNameTextView.setText(service.getName());
        serviceDescriptionTextView.setText(service.getDescription());
        serviceTagsTextView.setText(service.getTags());
        serviceRatingTextView.setText(String.valueOf(service.getRating()));
        serviceLocationTextView.setText(service.getLocation());

        return rootView;
    }

}
