package com.example.test1.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test1.R;
import com.example.test1.fragments.LocationEventFragment;
import com.example.test1.model.Subscription;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;


import java.text.SimpleDateFormat;
import java.util.Locale;

public class ListSubscriptionAdapter extends FirestoreRecyclerAdapter<Subscription, ListSubscriptionAdapter.ViewHolder> {
    private Context mContext;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    public ListSubscriptionAdapter(Context context, @NonNull FirestoreRecyclerOptions<Subscription> options) {
        super(options);
        mContext = context;
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    @Override
    protected void onBindViewHolder(ViewHolder viewHolder, int position, Subscription subscription) {
        viewHolder.nombre.setText(subscription.getNombre());

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String formattedDate = dateFormat.format(subscription.getFecha());
        viewHolder.fecha.setText(formattedDate);

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String formattedTime = timeFormat.format(subscription.getHora());
        viewHolder.hora.setText(formattedTime);
//        viewHolder.longitud.setText(String.valueOf(subscription.getLongitud()));
//        viewHolder.latitud.setText(String.valueOf(subscription.getLatitud()));

        Context context = viewHolder.itemView.getContext();

        viewHolder.locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (subscription != null && subscription.getLatitud() != null && subscription.getLongitud() != null) {
                    double latitud = subscription.getLatitud();
                    double longitud = subscription.getLongitud();

                    LatLng eventLocation = new LatLng(latitud, longitud);
                    openMapDialog(context, eventLocation);
                } else {
                    Log.e("ListSubscriptionAdapter", "Campos de ubicación nulos");
                }
            }
        });
    }

    private void openMapDialog(Context context, LatLng eventLocation) {
        if (eventLocation.latitude != 0 && eventLocation.longitude != 0) {
            LocationEventFragment locationEvent = LocationEventFragment.newInstance(eventLocation);
            if (context instanceof FragmentActivity) {
                locationEvent.show(((FragmentActivity) context).getSupportFragmentManager(), "mapDialog");
            } else {
                Log.e("ListSubscriptionAdapter", "El contexto no es una instancia de FragmentActivity");
            }
        } else {
            Log.e("ListSubscriptionAdapter", "Latitud o longitud nulos");
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_suscription, parent, false);
        return new ViewHolder(v);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nombre,fecha,hora;
        Button locationButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.nameTextView);
            fecha = itemView.findViewById(R.id.dateTextView);
            hora = itemView.findViewById(R.id.timeTextView);
            locationButton = itemView.findViewById(R.id.locationButton);
        }
    }
}