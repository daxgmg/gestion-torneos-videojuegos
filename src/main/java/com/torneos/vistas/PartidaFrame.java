package com.torneos.vistas;

import com.torneos.dominio.Equipo;
import com.torneos.dominio.Partida;
import com.torneos.dominio.Torneo;
import com.torneos.dominio.User;
import com.torneos.persistencia.EquipoDAO;
import com.torneos.persistencia.PartidaDAO;
import com.torneos.persistencia.TorneoDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Vista de administración para gestionar partidas (CRUD) disponible para administradores.
 * Permite registrar nuevas partidas, editar existentes y borrarlas.
 */
public class PartidaFrame extends JFrame {

    private User usuarioActual;
    private PartidaDAO partidaDAO = new PartidaDAO();
    private TorneoDAO torneoDAO = new TorneoDAO();
    private EquipoDAO equipoDAO = new EquipoDAO();

    private JTable tblPartidas;
    private DefaultTableModel model;
    private Partida selectedPartida;

    private JComboBox<ComboItem> cmbTorneo;
    private JComboBox<ComboItem> cmbEquipo1;
    private JComboBox<ComboItem> cmbEquipo2;
    private JTextField txtFecha;
    private JTextField txtResultado;

    private JButton btnNuevo;
    private JButton btnGuardar;
    private JButton btnEliminar;
    private JButton btnRegresar;

    public PartidaFrame(User user) {
        this.usuarioActual = user;
        initComponents();
        loadComboBoxes();
        loadData();
    }

