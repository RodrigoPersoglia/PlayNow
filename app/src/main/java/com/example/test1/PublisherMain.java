package com.example.test1;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.test1.fragments.LocalizationDialogFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class PublisherMain extends AppCompatActivity implements LocationSelectionListener {
    private Button createEventButton;
    private LinearLayout createEventLayout;
    EditText nombreEventoEditText,publicadorEditText,quantityEditText;

    private LatLng selectedLocation;
    private boolean locationSelected = false;

    private FirebaseFirestore mFirestore;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publisher_main);

        createEventButton = findViewById(R.id.createEventButton);
        createEventLayout = findViewById(R.id.createEventLayout);

        mFirestore = FirebaseFirestore.getInstance();

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
                nombreEventoEditText = otroLayout.findViewById(R.id.txt_nombreEvent);
                publicadorEditText = otroLayout.findViewById(R.id.txt_publicadorEvent);
                quantityEditText = otroLayout.findViewById(R.id.txt_quantityEvent);

                String nameEvent = nombreEventoEditText.getText().toString();
                String publicadorEvent = publicadorEditText.getText().toString();
                String quantityEvent = quantityEditText.getText().toString();


                if (Validations(nameEvent,quantityEvent,selectedLocation)) {
                    postEvent(nameEvent, publicadorEvent, quantityEvent, selectedLocation);
                }
            });
        });
    }

    private boolean Validations(String nameEvent,String quantityEvent,LatLng selectedLocation){
        Boolean result = true;

        if (!quantityEvent.matches("^[0-9]{1,1000}$") || quantityEvent.equals("0")) {
            result = false;
            quantityEditText.setError("Cantidad no válida");
            quantityEditText.requestFocus();
        }
        else {
            quantityEditText.setError(null);
        }

        if (nameEvent.length() < 4) {
            result = false;
            nombreEventoEditText.setError("el nombre del evento debe tener al menos 4 caracteres");
            nombreEventoEditText.requestFocus();
        }
        else {
            nombreEventoEditText.setError(null);
        }

        if (selectedLocation == null) {
            result = false;
            Toast.makeText(PublisherMain.this, "Debes seleccionar una ubicación antes de crear el evento", Toast.LENGTH_SHORT).show();
        }

        return result;
    }

    private void postEvent(String nameEvent, String publicadorEvent, String quantityEvent, LatLng selectedLocation) {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Map<String, Object> event = new HashMap<>();
        event.put("nombre", nameEvent);
        event.put("publicador", currentUser.getUid());
        event.put("cantidad", quantityEvent);
        event.put("status", "Incompleto");
        event.put("localizacion", this.selectedLocation.toString());

        mFirestore.collection("eventos")
                .add(event)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(PublisherMain.this, "Evento creado con éxito", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(PublisherMain.this, "Error al crear el evento: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
    public void onLocationSelected(LatLng location) {
        selectedLocation = location;
        locationSelected = true;
    }
}