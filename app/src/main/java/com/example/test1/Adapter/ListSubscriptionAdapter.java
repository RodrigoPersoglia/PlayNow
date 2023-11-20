package com.example.test1.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test1.R;
import com.example.test1.model.Subscription;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

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
        viewHolder.longitud.setText(String.valueOf(subscription.getLongitud()));
        viewHolder.latitud.setText(String.valueOf(subscription.getLatitud()));

//        Log.d("ListSubscriptionAdapter", "Latitud: " + subscription.getLatitud());
//        Log.d("ListSubscriptionAdapter", "Longitud: " + subscription.getLongitud());

//        viewHolder.locationButton.setTag(subscription.getLatitud(), subscription.getLongitud());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_suscription, parent, false);
        return new ViewHolder(v);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nombre,fecha,hora, latitud, longitud;
        Button locationButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.nameTextView);
            fecha = itemView.findViewById(R.id.dateTextView);
            hora = itemView.findViewById(R.id.timeTextView);
            longitud = itemView.findViewById(R.id.longitudTextView);
            latitud = itemView.findViewById(R.id.latitudTextView);

            locationButton = itemView.findViewById(R.id.locationButton);

        }
    }

//    private void mostrarUbicacionEvento(Subscription subscription) {
//        LocationEventFragment locationEventFragment = LocationEventFragment.newInstance(subscription);
//        locationEventFragment.show(((FragmentActivity) mContext).getSupportFragmentManager(), "LocationEventFragment");
//    }

}