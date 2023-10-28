package com.example.test1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class SubscriberMain extends AppCompatActivity {

    Button btn_logOut;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscriber_main);
        mAuth = FirebaseAuth.getInstance();
        btn_logOut = findViewById(R.id.btn_logout);
        btn_logOut.setOnClickListener(view -> LogOut());
    }

    private void LogOut(){
        mAuth.signOut();
        finish();
        startActivity(new Intent(SubscriberMain.this, MainActivity.class));
    }
}