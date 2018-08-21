package me.varunon9.sellmyservices.uifragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import me.varunon9.sellmyservices.R;
import me.varunon9.sellmyservices.UiFragmentActivity;
import me.varunon9.sellmyservices.db.DbHelper;
import me.varunon9.sellmyservices.db.models.Service;
import me.varunon9.sellmyservices.db.services.ServiceService;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnServiceListFragmentInteractionListener}
 * interface.
 */
public class SellerServicesFragment extends Fragment {

    private OnServiceListFragmentInteractionListener mListener;
    private UiFragmentActivity uiFragmentActivity;

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
        View view = inflater.inflate(R.layout.fragment_service_item_list, container, false);

        uiFragmentActivity = ((UiFragmentActivity) getActivity());

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));

            final List<Service> serviceList = new ArrayList<>();
            final ServiceItemRecyclerViewAdapter serviceItemRecyclerViewAdapter =
                    new ServiceItemRecyclerViewAdapter(serviceList, mListener);

            /**
             * A seller practically will not have more than 10-15 services
             * so db query to get services from sqlite will take roughly 20-30 ms
             * So ignoring Android memory leak warning
             */
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    ServiceService serviceService = new ServiceService(uiFragmentActivity.dbHelper);
                    List<Service> services = serviceService.getServices();
                    serviceList.addAll(services);
                    serviceItemRecyclerViewAdapter.notifyDataSetChanged();
                    return null;
                }
            }.execute();
            recyclerView.setAdapter(serviceItemRecyclerViewAdapter);
        }
        return view;
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
}
