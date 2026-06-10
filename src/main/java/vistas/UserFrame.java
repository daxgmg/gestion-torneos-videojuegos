package vistas;

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

    private MenuAdminFrame parent;
    private UserDAO userDAO = new UserDAO();
    private RolDAO rolDAO = new RolDAO();

    private JTable tblUsers;
    private DefaultTableModel tableModel;

    private JTextField txtNombre;
    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JComboBox<Rol> cbRol;

    private JButton btnNuevo;
    private JButton btnEditar;
    private JButton btnEliminar;

    private User userSeleccionado;

    public UserFrame(MenuAdminFrame parent) {
        this.parent = parent;

        setTitle("Gestión de Usuarios");
        setSize(750, 450);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                volverAlMenu();
            }
        });

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 247, 250));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(255, 193, 7));
        headerPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel lblTitle = new JLabel("Mantenimiento de Usuarios");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(new Color(33, 37, 41));
        headerPanel.add(lblTitle, BorderLayout.WEST);

        JButton btnVolver = new JButton("Volver al Menú");
        btnVolver.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnVolver.addActionListener(e -> volverAlMenu());
        headerPanel.add(btnVolver, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 10, 10, 10);

        tblUsers = new JTable();
        tblUsers.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblUsers.getSelectionModel().addListSelectionListener(e -> seleccionarFila());
        JScrollPane scrollPane = new JScrollPane(tblUsers);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.6;
        gbc.weighty = 1.0;
        centerPanel.add(scrollPane, gbc);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Datos del Usuario"));
        formPanel.setBackground(new Color(245, 247, 250));
        GridBagConstraints fgbc = new GridBagConstraints();
        fgbc.insets = new Insets(6, 6, 6, 6);
        fgbc.fill = GridBagConstraints.HORIZONTAL;

        fgbc.gridx = 0;
        fgbc.gridy = 0;
        formPanel.add(new JLabel("Nombre:"), fgbc);

        txtNombre = new JTextField(15);
        fgbc.gridx = 1;
        formPanel.add(txtNombre, fgbc);

        fgbc.gridx = 0;
        fgbc.gridy = 1;
        formPanel.add(new JLabel("Email:"), fgbc);

        txtEmail = new JTextField(15);
        fgbc.gridx = 1;
        formPanel.add(txtEmail, fgbc);

        fgbc.gridx = 0;
        fgbc.gridy = 2;
        formPanel.add(new JLabel("Contraseña:"), fgbc);

        txtPassword = new JPasswordField(15);
        fgbc.gridx = 1;
        formPanel.add(txtPassword, fgbc);

        fgbc.gridx = 0;
        fgbc.gridy = 3;
        formPanel.add(new JLabel("Rol:"), fgbc);

        cbRol = new JComboBox<>();
        fgbc.gridx = 1;
        formPanel.add(cbRol, fgbc);

        JPanel formBtns = new JPanel(new GridLayout(1, 3, 5, 5));
        formBtns.setBackground(new Color(245, 247, 250));
        btnNuevo = new JButton("Nuevo");
        btnEditar = new JButton("Editar");
        btnEliminar = new JButton("Eliminar");

        btnNuevo.addActionListener(e -> nuevoUsuario());
        btnEditar.addActionListener(e -> editarUsuario());
        btnEliminar.addActionListener(e -> eliminarUsuario());

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

        String[] cols = {"ID", "Nombre", "Email", "Rol"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblUsers.setModel(tableModel);

        cargarRoles();
        cargarUsuarios();
        limpiarFormulario();
    }

    private void volverAlMenu() {
        parent.setVisible(true);
        dispose();
    }

    private void cargarRoles() {
        try {
            List<Rol> roles = rolDAO.obtenerTodos();
            cbRol.removeAllItems();
            for (Rol r : roles) {
                cbRol.addItem(r);
            }
            cbRol.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object val, int idx, boolean isSel, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, val, idx, isSel, cellHasFocus);
                    if (val instanceof Rol) {
                        setText(((Rol) val).getNombre());
                    }
                    return this;
                }
            });
        } catch (Exception e) {
            System.err.println("Error al cargar roles: " + e.getMessage());
        }
    }

    private void cargarUsuarios() {
        tableModel.setRowCount(0);
        try {
            List<User> list = userDAO.obtenerTodos();
            for (User u : list) {
                String rolNom = u.getRol() != null ? u.getRol().getNombre() : "N/A";
                tableModel.addRow(new Object[]{u.getId(), u.getNombre(), u.getEmail(), rolNom});
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar usuarios: " + e.getMessage());
        }
    }

    private void seleccionarFila() {
        int selectedRow = tblUsers.getSelectedRow();
        if (selectedRow >= 0) {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            try {
                User u = userDAO.buscarPorId(id);
                if (u != null) {
                    userSeleccionado = u;
                    txtNombre.setText(u.getNombre());
                    txtEmail.setText(u.getEmail());
                    txtPassword.setText(u.getPassword());
                    
                    // Match rol in combobox
                    if (u.getRol() != null) {
                        for (int i = 0; i < cbRol.getItemCount(); i++) {
                            if (cbRol.getItemAt(i).getId() == u.getRol().getId()) {
                                cbRol.setSelectedIndex(i);
                                break;
                            }
                        }
                    }
                    btnEditar.setEnabled(true);
                    btnEliminar.setEnabled(true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            userSeleccionado = null;
        }
    }

    private void limpiarFormulario() {
        txtNombre.setText("");
        txtEmail.setText("");
        txtPassword.setText("");
        if (cbRol.getItemCount() > 0) cbRol.setSelectedIndex(0);
        tblUsers.clearSelection();
        userSeleccionado = null;

        btnEditar.setEnabled(false);
        btnEliminar.setEnabled(false);
    }

    private void nuevoUsuario() {
        String nombre = txtNombre.getText().trim();
        String email = txtEmail.getText().trim();
        String pwd = new String(txtPassword.getPassword());
        Rol rol = (Rol) cbRol.getSelectedItem();

        if (nombre.isEmpty() || email.isEmpty() || pwd.isEmpty() || rol == null) {
            JOptionPane.showMessageDialog(this, "Debe completar todos los campos.");
            return;
        }

        User u = new User();
        u.setNombre(nombre);
        u.setEmail(email);
        u.setPassword(pwd);
        u.setRol(rol);

        boolean success = userDAO.insertar(u);
        if (success) {
            JOptionPane.showMessageDialog(this, "Usuario guardado con éxito.");
            cargarUsuarios();
            limpiarFormulario();
        } else {
            JOptionPane.showMessageDialog(this, "Error al guardar el usuario.");
        }
    }

    private void editarUsuario() {
        if (userSeleccionado == null) return;

        String nombre = txtNombre.getText().trim();
        String email = txtEmail.getText().trim();
        String pwd = new String(txtPassword.getPassword());
        Rol rol = (Rol) cbRol.getSelectedItem();

        if (nombre.isEmpty() || email.isEmpty() || pwd.isEmpty() || rol == null) {
            JOptionPane.showMessageDialog(this, "Debe completar todos los campos.");
            return;
        }

        userSeleccionado.setNombre(nombre);
        userSeleccionado.setEmail(email);
        userSeleccionado.setPassword(pwd);
        userSeleccionado.setRol(rol);

        boolean success = userDAO.actualizar(userSeleccionado);
        if (success) {
            JOptionPane.showMessageDialog(this, "Usuario actualizado con éxito.");
            cargarUsuarios();
            limpiarFormulario();
        } else {
            JOptionPane.showMessageDialog(this, "Error al actualizar el usuario.");
        }
    }

    private void eliminarUsuario() {
        if (userSeleccionado == null) return;

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de eliminar al usuario " + userSeleccionado.getNombre() + "?",
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = userDAO.eliminar(userSeleccionado.getId());
            if (success) {
                JOptionPane.showMessageDialog(this, "Usuario eliminado con éxito.");
                cargarUsuarios();
                limpiarFormulario();
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar el usuario (podría tener registros de jugador asociados).");
            }
        }
    }
}
