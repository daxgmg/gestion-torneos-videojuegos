package com.torneos.vistas;

import com.torneos.dominio.Equipo;
import com.torneos.dominio.Partida;
import com.torneos.dominio.Jugador;
import com.torneos.persistencia.EquipoDAO;
import com.torneos.persistencia.PartidaDAO;
import com.torneos.persistencia.JugadorDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Vista de clasificación general de equipos participantes en las partidas.
 * Estilizada como una Lista de Recompensas oficial de la Marina (One Piece).
 */
public class ClasificacionFrame extends JFrame {

    private JTable tblClasificacion;
    private DefaultTableModel model;
    private JButton btnCerrar;

    public ClasificacionFrame() {
        initComponents();
        loadData();
    }

    private void initComponents() {
        setTitle("Lista de Recompensas de la Marina - Gestión de Torneos");
        setSize(800, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        // Fondo Dinámico de Madera de Roble Oscuro
        JPanel bgPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();
                
                // Fondo base marrón oscuro
                g2.setColor(new Color(34, 18, 11)); // Roble oscuro
                g2.fillRect(0, 0, w, h);
                
                // Tablones de madera
                g2.setColor(new Color(22, 11, 7)); // Divisiones más oscuras
                g2.setStroke(new BasicStroke(4));
                int plankW = 90;
                for (int x = plankW; x < w; x += plankW) {
                    g2.drawLine(x, 0, x, h);
                    // Clavos o remaches de los tablones
                    for (int y = 40; y < h; y += 120) {
                        g2.setColor(new Color(100, 100, 100)); // Gris metal
                        g2.fillOval(x - 4, y - 4, 8, 8);
                        g2.setColor(new Color(50, 50, 50));
                        g2.drawOval(x - 4, y - 4, 8, 8);
                    }
                    g2.setColor(new Color(22, 11, 7));
                }
                
                // Vetas de madera
                g2.setColor(new Color(48, 27, 18, 90));
                g2.setStroke(new BasicStroke(1));
                for (int y = 20; y < h; y += 30) {
                    g2.drawLine(0, y, w, y + 4);
                }
                
                g2.dispose();
            }
        };
        bgPanel.setLayout(null);
        setContentPane(bgPanel);

        // Botón Volver (Estética Pirata)
        JButton btnVolver = new PirateBackButton("<");
        btnVolver.setBounds(20, 15, 50, 38);
        btnVolver.addActionListener(e -> dispose());
        bgPanel.add(btnVolver);

        // Título de la Marina
        CustomAnimeTitleLabel lblTitulo = new CustomAnimeTitleLabel("RECOMPENSAS DE LA MARINA");
        lblTitulo.setBounds(90, 15, 620, 45);
        bgPanel.add(lblTitulo);

        // Tabla
        String[] columnas = {"Rango", "Capitán", "Tripulación", "Recompensa"};
        model = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblClasificacion = new JTable(model);
        tblClasificacion.setFont(new Font("Georgia", Font.BOLD, 13));
        tblClasificacion.setRowHeight(32);
        tblClasificacion.setBackground(new Color(244, 227, 193)); // Pergamino
        tblClasificacion.setForeground(new Color(60, 30, 10)); // Marrón oscuro
        tblClasificacion.setGridColor(new Color(180, 140, 90)); // Líneas rústicas
        tblClasificacion.setSelectionBackground(new Color(217, 119, 6)); // Oro viejo
        tblClasificacion.setSelectionForeground(Color.WHITE);

