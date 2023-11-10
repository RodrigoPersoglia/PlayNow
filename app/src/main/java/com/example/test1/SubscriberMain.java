package com.example.test1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
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

import com.example.test1.Adapter.EventListAdapter;
import com.example.test1.model.Event;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SubscriberMain extends AppCompatActivity {
    Button btn_logOut,btn_search,back_btn,search_event_btn;
    FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private LinearLayout search_lay, subscriber_lay;
    private Date selectedDate;
    EditText dateCalendarEditText,radio_txt,select_sportsEvent,date_CalendarEvent;
    AutoCompleteTextView sportsEventAutoComplete;
    RecyclerView recyclerView;
    EventListAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscriber_main);
        StartComponents();
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

            recyclerView = search_lay.findViewById(R.id.search_recycler);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            mFirestore = FirebaseFirestore.getInstance();
            Query query = mFirestore.collection("eventos");

            FirestoreRecyclerOptions<Event> firestoreRecyclerOptions =
                    new FirestoreRecyclerOptions.Builder<Event>().setQuery(query, Event.class).build();

            mAdapter = new EventListAdapter(firestoreRecyclerOptions);
            recyclerView.setAdapter(mAdapter);

            Toast.makeText(SubscriberMain.this, "Logica busqueda evento", Toast.LENGTH_SHORT).show();
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
}