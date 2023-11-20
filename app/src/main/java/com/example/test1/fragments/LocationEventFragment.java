package com.example.test1.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.DialogFragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.test1.R;

public class LocationEventFragment extends DialogFragment implements OnMapReadyCallback {

    private static final String ARG_EVENT_LOCATION = "eventLocation";

    private LatLng eventLocation;

    public static LocationEventFragment newInstance(LatLng eventLocation) {
        LocationEventFragment fragment = new LocationEventFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_EVENT_LOCATION, eventLocation);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_location_event, container, false);

        if (getArguments() != null) {
            eventLocation = getArguments().getParcelable(ARG_EVENT_LOCATION);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_event_location);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        view.findViewById(R.id.closeButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss(); // cierro modal
            }
        });

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (eventLocation != null) {
            googleMap.addMarker(new MarkerOptions().position(eventLocation).title("Event Location"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(eventLocation));
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(15.0f));
        }
    }
}