package com.itla.autobuses.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class ConexionDB {

    private static ConexionDB instancia;

    private final String url;
    private final String usuario;
    private final String clave;

    private ConexionDB() {
        Properties props = new Properties();
        File externo = new File("db.properties");
        try (InputStream in = externo.exists()
                ? new FileInputStream(externo)
                : ConexionDB.class.getResourceAsStream("/db.properties")) {
            if (in == null) {
                throw new IllegalStateException("No se encontro el archivo db.properties");
            }
            props.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo leer db.properties", e);
        }
        this.url = props.getProperty("db.url");
        this.usuario = props.getProperty("db.user");
        this.clave = props.getProperty("db.password");
    }

    public static synchronized ConexionDB getInstancia() {
        if (instancia == null) {
            instancia = new ConexionDB();
        }
        return instancia;
    }

    public Connection getConexion() throws SQLException {
        return DriverManager.getConnection(url, usuario, clave);
    }
}
