package com.example.test1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    Button login,register;
    TextView email_input, email_label, usuario_input, usuario_label,contraseña_input,contraseña_label;
    Switch sw;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StartComponents();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null){
            startActivity(new Intent(MainActivity.this, SubscriberMain.class));
            finish();
        }
    }

    private void StartComponents(){
        mAuth = FirebaseAuth.getInstance();
        sw = findViewById(R.id.publisher_switch);
        register = findViewById(R.id.btn_join);
        register.setOnClickListener(view -> GoTo(Register.class));
        email_input = findViewById(R.id.email_input);
        email_label = findViewById(R.id.email_label);
        usuario_input = findViewById(R.id.usuario_input);
        usuario_label = findViewById(R.id.usuario_label);
        contraseña_input = findViewById(R.id.contraseña_input);
        contraseña_label = findViewById(R.id.contraseña_label);
        login = findViewById(R.id.btn_register);
        login.setOnClickListener(view -> Login());
        GetExtras();
    }

    private void GetExtras(){
        Intent intent = getIntent();
        if (intent.hasExtra("user")) {
            String user = intent.getStringExtra("user");
            usuario_input.setText(user);
        }
        if (intent.hasExtra("pass")) {
            String pass = intent.getStringExtra("pass");
            contraseña_input.setText(pass);
        }
        if (intent.hasExtra("email")) {
            String email = intent.getStringExtra("email");
            email_input.setText(email);
        }
    }

    private void Login(){
        String email = email_input.getText().toString();
        String password = contraseña_input.getText().toString();

        if (!email.isEmpty() && !password.isEmpty()) {

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                Toast.makeText(MainActivity.this, "Bienvenido " + user.getEmail(), Toast.LENGTH_SHORT).show();
                                GoToView();
                            } else {
                                Toast.makeText(MainActivity.this, "Usuario y/o contraseña invalida", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(MainActivity.this, "Por favor, ingresa tu correo electrónico y contraseña.", Toast.LENGTH_SHORT).show();
        }
    }

    private void GoTo(Class activity){
        Intent intent = new Intent(this, activity);
        startActivity(intent);
    }

    private void GoToView(){
        Class rol = SubscriberMain.class;
        if(sw.isChecked()){
            rol = PublisherMain.class;
        }
        GoTo(rol);
        finish();
    }




}