package com.itla.autobuses.ui;

import com.itla.autobuses.model.Rol;
import com.itla.autobuses.model.Usuario;
import com.itla.autobuses.service.UsuarioService;
import com.itla.autobuses.util.NegocioException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioPanel extends JPanel {

    private final UsuarioService servicio = new UsuarioService();
    private List<Usuario> datos = new ArrayList<>();
    private Usuario actual = null;

    private final DefaultTableModel modelo = new DefaultTableModel(
            new String[]{"Id", "Usuario", "Rol", "Activo"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable tabla = new JTable(modelo);

    private final JTextField txtUsername = new JTextField();
    private final JPasswordField txtClave = new JPasswordField();
    private final JComboBox<Rol> cmbRol = new JComboBox<>();
    private final JCheckBox chkActivo = new JCheckBox("Activo", true);

    public UsuarioPanel() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        JPanel form = new JPanel(new GridLayout(2, 4, 8, 6));
        form.add(new JLabel("Usuario:")); form.add(txtUsername);
        form.add(new JLabel("Contrasena (solo al crear):")); form.add(txtClave);
        form.add(new JLabel("Rol:")); form.add(cmbRol);
        form.add(new JLabel("Estado:")); form.add(chkActivo);

        JButton btnNuevo = new JButton("Nuevo");
        JButton btnGuardar = new JButton("Guardar");
        JButton btnClave = new JButton("Restablecer contrasena");
        JButton btnDesactivar = new JButton("Desactivar");
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        botones.add(btnNuevo);
        botones.add(btnGuardar);
        botones.add(btnClave);
        botones.add(btnDesactivar);

        JPanel sur = new JPanel(new BorderLayout(0, 6));
        sur.setBorder(BorderFactory.createTitledBorder("Datos del usuario"));
        sur.add(form, BorderLayout.CENTER);
        sur.add(botones, BorderLayout.SOUTH);
        add(sur, BorderLayout.SOUTH);

        btnNuevo.addActionListener(e -> limpiar());
        btnGuardar.addActionListener(e -> guardar());
        btnClave.addActionListener(e -> restablecer());
        btnDesactivar.addActionListener(e -> desactivar());
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                seleccionar();
            }
        });

        try {
            for (Rol r : servicio.listarRoles()) {
                cmbRol.addItem(r);
            }
        } catch (NegocioException ex) {
            aviso(ex.getMessage());
        }
        cargar();
    }

    private void cargar() {
        try {
            datos = servicio.listar();
            modelo.setRowCount(0);
            for (Usuario u : datos) {
                modelo.addRow(new Object[]{u.getId(), u.getUsername(), u.getRolNombre(),
                        u.isActivo() ? "Si" : "No"});
            }
        } catch (NegocioException ex) {
            aviso(ex.getMessage());
        }
    }

    private void seleccionar() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            return;
        }
        actual = datos.get(fila);
        txtUsername.setText(actual.getUsername());
        txtUsername.setEnabled(false);
        txtClave.setText("");
        txtClave.setEnabled(false);
        chkActivo.setSelected(actual.isActivo());
        for (int i = 0; i < cmbRol.getItemCount(); i++) {
            if (cmbRol.getItemAt(i).getId() == actual.getIdRol()) {
                cmbRol.setSelectedIndex(i);
            }
        }
    }

    private void limpiar() {
        actual = null;
        tabla.clearSelection();
        txtUsername.setText("");
        txtUsername.setEnabled(true);
        txtClave.setText("");
        txtClave.setEnabled(true);
        chkActivo.setSelected(true);
        if (cmbRol.getItemCount() > 0) {
            cmbRol.setSelectedIndex(0);
        }
        txtUsername.requestFocus();
    }

    private void guardar() {
        Rol rol = (Rol) cmbRol.getSelectedItem();
        if (rol == null) {
            aviso("No hay roles disponibles.");
            return;
        }
        try {
            if (actual == null) {
                servicio.crear(txtUsername.getText(), new String(txtClave.getPassword()), rol.getId());
            } else {
                servicio.actualizar(actual, rol.getId(), chkActivo.isSelected());
            }
            JOptionPane.showMessageDialog(this, "Usuario guardado correctamente.");
            limpiar();
            cargar();
        } catch (NegocioException ex) {
            aviso(ex.getMessage());
        }
    }

    private void restablecer() {
        if (actual == null) {
            aviso("Selecciona un usuario de la tabla.");
            return;
        }
        JPasswordField campo = new JPasswordField();
        int r = JOptionPane.showConfirmDialog(this, campo,
                "Nueva contrasena para " + actual.getUsername() + " (min. 8 caracteres)",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (r != JOptionPane.OK_OPTION) {
            return;
        }
        try {
            servicio.restablecerPassword(actual, new String(campo.getPassword()));
            JOptionPane.showMessageDialog(this, "Contrasena restablecida.");
        } catch (NegocioException ex) {
            aviso(ex.getMessage());
        }
    }

    private void desactivar() {
        if (actual == null) {
            aviso("Selecciona un usuario de la tabla.");
            return;
        }
        int r = JOptionPane.showConfirmDialog(this,
                "Desactivar al usuario " + actual.getUsername() + "?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (r != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            servicio.desactivar(actual);
            limpiar();
            cargar();
        } catch (NegocioException ex) {
            aviso(ex.getMessage());
        }
    }

    private void aviso(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Aviso", JOptionPane.WARNING_MESSAGE);
    }
}
