package com.example.notas;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.*;

public class EliminarActivity extends AppCompatActivity {

    EditText etIdCitaEliminar;
    Button btnEliminar;
    DatabaseReference citasRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eliminar);

        etIdCitaEliminar = findViewById(R.id.etIdCitaEliminar); // Aquí debes ingresar el ID numérico autogenerado
        btnEliminar = findViewById(R.id.btnEliminar);
        citasRef = FirebaseDatabase.getInstance().getReference("citas");

        btnEliminar.setOnClickListener(v -> {
            String idCita = etIdCitaEliminar.getText().toString().trim();

            if (idCita.isEmpty()) {
                Toast.makeText(this, "Ingrese un ID de cita válido", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validación opcional: asegurar que sea numérico
            if (!idCita.matches("\\d+")) {
                Toast.makeText(this, "El ID debe ser un número", Toast.LENGTH_SHORT).show();
                return;
            }

            // Verificar si existe la cita
            citasRef.child(idCita).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        citasRef.child(idCita).removeValue().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(EliminarActivity.this, "Cita eliminada correctamente", Toast.LENGTH_SHORT).show();
                                etIdCitaEliminar.setText("");
                            } else {
                                Toast.makeText(EliminarActivity.this, "Error al eliminar la cita", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(EliminarActivity.this, "No se encontró una cita con ese ID", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Toast.makeText(EliminarActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}


