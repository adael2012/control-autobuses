package com.itla.autobuses.util;

import com.itla.autobuses.model.Usuario;

public final class Sesion {

    private static Sesion instancia;
    private Usuario usuario;

    private Sesion() { }

    public static synchronized Sesion getInstancia() {
        if (instancia == null) {
            instancia = new Sesion();
        }
        return instancia;
    }

    public void iniciar(Usuario u) { this.usuario = u; }
    public void cerrar() { this.usuario = null; }
    public Usuario getUsuario() { return usuario; }
    public boolean hayUsuario() { return usuario != null; }
    public boolean esAdmin() { return usuario != null && usuario.esAdmin(); }
}
