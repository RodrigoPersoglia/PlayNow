package com.example.test1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import com.example.test1.fragments.LocalizationDialogFragment;


public class PublisherMain extends AppCompatActivity implements LocationSelectionListener {
    private Button createEventButton;
    private LinearLayout createEventLayout;
    private LatLng selectedLocation;
    private boolean locationSelected = false;

    private FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publisher_main);

        createEventButton = findViewById(R.id.createEventButton);
        createEventLayout = findViewById(R.id.createEventLayout);

        mFirestore = FirebaseFirestore.getInstance(); // Inicializa Firestore

        createEventButton.setOnClickListener(view -> {
            createEventButton.setVisibility(View.GONE);
            createEventLayout.setVisibility(View.VISIBLE);

            View otroLayout = getLayoutInflater().inflate(R.layout.create_event, null);
            createEventLayout.addView(otroLayout);

            Button seleccionarUbicacionButton = otroLayout.findViewById(R.id.botonSeleccionarUbicacion);
            seleccionarUbicacionButton.setOnClickListener(saveEventView -> {
                LocalizationDialogFragment dialogFragment = new LocalizationDialogFragment();
                dialogFragment.setLocationSelectionListener(this);
                dialogFragment.show(getSupportFragmentManager(), "LocalizationDialogFragment");
            });

            Button saveEventButton = otroLayout.findViewById(R.id.saveEventButton);
            saveEventButton.setOnClickListener(saveEventView -> {
                EditText nombreEventoEditText = otroLayout.findViewById(R.id.txt_nombreEvent);
                EditText publicadorEditText = otroLayout.findViewById(R.id.txt_publicadorEvent);
                EditText statusEditText = otroLayout.findViewById(R.id.txt_statusEvent);

                String nameEvent = nombreEventoEditText.getText().toString();
                String publicadorEvent = publicadorEditText.getText().toString();
                String statusEvent = statusEditText.getText().toString();

                if (nameEvent.isEmpty()) {
                    Toast.makeText(PublisherMain.this, "El campo 'Nombre del Evento' es requerido", Toast.LENGTH_SHORT).show();
                    return;
                } else if (nameEvent.length() < 4) {
                    Toast.makeText(PublisherMain.this, "El campo 'Nombre del Evento' debe tener al menos 4 caracteres", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (publicadorEvent.isEmpty()) {
                    Toast.makeText(PublisherMain.this, "El campo 'Nombre del Publicador' es requerido", Toast.LENGTH_SHORT).show();
                    return;
                } else if (publicadorEvent.length() < 4) {
                    Toast.makeText(PublisherMain.this, "El campo 'Nombre del Publicador' debe tener al menos 4 caracteres", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (statusEvent.isEmpty()) {
                    Toast.makeText(PublisherMain.this, "El campo 'Estado' es requerido", Toast.LENGTH_SHORT).show();
                    return;
                } else if (statusEvent.length() < 4) {
                    Toast.makeText(PublisherMain.this, "El campo 'Estado' debe tener al menos 4 caracteres", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (selectedLocation == null) {
                    Toast.makeText(PublisherMain.this, "Debes seleccionar una ubicación antes de crear el evento", Toast.LENGTH_SHORT).show();
                }else {
                    postEvent(nameEvent, publicadorEvent, statusEvent, selectedLocation);
                }
            });
        });
    }

    private void postEvent(String nameEvent, String publicadorEvent, String statusEvent, LatLng selectedLocation) {

        Map<String, Object> event = new HashMap<>();
        event.put("nombre", nameEvent);
        event.put("publicador", publicadorEvent);
        event.put("status", statusEvent);
        event.put("localizacion", this.selectedLocation.toString());

        // Guardar el evento en Firestore
        mFirestore.collection("eventos")
                .add(event)
                .addOnSuccessListener(documentReference -> {
                    // se creó
                    Toast.makeText(PublisherMain.this, "Evento creado con éxito", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // falló
                    Toast.makeText(PublisherMain.this, "Error al crear el evento: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
    public void onLocationSelected(LatLng location) {
        selectedLocation = location;
        locationSelected = true;
    }
}