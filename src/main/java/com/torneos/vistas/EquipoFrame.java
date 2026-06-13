package com.torneos.vistas;

import com.torneos.dominio.Equipo;
import com.torneos.dominio.User;
import com.torneos.persistencia.EquipoDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class EquipoFrame extends JFrame {

    private User usuarioActual;
    private EquipoDAO equipoDAO = new EquipoDAO();
    private JTable tabla;
    private DefaultTableModel modeloTabla;

    public EquipoFrame(User user) {
        this.usuarioActual = user;
        initComponents();
        cargarDatos();
    }

    private void initComponents() {
        setTitle("Gestión de Equipos");
        setSize(600, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Título con botón de regresar
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        headerPanel.setBackground(Color.WHITE);

        JButton btnVolver = new JButton("<");
        btnVolver.setFont(new Font("Arial", Font.BOLD, 20));
        btnVolver.setForeground(Color.WHITE);
        btnVolver.setBackground(new Color(70, 130, 180));
        btnVolver.setOpaque(true);
        btnVolver.setContentAreaFilled(true);
        btnVolver.setFocusPainted(false);
        btnVolver.setBorderPainted(false);
        btnVolver.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVolver.setPreferredSize(new Dimension(50, 38));
        btnVolver.addActionListener(e -> dispose());
        headerPanel.add(btnVolver);

        JLabel lblTitulo = new JLabel("Gestión de Equipos");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitulo.setForeground(new Color(50, 50, 50));
        headerPanel.add(lblTitulo);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        String[] columnas = { "ID", "Nombre" };
        modeloTabla = new DefaultTableModel(columnas, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        tabla = new JTable(modeloTabla);
        tabla.setFont(new Font("Arial", Font.PLAIN, 13));
        tabla.setRowHeight(25);
        tabla.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        tabla.setSelectionBackground(new Color(200, 220, 255));
        mainPanel.add(new JScrollPane(tabla), BorderLayout.CENTER);

        JPanel botonesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        botonesPanel.setBackground(Color.WHITE);
        botonesPanel.add(crearBoton("➕ Nuevo", e -> mostrarFormulario(null)));
        botonesPanel.add(crearBoton("✏️ Editar", e -> editarSeleccionado()));
        botonesPanel.add(crearBotonRojo("🗑️ Eliminar", e -> eliminarSeleccionado()));
        mainPanel.add(botonesPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    private void cargarDatos() {
        modeloTabla.setRowCount(0);
        for (Equipo e : equipoDAO.obtenerTodos())
            modeloTabla.addRow(new Object[] { e.getId(), e.getNombre() });
    }

    private void mostrarFormulario(Equipo equipo) {
        JDialog dialog = new JDialog(this, equipo == null ? "Nuevo Equipo" : "Editar Equipo", true);
        dialog.setSize(350, 200);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(15, 20, 15, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);

        JTextField txtNombre = new JTextField(equipo != null ? equipo.getNombre() : "");
        txtNombre.setFont(new Font("Arial", Font.PLAIN, 14));
        txtNombre.setBackground(Color.WHITE);
        txtNombre.setForeground(Color.BLACK);
        txtNombre.setCaretColor(Color.BLACK);
        txtNombre.setOpaque(true);
        txtNombre.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        txtNombre.setPreferredSize(new Dimension(200, 32));

        JLabel lbl = new JLabel("Nombre:");
        lbl.setFont(new Font("Arial", Font.PLAIN, 14));
        lbl.setForeground(Color.BLACK);
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(lbl, gbc);
        gbc.gridx = 1;
        panel.add(txtNombre, gbc);

        JButton btnGuardar = crearBoton("💾 Guardar", e -> {
            Equipo eq = equipo != null ? equipo : new Equipo();
            eq.setNombre(txtNombre.getText());
            boolean ok = equipo == null ? equipoDAO.insertar(eq) : equipoDAO.actualizar(eq);
            if (ok) {
                dialog.dispose();
                cargarDatos();
            } else
                JOptionPane.showMessageDialog(dialog, "Error al guardar");
        });
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        panel.add(btnGuardar, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void editarSeleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona un equipo");
            return;
        }
        Equipo e = equipoDAO.buscarPorId((int) modeloTabla.getValueAt(fila, 0));
        if (e != null)
            mostrarFormulario(e);
    }

    private void eliminarSeleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona un equipo");
            return;
        }
        int id = (int) modeloTabla.getValueAt(fila, 0);
        if (JOptionPane.showConfirmDialog(this, "¿Eliminar?", "Confirmar",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
            if (equipoDAO.eliminar(id))
                cargarDatos();
    }

    private JButton crearBoton(String texto, java.awt.event.ActionListener l) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setForeground(Color.BLACK);
        btn.setBackground(new Color(235, 245, 255));
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(new Color(70, 130, 180), 2));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(l);
        return btn;
    }

    private JButton crearBotonRojo(String texto, java.awt.event.ActionListener l) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(200, 50, 50));
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(l);
        return btn;
    }
}