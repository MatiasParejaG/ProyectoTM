package com.example.notas;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListarActivity extends AppCompatActivity {

    RecyclerView rvCitas;
    CitaAdapter adapter;
    List<Cita> listaCitas;
    DatabaseReference userCitasRef;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar);

        rvCitas = findViewById(R.id.rvCitas);
        rvCitas.setLayoutManager(new LinearLayoutManager(this));
        listaCitas = new ArrayList<>();
        adapter = new CitaAdapter(listaCitas);
        rvCitas.setAdapter(adapter);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "Debe iniciar sesión primero", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();
        userCitasRef = FirebaseDatabase.getInstance().getReference("citas").child(userId);

        userCitasRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaCitas.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot citaSnap : snapshot.getChildren()) {
                        String id = citaSnap.getKey();
                        String fecha = citaSnap.child("fecha_cita").getValue(String.class);
                        String hora = citaSnap.child("hora_cita").getValue(String.class);
                        String telefono = citaSnap.child("telefono_paciente").getValue(String.class);
                        String nombre = citaSnap.child("nombre_paciente").getValue(String.class);
                        String dni = citaSnap.child("dni_paciente").getValue(String.class);

                        Cita cita = new Cita(id, dni, nombre, telefono, fecha, hora);
                        listaCitas.add(cita);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ListarActivity.this, "Error al leer datos: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}


