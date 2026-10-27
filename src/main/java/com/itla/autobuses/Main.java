package com.itla.autobuses;

import com.itla.autobuses.service.UsuarioService;
import com.itla.autobuses.ui.LoginFrame;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {

        }
        SwingUtilities.invokeLater(() -> {
            try {
                new UsuarioService().crearAdminInicialSiNoExiste();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                        "No se pudo conectar con la base de datos.\n" + e.getMessage(),
                        "Error de conexion", JOptionPane.ERROR_MESSAGE);
                return;
            }
            new LoginFrame().setVisible(true);
        });
    }
}
