package com.torneos.vistas;

import com.torneos.dominio.Partida;
import com.torneos.dominio.Torneo;
import com.torneos.persistencia.PartidaDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Vista que muestra las partidas de un torneo específico.
 * Recibe un objeto Torneo en el constructor.
 */
public class PartidasTorneoFrame extends JFrame {

    private Torneo torneo;
    private JTable tblPartidas;
    private DefaultTableModel model;
    private JButton btnCerrar;

    public PartidasTorneoFrame(Torneo torneo) {
        this.torneo = torneo;
        initComponents();
        loadData();
    }

    private void initComponents() {
        setTitle("Partidas del Torneo: " + torneo.getNombre());
        setSize(750, 480);
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

        JLabel lblTitulo = new JLabel("Partidas del Torneo: " + torneo.getNombre(), SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setBounds(80, 18, 590, 30);
        lblTitulo.setForeground(new Color(50, 50, 50));
        add(lblTitulo);

        // Tabla
        String[] columnas = {"ID", "Equipo 1", "Equipo 2", "Fecha", "Resultado"};
        model = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblPartidas = new JTable(model);
        tblPartidas.setFont(new Font("Arial", Font.PLAIN, 14));
        tblPartidas.setRowHeight(25);
        tblPartidas.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        tblPartidas.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(tblPartidas);
        scrollPane.setBounds(20, 70, 690, 270);
        add(scrollPane);

        // Botón Cerrar
        btnCerrar = new JButton("Regresar");
        btnCerrar.setFont(new Font("Arial", Font.BOLD, 14));
        btnCerrar.setForeground(Color.WHITE);
        btnCerrar.setBackground(new Color(150, 150, 150));
        btnCerrar.setOpaque(true);
        btnCerrar.setContentAreaFilled(true);
        btnCerrar.setFocusPainted(false);
        btnCerrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCerrar.setBounds(275, 360, 200, 42); // Alto mínimo 38px
        btnCerrar.addActionListener(e -> dispose());
        add(btnCerrar);

        setVisible(true);
    }

    private void loadData() {
        model.setRowCount(0);
        PartidaDAO dao = new PartidaDAO();
        List<Partida> todas = dao.obtenerTodos();
        for (Partida p : todas) {
            if (p.getTorneo() != null && p.getTorneo().getId() == torneo.getId()) {
                String res = p.getResultado();
                if (res == null || res.trim().isEmpty()) {
                    res = "Pendiente";
                }
                String eq1 = p.getEquipo1() != null ? p.getEquipo1().getNombre() : "N/A";
                String eq2 = p.getEquipo2() != null ? p.getEquipo2().getNombre() : "N/A";

                model.addRow(new Object[]{p.getId(), eq1, eq2, p.getFecha(), res});
            }
        }
    }
}
