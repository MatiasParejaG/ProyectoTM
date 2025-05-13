package com.example.notas;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ListarActivity extends AppCompatActivity {

    TextView tvLista;
    DatabaseReference citasRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar);

        tvLista = findViewById(R.id.tvLista);

        // Referencia a la base de datos Firebase
        citasRef = FirebaseDatabase.getInstance().getReference("citas");

        // Leer las citas desde Firebase
        citasRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                StringBuilder datos = new StringBuilder();

                if (snapshot.exists()) {
                    for (DataSnapshot citaSnap : snapshot.getChildren()) {
                        String id = citaSnap.getKey(); // Usamos la clave del nodo como ID
                        String fecha = citaSnap.child("fecha_cita").getValue(String.class);
                        String hora = citaSnap.child("hora_cita").getValue(String.class);
                        String telefono = citaSnap.child("telefono_paciente").getValue(String.class);
                        String nombre = citaSnap.child("nombre_paciente").getValue(String.class);
                        String dni = citaSnap.child("dni_paciente").getValue(String.class);

                        datos.append("ID de cita: ").append(id).append("\n")
                                .append("Paciente: ").append(nombre)
                                .append(" (DNI: ").append(dni).append(")\n")
                                .append("Fecha: ").append(fecha).append("\n")
                                .append("Hora: ").append(hora).append("\n")
                                .append("Teléfono: ").append(telefono).append("\n")
                                .append("-----------------------------------\n");
                    }
                } else {
                    datos.append("No hay citas registradas.");
                }

                tvLista.setText(datos.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ListarActivity.this, "Error al leer datos: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}
