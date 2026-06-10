package vistas;

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

    private MenuAdminFrame parent;
    private JugadorDAO jugadorDAO = new JugadorDAO();
    private EquipoDAO equipoDAO = new EquipoDAO();
    private UserDAO userDAO = new UserDAO();

    private JTable tblJugadores;
    private DefaultTableModel tableModel;

    private JTextField txtNombre;
    private JTextField txtAlias;
    private JComboBox<Equipo> cbEquipo;
    private JComboBox<User> cbUser;

    private JButton btnNuevo;
    private JButton btnEditar;
    private JButton btnEliminar;

    private Jugador jugadorSeleccionado;

    public JugadorFrame(MenuAdminFrame parent) {
        this.parent = parent;

        setTitle("Gestión de Jugadores");
        setSize(780, 480);
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
        headerPanel.setBackground(new Color(220, 53, 69));
        headerPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel lblTitle = new JLabel("Mantenimiento de Jugadores");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);
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

        tblJugadores = new JTable();
        tblJugadores.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblJugadores.getSelectionModel().addListSelectionListener(e -> seleccionarFila());
        JScrollPane scrollPane = new JScrollPane(tblJugadores);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.6;
        gbc.weighty = 1.0;
        centerPanel.add(scrollPane, gbc);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Datos del Jugador"));
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
        formPanel.add(new JLabel("Alias:"), fgbc);

        txtAlias = new JTextField(15);
        fgbc.gridx = 1;
        formPanel.add(txtAlias, fgbc);

        fgbc.gridx = 0;
        fgbc.gridy = 2;
        formPanel.add(new JLabel("Equipo:"), fgbc);

        cbEquipo = new JComboBox<>();
        fgbc.gridx = 1;
        formPanel.add(cbEquipo, fgbc);

        fgbc.gridx = 0;
        fgbc.gridy = 3;
        formPanel.add(new JLabel("Usuario:"), fgbc);

        cbUser = new JComboBox<>();
        fgbc.gridx = 1;
        formPanel.add(cbUser, fgbc);

        JPanel formBtns = new JPanel(new GridLayout(1, 3, 5, 5));
        formBtns.setBackground(new Color(245, 247, 250));
        btnNuevo = new JButton("Nuevo");
        btnEditar = new JButton("Editar");
        btnEliminar = new JButton("Eliminar");

        btnNuevo.addActionListener(e -> nuevoJugador());
        btnEditar.addActionListener(e -> editarJugador());
        btnEliminar.addActionListener(e -> eliminarJugador());

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

        String[] cols = {"ID", "Nombre", "Alias", "Equipo", "Usuario"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblJugadores.setModel(tableModel);

        cargarEquipos();
        cargarUsuarios();
        cargarJugadores();
        limpiarFormulario();
    }

    private void volverAlMenu() {
        parent.setVisible(true);
        dispose();
    }

    private void cargarEquipos() {
        try {
            List<Equipo> lista = equipoDAO.obtenerTodos();
            cbEquipo.removeAllItems();
            for (Equipo eq : lista) {
                cbEquipo.addItem(eq);
            }
            cbEquipo.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object val, int idx, boolean isSel, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, val, idx, isSel, cellHasFocus);
                    if (val instanceof Equipo) {
                        setText(((Equipo) val).getNombre());
                    }
                    return this;
                }
            });
        } catch (Exception e) {
            System.err.println("Error al cargar equipos: " + e.getMessage());
        }
    }

    private void cargarUsuarios() {
        try {
            List<User> lista = userDAO.obtenerTodos();
            cbUser.removeAllItems();
            for (User u : lista) {
                cbUser.addItem(u);
            }
            cbUser.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object val, int idx, boolean isSel, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, val, idx, isSel, cellHasFocus);
                    if (val instanceof User) {
                        setText(((User) val).getNombre() + " (" + ((User) val).getEmail() + ")");
                    }
                    return this;
                }
            });
        } catch (Exception e) {
            System.err.println("Error al cargar usuarios: " + e.getMessage());
        }
    }

    private void cargarJugadores() {
        tableModel.setRowCount(0);
        try {
            List<Jugador> list = jugadorDAO.obtenerTodos();
            for (Jugador j : list) {
                String eqNom = j.getEquipo() != null ? j.getEquipo().getNombre() : "N/A";
                String uNom = j.getUser() != null ? j.getUser().getNombre() : "N/A";
                tableModel.addRow(new Object[]{j.getId(), j.getNombre(), j.getAlias(), eqNom, uNom});
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar jugadores: " + e.getMessage());
        }
    }

    private void seleccionarFila() {
        int selectedRow = tblJugadores.getSelectedRow();
        if (selectedRow >= 0) {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            try {
                Jugador j = jugadorDAO.buscarPorId(id);
                if (j != null) {
                    jugadorSeleccionado = j;
                    txtNombre.setText(j.getNombre());
                    txtAlias.setText(j.getAlias());

                    // Select Team
                    if (j.getEquipo() != null) {
                        for (int i = 0; i < cbEquipo.getItemCount(); i++) {
                            if (cbEquipo.getItemAt(i).getId() == j.getEquipo().getId()) {
                                cbEquipo.setSelectedIndex(i);
                                break;
                            }
                        }
                    }

                    // Select User
                    if (j.getUser() != null) {
                        for (int i = 0; i < cbUser.getItemCount(); i++) {
                            if (cbUser.getItemAt(i).getId() == j.getUser().getId()) {
                                cbUser.setSelectedIndex(i);
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
            jugadorSeleccionado = null;
        }
    }

    private void limpiarFormulario() {
        txtNombre.setText("");
        txtAlias.setText("");
        if (cbEquipo.getItemCount() > 0) cbEquipo.setSelectedIndex(0);
        if (cbUser.getItemCount() > 0) cbUser.setSelectedIndex(0);
        tblJugadores.clearSelection();
        jugadorSeleccionado = null;

        btnEditar.setEnabled(false);
        btnEliminar.setEnabled(false);
    }

    private void nuevoJugador() {
        String nombre = txtNombre.getText().trim();
        String alias = txtAlias.getText().trim();
        Equipo eq = (Equipo) cbEquipo.getSelectedItem();
        User u = (User) cbUser.getSelectedItem();

        if (nombre.isEmpty() || alias.isEmpty() || eq == null || u == null) {
            JOptionPane.showMessageDialog(this, "Debe completar todos los campos.");
            return;
        }

        Jugador j = new Jugador();
        j.setNombre(nombre);
        j.setAlias(alias);
        j.setEquipo(eq);
        j.setUser(u);

        boolean success = jugadorDAO.insertar(j);
        if (success) {
            JOptionPane.showMessageDialog(this, "Jugador guardado con éxito.");
            cargarJugadores();
            limpiarFormulario();
        } else {
            JOptionPane.showMessageDialog(this, "Error al guardar el jugador.");
        }
    }

    private void editarJugador() {
        if (jugadorSeleccionado == null) return;

        String nombre = txtNombre.getText().trim();
        String alias = txtAlias.getText().trim();
        Equipo eq = (Equipo) cbEquipo.getSelectedItem();
        User u = (User) cbUser.getSelectedItem();

        if (nombre.isEmpty() || alias.isEmpty() || eq == null || u == null) {
            JOptionPane.showMessageDialog(this, "Debe completar todos los campos.");
            return;
        }

        jugadorSeleccionado.setNombre(nombre);
        jugadorSeleccionado.setAlias(alias);
        jugadorSeleccionado.setEquipo(eq);
        jugadorSeleccionado.setUser(u);

        boolean success = jugadorDAO.actualizar(jugadorSeleccionado);
        if (success) {
            JOptionPane.showMessageDialog(this, "Jugador actualizado con éxito.");
            cargarJugadores();
            limpiarFormulario();
        } else {
            JOptionPane.showMessageDialog(this, "Error al actualizar el jugador.");
        }
    }

    private void eliminarJugador() {
        if (jugadorSeleccionado == null) return;

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de eliminar al jugador " + jugadorSeleccionado.getNombre() + "?",
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = jugadorDAO.eliminar(jugadorSeleccionado.getId());
            if (success) {
                JOptionPane.showMessageDialog(this, "Jugador eliminado con éxito.");
                cargarJugadores();
                limpiarFormulario();
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar el jugador.");
            }
        }
    }
}
