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
import com.example.test1.model.Subscription;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class EventListSearchAdapter extends FirestoreRecyclerAdapter<Event, EventListSearchAdapter.ViewHolder> {
    private Context mContext;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    public EventListSearchAdapter(Context context, @NonNull FirestoreRecyclerOptions<Event> options) {
        super(options);
        mContext = context;
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
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

        viewHolder.add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AgregarSuscriptor(event,mAuth.getUid());
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

    public void AgregarSuscriptor(Event event, final String nuevoSuscriptor) {
        Query query = db.collection("eventos").whereEqualTo("id", event.getId());

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        document.getReference().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot documentSnapshot = task.getResult();
                                    if (documentSnapshot.exists()) {
                                        String status = (String) documentSnapshot.get("status");
                                        if(status.equals("Completo")){
                                            Toast.makeText(mContext,"El evento ya esta completo", Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                        ArrayList<String> suscriptores = (ArrayList<String>) documentSnapshot.get("suscriptores");

                                        // Agrega el nuevo suscriptor al array
                                        if (suscriptores == null) {
                                            suscriptores = new ArrayList<>();
                                        }
                                        if(suscriptores.contains(mAuth.getUid())){
                                            Toast.makeText(mContext,"Ya se encuentra suscripto a este evento", Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                        suscriptores.add(nuevoSuscriptor);
                                        final ArrayList<String> finalSuscriptores = suscriptores;

                                        document.getReference().update("suscriptores", suscriptores)
                                                .addOnSuccessListener(aVoid -> {
                                                    Toast.makeText(mContext,"Se ha suscripto correctamente al evento", Toast.LENGTH_SHORT).show();
                                                    Subscription suscripcion = new Subscription(mAuth.getUid(),event.getId(),event.getFecha(),event.getHora(),
                                                    event.getNombre(),"Suscripto",event.getDeporte());
                                                    agregarSuscripcion(suscripcion);
                                                    if (finalSuscriptores.size() == event.getCantidad()) {
                                                        document.getReference().update("status", "Completo")
                                                                .addOnSuccessListener(aVoid1 -> {
                                                                })
                                                                .addOnFailureListener(e -> {
                                                                    Toast.makeText(mContext,"Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                });
                                                    }
                                                })
                                                .addOnFailureListener(e -> {
                                                    Toast.makeText(mContext,"Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                                });
                                    } else {
                                        Toast.makeText(mContext,"El documento no existe", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(mContext,"Error al obtener el documento", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                } else {
                    Toast.makeText(mContext,"Error al consultar la base de datos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void agregarSuscripcion(Subscription suscripcion) {
        db.collection("suscripciones")
                .add(suscripcion)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(mContext,"Se añadio la suscripcion a su lista de suscripciones", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {

                });
    }
}