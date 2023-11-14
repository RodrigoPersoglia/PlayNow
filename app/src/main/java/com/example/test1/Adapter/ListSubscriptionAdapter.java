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
import com.example.test1.model.Event;
import com.example.test1.model.Subscription;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    protected void onBindViewHolder(ViewHolder viewHolder, int position, Subscription suscripcion) {
        viewHolder.nombre.setText(suscripcion.getNombre());

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String formattedDate = dateFormat.format(suscripcion.getFecha());
        viewHolder.fecha.setText(formattedDate);

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String formattedTime = timeFormat.format(suscripcion.getHora());
        viewHolder.hora.setText(formattedTime);

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_suscription, parent, false);
        return new ViewHolder(v);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nombre,fecha,hora;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.nameTextView);
            fecha = itemView.findViewById(R.id.dateTextView);
            hora = itemView.findViewById(R.id.timeTextView);
        }
    }

}