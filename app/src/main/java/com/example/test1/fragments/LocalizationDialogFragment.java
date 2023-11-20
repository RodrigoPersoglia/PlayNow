package com.example.test1.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.example.test1.LocationSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.test1.R;

public class LocalizationDialogFragment extends DialogFragment {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private LatLng selectedLocation = new LatLng(-34.7755809, -58.269185); // ubicación inicial UNAJ
    private LocationSelectionListener locationSelectionListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_localization, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
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
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = 1000;
            int height = 1100;
            dialog.getWindow().setLayout(width, height);
        }
    }

    private OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            googleMap.getUiSettings().setZoomControlsEnabled(true);

            // ubicacion inicial
            googleMap.addMarker(new MarkerOptions().position(selectedLocation).title("Ubicación de partida"));

            // logo en punto de inicio
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(selectedLocation));

            float zoomLevel = 15.0f;
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLocation, zoomLevel));

            // Click en el mapa
            googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    // ubicación seleccionada por el usuario
                    selectedLocation = latLng;

                    // elimino la ubicación anterior
                    googleMap.clear();

                    // agrego nueva ubicación
                    googleMap.addMarker(new MarkerOptions().position(selectedLocation).title("Ubicación seleccionada"));

                    // pasa ubicacion
                    if (locationSelectionListener != null) {
                        locationSelectionListener.onLocationSelected(selectedLocation);
                    }
                }
            });
        }
    };

    public void setLocationSelectionListener(LocationSelectionListener listener) {
        this.locationSelectionListener = listener;
    }
}