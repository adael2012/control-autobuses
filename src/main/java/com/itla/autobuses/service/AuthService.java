package com.itla.autobuses.service;

import com.itla.autobuses.dao.UsuarioDAO;
import com.itla.autobuses.model.Usuario;
import com.itla.autobuses.util.NegocioException;
import com.itla.autobuses.util.Seguridad;
import com.itla.autobuses.util.Sesion;

import java.sql.SQLException;

public class AuthService {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public Usuario login(String username, String password) throws NegocioException {
        if (username == null || username.isBlank() || password == null || password.isEmpty()) {
            throw new NegocioException("Ingresa usuario y contrasena.");
        }
        try {
            Usuario u = usuarioDAO.obtenerPorUsername(username.trim());

            if (u == null || !Seguridad.verificar(password, u.getSalt(), u.getPasswordHash())) {
                throw new NegocioException("Usuario o contrasena incorrectos.");
            }
            if (!u.isActivo()) {
                throw new NegocioException("Este usuario esta desactivado.");
            }
            Sesion.getInstancia().iniciar(u);
            BitacoraHelper.registrar("LOGIN", "Usuario", u.getId(), "Inicio de sesion");
            return u;
        } catch (SQLException e) {
            throw new NegocioException("Error al acceder a la base de datos: " + e.getMessage(), e);
        }
    }

    public void logout() {
        Sesion s = Sesion.getInstancia();
        if (s.hayUsuario()) {
            BitacoraHelper.registrar("LOGOUT", "Usuario", s.getUsuario().getId(), "Cierre de sesion");
        }
        s.cerrar();
    }
}
