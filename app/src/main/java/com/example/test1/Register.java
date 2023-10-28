package com.example.test1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.SetOptions;
import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    Button btn_volver, btn_register;
    EditText name, email, password, phone;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        btn_volver = (Button) findViewById(R.id.btn_volver);
        btn_volver.setOnClickListener(view -> GoToLogin());
        btn_register = (Button) findViewById(R.id.btn_register);
        name = (EditText) findViewById(R.id.usuario_input);
        email = (EditText) findViewById(R.id.email_input);
        password = (EditText) findViewById(R.id.contraseña_input);
        phone = (EditText) findViewById(R.id.telefono_input);
        mAuth = FirebaseAuth.getInstance();
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nameUser = name.getText().toString().trim();
                String emailUser = email.getText().toString().trim();
                String passUser = password.getText().toString().trim();
                String phoneUser = phone.getText().toString().trim();
                String rolUser = "ambos";

                if (nameUser.isEmpty() && emailUser.isEmpty() && passUser.isEmpty()) {
                    Toast.makeText(Register.this, "Complete los datos", Toast.LENGTH_SHORT).show();
                } else {
                    registerUser(nameUser, emailUser, passUser, phoneUser, rolUser);
                }
            }
        });
    }

    private void GoToLogin() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void GoToLoginWithParameters(String user, String pass, String email) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("user", user);
        intent.putExtra("pass", pass);
        intent.putExtra("email", email);
        startActivity(intent);
    }

    private void registerUser(String nameUser, String emailUser, String passUser, String phoneUser, String rolUser) {
        mAuth.createUserWithEmailAndPassword(emailUser, passUser)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Insert(emailUser, passUser, nameUser, phoneUser, rolUser, 0);
                            GoToLoginWithParameters(nameUser, passUser, emailUser);
                        } else {
                            Toast.makeText(Register.this, "Error al registrar: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void Insert(String emailUser, String passUser, String nameUser, String phoneUser, String rolUser, Integer retry) {

        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            DocumentReference userRef = db.collection("usuarios").document(userId);

            Map<String, Object> datosUsuario = new HashMap<>();
            datosUsuario.put("usuario", nameUser);
            datosUsuario.put("email", emailUser);
            datosUsuario.put("telefono", phoneUser);
            datosUsuario.put("rol", rolUser);

            userRef.set(datosUsuario, SetOptions.merge())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(Register.this, "Usuario creado correctamente", Toast.LENGTH_SHORT).show();
                            mAuth.signOut();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Register.this, "Error al crear el perfil del usuario", Toast.LENGTH_SHORT).show();

                        }
                    });
        } else {
            if (retry < 3)
                mAuth.signInWithEmailAndPassword(emailUser, passUser);
            Insert(emailUser, passUser, nameUser, phoneUser, rolUser, retry++);
        }
    }

}