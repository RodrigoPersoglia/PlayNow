package com.example.test1.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test1.R;
import com.example.test1.SubscriberMain;
import com.example.test1.model.Event;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class EventListSearchAdapter extends FirestoreRecyclerAdapter<Event, EventListSearchAdapter.ViewHolder> {
    private Context mContext;
    public EventListSearchAdapter(Context context, @NonNull FirestoreRecyclerOptions<Event> options) {
        super(options);
        mContext = context;
    }

    @Override
    protected void onBindViewHolder(ViewHolder viewHolder, int position, Event event) {
        viewHolder.nombre.setText(event.getNombre());

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String formattedDate = dateFormat.format(event.getFecha());
        viewHolder.fecha.setText(formattedDate);

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String formattedTime = timeFormat.format(event.getHora());
        viewHolder.hora.setText(formattedTime);

        String id = event.getId();
        viewHolder.add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext,
                        id, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_event_search, parent, false);
        return new ViewHolder(v);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nombre,fecha,hora;
        Button add_btn;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.nameTextView);
            fecha = itemView.findViewById(R.id.dateTextView);
            hora = itemView.findViewById(R.id.timeTextView);
            add_btn = itemView.findViewById(R.id.editButton);
        }
    }
}