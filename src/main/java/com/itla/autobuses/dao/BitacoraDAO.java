package com.itla.autobuses.dao;

import com.itla.autobuses.util.ConexionDB;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

public class BitacoraDAO {

    public void insertar(int idUsuario, String accion, String tabla,
                         int idRegistro, String detalle) throws SQLException {
        try (Connection c = ConexionDB.getInstancia().getConexion();
             CallableStatement cs = c.prepareCall("{call sp_Bitacora_Insertar(?,?,?,?,?)}")) {
            cs.setInt(1, idUsuario);
            cs.setString(2, accion);
            cs.setString(3, tabla);
            cs.setInt(4, idRegistro);
            cs.setString(5, detalle);
            cs.execute();
        }
    }
}
