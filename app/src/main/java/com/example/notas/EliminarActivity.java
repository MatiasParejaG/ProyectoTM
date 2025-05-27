package com.example.notas;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

public class EliminarActivity extends AppCompatActivity {

    private EditText etIdCitaEliminar;
    private Button btnEliminar;
    private DatabaseReference userCitasRef;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eliminar);

        // Inicializar Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Verificar que el usuario esté logueado
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "Debe iniciar sesión primero", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Inicializar vistas
        etIdCitaEliminar = findViewById(R.id.etIdCitaEliminar);
        btnEliminar = findViewById(R.id.btnEliminar);

        // Configurar ProgressDialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Eliminando cita...");
        progressDialog.setCancelable(false);

        // Referencia a las citas del usuario actual
        String userId = mAuth.getCurrentUser().getUid();
        userCitasRef = FirebaseDatabase.getInstance().getReference("citas").child(userId);

        btnEliminar.setOnClickListener(v -> eliminarCita());
    }

    private void eliminarCita() {
        String idCitaStr = etIdCitaEliminar.getText().toString().trim();

        // Validar ID
        if (idCitaStr.isEmpty()) {
            etIdCitaEliminar.setError("Ingrese un ID de cita");
            etIdCitaEliminar.requestFocus();
            return;
        }

        if (!idCitaStr.matches("\\d+")) {
            etIdCitaEliminar.setError("El ID debe ser numérico");
            etIdCitaEliminar.requestFocus();
            return;
        }

        int idCita = Integer.parseInt(idCitaStr);
        progressDialog.show();

        // Verificar existencia y pertenencia de la cita
        userCitasRef.child(String.valueOf(idCita)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    mostrarDialogoConfirmacion(idCita, snapshot);
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(EliminarActivity.this,
                            "No se encontró la cita #" + idCita + " en tus registros",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(EliminarActivity.this,
                        "Error de conexión: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarDialogoConfirmacion(int idCita, DataSnapshot snapshot) {
        // Obtener datos de la cita para mostrar en el diálogo
        String nombrePaciente = snapshot.child("nombre_paciente").getValue(String.class);
        String fecha = snapshot.child("fecha_cita").getValue(String.class);
        String hora = snapshot.child("hora_cita").getValue(String.class);

        String mensaje = "¿Eliminar cita #" + idCita + "?\n\n" +
                "Paciente: " + nombrePaciente + "\n" +
                "Fecha: " + fecha + "\n" +
                "Hora: " + hora.substring(0, 5); // Mostrar solo HH:mm

        new AlertDialog.Builder(this)
                .setTitle("Confirmar eliminación")
                .setMessage(mensaje)
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    ejecutarEliminacion(idCita);
                })
                .setNegativeButton("Cancelar", (dialog, which) -> {
                    progressDialog.dismiss();
                })
                .show();
    }

    private void ejecutarEliminacion(int idCita) {
        userCitasRef.child(String.valueOf(idCita)).removeValue()
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();

                    if (task.isSuccessful()) {
                        Toast.makeText(EliminarActivity.this,
                                "Cita #" + idCita + " eliminada correctamente",
                                Toast.LENGTH_SHORT).show();
                        etIdCitaEliminar.setText("");
                        etIdCitaEliminar.requestFocus();
                    } else {
                        Toast.makeText(EliminarActivity.this,
                                "Error al eliminar: " +
                                        (task.getException() != null ? task.getException().getMessage() : ""),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        super.onDestroy();
    }
}


