package com.example.test1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.test1.model.NotificationHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    Button login, register;
    TextView email, password;
    Switch sw;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    String role;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StartComponents();
    }

    @Override
    protected void onStart() {
        super.onStart();
        String usuario = sharedPreferences.getString("usuario", "");
        email.setText(usuario);
        String contrasena = sharedPreferences.getString("contrasena", "");
        password.setText(contrasena);
        String rol = sharedPreferences.getString("rol", "");
        if(rol.equals("Publicador")){
            sw.setChecked(true);
        }
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            ValidateRole(user.getEmail(),contrasena);
        }
    }

    private void StartComponents() {
        sharedPreferences = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        mAuth = FirebaseAuth.getInstance();
        sw = findViewById(R.id.publisher_switch);
        register = findViewById(R.id.btn_join);
        register.setOnClickListener(view -> GoTo(Register.class));
        email = findViewById(R.id.email_input);
        password = findViewById(R.id.contraseña_input);
        login = findViewById(R.id.btn_register);
        login.setOnClickListener(view -> Login());
        GetExtras();
    }

    private void GetExtras() {
        Intent intent = getIntent();
        if (intent.hasExtra("pass")) {
            String pass = intent.getStringExtra("pass");
            password.setText(pass);
        }
        if (intent.hasExtra("email")) {
            String email = intent.getStringExtra("email");
            this.email.setText(email);
        }
    }

    private boolean Validations(String emailUser, String passUser) {
        Boolean result = true;

        if (passUser.length() < 8) {
            result = false;
            password.setError("Contraseña no válida");
            password.requestFocus();
            password.setBackgroundResource(R.drawable.edit_text_error_background);
        } else {
            password.setError(null);
            password.setBackgroundResource(android.R.drawable.edit_text);
        }

        if (!validarEmail(emailUser)) {
            result = false;
            email.setError("Email no válido");
            email.requestFocus();
            email.setBackgroundResource(R.drawable.edit_text_error_background);
        } else {
            email.setError(null);
            email.setBackgroundResource(android.R.drawable.edit_text);
        }

        return result;
    }

    public boolean validarEmail(String email) {
        String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private void Login() {
        String email = this.email.getText().toString();
        String password = this.password.getText().toString();
        if (Validations(email, password)) {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                ValidateRole(email,password);
                            } else {
                                Toast.makeText(MainActivity.this, "Usuario y/o contraseña invalida", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void GoTo(Class activity) {
        Intent intent = new Intent(this, activity);
        startActivity(intent);
        finish();
    }

    private void GoToView(String usuario,String pass) {
        editor.putString("usuario", usuario);
        editor.putString("contrasena", pass);
        String lastRole = "Suscriptor";

        Class rol = SubscriberMain.class;
        if (sw.isChecked()) {
            lastRole = "Publicador";
            rol = PublisherMain.class;
        }
        GoTo(rol);
        editor.putString("rol", lastRole);
        editor.apply();
    }

    public void ValidateRole(String email,String pass) {

        db = FirebaseFirestore.getInstance();
        CollectionReference usuariosRef = db.collection("usuarios");
        Query query = usuariosRef.whereEqualTo("email", email);
        query.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(Task<QuerySnapshot> task) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            String roleUser = "";
                            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                roleUser = document.getString("rol");

                            }
                            if (CheckRole(roleUser)) {
                                Toast.makeText(MainActivity.this, "Bienvenido " + user.getEmail(), Toast.LENGTH_SHORT).show();
                                GoToView(email,pass);
                            } else {
                                Toast.makeText(MainActivity.this, "El rol seleccionado no coincide con su perfil de usuario", Toast.LENGTH_SHORT).show();
                                mAuth.signOut();
                            }

                        } else {
                            Toast.makeText(MainActivity.this, "Error al obtener su perfil de usuario", Toast.LENGTH_SHORT).show();
                            mAuth.signOut();
                        }
                    }
                });
    }

    private boolean CheckRole(String roleUser){
        role = sw.isChecked() ? "Publicador" : "Suscriptor";
        if (roleUser.equals("Ambos") || roleUser.equals(role)){
            return true;
        }
        return false;
    }

}