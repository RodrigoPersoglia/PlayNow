package com.example.test1;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PublisherMain extends AppCompatActivity implements LocationSelectionListener {
    private Button createEventButton;
    private LinearLayout createEventLayout;
    EditText nombreEventoEditText, publicadorEditText, quantityEditText, dateCalendarEditText,timeEventEditText;
    AutoCompleteTextView sportsEventAutoComplete;
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

            sportsEventAutoComplete = otroLayout.findViewById(R.id.select_sportsEvent);

            ArrayAdapter<String> autoCompleteAdapter = new ArrayAdapter<String>(this,
                    R.layout.custom_dropdown_item, R.id.text1, getResources().getStringArray(R.array.list_sports));

            sportsEventAutoComplete.setAdapter(autoCompleteAdapter);

            sportsEventAutoComplete.setOnClickListener(view1 -> {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(sportsEventAutoComplete.getWindowToken(), 0);
                sportsEventAutoComplete.clearFocus();
                sportsEventAutoComplete.showDropDown();
            });

            dateCalendarEditText = otroLayout.findViewById(R.id.date_CalendarEvent);

            Button dateButton = otroLayout.findViewById(R.id.image_calendar);
            dateButton.setOnClickListener(viewCalendary -> {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                // calendario
                DatePickerDialog datePickerDialog = new DatePickerDialog(PublisherMain.this, (datePickerView, year1, monthOfYear, dayOfMonth) -> {
                    // seteo la fecha en el text
                    calendar.set(year1, monthOfYear, dayOfMonth);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    dateCalendarEditText.setText(dateFormat.format(calendar.getTime()));
                }, year, month, day);

                // muestro calendario click
                datePickerDialog.show();
            });

            Button timeButton = otroLayout.findViewById(R.id.image_time);
            timeEventEditText = otroLayout.findViewById(R.id.time_Event);  // Mover esta línea aquí

            timeButton.setOnClickListener(viewTime -> {
                // hora actual
                Calendar currentTime = Calendar.getInstance();
                int hour = currentTime.get(Calendar.HOUR_OF_DAY);
                int minute = currentTime.get(Calendar.MINUTE);

                // reloj
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        PublisherMain.this,
                        (viewTim, selectedHour, selectedMinute) -> {
                            String amPm;
                            if (selectedHour < 12) {
                                amPm = "AM";
                            } else {
                                amPm = "PM";
                                selectedHour -= 12;
                            }

                            // seteo la hora en el text
                            String selectedTime = String.format(Locale.getDefault(), "%02d:%02d %s", selectedHour, selectedMinute, amPm);
                            timeEventEditText.setText(selectedTime);
                        },
                        hour, minute, false // formato de 24 horas en false
                );

                // muestro el reloj click
                timePickerDialog.show();
            });

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
                dateCalendarEditText = otroLayout.findViewById(R.id.date_CalendarEvent);
                timeEventEditText = otroLayout.findViewById(R.id.time_Event);


                String nameEvent = nombreEventoEditText.getText().toString();
                String publicadorEvent = publicadorEditText.getText().toString();
                String quantityEvent = quantityEditText.getText().toString();
                String sportsEvent = sportsEventAutoComplete.getText().toString();
                String dateEvent = dateCalendarEditText.getText().toString();
                String timeEvent = timeEventEditText.getText().toString();


                if (Validations(nameEvent, quantityEvent, sportsEvent, dateEvent, timeEvent, selectedLocation)) {
                    postEvent(nameEvent, publicadorEvent, quantityEvent, sportsEvent, dateEvent, timeEvent, selectedLocation);
                }
            });
        });
    }

    private boolean Validations(String nameEvent, String quantityEvent, String sportsEvent, String dateEvent, String timeEvent, LatLng selectedLocation) {
        Boolean result = true;

        if (!quantityEvent.matches("^[0-9]{1,1000}$") || quantityEvent.equals("0")) {
            result = false;
            quantityEditText.setError("Cantidad no válida");
            quantityEditText.requestFocus();
        } else {
            quantityEditText.setError(null);
        }

        if (nameEvent.length() < 4) {
            result = false;
            nombreEventoEditText.setError("El Nombre del evento debe tener al menos 4 caracteres");
            nombreEventoEditText.requestFocus();
        } else {
            nombreEventoEditText.setError(null);
        }
        if (sportsEvent.trim().isEmpty()) {
            result = false;
            Toast.makeText(PublisherMain.this, "Debes seleccionar un Deporte antes de crear el evento", Toast.LENGTH_SHORT).show();
        }
        if (dateEvent.trim().isEmpty()) {
            result = false;
            Toast.makeText(PublisherMain.this, "Debes seleccionar una Fecha antes de crear el evento", Toast.LENGTH_SHORT).show();
        }
        if (timeEvent.trim().isEmpty()) {
            result = false;
            Toast.makeText(PublisherMain.this, "Debes seleccionar una Hora antes de crear el evento", Toast.LENGTH_SHORT).show();
        }
        if (selectedLocation == null) {
            result = false;
            Toast.makeText(PublisherMain.this, "Debes seleccionar una Ubicación antes de crear el evento", Toast.LENGTH_SHORT).show();
        }

        return result;
    }

    private void postEvent(String nameEvent, String publicadorEvent, String quantityEvent, String sportsEvent, String dateEvent, String timeEvent, LatLng selectedLocation) {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Map<String, Object> event = new HashMap<>();
        event.put("nombre", nameEvent);
        event.put("publicador", currentUser.getUid());
        event.put("cantidad", quantityEvent);
        event.put("status", "Incompleto");
        event.put("deporte", sportsEvent);
        event.put("fecha", dateEvent);
        event.put("hora", timeEvent);
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