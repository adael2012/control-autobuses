package com.itla.autobuses.service;

import com.itla.autobuses.dao.BitacoraDAO;
import com.itla.autobuses.util.Sesion;

final class BitacoraHelper {

    private static final BitacoraDAO DAO = new BitacoraDAO();

    private BitacoraHelper() { }

    static void registrar(String accion, String tabla, int idRegistro, String detalle) {
        if (!Sesion.getInstancia().hayUsuario()) {
            return;
        }
        try {
            DAO.insertar(Sesion.getInstancia().getUsuario().getId(),
                    accion, tabla, idRegistro, detalle);
        } catch (Exception ignored) {

        }
    }
}