        // Alinear texto al centro en las celdas
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (c instanceof JLabel) {
                    ((JLabel) c).setHorizontalAlignment(SwingConstants.CENTER);
                }
                return c;
            }
        };
        tblClasificacion.setDefaultRenderer(Object.class, centerRenderer);

        // Estilo especial para la columna de Recompensa (Rojo carmesí pirata)
        tblClasificacion.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (c instanceof JLabel) {
                    JLabel lbl = (JLabel) c;
                    lbl.setHorizontalAlignment(SwingConstants.CENTER);
                    lbl.setForeground(new Color(153, 27, 27)); // Rojo carmesí
                    lbl.setFont(new Font("Georgia", Font.BOLD, 14));
                }
                return c;
            }
        });

        // Cabecera estilo Marina (Azul Marino con letras blancas)
        tblClasificacion.getTableHeader().setBackground(new Color(15, 23, 42)); // Azul marino
        tblClasificacion.getTableHeader().setForeground(Color.WHITE);
        tblClasificacion.getTableHeader().setFont(new Font("Georgia", Font.BOLD, 13));
        tblClasificacion.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(251, 191, 36)));

        JScrollPane scrollPane = new JScrollPane(tblClasificacion);
        scrollPane.setBounds(20, 75, 740, 310);
        scrollPane.getViewport().setBackground(new Color(34, 18, 11)); // Combinar con madera
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(101, 67, 33), 3));
        bgPanel.add(scrollPane);

        // Botón Cerrar (Rústico de madera)
        btnCerrar = new PirateBackButton("Regresar");
        btnCerrar.setBounds(300, 405, 200, 42);
        btnCerrar.addActionListener(e -> dispose());
        bgPanel.add(btnCerrar);

        setVisible(true);
    }

    private void loadData() {
        model.setRowCount(0);

        EquipoDAO equipoDAO = new EquipoDAO();
        List<Equipo> equipos = equipoDAO.obtenerTodos();

        PartidaDAO partidaDAO = new PartidaDAO();
        List<Partida> partidas = partidaDAO.obtenerTodos();

        JugadorDAO jugadorDAO = new JugadorDAO();
        List<Jugador> todosJugadores = jugadorDAO.obtenerTodos();

        // Estructura para acumular estadísticas
        Map<Integer, TeamStats> statsMap = new HashMap<>();
        for (Equipo eq : equipos) {
            TeamStats ts = new TeamStats();
            ts.id = eq.getId();
            ts.nombre = eq.getNombre();
            statsMap.put(eq.getId(), ts);
        }

        // Procesar las partidas para calcular puntos y estadísticas
        for (Partida p : partidas) {
            String res = p.getResultado();
            if (res == null || res.trim().isEmpty() || "Pendiente".equalsIgnoreCase(res.trim())) {
                continue;
            }

            String[] partes = res.split("-");
            if (partes.length == 2) {
                try {
                    int goles1 = Integer.parseInt(partes[0].trim());
                    int goles2 = Integer.parseInt(partes[1].trim());

                    if (p.getEquipo1() == null || p.getEquipo2() == null) {
                        continue;
                    }

                    TeamStats s1 = statsMap.get(p.getEquipo1().getId());
                    TeamStats s2 = statsMap.get(p.getEquipo2().getId());

                    if (s1 != null && s2 != null) {
                        s1.jugadas++;
                        s2.jugadas++;

                        if (goles1 > goles2) {
                            s1.victorias++;
                            s1.puntos += 3;
                            s2.derrotas++;
                        } else if (goles2 > goles1) {
                            s2.victorias++;
                            s2.puntos += 3;
                            s1.derrotas++;
                        } else {
                            s1.puntos += 1;
                            s2.puntos += 1;
                        }
                    }
                } catch (NumberFormatException e) {
                    // Ignorar partidas con formato de resultado incorrecto
                }
            }
        }

        // Ordenar estadísticas de mayor a menor puntuación
        List<TeamStats> statsList = new ArrayList<>(statsMap.values());
        Collections.sort(statsList);

        // Agregar a la tabla
        int posicion = 1;
        for (TeamStats ts : statsList) {
            String capitan = "Sin Capitán";
            for (Jugador j : todosJugadores) {
                if (j.getEquipo() != null && j.getEquipo().getId() == ts.id) {
                    capitan = j.getAlias() != null && !j.getAlias().trim().isEmpty() ? j.getAlias() : j.getNombre();
                    break;
                }
            }

            // Calcular recompensa en Berries (฿ 50.000.000 por punto)
            long recompensaVal = ts.puntos * 50000000L;
            if (recompensaVal == 0) {
                recompensaVal = 5000000L; // Mínimo de recompensa
            }
            String recompensaStr = String.format("฿ %,d-", recompensaVal).replace(',', '.');

            model.addRow(new Object[]{
                    posicion++,
                    capitan,
                    ts.nombre,
                    recompensaStr
            });
        }
    }

    private static class TeamStats implements Comparable<TeamStats> {
        int id;
        String nombre;
        int jugadas = 0;
        int victorias = 0;
        int derrotas = 0;
        int puntos = 0;

        @Override
        public int compareTo(TeamStats o) {
            return Integer.compare(o.puntos, this.puntos);
        }
    }

    private static class CustomAnimeTitleLabel extends JLabel {
        public CustomAnimeTitleLabel(String text) {
            super(text, SwingConstants.CENTER);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            Font font = new Font("Arial Black", Font.BOLD, 26);
            g2.setFont(font);

            String text = getText();
            FontMetrics fm = g2.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(text)) / 2;
            int y = fm.getAscent() + (getHeight() - fm.getHeight()) / 2;

            java.awt.font.TextLayout tl = new java.awt.font.TextLayout(text, font, g2.getFontRenderContext());
            g2.translate(x, y);

            // Borde grueso negro
            g2.setColor(new Color(40, 20, 0));
            g2.setStroke(new BasicStroke(6, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.draw(tl.getOutline(null));

            // Degradado oro y amarillo
            GradientPaint gp = new GradientPaint(0, -fm.getAscent(), new Color(253, 224, 71), 0, 0, new Color(234, 88, 12));
            g2.setPaint(gp);
            g2.fill(tl.getOutline(null));

            g2.dispose();
        }
    }

    private static class PirateBackButton extends JButton {
        private boolean hover = false;

        public PirateBackButton(String text) {
            super(text);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setFont(new Font("Arial Black", Font.BOLD, 18));
            setForeground(Color.WHITE);
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    hover = true;
                    repaint();
                }
                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    hover = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            // Sombra
            g2.setColor(new Color(60, 30, 10));
            g2.fillRoundRect(0, 3, w, h - 3, 10, 10);

            // Fondo Madera Rústica
            if (hover) {
                g2.setPaint(new GradientPaint(0, 0, new Color(139, 92, 26), 0, h, new Color(217, 119, 6)));
            } else {
                g2.setPaint(new GradientPaint(0, 0, new Color(101, 67, 33), 0, h, new Color(60, 30, 10)));
            }
            g2.fillRoundRect(0, 0, w, h - 3, 10, 10);

            // Borde oro
            g2.setStroke(new BasicStroke(2));
            g2.setColor(new Color(251, 191, 36));
            g2.drawRoundRect(0, 0, w - 1, h - 4, 10, 10);

            // Texto con sombra
            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();
            String text = getText();
            int tx = (w - fm.stringWidth(text)) / 2;
            int ty = (h - fm.getHeight()) / 2 + fm.getAscent() - 2;

            g2.setColor(Color.BLACK);
            g2.drawString(text, tx + 1, ty + 1);

            g2.setColor(Color.WHITE);
            g2.drawString(text, tx, ty);

            g2.dispose();
        }
    }
}
