package com.torneos.vistas;

import com.torneos.dominio.Equipo;
import com.torneos.dominio.Jugador;
import com.torneos.dominio.User;
import com.torneos.persistencia.EquipoDAO;
import com.torneos.persistencia.JugadorDAO;
import com.torneos.persistencia.UserDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class JugadorFrame extends JFrame {

    private User usuarioActual;
    private JugadorDAO jugadorDAO = new JugadorDAO();
    private EquipoDAO equipoDAO = new EquipoDAO();
    private UserDAO userDAO = new UserDAO();
    private JTable tabla;
    private DefaultTableModel modeloTabla;

    public JugadorFrame(User user) {
        this.usuarioActual = user;
        initComponents();
        cargarDatos();
    }

    private void initComponents() {
        setTitle("Gestión de Jugadores");
        setSize(750, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel lblTitulo = new JLabel("Gestión de Jugadores");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitulo.setForeground(new Color(50, 50, 50));
        mainPanel.add(lblTitulo, BorderLayout.NORTH);

        String[] columnas = { "ID", "Nombre", "Alias", "Equipo" };
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
        for (Jugador j : jugadorDAO.obtenerTodos())
            modeloTabla.addRow(new Object[] {
                    j.getId(), j.getNombre(), j.getAlias(),
                    j.getEquipo() != null ? j.getEquipo().getNombre() : ""
            });
    }

    private void mostrarFormulario(Jugador jugador) {
        JDialog dialog = new JDialog(this, jugador == null ? "Nuevo Jugador" : "Editar Jugador", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(15, 20, 15, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);

        JTextField txtNombre = crearTextField(jugador != null ? jugador.getNombre() : "");
        JTextField txtAlias = crearTextField(jugador != null ? jugador.getAlias() : "");

        List<Equipo> equipos = equipoDAO.obtenerTodos();
        JComboBox<String> cmbEquipo = new JComboBox<>();
        for (Equipo e : equipos)
            cmbEquipo.addItem(e.getNombre());
        cmbEquipo.setFont(new Font("Arial", Font.PLAIN, 14));
        if (jugador != null && jugador.getEquipo() != null)
            cmbEquipo.setSelectedItem(jugador.getEquipo().getNombre());

        agregarFila(panel, gbc, 0, "Nombre:", txtNombre);
        agregarFila(panel, gbc, 1, "Alias:", txtAlias);
        agregarFila(panel, gbc, 2, "Equipo:", cmbEquipo);

        JButton btnGuardar = crearBoton("💾 Guardar", e -> {
            String equipoSeleccionado = (String) cmbEquipo.getSelectedItem();
            Equipo equipoObj = equipos.stream()
                    .filter(eq -> eq.getNombre().equals(equipoSeleccionado))
                    .findFirst().orElse(null);

            Jugador j = jugador != null ? jugador : new Jugador();
            j.setNombre(txtNombre.getText());
            j.setAlias(txtAlias.getText());
            j.setEquipo(equipoObj);
            j.setUser(usuarioActual);

            boolean ok = jugador == null ? jugadorDAO.insertar(j) : jugadorDAO.actualizar(j);
            if (ok) {
                dialog.dispose();
                cargarDatos();
            } else
                JOptionPane.showMessageDialog(dialog, "Error al guardar");
        });

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        panel.add(btnGuardar, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void editarSeleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona un jugador");
            return;
        }
        Jugador j = jugadorDAO.buscarPorId((int) modeloTabla.getValueAt(fila, 0));
        if (j != null)
            mostrarFormulario(j);
    }

    private void eliminarSeleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona un jugador");
            return;
        }
        int id = (int) modeloTabla.getValueAt(fila, 0);
        if (JOptionPane.showConfirmDialog(this, "¿Eliminar?", "Confirmar",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
            if (jugadorDAO.eliminar(id))
                cargarDatos();
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