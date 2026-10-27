package com.itla.autobuses.model;

import java.time.LocalDate;

public class Conductor {
    private int id;
    private String cedula;
    private String nombre;
    private String apellido;
    private String telefono;
    private String numeroLicencia;
    private LocalDate vencimientoLicencia;
    private boolean activo = true;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getCedula() { return cedula; }
    public void setCedula(String cedula) { this.cedula = cedula; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getNumeroLicencia() { return numeroLicencia; }
    public void setNumeroLicencia(String numeroLicencia) { this.numeroLicencia = numeroLicencia; }
    public LocalDate getVencimientoLicencia() { return vencimientoLicencia; }
    public void setVencimientoLicencia(LocalDate v) { this.vencimientoLicencia = v; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public boolean licenciaVigente() {
        return vencimientoLicencia != null && !vencimientoLicencia.isBefore(LocalDate.now());
    }
}
