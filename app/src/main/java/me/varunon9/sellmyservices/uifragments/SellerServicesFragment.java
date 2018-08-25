package me.varunon9.sellmyservices.uifragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import me.varunon9.sellmyservices.R;
import me.varunon9.sellmyservices.UiFragmentActivity;
import me.varunon9.sellmyservices.constants.AppConstants;
import me.varunon9.sellmyservices.db.DbHelper;
import me.varunon9.sellmyservices.db.models.Service;
import me.varunon9.sellmyservices.db.services.ServiceService;
import me.varunon9.sellmyservices.utils.AjaxCallback;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnServiceListFragmentInteractionListener}
 * interface.
 */
public class SellerServicesFragment extends Fragment {

    private OnServiceListFragmentInteractionListener mListener;
    private UiFragmentActivity uiFragmentActivity;
    private String TAG = "SellerServicesFragment";

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SellerServicesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_service_item_list, container, false);

        uiFragmentActivity = ((UiFragmentActivity) getActivity());

        // Set the adapter
        Context context = rootView.getContext();
        RecyclerView recyclerView = rootView.findViewById(R.id.serviceListRecyclerView);

        if (recyclerView instanceof RecyclerView) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));

            final List<Service> serviceList = new ArrayList<>();
            final ServiceItemRecyclerViewAdapter serviceItemRecyclerViewAdapter =
                    new ServiceItemRecyclerViewAdapter(serviceList, mListener);
            final ServiceService serviceService =
                    new ServiceService(uiFragmentActivity.dbHelper);

            if (uiFragmentActivity.singleton.isFetchServicesFromServer()) {
                /**
                 * get services from server, override to SQLite and populate listAdapter
                 */
                fetchServicesFromServer(serviceItemRecyclerViewAdapter,
                        serviceService, serviceList);
            } else {
                /**
                 * A seller practically will not have more than 10-15 services
                 * so db query to get services from SQLite will take roughly 20-30 ms
                 * So ignoring Android memory leak warning
                 */
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        List<Service> services = serviceService.getServices();
                        serviceList.addAll(services);

                        // if serviceList is empty then show that no service exist with a dummy service
                        if (serviceList.isEmpty()) {
                            addDummyService(serviceList);
                        }
                        serviceItemRecyclerViewAdapter.notifyDataSetChanged();
                        return null;
                    }
                }.execute();
            }
            recyclerView.setAdapter(serviceItemRecyclerViewAdapter);
        }
        return rootView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnServiceListFragmentInteractionListener) {
            mListener = (OnServiceListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnServiceListFragmentInteractionListener {
        void onServiceListFragmentInteraction(Service service);
    }

    // will be called only one time
    private void fetchServicesFromServer(
            final ServiceItemRecyclerViewAdapter serviceItemRecyclerViewAdapter,
            final ServiceService serviceService, final List<Service> serviceList) {
        uiFragmentActivity.showProgressDialog("Getting Services",
                "Please wait", false);
        try {
            String url = AppConstants.Urls.SERVICES;

            uiFragmentActivity.ajaxUtility.makeHttpRequest(url, "GET", null,
                    new AjaxCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    try {
                        JSONArray serviceArray = response.getJSONArray("result");
                        Log.d(TAG, serviceArray.toString());

                        // clear SQLite db
                        serviceService.removeAllServices();

                        // add services to SQLite
                        for (int i = 0; i < serviceArray.length(); i++) {
                            JSONObject serviceObject = serviceArray.getJSONObject(i);
                            Service service = getServiceFromJsonObject(serviceObject);
                            serviceList.add(service);

                            serviceService.createService(service);
                        }

                        if (serviceList.isEmpty()) {
                            addDummyService(serviceList);
                        }
                        serviceItemRecyclerViewAdapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                        uiFragmentActivity.showMessage("Parsing error");
                    }
                }

                @Override
                public void onError(JSONObject response) {
                    try {
                        String message = response.getString("message");
                        int statusCode = response.getInt("statusCode");
                        uiFragmentActivity.showMessage(statusCode + ": " + message);
                    } catch (Exception e) {
                        e.printStackTrace();
                        uiFragmentActivity.showMessage(AppConstants.GENERIC_ERROR_MESSAGE);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            uiFragmentActivity.showMessage(AppConstants.GENERIC_ERROR_MESSAGE);
        } finally {
            uiFragmentActivity.dismissProgressDialog();
        }
    }

    private Service getServiceFromJsonObject(JSONObject serviceObject) {
        Service service = new Service();
        try {
            service.setId(serviceObject.getInt(AppConstants.Service.ID));
            service.setName(serviceObject.getString(AppConstants.Service.NAME));
            service.setDescription(serviceObject.getString(AppConstants.Service.DESCRIPTION));
            service.setTags(serviceObject.getString(AppConstants.Service.TAGS));
            service.setRating(serviceObject.getDouble(AppConstants.Service.RATING));
            service.setRatingCount(serviceObject.getInt(AppConstants.Service.RATING_COUNT));
            service.setLatitude(serviceObject.getDouble(AppConstants.Service.LATITUDE));
            service.setLongitude(serviceObject.getDouble(AppConstants.Service.LONGITUDE));
            service.setLocation(serviceObject.getString(AppConstants.Service.LOCATION));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return service;
    }

    private void addDummyService(List<Service> serviceList) {
        Service dummyService = new Service();
        dummyService.setId(0);
        dummyService.setName("No service exist");
        dummyService.setDescription("Please add your services");
        serviceList.add(dummyService);
    }
}
