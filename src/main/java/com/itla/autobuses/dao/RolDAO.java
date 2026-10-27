package com.itla.autobuses.dao;

import com.itla.autobuses.model.Rol;
import com.itla.autobuses.util.ConexionDB;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RolDAO {

    public List<Rol> listar() throws SQLException {
        List<Rol> lista = new ArrayList<>();
        try (Connection c = ConexionDB.getInstancia().getConexion();
             CallableStatement cs = c.prepareCall("{call sp_Rol_Listar}");
             ResultSet rs = cs.executeQuery()) {
            while (rs.next()) {
                lista.add(new Rol(rs.getInt("IdRol"), rs.getString("Nombre")));
            }
        }
        return lista;
    }
}
