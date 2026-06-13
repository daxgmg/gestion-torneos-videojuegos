package com.torneos.vistas;

import com.torneos.dominio.Torneo;
import com.torneos.dominio.User;
import com.torneos.persistencia.TorneoDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TorneoFrame extends JFrame {

    private User usuarioActual;
    private TorneoDAO torneoDAO = new TorneoDAO();
    private JTable tabla;
    private DefaultTableModel modeloTabla;

    public TorneoFrame(User user) {
        this.usuarioActual = user;
        initComponents();
        cargarDatos();
    }

    private void initComponents() {
        setTitle("Gestión de Torneos");
        setSize(750, 500);
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

        JLabel lblTitulo = new JLabel("Gestión de Torneos");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitulo.setForeground(new Color(50, 50, 50));
        headerPanel.add(lblTitulo);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Tabla
        String[] columnas = { "ID", "Nombre", "Fecha Inicio", "Fecha Fin", "Estado" };
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
        JScrollPane scroll = new JScrollPane(tabla);
        mainPanel.add(scroll, BorderLayout.CENTER);

        // Botones
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
        List<Torneo> lista = torneoDAO.obtenerTodos();
        for (Torneo t : lista) {
            modeloTabla.addRow(new Object[] {
                    t.getId(), t.getNombre(), t.getFechaInicio(), t.getFechaFin(), t.getEstado()
            });
        }
    }

    private void mostrarFormulario(Torneo torneo) {
        JDialog dialog = new JDialog(this, torneo == null ? "Nuevo Torneo" : "Editar Torneo", true);
        dialog.setSize(400, 320);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(15, 20, 15, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);

        JTextField txtNombre = crearTextField(torneo != null ? torneo.getNombre() : "");
        JTextField txtFechaInicio = crearTextField(torneo != null ? torneo.getFechaInicio() : "");
        JTextField txtFechaFin = crearTextField(torneo != null ? torneo.getFechaFin() : "");
        String[] estados = { "ACTIVO", "FINALIZADO" };
        JComboBox<String> cmbEstado = new JComboBox<>(estados);
        cmbEstado.setFont(new Font("Arial", Font.PLAIN, 14));
        if (torneo != null)
            cmbEstado.setSelectedItem(torneo.getEstado());

        agregarFila(panel, gbc, 0, "Nombre:", txtNombre);
        agregarFila(panel, gbc, 1, "Fecha Inicio (yyyy-MM-dd):", txtFechaInicio);
        agregarFila(panel, gbc, 2, "Fecha Fin (yyyy-MM-dd):", txtFechaFin);
        agregarFila(panel, gbc, 3, "Estado:", cmbEstado);

        JButton btnGuardar = crearBoton("💾 Guardar", e -> {
            Torneo t = torneo != null ? torneo : new Torneo();
            t.setNombre(txtNombre.getText());
            t.setFechaInicio(txtFechaInicio.getText());
            t.setFechaFin(txtFechaFin.getText());
            t.setEstado((String) cmbEstado.getSelectedItem());
            boolean ok = torneo == null ? torneoDAO.insertar(t) : torneoDAO.actualizar(t);
            if (ok) {
                JOptionPane.showMessageDialog(dialog, "Guardado correctamente");
                dialog.dispose();
                cargarDatos();
            } else {
                JOptionPane.showMessageDialog(dialog, "Error al guardar", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        panel.add(btnGuardar, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void editarSeleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona un torneo");
            return;
        }
        int id = (int) modeloTabla.getValueAt(fila, 0);
        Torneo t = torneoDAO.buscarPorId(id);
        if (t != null)
            mostrarFormulario(t);
    }

    private void eliminarSeleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona un torneo");
            return;
        }
        int id = (int) modeloTabla.getValueAt(fila, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "¿Eliminar este torneo?", "Confirmar",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (torneoDAO.eliminar(id))
                cargarDatos();
            else
                JOptionPane.showMessageDialog(this, "Error al eliminar", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JTextField crearTextField(String valor) {
        JTextField txt = new JTextField(valor);
        txt.setFont(new Font("Arial", Font.PLAIN, 14));
        txt.setBackground(Color.WHITE);
        txt.setForeground(Color.BLACK);
        txt.setCaretColor(Color.BLACK);
        txt.setOpaque(true);
        txt.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        txt.setPreferredSize(new Dimension(200, 32));
        return txt;
    }

    private void agregarFila(JPanel p, GridBagConstraints g, int fila, String label, JComponent comp) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Arial", Font.PLAIN, 13));
        lbl.setForeground(Color.BLACK);
        g.gridx = 0;
        g.gridy = fila;
        g.gridwidth = 1;
        p.add(lbl, g);
        g.gridx = 1;
        p.add(comp, g);
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