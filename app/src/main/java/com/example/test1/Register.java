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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Register extends AppCompatActivity {
    Button btn_volver,btn_register;
    EditText name, email, password;
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
        mAuth = FirebaseAuth.getInstance();
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nameUser = name.getText().toString().trim();
                String emailUser = email.getText().toString().trim();
                String passUser = password.getText().toString().trim();

                if (nameUser.isEmpty() && emailUser.isEmpty() && passUser.isEmpty()){
                    Toast.makeText(Register.this, "Complete los datos", Toast.LENGTH_SHORT).show();
                }else{
                    registerUser(nameUser, emailUser, passUser);
                }
            }
        });


    }
    private void GoToLogin(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void GoToLoginWithParameters(String user, String pass, String email){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("user",user);
        intent.putExtra("pass",pass);
        intent.putExtra("email",email);
        startActivity(intent);
        finish();
    }

    private void registerUser(String nameUser, String emailUser, String passUser) {
        mAuth.createUserWithEmailAndPassword(emailUser, passUser)
              .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    GoToLoginWithParameters(nameUser,passUser,emailUser);
                    Toast.makeText(Register.this, "El usuario se ha registrado con el mail " + user.getEmail(), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(Register.this, "Error al registrar: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}