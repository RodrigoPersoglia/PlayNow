package com.example.test1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.test1.Adapter.EventListSearchAdapter;
import com.example.test1.model.Event;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import android.Manifest;

public class SubscriberMain extends AppCompatActivity {
    Button btn_logOut,btn_search,back_btn,search_event_btn;
    FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private LinearLayout search_lay, subscriber_lay;
    private Date selectedDate;
    EditText dateCalendarEditText,radio_txt,select_sportsEvent,date_CalendarEvent;
    AutoCompleteTextView sportsEventAutoComplete;
    RecyclerView recyclerView;
    EventListSearchAdapter mAdapter;

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private FusedLocationProviderClient fusedLocationClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscriber_main);
        StartComponents();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Process();
            } else {
                Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }

    private void StartComponents() {
        search_lay = findViewById(R.id.search_lay);
        search_lay.setVisibility(View.GONE);
        View search_event = getLayoutInflater().inflate(R.layout.search_event, null);
        search_lay.addView(search_event);
        subscriber_lay = findViewById(R.id.subscriber_lay);
        mAuth = FirebaseAuth.getInstance();
        btn_logOut = findViewById(R.id.btn_logout);
        btn_logOut.setOnClickListener(view -> LogOut());
        btn_search = findViewById(R.id.search_btn);
        btn_search.setOnClickListener(view -> Search());
        back_btn = search_lay.findViewById(R.id.back_btn);
        back_btn.setOnClickListener(view -> Back());
        search_event_btn = search_lay.findViewById(R.id.search_event_btn);
        search_event_btn.setOnClickListener(view -> SearchEvent());
        radio_txt = search_lay.findViewById(R.id.radio_txt);
        select_sportsEvent = search_lay.findViewById(R.id.select_sportsEvent);
        date_CalendarEvent = search_lay.findViewById(R.id.date_CalendarEvent);
    }

    private void LogOut(){
        mAuth.signOut();
        finish();
        startActivity(new Intent(SubscriberMain.this, MainActivity.class));
    }

    private void Search(){
        subscriber_lay.setVisibility(View.GONE);
        search_lay.setVisibility(View.VISIBLE);
        dateCalendarEditText = search_lay.findViewById(R.id.date_CalendarEvent);

        Button dateButton = search_lay.findViewById(R.id.image_calendar);
        dateButton.setOnClickListener(viewCalendary -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(SubscriberMain.this, (datePickerView, year1, monthOfYear, dayOfMonth) -> {
                calendar.set(year1, monthOfYear, dayOfMonth);
                selectedDate = calendar.getTime();
                SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
                String fechaFormateada = formatoFecha.format(selectedDate);
                dateCalendarEditText.setText(fechaFormateada);
            }, year, month, day);

            datePickerDialog.show();
        });

        sportsEventAutoComplete = search_lay.findViewById(R.id.select_sportsEvent);

        ArrayAdapter<String> autoCompleteAdapter = new ArrayAdapter<String>(this,
                R.layout.custom_dropdown_item, R.id.text1, getResources().getStringArray(R.array.list_sports));

        sportsEventAutoComplete.setAdapter(autoCompleteAdapter);

        sportsEventAutoComplete.setOnClickListener(view1 -> {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(sportsEventAutoComplete.getWindowToken(), 0);
            sportsEventAutoComplete.clearFocus();
            sportsEventAutoComplete.showDropDown();
        });
    }

    private void Back(){
        subscriber_lay.setVisibility(View.VISIBLE);
        search_lay.setVisibility(View.GONE);
    }

    private void SearchEvent(){
        if(Validations()){
            Process();
        }
    }

    private boolean Validations() {
        Boolean result = true;

        String calendar = String.valueOf(date_CalendarEvent.getText());
        if (calendar.trim().isEmpty()) {
            result = false;
            date_CalendarEvent.setError("Seleccione una fecha");
            date_CalendarEvent.requestFocus();
        } else {
            date_CalendarEvent.setError(null);
        }

        String sport = String.valueOf(select_sportsEvent.getText());
        if (sport.trim().isEmpty()) {
            result = false;
            select_sportsEvent.setError("Seleccione un deporte");
            select_sportsEvent.requestFocus();
        } else {
            select_sportsEvent.setError(null);
        }

        String radio = String.valueOf(radio_txt.getText());
        if (!radio.matches("^[0-9]{1,1000}$") || radio.equals("0")) {
            result = false;
            radio_txt.setError("Cantidad no válida");
            radio_txt.requestFocus();
        } else {
            radio_txt.setError(null);
        }

        return result;
    }

    private void Process() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                double latitude = location.getLatitude();
                                double longitude = location.getLongitude();
                                ShowCards(latitude, longitude);
                            } else {
                                Toast.makeText(SubscriberMain.this,
                                        "No se pudo obtener la ubicación", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void ShowCards(double latitude, double longitude) {
        String mensaje = "Ubicación: Latitud " + latitude + ", Longitud " + longitude;
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
        recyclerView = search_lay.findViewById(R.id.search_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(subscriber_lay.getContext()));
        mFirestore = FirebaseFirestore.getInstance();
        Query query = mFirestore.collection("eventos");

        FirestoreRecyclerOptions<Event> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<Event>().setQuery(query, Event.class).build();
        mAdapter = new EventListSearchAdapter(firestoreRecyclerOptions);
        mAdapter.startListening();
        recyclerView.setAdapter(mAdapter);
    }

}