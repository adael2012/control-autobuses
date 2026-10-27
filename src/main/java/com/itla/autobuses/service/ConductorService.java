package com.itla.autobuses.service;

import com.itla.autobuses.dao.ConductorDAO;
import com.itla.autobuses.model.Conductor;
import com.itla.autobuses.util.NegocioException;
import com.itla.autobuses.util.Sesion;

import java.sql.SQLException;
import java.util.List;

public class ConductorService {

    private final ConductorDAO dao = new ConductorDAO();

    public List<Conductor> listar() throws NegocioException {
        try {
            return dao.listar();
        } catch (SQLException e) {
            throw UsuarioService.errorBD(e);
        }
    }

    public List<Conductor> buscar(String texto) throws NegocioException {
        if (texto == null || texto.isBlank()) {
            return listar();
        }
        try {
            return dao.buscar(texto.trim());
        } catch (SQLException e) {
            throw UsuarioService.errorBD(e);
        }
    }

    public void guardar(Conductor k) throws NegocioException {
        exigirAdmin();
        validar(k);
        try {
            if (k.getId() == 0) {
                int id = dao.insertar(k);
                k.setId(id);
                BitacoraHelper.registrar("CREAR", "Conductor", id, k.getNombre() + " " + k.getApellido());
            } else {
                dao.actualizar(k);
                BitacoraHelper.registrar("ACTUALIZAR", "Conductor", k.getId(),
                        k.getNombre() + " " + k.getApellido());
            }
        } catch (SQLException e) {
            if (UsuarioService.esDuplicado(e)) {
                throw new NegocioException("Ya existe un conductor con esa cedula o numero de licencia.");
            }
            throw UsuarioService.errorBD(e);
        }
    }

    public void desactivar(Conductor k) throws NegocioException {
        exigirAdmin();
        try {
            dao.desactivar(k.getId());
            BitacoraHelper.registrar("DESACTIVAR", "Conductor", k.getId(),
                    k.getNombre() + " " + k.getApellido());
        } catch (SQLException e) {
            throw UsuarioService.errorBD(e);
        }
    }

    private void validar(Conductor k) throws NegocioException {
        if (k.getCedula() == null || !k.getCedula().matches("\\d{11}")) {
            throw new NegocioException("La cedula debe tener 11 digitos, sin guiones.");
        }
        if (vacio(k.getNombre()) || vacio(k.getApellido())) {
            throw new NegocioException("El nombre y el apellido son obligatorios.");
        }
        if (!vacio(k.getTelefono()) && !k.getTelefono().matches("[0-9+\\-() ]{7,15}")) {
            throw new NegocioException("El telefono no tiene un formato valido.");
        }
        if (vacio(k.getNumeroLicencia())) {
            throw new NegocioException("El numero de licencia es obligatorio.");
        }
        if (k.getVencimientoLicencia() == null) {
            throw new NegocioException("Indica la fecha de vencimiento de la licencia.");
        }
        if (k.isActivo() && !k.licenciaVigente()) {
            throw new NegocioException("La licencia esta vencida; no se puede dejar al conductor activo.");
        }
    }

    private boolean vacio(String s) {
        return s == null || s.isBlank();
    }

    private void exigirAdmin() throws NegocioException {
        if (!Sesion.getInstancia().esAdmin()) {
            throw new NegocioException("Solo el Administrador puede realizar esta accion.");
        }
    }
}
