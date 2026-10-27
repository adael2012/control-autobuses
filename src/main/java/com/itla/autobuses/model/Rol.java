package com.itla.autobuses.model;

public class Rol {
    public static final String ADMINISTRADOR = "Administrador";

    private final int id;
    private final String nombre;

    public Rol(int id, String nombre) { this.id = id; this.nombre = nombre; }

    public int getId() { return id; }
    public String getNombre() { return nombre; }

    @Override
    public String toString() { return nombre; }
}
