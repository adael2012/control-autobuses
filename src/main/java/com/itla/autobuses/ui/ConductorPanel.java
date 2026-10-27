package com.itla.autobuses.ui;

import com.itla.autobuses.model.Conductor;
import com.itla.autobuses.service.ConductorService;
import com.itla.autobuses.util.NegocioException;
import com.itla.autobuses.util.Sesion;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class ConductorPanel extends JPanel {

    private final ConductorService servicio = new ConductorService();
    private List<Conductor> datos = new ArrayList<>();
    private Conductor actual = new Conductor();

    private final JTextField txtBuscar = new JTextField(20);
    private final DefaultTableModel modelo = new DefaultTableModel(
            new String[]{"Id", "Cedula", "Nombre", "Apellido", "Telefono", "Licencia", "Vence", "Activo"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable tabla = new JTable(modelo);

    private final JTextField txtCedula = new JTextField();
    private final JTextField txtNombre = new JTextField();
    private final JTextField txtApellido = new JTextField();
    private final JTextField txtTelefono = new JTextField();
    private final JTextField txtLicencia = new JTextField();
    private final JTextField txtVence = new JTextField();
    private final JCheckBox chkActivo = new JCheckBox("Activo", true);

    private final JButton btnNuevo = new JButton("Nuevo");
    private final JButton btnGuardar = new JButton("Guardar");
    private final JButton btnDesactivar = new JButton("Desactivar");

    public ConductorPanel() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnBuscar = new JButton("Buscar");
        JButton btnTodos = new JButton("Mostrar todos");
        JPanel norte = new JPanel(new FlowLayout(FlowLayout.LEFT));
        norte.add(new JLabel("Buscar (cedula, nombre o licencia):"));
        norte.add(txtBuscar);
        norte.add(btnBuscar);
        norte.add(btnTodos);
        add(norte, BorderLayout.NORTH);

        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setAutoCreateRowSorter(true);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        JPanel form = new JPanel(new GridLayout(4, 4, 8, 6));
        form.add(new JLabel("Cedula (11 digitos):")); form.add(txtCedula);
        form.add(new JLabel("Nombre:")); form.add(txtNombre);
        form.add(new JLabel("Apellido:")); form.add(txtApellido);
        form.add(new JLabel("Telefono:")); form.add(txtTelefono);
        form.add(new JLabel("No. de licencia:")); form.add(txtLicencia);
        form.add(new JLabel("Vence (aaaa-mm-dd):")); form.add(txtVence);
        form.add(new JLabel("Estado:")); form.add(chkActivo);
        form.add(new JLabel("")); form.add(new JLabel(""));

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        botones.add(btnNuevo);
        botones.add(btnGuardar);
        botones.add(btnDesactivar);

        JPanel sur = new JPanel(new BorderLayout(0, 6));
        sur.setBorder(BorderFactory.createTitledBorder("Datos del conductor"));
        sur.add(form, BorderLayout.CENTER);
        sur.add(botones, BorderLayout.SOUTH);
        add(sur, BorderLayout.SOUTH);

        btnBuscar.addActionListener(e -> cargar(txtBuscar.getText()));
        txtBuscar.addActionListener(e -> cargar(txtBuscar.getText()));
        btnTodos.addActionListener(e -> { txtBuscar.setText(""); cargar(""); });
        btnNuevo.addActionListener(e -> limpiar());
        btnGuardar.addActionListener(e -> guardar());
        btnDesactivar.addActionListener(e -> desactivar());
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                seleccionar();
            }
        });

        if (!Sesion.getInstancia().esAdmin()) {
            for (JComponent c : new JComponent[]{txtCedula, txtNombre, txtApellido, txtTelefono,
                    txtLicencia, txtVence, chkActivo}) {
                c.setEnabled(false);
            }
            botones.setVisible(false);
        }
        cargar("");
    }

    private void cargar(String texto) {
        try {
            datos = servicio.buscar(texto);
            modelo.setRowCount(0);
            for (Conductor k : datos) {
                modelo.addRow(new Object[]{k.getId(), k.getCedula(), k.getNombre(), k.getApellido(),
                        k.getTelefono(), k.getNumeroLicencia(), k.getVencimientoLicencia(),
                        k.isActivo() ? "Si" : "No"});
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
        int id = (int) modelo.getValueAt(tabla.convertRowIndexToModel(fila), 0);
        for (Conductor k : datos) {
            if (k.getId() == id) {
                actual = k;
                txtCedula.setText(k.getCedula());
                txtNombre.setText(k.getNombre());
                txtApellido.setText(k.getApellido());
                txtTelefono.setText(k.getTelefono());
                txtLicencia.setText(k.getNumeroLicencia());
                txtVence.setText(k.getVencimientoLicencia().toString());
                chkActivo.setSelected(k.isActivo());
                return;
            }
        }
    }

    private void limpiar() {
        actual = new Conductor();
        tabla.clearSelection();
        for (JTextField t : new JTextField[]{txtCedula, txtNombre, txtApellido, txtTelefono,
                txtLicencia, txtVence}) {
            t.setText("");
        }
        chkActivo.setSelected(true);
        txtCedula.requestFocus();
    }

    private void guardar() {
        try {
            actual.setCedula(txtCedula.getText().trim());
            actual.setNombre(txtNombre.getText().trim());
            actual.setApellido(txtApellido.getText().trim());
            actual.setTelefono(txtTelefono.getText().trim());
            actual.setNumeroLicencia(txtLicencia.getText().trim());
            actual.setActivo(chkActivo.isSelected());
            try {
                actual.setVencimientoLicencia(LocalDate.parse(txtVence.getText().trim()));
            } catch (DateTimeParseException ex) {
                throw new NegocioException("La fecha debe tener el formato aaaa-mm-dd (ej. 2028-05-30).");
            }
            servicio.guardar(actual);
            JOptionPane.showMessageDialog(this, "Conductor guardado correctamente.");
            limpiar();
            cargar(txtBuscar.getText());
        } catch (NegocioException ex) {
            aviso(ex.getMessage());
        }
    }

    private void desactivar() {
        if (actual.getId() == 0) {
            aviso("Selecciona un conductor de la tabla.");
            return;
        }
        int r = JOptionPane.showConfirmDialog(this,
                "Desactivar a " + actual.getNombre() + " " + actual.getApellido() + "?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (r != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            servicio.desactivar(actual);
            limpiar();
            cargar(txtBuscar.getText());
        } catch (NegocioException ex) {
            aviso(ex.getMessage());
        }
    }

    private void aviso(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Aviso", JOptionPane.WARNING_MESSAGE);
    }
}
