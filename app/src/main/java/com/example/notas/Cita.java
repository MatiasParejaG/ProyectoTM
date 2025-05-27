package com.example.notas;

public class Cita {
    public String id;
    public String dni;
    public String nombre;
    public String telefono;
    public String fecha;
    public String hora;

    public Cita() {} // Constructor vacío requerido para Firebase

    public Cita(String id, String dni, String nombre, String telefono, String fecha, String hora) {
        this.id = id;
        this.dni = dni;
        this.nombre = nombre;
        this.telefono = telefono;
        this.fecha = fecha;
        this.hora = hora;
    }
}