    private void initComponents() {
        setTitle("Gestión de Partidas - Panel de Administración");
        setSize(850, 560);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(Color.WHITE);
        setLayout(null);

        JButton btnVolver = new JButton("<");
        btnVolver.setFont(new Font("Arial", Font.BOLD, 20));
        btnVolver.setForeground(Color.WHITE);
        btnVolver.setBackground(new Color(70, 130, 180));
        btnVolver.setOpaque(true);
        btnVolver.setContentAreaFilled(true);
        btnVolver.setFocusPainted(false);
        btnVolver.setBorderPainted(false);
        btnVolver.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVolver.setBounds(20, 15, 50, 38);
        btnVolver.addActionListener(e -> dispose());
        add(btnVolver);

        JLabel lblTitulo = new JLabel("Gestión de Partidas", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitulo.setBounds(80, 15, 710, 30);
        lblTitulo.setForeground(new Color(50, 50, 50));
        add(lblTitulo);

        // Tabla (Izquierda)
        String[] columnas = {"ID", "Torneo", "Equipo 1", "Equipo 2", "Fecha", "Resultado"};
        model = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblPartidas = new JTable(model);
        tblPartidas.setFont(new Font("Arial", Font.PLAIN, 13));
        tblPartidas.setRowHeight(25);
        tblPartidas.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        tblPartidas.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(tblPartidas);
        scrollPane.setBounds(20, 65, 510, 350);
        add(scrollPane);

        // Formulario (Derecha)
        JLabel lblTorneo = new JLabel("Torneo:");
        lblTorneo.setFont(new Font("Arial", Font.PLAIN, 14));
        lblTorneo.setBounds(550, 65, 260, 20);
        add(lblTorneo);

        cmbTorneo = new JComboBox<>();
        cmbTorneo.setFont(new Font("Arial", Font.PLAIN, 14));
        cmbTorneo.setBounds(550, 85, 260, 38); // Alto mínimo 38px
        add(cmbTorneo);

        JLabel lblEquipo1 = new JLabel("Equipo 1:");
        lblEquipo1.setFont(new Font("Arial", Font.PLAIN, 14));
        lblEquipo1.setBounds(550, 135, 260, 20);
        add(lblEquipo1);

        cmbEquipo1 = new JComboBox<>();
        cmbEquipo1.setFont(new Font("Arial", Font.PLAIN, 14));
        cmbEquipo1.setBounds(550, 155, 260, 38);
        add(cmbEquipo1);

        JLabel lblEquipo2 = new JLabel("Equipo 2:");
        lblEquipo2.setFont(new Font("Arial", Font.PLAIN, 14));
        lblEquipo2.setBounds(550, 205, 260, 20);
        add(lblEquipo2);

        cmbEquipo2 = new JComboBox<>();
        cmbEquipo2.setFont(new Font("Arial", Font.PLAIN, 14));
        cmbEquipo2.setBounds(550, 225, 260, 38);
        add(cmbEquipo2);

        JLabel lblFecha = new JLabel("Fecha (yyyy-MM-dd):");
        lblFecha.setFont(new Font("Arial", Font.PLAIN, 14));
        lblFecha.setBounds(550, 275, 260, 20);
        add(lblFecha);

        txtFecha = new JTextField();
        txtFecha.setFont(new Font("Arial", Font.PLAIN, 14));
        txtFecha.setBounds(550, 295, 260, 38);
        txtFecha.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        add(txtFecha);

        JLabel lblResultado = new JLabel("Resultado (ej. 3-1, o vacío):");
        lblResultado.setFont(new Font("Arial", Font.PLAIN, 14));
        lblResultado.setBounds(550, 345, 260, 20);
        add(lblResultado);

        txtResultado = new JTextField();
        txtResultado.setFont(new Font("Arial", Font.PLAIN, 14));
        txtResultado.setBounds(550, 365, 260, 38);
        txtResultado.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        add(txtResultado);

        // Botones de acción (Abajo)
        btnNuevo = new JButton("➕ Nuevo");
        btnNuevo.setFont(new Font("Arial", Font.BOLD, 14));
        btnNuevo.setForeground(Color.BLACK);
        btnNuevo.setBackground(new Color(235, 245, 255));
        btnNuevo.setOpaque(true);
        btnNuevo.setContentAreaFilled(true);
        btnNuevo.setBorder(BorderFactory.createLineBorder(new Color(70, 130, 180), 2));
        btnNuevo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnNuevo.setBounds(20, 445, 120, 42); // Alto mínimo 38px
        btnNuevo.addActionListener(e -> clearForm());
        add(btnNuevo);

        btnGuardar = new JButton("💾 Guardar");
        btnGuardar.setFont(new Font("Arial", Font.BOLD, 14));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setBackground(new Color(70, 130, 180));
        btnGuardar.setOpaque(true);
        btnGuardar.setContentAreaFilled(true);
        btnGuardar.setFocusPainted(false);
        btnGuardar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGuardar.setBounds(160, 445, 120, 42);
        btnGuardar.addActionListener(e -> guardarPartida());
        add(btnGuardar);

        btnEliminar = new JButton("🗑️ Eliminar");
        btnEliminar.setFont(new Font("Arial", Font.BOLD, 14));
        btnEliminar.setForeground(Color.WHITE);
        btnEliminar.setBackground(new Color(200, 50, 50));
        btnEliminar.setOpaque(true);
        btnEliminar.setContentAreaFilled(true);
        btnEliminar.setFocusPainted(false);
        btnEliminar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEliminar.setBounds(300, 445, 120, 42);
        btnEliminar.addActionListener(e -> eliminarPartida());
        add(btnEliminar);

        btnRegresar = new JButton("Regresar");
        btnRegresar.setFont(new Font("Arial", Font.BOLD, 14));
        btnRegresar.setForeground(Color.WHITE);
        btnRegresar.setBackground(new Color(150, 150, 150));
        btnRegresar.setOpaque(true);
        btnRegresar.setContentAreaFilled(true);
        btnRegresar.setFocusPainted(false);
        btnRegresar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRegresar.setBounds(690, 445, 120, 42);
        btnRegresar.addActionListener(e -> dispose());
        add(btnRegresar);

        // Listener de selección en la tabla
        tblPartidas.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tblPartidas.getSelectedRow();
                if (row >= 0) {
                    int id = (int) model.getValueAt(row, 0);
                    selectedPartida = partidaDAO.buscarPorId(id);
                    if (selectedPartida != null) {
                        txtFecha.setText(selectedPartida.getFecha());
                        txtResultado.setText(selectedPartida.getResultado() != null ? selectedPartida.getResultado() : "");

                        // Seleccionar Torneo
                        for (int i = 0; i < cmbTorneo.getItemCount(); i++) {
                            if (cmbTorneo.getItemAt(i).id == selectedPartida.getTorneo().getId()) {
                                cmbTorneo.setSelectedIndex(i);
                                break;
                            }
                        }

                        // Seleccionar Equipo 1
                        for (int i = 0; i < cmbEquipo1.getItemCount(); i++) {
                            if (cmbEquipo1.getItemAt(i).id == selectedPartida.getEquipo1().getId()) {
                                cmbEquipo1.setSelectedIndex(i);
                                break;
                            }
                        }

                        // Seleccionar Equipo 2
                        for (int i = 0; i < cmbEquipo2.getItemCount(); i++) {
                            if (cmbEquipo2.getItemAt(i).id == selectedPartida.getEquipo2().getId()) {
                                cmbEquipo2.setSelectedIndex(i);
                                break;
                            }
                        }
                    }
                }
            }
        });

        setVisible(true);
    }

    private void loadComboBoxes() {
        cmbTorneo.removeAllItems();
        List<Torneo> torneos = torneoDAO.obtenerTodos();
        for (Torneo t : torneos) {
            cmbTorneo.addItem(new ComboItem(t.getId(), t.getNombre()));
        }

        cmbEquipo1.removeAllItems();
        cmbEquipo2.removeAllItems();
        List<Equipo> equipos = equipoDAO.obtenerTodos();
        for (Equipo eq : equipos) {
            cmbEquipo1.addItem(new ComboItem(eq.getId(), eq.getNombre()));
            cmbEquipo2.addItem(new ComboItem(eq.getId(), eq.getNombre()));
        }
    }

    private void loadData() {
        model.setRowCount(0);
        List<Partida> lista = partidaDAO.obtenerTodos();
        for (Partida p : lista) {
            String torneo = p.getTorneo() != null ? p.getTorneo().getNombre() : "N/A";
            String eq1 = p.getEquipo1() != null ? p.getEquipo1().getNombre() : "N/A";
            String eq2 = p.getEquipo2() != null ? p.getEquipo2().getNombre() : "N/A";
            String resultado = p.getResultado() != null && !p.getResultado().trim().isEmpty() ? p.getResultado() : "Pendiente";
            model.addRow(new Object[]{
                    p.getId(), torneo, eq1, eq2, p.getFecha(), resultado
            });
        }
    }

    private void clearForm() {
        selectedPartida = null;
        txtFecha.setText("");
        txtResultado.setText("");
        if (cmbTorneo.getItemCount() > 0) cmbTorneo.setSelectedIndex(0);
        if (cmbEquipo1.getItemCount() > 0) cmbEquipo1.setSelectedIndex(0);
        if (cmbEquipo2.getItemCount() > 0) cmbEquipo2.setSelectedIndex(0);
        tblPartidas.clearSelection();
    }

    private void guardarPartida() {
        ComboItem torneoItem = (ComboItem) cmbTorneo.getSelectedItem();
        ComboItem eq1Item = (ComboItem) cmbEquipo1.getSelectedItem();
        ComboItem eq2Item = (ComboItem) cmbEquipo2.getSelectedItem();
        String fecha = txtFecha.getText().trim();
        String resultado = txtResultado.getText().trim();

        if (torneoItem == null || eq1Item == null || eq2Item == null || fecha.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Completa todos los campos obligatorios (fecha es requerida)", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (eq1Item.id == eq2Item.id) {
            JOptionPane.showMessageDialog(this, "Equipo 1 y Equipo 2 deben ser diferentes", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validar formato fecha
        if (!fecha.matches("\\d{4}-\\d{2}-\\d{2}")) {
            JOptionPane.showMessageDialog(this, "La fecha debe estar en formato yyyy-MM-dd (ej. 2026-06-15)", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validar formato resultado (si no está vacío)
        if (!resultado.isEmpty()) {
            if (!resultado.matches("\\d+-\\d+")) {
                JOptionPane.showMessageDialog(this, "El resultado debe estar en formato X-Y (ejemplo: 3-1)", "Atención", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        Torneo t = new Torneo();
        t.setId(torneoItem.id);

        Equipo e1 = new Equipo();
        e1.setId(eq1Item.id);

        Equipo e2 = new Equipo();
        e2.setId(eq2Item.id);

        boolean ok;
        if (selectedPartida == null) {
            Partida p = new Partida();
            p.setTorneo(t);
            p.setEquipo1(e1);
            p.setEquipo2(e2);
            p.setFecha(fecha);
            p.setResultado(resultado.isEmpty() ? null : resultado);
            ok = partidaDAO.insertar(p);
        } else {
            selectedPartida.setTorneo(t);
            selectedPartida.setEquipo1(e1);
            selectedPartida.setEquipo2(e2);
            selectedPartida.setFecha(fecha);
            selectedPartida.setResultado(resultado.isEmpty() ? null : resultado);
            ok = partidaDAO.actualizar(selectedPartida);
        }

        if (ok) {
            JOptionPane.showMessageDialog(this, "Partida guardada correctamente");
            loadData();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Error al guardar la partida. Verifica que la fecha sea válida.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarPartida() {
        if (selectedPartida == null) {
            JOptionPane.showMessageDialog(this, "Selecciona una partida de la lista para eliminar", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "¿Seguro que deseas eliminar esta partida?", "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            boolean ok = partidaDAO.eliminar(selectedPartida.getId());
            if (ok) {
                JOptionPane.showMessageDialog(this, "Partida eliminada correctamente");
                loadData();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar la partida", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /** Clase interna de utilidad para almacenar ids y descripciones en ComboBoxes. */
    private static class ComboItem {
        int id;
        String name;

        ComboItem(int id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
