package com.example.notas;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.*;

public class ModificarActivity extends AppCompatActivity {

    EditText etIdCita, etNuevoTelefono;
    Button btnActualizar;
    DatabaseReference citasRef, contadorRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificar);

        etIdCita = findViewById(R.id.etIdCitaModificar); // Aquí el usuario debe ingresar el ID autoincremental
        etNuevoTelefono = findViewById(R.id.etNuevoTelefono);
        btnActualizar = findViewById(R.id.btnActualizar);

        citasRef = FirebaseDatabase.getInstance().getReference("citas");
        contadorRef = FirebaseDatabase.getInstance().getReference("contador_citas");

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

            // Convertir el ID ingresado a un entero para verificar si existe
            int idCita = Integer.parseInt(idCitaStr);

            // Verificar si el ID existe en Firebase
            citasRef.child(String.valueOf(idCita)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        // Actualizar el campo "telefono_paciente"
                        citasRef.child(String.valueOf(idCita)).child("telefono_paciente").setValue(nuevoTelefono)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(ModificarActivity.this, "Teléfono actualizado correctamente", Toast.LENGTH_SHORT).show();
                                        etIdCita.setText("");
                                        etNuevoTelefono.setText("");

                                        // Volver a la actividad principal o donde sea necesario
                                        Intent intent = new Intent(ModificarActivity.this, MainActivity.class);
                                        startActivity(intent);
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




