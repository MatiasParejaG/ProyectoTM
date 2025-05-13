package com.example.notas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button btnAgendarCita, btnModificarCita, btnEliminarCita, btnListarCitas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAgendarCita = findViewById(R.id.btnAgendarCita);
        btnModificarCita = findViewById(R.id.btnModificarCita);
        btnEliminarCita = findViewById(R.id.btnEliminarCita);
        btnListarCitas = findViewById(R.id.btnListarCitas);

        btnAgendarCita.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Insertar.class);
            startActivity(intent);
        });

        btnModificarCita.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ModificarActivity.class);
            startActivity(intent);
        });

        btnEliminarCita.setOnClickListener(v -> {
            startActivity(new Intent(this, EliminarActivity.class));
        });

        btnListarCitas.setOnClickListener(v -> {
            startActivity(new Intent(this, ListarActivity.class));
        });
    }
}
