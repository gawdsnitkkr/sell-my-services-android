package me.varunon9.sellmyservices.uifragments;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import me.varunon9.sellmyservices.R;
import me.varunon9.sellmyservices.db.models.Service;
import me.varunon9.sellmyservices.uifragments.SellerServicesFragment.OnServiceListFragmentInteractionListener;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Service} and makes a call to the
 * specified {@link OnServiceListFragmentInteractionListener}.
 */
public class ServiceItemRecyclerViewAdapter
        extends RecyclerView.Adapter<ServiceItemRecyclerViewAdapter.ViewHolder> {

    private final List<Service> serviceList;
    private final OnServiceListFragmentInteractionListener mListener;

    public ServiceItemRecyclerViewAdapter(List<Service> serviceList,
                                          OnServiceListFragmentInteractionListener listener) {
        this.serviceList = serviceList;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_service_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = serviceList.get(position);
        holder.serviceRatingView.setText(String.valueOf(serviceList.get(position).getRating()));
        holder.serviceNameView.setText(serviceList.get(position).getName());
        holder.serviceDescriptionView.setText(serviceList.get(position).getDescription());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onServiceListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView serviceNameView;
        public final TextView serviceRatingView;
        public final TextView serviceDescriptionView;
        public Service mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            serviceRatingView = (TextView) view.findViewById(R.id.serviceRating);
            serviceNameView = (TextView) view.findViewById(R.id.serviceName);
            serviceDescriptionView = (TextView) view.findViewById(R.id.serviceDescription);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + serviceNameView.getText() + "'";
        }
    }
}
