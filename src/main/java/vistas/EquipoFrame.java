package vistas;

import com.torneos.dominio.Equipo;
import com.torneos.persistencia.EquipoDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class EquipoFrame extends JFrame {

    private MenuAdminFrame parent;
    private EquipoDAO equipoDAO = new EquipoDAO();

    private JTable tblEquipos;
    private DefaultTableModel tableModel;

    private JTextField txtNombre;

    private JButton btnNuevo;
    private JButton btnEditar;
    private JButton btnEliminar;

    private Equipo equipoSeleccionado;

    public EquipoFrame(MenuAdminFrame parent) {
        this.parent = parent;

        setTitle("Gestión de Equipos");
        setSize(700, 450);
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
        headerPanel.setBackground(new Color(25, 135, 84));
        headerPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel lblTitle = new JLabel("Mantenimiento de Equipos");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);
        headerPanel.add(lblTitle, BorderLayout.WEST);

        JButton btnVolver = new JButton("Volver al Menú");
        btnVolver.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnVolver.addActionListener(e -> volverAlMenu());
        headerPanel.add(btnVolver, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Center Split Panel (Table on left, Form on right)
        JPanel centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 10, 10, 10);

        // 1. Table
        tblEquipos = new JTable();
        tblEquipos.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblEquipos.getSelectionModel().addListSelectionListener(e -> seleccionarFila());
        JScrollPane scrollPane = new JScrollPane(tblEquipos);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.6;
        gbc.weighty = 1.0;
        centerPanel.add(scrollPane, gbc);

        // 2. Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Datos del Equipo"));
        formPanel.setBackground(new Color(245, 247, 250));
        GridBagConstraints fgbc = new GridBagConstraints();
        fgbc.insets = new Insets(8, 8, 8, 8);
        fgbc.fill = GridBagConstraints.HORIZONTAL;

        fgbc.gridx = 0;
        fgbc.gridy = 0;
        formPanel.add(new JLabel("Nombre:"), fgbc);

        txtNombre = new JTextField(15);
        fgbc.gridx = 1;
        formPanel.add(txtNombre, fgbc);

        // Buttons for Form
        JPanel formBtns = new JPanel(new GridLayout(1, 3, 5, 5));
        formBtns.setBackground(new Color(245, 247, 250));
        btnNuevo = new JButton("Nuevo");
        btnEditar = new JButton("Editar");
        btnEliminar = new JButton("Eliminar");

        btnNuevo.addActionListener(e -> nuevoEquipo());
        btnEditar.addActionListener(e -> editarEquipo());
        btnEliminar.addActionListener(e -> eliminarEquipo());

        formBtns.add(btnNuevo);
        formBtns.add(btnEditar);
        formBtns.add(btnEliminar);

        fgbc.gridx = 0;
        fgbc.gridy = 1;
        fgbc.gridwidth = 2;
        fgbc.insets = new Insets(15, 5, 5, 5);
        formPanel.add(formBtns, fgbc);

        gbc.gridx = 1;
        gbc.weightx = 0.4;
        centerPanel.add(formPanel, gbc);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        add(mainPanel);

        // Initialize Table
        String[] cols = {"ID", "Nombre"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblEquipos.setModel(tableModel);

        cargarEquipos();
        limpiarFormulario();
    }

    private void volverAlMenu() {
        parent.setVisible(true);
        dispose();
    }

    private void cargarEquipos() {
        tableModel.setRowCount(0);
        try {
            List<Equipo> lista = equipoDAO.obtenerTodos();
            for (Equipo e : lista) {
                tableModel.addRow(new Object[]{e.getId(), e.getNombre()});
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar equipos: " + ex.getMessage());
        }
    }

    private void seleccionarFila() {
        int selectedRow = tblEquipos.getSelectedRow();
        if (selectedRow >= 0) {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            String nombre = (String) tableModel.getValueAt(selectedRow, 1);

            equipoSeleccionado = new Equipo(id, nombre);
            txtNombre.setText(nombre);

            btnEditar.setEnabled(true);
            btnEliminar.setEnabled(true);
        } else {
            equipoSeleccionado = null;
        }
    }

    private void limpiarFormulario() {
        txtNombre.setText("");
        tblEquipos.clearSelection();
        equipoSeleccionado = null;

        btnEditar.setEnabled(false);
        btnEliminar.setEnabled(false);
    }

    private void nuevoEquipo() {
        String nombre = txtNombre.getText().trim();

        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar el nombre del equipo.");
            return;
        }

        Equipo eq = new Equipo();
        eq.setNombre(nombre);

        boolean success = equipoDAO.insertar(eq);
        if (success) {
            JOptionPane.showMessageDialog(this, "Equipo guardado con éxito.");
            cargarEquipos();
            limpiarFormulario();
        } else {
            JOptionPane.showMessageDialog(this, "Error al guardar el equipo.");
        }
    }

    private void editarEquipo() {
        if (equipoSeleccionado == null) return;

        String nombre = txtNombre.getText().trim();

        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar el nombre del equipo.");
            return;
        }

        equipoSeleccionado.setNombre(nombre);

        boolean success = equipoDAO.actualizar(equipoSeleccionado);
        if (success) {
            JOptionPane.showMessageDialog(this, "Equipo actualizado con éxito.");
            cargarEquipos();
            limpiarFormulario();
        } else {
            JOptionPane.showMessageDialog(this, "Error al actualizar el equipo.");
        }
    }

    private void eliminarEquipo() {
        if (equipoSeleccionado == null) return;

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de eliminar el equipo " + equipoSeleccionado.getNombre() + "?",
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = equipoDAO.eliminar(equipoSeleccionado.getId());
            if (success) {
                JOptionPane.showMessageDialog(this, "Equipo eliminado con éxito.");
                cargarEquipos();
                limpiarFormulario();
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar el equipo (podría estar vinculado a partidas o jugadores).");
            }
        }
    }
}
