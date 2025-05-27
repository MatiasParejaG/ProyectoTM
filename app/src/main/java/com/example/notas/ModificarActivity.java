package com.example.notas;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.HashMap;
import java.util.Map;

public class ModificarActivity extends AppCompatActivity {

    EditText etIdCita, etNuevoTelefono;
    Button btnActualizar;
    DatabaseReference citasRef;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificar);

        etIdCita = findViewById(R.id.etIdCitaModificar);
        etNuevoTelefono = findViewById(R.id.etNuevoTelefono);
        btnActualizar = findViewById(R.id.btnActualizar);

        auth = FirebaseAuth.getInstance();
        citasRef = FirebaseDatabase.getInstance().getReference("citas");

        btnActualizar.setOnClickListener(v -> {
            String idCitaStr = etIdCita.getText().toString().trim();
            String nuevoTelefono = etNuevoTelefono.getText().toString().trim();

            if (idCitaStr.isEmpty() || nuevoTelefono.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!nuevoTelefono.matches("\\d{6,15}")) {
                Toast.makeText(this, "Teléfono inválido", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseUser currentUser = auth.getCurrentUser();
            if (currentUser == null) {
                Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
                return;
            }

            String uid = currentUser.getUid();
            String idCita = idCitaStr;

            citasRef.child(uid).child(idCita).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        citasRef.child(uid).child(idCita).child("telefono_paciente").setValue(nuevoTelefono)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(ModificarActivity.this, "Teléfono actualizado correctamente", Toast.LENGTH_SHORT).show();
                                        etIdCita.setText("");
                                        etNuevoTelefono.setText("");
                                        startActivity(new Intent(ModificarActivity.this, MainActivity.class));
                                        finish();
                                    } else {
                                        Toast.makeText(ModificarActivity.this, "Error al actualizar", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(ModificarActivity.this, "No se encontró una cita con ese ID", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Toast.makeText(ModificarActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}




