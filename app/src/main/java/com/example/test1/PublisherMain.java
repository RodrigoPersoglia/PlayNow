package com.example.test1;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test1.Adapter.EventListAdapter;
import com.example.test1.fragments.LocalizationDialogFragment;
import com.example.test1.model.Event;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class PublisherMain extends AppCompatActivity implements LocationSelectionListener {
    private Button createEventButton,logout_btn,back_btn;
    private LinearLayout publisherLayout,createEventLayout;
    EditText nombreEventoEditText, publicadorEditText, quantityEditText, dateCalendarEditText, timeEventEditText;
    AutoCompleteTextView sportsEventAutoComplete;
    private LatLng selectedLocation;
    private boolean locationSelected = false;
    private FirebaseFirestore mFirestore;
    FirebaseAuth mAuth;
    RecyclerView recyclerView;
    EventListAdapter mAdapter;
    String horaFormateada;
    private Date selectedDate;
    private int selectedTimeMinutes = -1;
    @Override
    protected void onStart() {
        super.onStart();
        mAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_publisher_main);
        logout_btn = findViewById(R.id.logout_btn);
        logout_btn.setOnClickListener(view ->LogOut());
        createEventButton = findViewById(R.id.createEventButton);
        publisherLayout = findViewById(R.id.Publisher_layout);
        createEventLayout = findViewById(R.id.create_event_layout);
        View otroLayout = getLayoutInflater().inflate(R.layout.create_event, null);
        createEventLayout.addView(otroLayout);
        createEventLayout.setVisibility(View.GONE);
        back_btn = createEventLayout.findViewById(R.id.back_btn);
        back_btn.setOnClickListener(view -> Back());
        mFirestore = FirebaseFirestore.getInstance();

        // configuro Firestore y RecyclerView
        recyclerView = findViewById(R.id.listRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Query query = mFirestore.collection("eventos").whereEqualTo("publicador",mAuth.getUid());

        FirestoreRecyclerOptions<Event> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<Event>().setQuery(query, Event.class).build();

        mAdapter = new EventListAdapter(firestoreRecyclerOptions, this);
        recyclerView.setAdapter(mAdapter);

        createEventButton.setOnClickListener(view -> {
            publisherLayout.setVisibility(View.GONE);
            createEventLayout.setVisibility(View.VISIBLE);

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

                DatePickerDialog datePickerDialog = new DatePickerDialog(PublisherMain.this, (datePickerView, year1, monthOfYear, dayOfMonth) -> {
                    calendar.set(year1, monthOfYear, dayOfMonth);
                    selectedDate = calendar.getTime(); // guardo la fecha seleccionada
                    SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
                    String fechaFormateada = formatoFecha.format(selectedDate);
                    dateCalendarEditText.setText(fechaFormateada);
                }, year, month, day);

                datePickerDialog.show();
            });

            timeEventEditText = otroLayout.findViewById(R.id.time_Event);

            Button timeButton = otroLayout.findViewById(R.id.image_time);
            timeButton.setOnClickListener(viewTime -> {
                // hora actual
                Calendar calendarTime = Calendar.getInstance();
                int hour = calendarTime.get(Calendar.HOUR_OF_DAY);
                int minute = calendarTime.get(Calendar.MINUTE);

                // reloj
                TimePickerDialog timePickerDialog = new TimePickerDialog(PublisherMain.this,
                        (timePicker, selectedHour, selectedMinute) -> {
                            calendarTime.set(Calendar.HOUR_OF_DAY, selectedHour);
                            calendarTime.set(Calendar.MINUTE, selectedMinute);
                            selectedTimeMinutes = selectedHour * 60 + selectedMinute;

                            SimpleDateFormat horaFormateada = new SimpleDateFormat("HH:mm", Locale.getDefault());
                            String formattedTime = horaFormateada.format(calendarTime.getTime());
                            timeEventEditText.setText(formattedTime);
                        },
                        hour, // hora actual
                        minute, // minutos actuales
                        true);

                // mostrar el TimePickerDialog
                timePickerDialog.show();
            });


            Button seleccionarUbicacionButton = otroLayout.findViewById(R.id.botonSeleccionarUbicacion);
            seleccionarUbicacionButton.setOnClickListener(saveEventView -> {
                LocalizationDialogFragment dialogFragment = new LocalizationDialogFragment();
                dialogFragment.setLocationSelectionListener(this);
                dialogFragment.show(getSupportFragmentManager(), "LocalizationDialogFragment");
            });

            Button saveEventButton = otroLayout.findViewById(R.id.search_event_btn);
            saveEventButton.setOnClickListener(saveEventView -> {
                nombreEventoEditText = otroLayout.findViewById(R.id.txt_nombreEvent);
                publicadorEditText = otroLayout.findViewById(R.id.txt_publicadorEvent);
                quantityEditText = otroLayout.findViewById(R.id.radio_txt);
                dateCalendarEditText = otroLayout.findViewById(R.id.date_CalendarEvent);
                timeEventEditText = otroLayout.findViewById(R.id.time_Event);

                String nameEvent = nombreEventoEditText.getText().toString();
                String quantityEvent = quantityEditText.getText().toString();
                String sportsEvent = sportsEventAutoComplete.getText().toString();

                if (Validations(nameEvent, quantityEvent, sportsEvent, selectedDate, selectedTimeMinutes, selectedLocation)) {
                    // creo el objeto Date para la hora seleccionada
                    Calendar calendarTime = Calendar.getInstance();
                    calendarTime.set(Calendar.HOUR_OF_DAY, selectedTimeMinutes / 60); // hora
                    calendarTime.set(Calendar.MINUTE, selectedTimeMinutes % 60); // minutos
                    Date selectedTimeDate = calendarTime.getTime();

                    postEvent(nameEvent, quantityEvent, sportsEvent, selectedDate, selectedTimeDate, selectedLocation);
                }
            });
        });
    }

    private boolean Validations(String nameEvent, String quantityEvent, String sportsEvent, Date dateEvent, int timeEventMinutes, LatLng selectedLocation) {
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
        if (dateEvent == null) {
            result = false;
            Toast.makeText(PublisherMain.this, "Debes seleccionar una Fecha antes de crear el evento", Toast.LENGTH_SHORT).show();
        }

        if (timeEventMinutes < 0) {
            result = false;
            Toast.makeText(PublisherMain.this, "Debes seleccionar una Hora antes de crear el evento", Toast.LENGTH_SHORT).show();
        }
        if (selectedLocation == null) {
            result = false;
            Toast.makeText(PublisherMain.this, "Debes seleccionar una Ubicación antes de crear el evento", Toast.LENGTH_SHORT).show();
        }

        return result;
    }

    private void postEvent(String nameEvent, String quantityEvent, String sportsEvent, Date dateEventStr, Date timeEventStr, LatLng selectedLocation) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        List<String> suscriptores = new ArrayList<>();
        Map<String, Object> event = new HashMap<>();
        event.put("id", UUID.randomUUID().toString());
        event.put("nombre", nameEvent);
        event.put("publicador", currentUser.getUid());
        event.put("cantidad", Integer.parseInt(quantityEvent));
        event.put("status", "Incompleto");
        event.put("deporte", sportsEvent);
        event.put("fecha", dateEventStr);
        event.put("hora", timeEventStr);
        event.put("latitud", selectedLocation.latitude);
        event.put("longitud", selectedLocation.longitude);
        event.put("suscriptores", suscriptores);

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

    private void LogOut(){
        mAuth.signOut();
        finish();
        startActivity(new Intent(PublisherMain.this, MainActivity.class));
    }

    private void Back(){
        publisherLayout.setVisibility(View.VISIBLE);
        createEventLayout.setVisibility(View.GONE);
    }
}