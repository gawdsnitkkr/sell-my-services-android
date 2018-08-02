package me.varunon9.sellmyservices.uifragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import me.varunon9.sellmyservices.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AboutUsFragment extends Fragment {


    public AboutUsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_about_us, container, false);
        WebView aboutUsWebView = (WebView) rootView.findViewById(R.id.about_us_webView);
        aboutUsWebView.loadUrl("file:///android_asset/aboutUs.html");
        return rootView;
    }

}
