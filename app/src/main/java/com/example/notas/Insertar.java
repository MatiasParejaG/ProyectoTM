package com.example.notas;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Insertar extends AppCompatActivity {

    EditText etDni, etNombre, etTelefono, etFecha, etHora;
    Button btnGuardar;
    DatabaseReference citasRef, contadorRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insertar);

        etDni = findViewById(R.id.etDni);
        etNombre = findViewById(R.id.etNombre);
        etTelefono = findViewById(R.id.etTelefono);
        etFecha = findViewById(R.id.etFecha);
        etHora = findViewById(R.id.etHora);
        btnGuardar = findViewById(R.id.btnGuardar);

        // Referencia a Firebase Realtime Database
        citasRef = FirebaseDatabase.getInstance().getReference("citas");
        contadorRef = FirebaseDatabase.getInstance().getReference("contador_citas");

        // DatePicker
        etFecha.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (view, year1, monthOfYear, dayOfMonth) -> {
                        String fechaSeleccionada = year1 + "-" + String.format("%02d", monthOfYear + 1) + "-" + String.format("%02d", dayOfMonth);
                        etFecha.setText(fechaSeleccionada);
                    },
                    year, month, day
            );
            datePickerDialog.show();
        });

        // TimePicker
        etHora.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    this,
                    (view, hourOfDay, minute1) -> {
                        String horaSeleccionada = String.format("%02d:%02d", hourOfDay, minute1);
                        etHora.setText(horaSeleccionada);
                    },
                    hour, minute, true
            );
            timePickerDialog.show();
        });

        btnGuardar.setOnClickListener(v -> {
            String dni = etDni.getText().toString().trim();
            String nombre = etNombre.getText().toString().trim();
            String telefono = etTelefono.getText().toString().trim();
            String fecha = etFecha.getText().toString().trim();
            String hora = etHora.getText().toString().trim();

            // Validaciones
            if (dni.isEmpty() || nombre.isEmpty() || telefono.isEmpty() || fecha.isEmpty() || hora.isEmpty()) {
                Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!dni.matches("\\d+")) {
                Toast.makeText(this, "DNI debe ser numérico", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!nombre.matches("[a-zA-Z\\s]+")) {
                Toast.makeText(this, "Nombre inválido", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!telefono.matches("\\d{7,15}")) {
                Toast.makeText(this, "Teléfono inválido", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!fecha.matches("\\d{4}-\\d{2}-\\d{2}")) {
                Toast.makeText(this, "Fecha inválida. Use el formato yyyy-MM-dd", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!hora.matches("\\d{2}:\\d{2}")) {
                Toast.makeText(this, "Hora inválida. Use el formato HH:mm", Toast.LENGTH_SHORT).show();
                return;
            }

            // Obtener el siguiente ID desde el contador
            contadorRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    int idCita = snapshot.exists() ? snapshot.getValue(Integer.class) : 1;

                    // Crear objeto para la cita
                    Map<String, Object> cita = new HashMap<>();
                    cita.put("id", idCita);
                    cita.put("dni_paciente", dni);
                    cita.put("nombre_paciente", nombre);
                    cita.put("telefono_paciente", telefono);
                    cita.put("fecha_cita", fecha);
                    cita.put("hora_cita", hora + ":00"); // HH:mm:ss

                    // Insertar en Firebase
                    citasRef.child(String.valueOf(idCita)).setValue(cita).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Actualizar el contador de citas
                            contadorRef.setValue(idCita + 1);
                            Toast.makeText(Insertar.this, "Cita registrada correctamente", Toast.LENGTH_LONG).show();
                            limpiarCampos();
                        } else {
                            Toast.makeText(Insertar.this, "Error al guardar la cita", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Toast.makeText(Insertar.this, "Error al obtener el contador", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void limpiarCampos() {
        etDni.setText("");
        etNombre.setText("");
        etTelefono.setText("");
        etFecha.setText("");
        etHora.setText("");
        etDni.requestFocus();
    }
}


