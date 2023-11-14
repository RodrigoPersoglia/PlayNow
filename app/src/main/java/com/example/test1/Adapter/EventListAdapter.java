package com.example.test1.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test1.PublisherMain;
import com.example.test1.R;
//import com.example.test1.fragments.UpdateEventFragment;
import com.example.test1.fragments.UpdateEventFragment;
import com.example.test1.model.Event;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class EventListAdapter extends FirestoreRecyclerAdapter<Event, EventListAdapter.ViewHolder> {
    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    Activity activity;
    FragmentManager fragmentManager;
    public EventListAdapter(@NonNull FirestoreRecyclerOptions<Event> options,Activity activity, FragmentManager fragmentManager) {

        super(options);
        this.activity = activity;
        this.fragmentManager = fragmentManager;
    }
    @Override
    protected void onBindViewHolder(ViewHolder viewHolder, int position, Event event) {
        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(viewHolder.getBindingAdapterPosition());
        final String id = documentSnapshot.getId();

        viewHolder.nombre.setText(event.getNombre());

        // Formatear fecha y hora como una cadena antes de asignarla al TextView
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String formattedDate = dateFormat.format(event.getFecha());
        viewHolder.fecha.setText(formattedDate);

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String formattedTime = timeFormat.format(event.getHora());
        viewHolder.hora.setText(formattedTime);

        viewHolder.btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Puedes iniciar la actividad aquí o abrir el fragmento para actualizar
                // utilizando el código que ya tenías comentado.
                Intent i = new Intent(activity, PublisherMain.class);
                i.putExtra("Id", id);

                UpdateEventFragment updateEventFragment = new UpdateEventFragment();
                Bundle bundle = new Bundle();
                bundle.putString("Id", id);
                updateEventFragment.setArguments(bundle);
                updateEventFragment.show(fragmentManager, "open fragment");
            }
        });

        viewHolder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showModal(id);
            }
        });
    }

    private void deleteEvent(String id) {
        mFirestore.collection("eventos").document(id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(activity, "Evento eliminado correctamente.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(activity, "Error al eliminar evento: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("DELETE_EVENT_ERROR", "Error al eliminar evento", e);
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_events, parent, false);
        return new ViewHolder(v);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nombre,fecha,hora;
        MaterialButton btn_delete, btn_update;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.nameTextView);
            fecha = itemView.findViewById(R.id.dateTextView);
            hora = itemView.findViewById(R.id.timeTextView);
            btn_delete = itemView.findViewById(R.id.btn_eliminar);
            btn_update = itemView.findViewById(R.id.btn_editar);
        }
    }
    private void showModal(final String eventId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage("¿Estás seguro de que deseas eliminar este evento?")
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteEvent(eventId);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        builder.create().show();
    }

}