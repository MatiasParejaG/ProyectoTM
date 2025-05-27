package com.example.notas;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CitaAdapter extends RecyclerView.Adapter<CitaAdapter.CitaViewHolder> {

    private List<Cita> listaCitas;

    public CitaAdapter(List<Cita> lista) {
        this.listaCitas = lista;
    }

    @NonNull
    @Override
    public CitaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cita, parent, false);
        return new CitaViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CitaViewHolder holder, int position) {
        Cita cita = listaCitas.get(position);
        holder.tvIdCita.setText("ID de cita: " + cita.id);
        holder.tvPaciente.setText("Paciente: " + cita.nombre + " (DNI: " + cita.dni + ")");
        holder.tvFechaHora.setText("Fecha: " + cita.fecha + " - Hora: " + cita.hora);
        holder.tvTelefono.setText("Teléfono: " + cita.telefono);
    }

    @Override
    public int getItemCount() {
        return listaCitas.size();
    }

    public static class CitaViewHolder extends RecyclerView.ViewHolder {
        TextView tvIdCita, tvPaciente, tvFechaHora, tvTelefono;

        public CitaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvIdCita = itemView.findViewById(R.id.tvIdCita);
            tvPaciente = itemView.findViewById(R.id.tvPaciente);
            tvFechaHora = itemView.findViewById(R.id.tvFechaHora);
            tvTelefono = itemView.findViewById(R.id.tvTelefono);
        }
    }
}

