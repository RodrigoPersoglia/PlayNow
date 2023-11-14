package com.example.test1.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.test1.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UpdateEventFragment extends DialogFragment {
    String id_event;
    Button btn_guardar;
    EditText nombre, cantidad;
    private FirebaseFirestore mFirestore;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id_event = getArguments().getString("Id"); // Cambia "id_event" a "Id"
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.update_fragment_event, container, false);
        nombre = v.findViewById(R.id.update_nombreEvent);
        cantidad = v.findViewById(R.id.update_Cantidad);
        btn_guardar = v.findViewById(R.id.btn_update);
        mFirestore = FirebaseFirestore.getInstance();
        btn_guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombreEvent = nombre.getText().toString().trim();
                String cantidadEventStr = cantidad.getText().toString().trim();
                if (!nombreEvent.isEmpty() && !cantidadEventStr.isEmpty()) {
                    try {
                        Integer cantidadEvent = Integer.parseInt(cantidadEventStr);
                        updateEvent(nombreEvent, cantidadEvent);
                    } catch (NumberFormatException e) {
                        Toast.makeText(getContext(), "Ingrese una cantidad válida", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Ingrese datos correctos", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return v;
    }
    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = 1000;
            int height = 1100;
            dialog.getWindow().setLayout(width, height);
        }
    }
    private void updateEvent(String nombreEvent, Integer cantidadEvent) {
        Map<String, Object> map = new HashMap<>();
        map.put("nombre", nombreEvent);
        map.put("cantidad", cantidadEvent);

        mFirestore.collection("eventos").document(id_event).update(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getContext(), "Actualizado exitosamente", Toast.LENGTH_SHORT).show();
                        getDialog().dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Error al actualizar", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}