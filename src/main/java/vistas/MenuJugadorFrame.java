package vistas;

import com.torneos.dominio.Equipo;
import com.torneos.dominio.Jugador;
import com.torneos.dominio.Partida;
import com.torneos.dominio.Torneo;
import com.torneos.dominio.User;
import com.torneos.persistencia.EquipoDAO;
import com.torneos.persistencia.JugadorDAO;
import com.torneos.persistencia.PartidaDAO;
import com.torneos.persistencia.TorneoDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MenuJugadorFrame extends JFrame {

    private User loggedInUser;
    private Jugador miJugador;
    private Equipo miEquipo;

    private JTabbedPane tabbedPane;
    private JTable tblTorneos;
    private JTable tblMisPartidas;
    private JTable tblClasificacion;

    public MenuJugadorFrame(User user) {
        this.loggedInUser = user;
        cargarDatosJugador();

        setTitle("Panel de Jugador - Gestión de Torneos");
        setSize(800, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 247, 250));

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(25, 135, 84)); // Green color for player
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel lblTitle = new JLabel("Área del Jugador");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(Color.WHITE);
        headerPanel.add(lblTitle, BorderLayout.WEST);

        String welcomeMsg = "Bienvenido, " + user.getNombre();
        if (miEquipo != null) {
            welcomeMsg += " | Equipo: " + miEquipo.getNombre();
        }
        JLabel lblWelcome = new JLabel(welcomeMsg);
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblWelcome.setForeground(new Color(230, 245, 235));
        headerPanel.add(lblWelcome, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Tabbed Pane for Options
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 12));

        // 1. Torneos disponibles
        JPanel pnlTorneos = new JPanel(new BorderLayout());
        pnlTorneos.setBorder(new EmptyBorder(10, 10, 10, 10));
        tblTorneos = new JTable();
        pnlTorneos.add(new JScrollPane(tblTorneos), BorderLayout.CENTER);
        JButton btnRefrescarTorneos = new JButton("Actualizar Lista");
        btnRefrescarTorneos.addActionListener(e -> cargarTorneos());
        JPanel pnlCtrlTorneos = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlCtrlTorneos.add(btnRefrescarTorneos);
        pnlTorneos.add(pnlCtrlTorneos, BorderLayout.SOUTH);
        tabbedPane.addTab("Torneos Disponibles", pnlTorneos);

        // 2. Mis partidas
        JPanel pnlPartidas = new JPanel(new BorderLayout());
        pnlPartidas.setBorder(new EmptyBorder(10, 10, 10, 10));
        tblMisPartidas = new JTable();
        pnlPartidas.add(new JScrollPane(tblMisPartidas), BorderLayout.CENTER);
        JButton btnRefrescarPartidas = new JButton("Actualizar Partidas");
        btnRefrescarPartidas.addActionListener(e -> cargarMisPartidas());
        JPanel pnlCtrlPartidas = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlCtrlPartidas.add(btnRefrescarPartidas);
        pnlPartidas.add(pnlCtrlPartidas, BorderLayout.SOUTH);
        tabbedPane.addTab("Mis Partidas", pnlPartidas);

        // 3. Clasificación
        JPanel pnlClasif = new JPanel(new BorderLayout());
        pnlClasif.setBorder(new EmptyBorder(10, 10, 10, 10));
        tblClasificacion = new JTable();
        pnlClasif.add(new JScrollPane(tblClasificacion), BorderLayout.CENTER);
        JButton btnRefrescarClasif = new JButton("Calcular Clasificación");
        btnRefrescarClasif.addActionListener(e -> calcularClasificacion());
        JPanel pnlCtrlClasif = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlCtrlClasif.add(btnRefrescarClasif);
        pnlClasif.add(pnlCtrlClasif, BorderLayout.SOUTH);
        tabbedPane.addTab("Clasificación de Equipos", pnlClasif);

        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        // Bottom logout
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        footerPanel.setBackground(new Color(245, 247, 250));
        JButton btnLogout = new JButton("Cerrar sesión");
        btnLogout.setBackground(new Color(108, 117, 125));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });
        footerPanel.add(btnLogout);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Load initial data
        cargarTorneos();
        cargarMisPartidas();
        calcularClasificacion();
    }

    private void cargarDatosJugador() {
        try {
            JugadorDAO jugadorDAO = new JugadorDAO();
            List<Jugador> jugadores = jugadorDAO.obtenerTodos();
            for (Jugador j : jugadores) {
                if (j.getUser() != null && j.getUser().getId() == loggedInUser.getId()) {
                    this.miJugador = j;
                    this.miEquipo = j.getEquipo();
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("Error al cargar datos del jugador: " + e.getMessage());
        }
    }

    private void cargarTorneos() {
        String[] cols = {"ID", "Nombre", "Fecha Inicio", "Fecha Fin", "Estado"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        try {
            TorneoDAO torneoDAO = new TorneoDAO();
            List<Torneo> torneos = torneoDAO.obtenerTodos();
            for (Torneo t : torneos) {
                model.addRow(new Object[]{t.getId(), t.getNombre(), t.getFechaInicio(), t.getFechaFin(), t.getEstado()});
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar torneos: " + e.getMessage());
        }
        tblTorneos.setModel(model);
    }

    private void cargarMisPartidas() {
        String[] cols = {"ID", "Torneo", "Mi Equipo", "Rival", "Resultado", "Fecha"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        if (miEquipo == null) {
            model.addRow(new Object[]{"-", "Sin equipo asignado", "", "", "", ""});
            tblMisPartidas.setModel(model);
            return;
        }

        try {
            PartidaDAO partidaDAO = new PartidaDAO();
            List<Partida> partidas = partidaDAO.obtenerTodos();
            int miEqId = miEquipo.getId();

            for (Partida p : partidas) {
                if (p.getEquipo1() != null && p.getEquipo2() != null) {
                    int id1 = p.getEquipo1().getId();
                    int id2 = p.getEquipo2().getId();

                    if (id1 == miEqId || id2 == miEqId) {
                        String miNom = (id1 == miEqId) ? p.getEquipo1().getNombre() : p.getEquipo2().getNombre();
                        String rivNom = (id1 == miEqId) ? p.getEquipo2().getNombre() : p.getEquipo1().getNombre();
                        String torneoNom = p.getTorneo() != null ? p.getTorneo().getNombre() : "N/A";
                        String resultadoStr = p.getResultado() != null ? p.getResultado() : "Pendiente";

                        model.addRow(new Object[]{p.getId(), torneoNom, miNom, rivNom, resultadoStr, p.getFecha()});
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar partidas: " + e.getMessage());
        }
        tblMisPartidas.setModel(model);
    }

    private void calcularClasificacion() {
        String[] cols = {"Puesto", "Equipo", "PJ", "PG", "PE", "PP", "Puntos"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        try {
            EquipoDAO equipoDAO = new EquipoDAO();
            PartidaDAO partidaDAO = new PartidaDAO();

            List<Equipo> equipos = equipoDAO.obtenerTodos();
            List<Partida> partidas = partidaDAO.obtenerTodos();

            // Map to track stats
            Map<Integer, TeamStats> statsMap = new HashMap<>();
            for (Equipo eq : equipos) {
                statsMap.put(eq.getId(), new TeamStats(eq.getNombre()));
            }

            // Calculate points for completed matches
            for (Partida p : partidas) {
                if (p.getResultado() == null || p.getResultado().trim().isEmpty() || p.getResultado().equalsIgnoreCase("Pendiente")) {
                    continue; // Skip pending matches
                }

                if (p.getEquipo1() == null || p.getEquipo2() == null) {
                    continue;
                }

                int id1 = p.getEquipo1().getId();
                int id2 = p.getEquipo2().getId();

                TeamStats stats1 = statsMap.get(id1);
                TeamStats stats2 = statsMap.get(id2);

                if (stats1 == null || stats2 == null) {
                    continue;
                }

                int[] marcador = parsearMarcador(p.getResultado());
                if (marcador != null) {
                    int score1 = marcador[0];
                    int score2 = marcador[1];

                    stats1.pj++;
                    stats2.pj++;

                    if (score1 > score2) {
                        stats1.pg++;
                        stats1.puntos += 3;
                        stats2.pp++;
                    } else if (score1 < score2) {
                        stats2.pg++;
                        stats2.puntos += 3;
                        stats1.pp++;
                    } else {
                        stats1.pe++;
                        stats1.puntos += 1;
                        stats2.pe++;
                        stats2.puntos += 1;
                    }
                }
            }

            // Sort teams by points
            List<TeamStats> ranking = new ArrayList<>(statsMap.values());
            ranking.sort((a, b) -> Integer.compare(b.puntos, a.puntos));

            int puesto = 1;
            for (TeamStats s : ranking) {
                model.addRow(new Object[]{puesto++, s.nombre, s.pj, s.pg, s.pe, s.pp, s.puntos});
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al calcular clasificación: " + e.getMessage());
        }

        tblClasificacion.setModel(model);
    }

    private int[] parsearMarcador(String resultado) {
        if (resultado == null) return null;
        Pattern p = Pattern.compile("\\d+");
        Matcher m = p.matcher(resultado);
        List<Integer> numbers = new ArrayList<>();
        while (m.find()) {
            numbers.add(Integer.parseInt(m.group()));
        }
        if (numbers.size() >= 2) {
            return new int[]{numbers.get(0), numbers.get(1)};
        }
        return null;
    }

    private static class TeamStats {
        String nombre;
        int pj = 0;
        int pg = 0;
        int pe = 0;
        int pp = 0;
        int puntos = 0;

        TeamStats(String nombre) {
            this.nombre = nombre;
        }
    }
}
