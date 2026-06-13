package com.torneos.vistas;

import com.torneos.dominio.Rol;
import com.torneos.dominio.User;
import com.torneos.persistencia.RolDAO;
import com.torneos.persistencia.UserDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class UserFrame extends JFrame {

    private User usuarioActual;
    private UserDAO userDAO = new UserDAO();
    private RolDAO rolDAO = new RolDAO();
    private JTable tabla;
    private DefaultTableModel modeloTabla;

    public UserFrame(User user) {
        this.usuarioActual = user;
        initComponents();
        cargarDatos();
    }

    private void initComponents() {
        setTitle("Gestión de Usuarios");
        setSize(700, 450);
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

        JLabel lblTitulo = new JLabel("Gestión de Usuarios");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitulo.setForeground(new Color(50, 50, 50));
        headerPanel.add(lblTitulo);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        String[] columnas = { "ID", "Nombre", "Email", "Rol" };
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
        for (User u : userDAO.obtenerTodos())
            modeloTabla.addRow(new Object[] {
                    u.getId(), u.getNombre(), u.getEmail(),
                    u.getRol() != null ? u.getRol().getNombre() : ""
            });
    }

    private void mostrarFormulario(User user) {
        JDialog dialog = new JDialog(this, user == null ? "Nuevo Usuario" : "Editar Usuario", true);
        dialog.setSize(400, 320);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(15, 20, 15, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);

        JTextField txtNombre = crearTextField(user != null ? user.getNombre() : "");
        JTextField txtEmail = crearTextField(user != null ? user.getEmail() : "");
        JPasswordField txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Arial", Font.PLAIN, 14));
        txtPassword.setBackground(Color.WHITE);
        txtPassword.setForeground(Color.BLACK);
        txtPassword.setCaretColor(Color.BLACK);
        txtPassword.setOpaque(true);
        txtPassword.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        txtPassword.setPreferredSize(new Dimension(200, 32));

        List<Rol> roles = rolDAO.obtenerTodos();
        JComboBox<String> cmbRol = new JComboBox<>();
        for (Rol r : roles)
            cmbRol.addItem(r.getNombre());
        cmbRol.setFont(new Font("Arial", Font.PLAIN, 14));
        if (user != null && user.getRol() != null)
            cmbRol.setSelectedItem(user.getRol().getNombre());

        agregarFila(panel, gbc, 0, "Nombre:", txtNombre);
        agregarFila(panel, gbc, 1, "Email:", txtEmail);
        agregarFila(panel, gbc, 2, "Contraseña:", txtPassword);
        agregarFila(panel, gbc, 3, "Rol:", cmbRol);

        JButton btnGuardar = crearBoton("💾 Guardar", e -> {
            String rolSeleccionado = (String) cmbRol.getSelectedItem();
            Rol rolObj = roles.stream()
                    .filter(r -> r.getNombre().equals(rolSeleccionado))
                    .findFirst().orElse(null);

            User u = user != null ? user : new User();
            u.setNombre(txtNombre.getText());
            u.setEmail(txtEmail.getText());
            if (txtPassword.getPassword().length > 0)
                u.setPassword(new String(txtPassword.getPassword()));
            u.setRol(rolObj);

            boolean ok = user == null ? userDAO.insertar(u) : userDAO.actualizar(u);
            if (ok) {
                dialog.dispose();
                cargarDatos();
            } else
                JOptionPane.showMessageDialog(dialog, "Error al guardar");
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
            JOptionPane.showMessageDialog(this, "Selecciona un usuario");
            return;
        }
        User u = userDAO.buscarPorId((int) modeloTabla.getValueAt(fila, 0));
        if (u != null)
            mostrarFormulario(u);
    }

    private void eliminarSeleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona un usuario");
            return;
        }
        int id = (int) modeloTabla.getValueAt(fila, 0);
        if (JOptionPane.showConfirmDialog(this, "¿Eliminar?", "Confirmar",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
            if (userDAO.eliminar(id))
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