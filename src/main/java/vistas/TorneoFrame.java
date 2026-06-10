package vistas;

import com.torneos.dominio.Torneo;
import com.torneos.persistencia.TorneoDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TorneoFrame extends JFrame {

    private MenuAdminFrame parent;
    private TorneoDAO torneoDAO = new TorneoDAO();

    private JTable tblTorneos;
    private DefaultTableModel tableModel;

    private JTextField txtNombre;
    private JTextField txtFechaInicio;
    private JTextField txtFechaFin;
    private JComboBox<String> cbEstado;

    private JButton btnNuevo;
    private JButton btnEditar;
    private JButton btnEliminar;

    private Torneo torneoSeleccionado;

    public TorneoFrame(MenuAdminFrame parent) {
        this.parent = parent;

        setTitle("Gestión de Torneos");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);

        // Window listener to return to menu on close
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                volverAlMenu();
            }
        });

        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 247, 250));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(13, 110, 253));
        headerPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel lblTitle = new JLabel("Mantenimiento de Torneos");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);
        headerPanel.add(lblTitle, BorderLayout.WEST);

        JButton btnVolver = new JButton("Volver al Menú");
        btnVolver.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnVolver.addActionListener(e -> volverAlMenu());
        headerPanel.add(btnVolver, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Center Split Panel (Table on top/left, Form on bottom/right)
        JPanel centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 10, 10, 10);

        // 1. Table of Tournaments
        tblTorneos = new JTable();
        tblTorneos.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblTorneos.getSelectionModel().addListSelectionListener(e -> seleccionarFila());
        JScrollPane scrollPane = new JScrollPane(tblTorneos);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.6;
        gbc.weighty = 1.0;
        centerPanel.add(scrollPane, gbc);

        // 2. Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Datos del Torneo"));
        formPanel.setBackground(new Color(245, 247, 250));
        GridBagConstraints fgbc = new GridBagConstraints();
        fgbc.insets = new Insets(5, 5, 5, 5);
        fgbc.fill = GridBagConstraints.HORIZONTAL;

        fgbc.gridx = 0;
        fgbc.gridy = 0;
        formPanel.add(new JLabel("Nombre:"), fgbc);

        txtNombre = new JTextField(15);
        fgbc.gridx = 1;
        formPanel.add(txtNombre, fgbc);

        fgbc.gridx = 0;
        fgbc.gridy = 1;
        formPanel.add(new JLabel("Fecha Inicio (yyyy-MM-dd):"), fgbc);

        txtFechaInicio = new JTextField(15);
        fgbc.gridx = 1;
        formPanel.add(txtFechaInicio, fgbc);

        fgbc.gridx = 0;
        fgbc.gridy = 2;
        formPanel.add(new JLabel("Fecha Fin (yyyy-MM-dd):"), fgbc);

        txtFechaFin = new JTextField(15);
        fgbc.gridx = 1;
        formPanel.add(txtFechaFin, fgbc);

        fgbc.gridx = 0;
        fgbc.gridy = 3;
        formPanel.add(new JLabel("Estado:"), fgbc);

        cbEstado = new JComboBox<>(new String[]{"ACTIVO", "INACTIVO", "PENDIENTE", "FINALIZADO"});
        fgbc.gridx = 1;
        formPanel.add(cbEstado, fgbc);

        // Buttons for Form
        JPanel formBtns = new JPanel(new GridLayout(1, 3, 5, 5));
        formBtns.setBackground(new Color(245, 247, 250));
        btnNuevo = new JButton("Nuevo");
        btnEditar = new JButton("Editar");
        btnEliminar = new JButton("Eliminar");

        btnNuevo.addActionListener(e -> nuevoTorneo());
        btnEditar.addActionListener(e -> editarTorneo());
        btnEliminar.addActionListener(e -> eliminarTorneo());

        formBtns.add(btnNuevo);
        formBtns.add(btnEditar);
        formBtns.add(btnEliminar);

        fgbc.gridx = 0;
        fgbc.gridy = 4;
        fgbc.gridwidth = 2;
        fgbc.insets = new Insets(15, 5, 5, 5);
        formPanel.add(formBtns, fgbc);

        gbc.gridx = 1;
        gbc.weightx = 0.4;
        centerPanel.add(formPanel, gbc);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        add(mainPanel);

        // Initialize Table
        String[] cols = {"ID", "Nombre", "Fecha Inicio", "Fecha Fin", "Estado"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblTorneos.setModel(tableModel);

        cargarTorneos();
        limpiarFormulario();
    }

    private void volverAlMenu() {
        parent.setVisible(true);
        dispose();
    }

    private void cargarTorneos() {
        tableModel.setRowCount(0);
        try {
            List<Torneo> lista = torneoDAO.obtenerTodos();
            for (Torneo t : lista) {
                tableModel.addRow(new Object[]{t.getId(), t.getNombre(), t.getFechaInicio(), t.getFechaFin(), t.getEstado()});
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar torneos: " + e.getMessage());
        }
    }

    private void seleccionarFila() {
        int selectedRow = tblTorneos.getSelectedRow();
        if (selectedRow >= 0) {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            String nombre = (String) tableModel.getValueAt(selectedRow, 1);
            String fechaInicio = (String) tableModel.getValueAt(selectedRow, 2);
            String fechaFin = (String) tableModel.getValueAt(selectedRow, 3);
            String estado = (String) tableModel.getValueAt(selectedRow, 4);

            torneoSeleccionado = new Torneo(id, nombre, fechaInicio, fechaFin, estado);

            txtNombre.setText(nombre);
            txtFechaInicio.setText(fechaInicio);
            txtFechaFin.setText(fechaFin);
            cbEstado.setSelectedItem(estado);

            btnEditar.setEnabled(true);
            btnEliminar.setEnabled(true);
        } else {
            torneoSeleccionado = null;
        }
    }

    private void limpiarFormulario() {
        txtNombre.setText("");
        txtFechaInicio.setText("");
        txtFechaFin.setText("");
        cbEstado.setSelectedIndex(0);
        tblTorneos.clearSelection();
        torneoSeleccionado = null;

        btnEditar.setEnabled(false);
        btnEliminar.setEnabled(false);
    }

    private void nuevoTorneo() {
        String nombre = txtNombre.getText().trim();
        String fechaInicio = txtFechaInicio.getText().trim();
        String fechaFin = txtFechaFin.getText().trim();
        String estado = (String) cbEstado.getSelectedItem();

        if (nombre.isEmpty() || fechaInicio.isEmpty() || fechaFin.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe completar todos los campos.");
            return;
        }

        // Validate format
        if (!validarFormatoFecha(fechaInicio) || !validarFormatoFecha(fechaFin)) {
            JOptionPane.showMessageDialog(this, "Las fechas deben tener el formato yyyy-MM-dd");
            return;
        }

        Torneo t = new Torneo();
        t.setNombre(nombre);
        t.setFechaInicio(fechaInicio);
        t.setFechaFin(fechaFin);
        t.setEstado(estado);

        boolean success = torneoDAO.insertar(t);
        if (success) {
            JOptionPane.showMessageDialog(this, "Torneo guardado con éxito.");
            cargarTorneos();
            limpiarFormulario();
        } else {
            JOptionPane.showMessageDialog(this, "Error al guardar el torneo.");
        }
    }

    private void editarTorneo() {
        if (torneoSeleccionado == null) return;

        String nombre = txtNombre.getText().trim();
        String fechaInicio = txtFechaInicio.getText().trim();
        String fechaFin = txtFechaFin.getText().trim();
        String estado = (String) cbEstado.getSelectedItem();

        if (nombre.isEmpty() || fechaInicio.isEmpty() || fechaFin.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe completar todos los campos.");
            return;
        }

        if (!validarFormatoFecha(fechaInicio) || !validarFormatoFecha(fechaFin)) {
            JOptionPane.showMessageDialog(this, "Las fechas deben tener el formato yyyy-MM-dd");
            return;
        }

        torneoSeleccionado.setNombre(nombre);
        torneoSeleccionado.setFechaInicio(fechaInicio);
        torneoSeleccionado.setFechaFin(fechaFin);
        torneoSeleccionado.setEstado(estado);

        boolean success = torneoDAO.actualizar(torneoSeleccionado);
        if (success) {
            JOptionPane.showMessageDialog(this, "Torneo actualizado con éxito.");
            cargarTorneos();
            limpiarFormulario();
        } else {
            JOptionPane.showMessageDialog(this, "Error al actualizar el torneo.");
        }
    }

    private void eliminarTorneo() {
        if (torneoSeleccionado == null) return;

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de eliminar el torneo " + torneoSeleccionado.getNombre() + "?",
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = torneoDAO.eliminar(torneoSeleccionado.getId());
            if (success) {
                JOptionPane.showMessageDialog(this, "Torneo eliminado con éxito.");
                cargarTorneos();
                limpiarFormulario();
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar el torneo (podría tener inscripciones vinculadas).");
            }
        }
    }

    private boolean validarFormatoFecha(String fecha) {
        return fecha.matches("\\d{4}-\\d{2}-\\d{2}");
    }
}
