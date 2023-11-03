package com.example.test1;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Register extends AppCompatActivity {
    Button btn_volver, btn_register;
    EditText name, email, password,password2, phone;
    FirebaseAuth mAuth;
    RadioGroup radioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        StartComponents();
    }

    private void StartComponents(){
        btn_volver = findViewById(R.id.btn_volver);
        btn_volver.setOnClickListener(view -> GoToLogin());
        btn_register = findViewById(R.id.btn_register);
        name = findViewById(R.id.usuario_input);
        email = findViewById(R.id.email_input);
        password = findViewById(R.id.contraseña_input);
        password2 = findViewById(R.id.contraseña_input2);
        phone = findViewById(R.id.telefono_input);
        radioGroup = findViewById(R.id.rg_rol);
        mAuth = FirebaseAuth.getInstance();
        btn_register.setOnClickListener(view -> Register());
    }

    private void Register(){
        String nameUser = name.getText().toString().trim();
        String emailUser = email.getText().toString().trim();
        String passUser = password.getText().toString().trim();
        String passUser2 = password2.getText().toString().trim();
        String phoneUser = phone.getText().toString().trim();
        String rolUser = GetRole();

        if (Validations(nameUser, emailUser, passUser,passUser2, phoneUser)) {
            registerUser(nameUser, emailUser, passUser, phoneUser, rolUser);
        }
    }

    private boolean Validations(String nameUser, String emailUser, String passUser, String passUser2, String phoneUser){
        Boolean result = true;

        if (!phoneUser.matches("^[0-9]{8,16}$")) {
            result = false;
            phone.setError("Teléfono no válido");
            phone.requestFocus();
            phone.setBackgroundResource(R.drawable.edit_text_error_background);
        }
        else {
            phone.setError(null);
            phone.setBackgroundResource(android.R.drawable.edit_text);
        }

        if (!passUser2.equals(passUser)) {
            result = false;
            password2.setError("Las contraseñas no coinciden");
            password2.requestFocus();
            password2.setBackgroundResource(R.drawable.edit_text_error_background);

        }
        else {
            password2.setError(null);
            password2.setBackgroundResource(android.R.drawable.edit_text);
        }

        if (passUser.length() < 8) {
            result = false;
            password.setError("Contraseña no válida");
            password.requestFocus();
            password.setBackgroundResource(R.drawable.edit_text_error_background);
        }
        else {
            password.setError(null);
            password.setBackgroundResource(android.R.drawable.edit_text);
        }

        if (!nameUser.matches("^[a-zA-Z0-9_-]{3,15}$")) {
            result = false;
            name.setError("Usuario no válido");
            name.requestFocus();
            name.setBackgroundResource(R.drawable.edit_text_error_background);
        }
        else {
            name.setError(null);
            name.setBackgroundResource(android.R.drawable.edit_text);
        }

        if(!validarEmail(emailUser)){
            result = false;
            email.setError("Email no válido");
            email.requestFocus();
            email.setBackgroundResource(R.drawable.edit_text_error_background);
        }
        else {
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

    private String GetRole(){
        int selectedId = radioGroup.getCheckedRadioButtonId();
        if (selectedId != -1) {
            RadioButton selectedRadioButton = findViewById(selectedId);
            return selectedRadioButton.getText().toString();
        }
        else{
            return "Ambos";
        }
    }

    private void GoToLogin() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void GoToLoginWithParameters(String pass, String email) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("pass", pass);
        intent.putExtra("email", email);
        startActivity(intent);
        finish();
    }

    private void registerUser(String nameUser, String emailUser, String passUser, String phoneUser, String rolUser) {
        mAuth.createUserWithEmailAndPassword(emailUser, passUser)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Insert(emailUser, passUser, nameUser, phoneUser, rolUser, 0);

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
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(Register.this, "Usuario creado correctamente", Toast.LENGTH_SHORT).show();
                                mAuth.signOut();
                                GoToLoginWithParameters(passUser, emailUser);
                            } else {
                                Toast.makeText(Register.this, "Error al crear el perfil del usuario", Toast.LENGTH_SHORT).show();
                                GoToLoginWithParameters(passUser, emailUser);
                            }
                        }
                    });
        }
        else {
            if (retry < 3)
                retry+=1;
                mAuth.signInWithEmailAndPassword(emailUser, passUser);
            Insert(emailUser, passUser, nameUser, phoneUser, rolUser, retry);
        }
    }

}