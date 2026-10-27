package com.itla.autobuses.dao;

import com.itla.autobuses.model.Conductor;
import com.itla.autobuses.util.ConexionDB;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ConductorDAO {

    public List<Conductor> listar() throws SQLException {
        List<Conductor> lista = new ArrayList<>();
        try (Connection c = ConexionDB.getInstancia().getConexion();
             CallableStatement cs = c.prepareCall("{call sp_Conductor_Listar}");
             ResultSet rs = cs.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public List<Conductor> buscar(String texto) throws SQLException {
        List<Conductor> lista = new ArrayList<>();
        try (Connection c = ConexionDB.getInstancia().getConexion();
             CallableStatement cs = c.prepareCall("{call sp_Conductor_Buscar(?)}")) {
            cs.setString(1, texto);
            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }
        return lista;
    }

    public int insertar(Conductor k) throws SQLException {
        try (Connection c = ConexionDB.getInstancia().getConexion();
             CallableStatement cs = c.prepareCall("{call sp_Conductor_Insertar(?,?,?,?,?,?,?)}")) {
            cs.setString(1, k.getCedula());
            cs.setString(2, k.getNombre());
            cs.setString(3, k.getApellido());
            cs.setString(4, k.getTelefono());
            cs.setString(5, k.getNumeroLicencia());
            cs.setDate(6, Date.valueOf(k.getVencimientoLicencia()));
            cs.setBoolean(7, k.isActivo());
            try (ResultSet rs = cs.executeQuery()) {
                return rs.next() ? rs.getInt("IdConductor") : 0;
            }
        }
    }

    public void actualizar(Conductor k) throws SQLException {
        try (Connection c = ConexionDB.getInstancia().getConexion();
             CallableStatement cs = c.prepareCall("{call sp_Conductor_Actualizar(?,?,?,?,?,?,?,?)}")) {
            cs.setInt(1, k.getId());
            cs.setString(2, k.getCedula());
            cs.setString(3, k.getNombre());
            cs.setString(4, k.getApellido());
            cs.setString(5, k.getTelefono());
            cs.setString(6, k.getNumeroLicencia());
            cs.setDate(7, Date.valueOf(k.getVencimientoLicencia()));
            cs.setBoolean(8, k.isActivo());
            cs.execute();
        }
    }

    public void desactivar(int idConductor) throws SQLException {
        try (Connection c = ConexionDB.getInstancia().getConexion();
             CallableStatement cs = c.prepareCall("{call sp_Conductor_Eliminar(?)}")) {
            cs.setInt(1, idConductor);
            cs.execute();
        }
    }

    private Conductor mapear(ResultSet rs) throws SQLException {
        Conductor k = new Conductor();
        k.setId(rs.getInt("IdConductor"));
        k.setCedula(rs.getString("Cedula"));
        k.setNombre(rs.getString("Nombre"));
        k.setApellido(rs.getString("Apellido"));
        k.setTelefono(rs.getString("Telefono"));
        k.setNumeroLicencia(rs.getString("NumeroLicencia"));
        k.setVencimientoLicencia(rs.getDate("VencimientoLicencia").toLocalDate());
        k.setActivo(rs.getBoolean("Activo"));
        return k;
    }
}
