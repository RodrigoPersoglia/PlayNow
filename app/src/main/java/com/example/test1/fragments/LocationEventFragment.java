package com.example.test1.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.test1.R;
import com.example.test1.model.Subscription;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class LocationEventFragment extends DialogFragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Subscription subscription;

    public LocationEventFragment() {
        // Constructor público vacío requerido
    }

    public static LocationEventFragment newInstance(Subscription subscription) {
        LocationEventFragment fragment = new LocationEventFragment();
        Bundle args = new Bundle();
        args.putSerializable("subscription", subscription);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_fragment_location_event, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MapView mapView = view.findViewById(R.id.map_event_location);
        if (mapView != null) {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }

        // Obtener la suscripción de los argumentos
        if (getArguments() != null) {
            subscription = (Subscription) getArguments().getSerializable("subscription");
        }

        // Configurar otros elementos de tu diseño aquí si es necesario
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new Dialog(requireContext(), R.style.DialogTheme);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (subscription != null) {
            // Obtener la ubicación del evento desde la suscripción
            LatLng eventLocation = new LatLng(subscription.getLatitud(), subscription.getLongitud());
            mMap.addMarker(new MarkerOptions().position(eventLocation).title(subscription.getNombre()));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(eventLocation, 15.0f));
        }
    }
}