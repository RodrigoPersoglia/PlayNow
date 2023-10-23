package com.example.test1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class SecondActivity extends AppCompatActivity {

    private Button back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        String param = getIntent().getStringExtra("resultado");
        TextView tv = findViewById(R.id.resultado);
        tv.setText(param);
        back = findViewById(R.id.back);
        back.setOnClickListener(view -> CambiarPagina());
    }

    private void CambiarPagina(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}