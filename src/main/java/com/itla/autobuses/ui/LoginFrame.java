package com.itla.autobuses.ui;

import com.itla.autobuses.service.AuthService;
import com.itla.autobuses.util.NegocioException;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private final JTextField txtUsuario = new JTextField(18);
    private final JPasswordField txtClave = new JPasswordField(18);
    private final JLabel lblError = new JLabel(" ");
    private final JButton btnIngresar = new JButton("Ingresar");
    private final AuthService auth = new AuthService();

    public LoginFrame() {
        super("Control de Autobuses - Iniciar sesion");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JLabel titulo = new JLabel("Sistema de Control de Autobuses", SwingConstants.CENTER);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 16f));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.anchor = GridBagConstraints.WEST;
        g.gridx = 0; g.gridy = 0; form.add(new JLabel("Usuario:"), g);
        g.gridx = 1; form.add(txtUsuario, g);
        g.gridx = 0; g.gridy = 1; form.add(new JLabel("Contrasena:"), g);
        g.gridx = 1; form.add(txtClave, g);
        g.gridx = 0; g.gridy = 2; g.gridwidth = 2; g.anchor = GridBagConstraints.CENTER;
        form.add(btnIngresar, g);
        g.gridy = 3; lblError.setForeground(new Color(180, 30, 30));
        form.add(lblError, g);

        JPanel raiz = new JPanel(new BorderLayout(0, 10));
        raiz.setBorder(BorderFactory.createEmptyBorder(18, 24, 18, 24));
        raiz.add(titulo, BorderLayout.NORTH);
        raiz.add(form, BorderLayout.CENTER);
        setContentPane(raiz);

        getRootPane().setDefaultButton(btnIngresar);
        btnIngresar.addActionListener(e -> intentarLogin());

        pack();
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void intentarLogin() {
        lblError.setText(" ");
        btnIngresar.setEnabled(false);
        try {
            auth.login(txtUsuario.getText(), new String(txtClave.getPassword()));
            new MainFrame().setVisible(true);
            dispose();
        } catch (NegocioException ex) {
            lblError.setText(ex.getMessage());
            txtClave.setText("");
            txtClave.requestFocus();
        } finally {
            btnIngresar.setEnabled(true);
        }
    }
}
