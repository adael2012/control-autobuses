package com.itla.autobuses.ui;

import com.itla.autobuses.model.Usuario;
import com.itla.autobuses.service.AuthService;
import com.itla.autobuses.util.Sesion;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private final AuthService auth = new AuthService();

    public MainFrame() {
        super("Sistema de Control de Autobuses");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        Usuario u = Sesion.getInstancia().getUsuario();

        JLabel lblUsuario = new JLabel("Usuario: " + u.getUsername() + "  |  Rol: " + u.getRolNombre());
        JButton btnSalir = new JButton("Cerrar sesion");
        btnSalir.addActionListener(e -> cerrarSesion());

        JPanel barra = new JPanel(new BorderLayout());
        barra.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        barra.add(lblUsuario, BorderLayout.WEST);
        barra.add(btnSalir, BorderLayout.EAST);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Conductores", new ConductorPanel());
        if (u.esAdmin()) {
            tabs.addTab("Usuarios", new UsuarioPanel());
        }

        setLayout(new BorderLayout());
        add(barra, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);

        setSize(980, 620);
        setLocationRelativeTo(null);
    }

    private void cerrarSesion() {
        auth.logout();
        new LoginFrame().setVisible(true);
        dispose();
    }
}
