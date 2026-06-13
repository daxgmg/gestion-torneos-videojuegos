package com.torneos.vistas;

import com.torneos.dominio.Equipo;
import com.torneos.dominio.Jugador;
import com.torneos.dominio.Partida;
import com.torneos.dominio.User;
import com.torneos.persistencia.JugadorDAO;
import com.torneos.persistencia.PartidaDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Vista para el jugador logueado que muestra sus partidas disputadas o pendientes.
 * Filtrada por el equipo del jugador correspondiente.
 * Rediseñada con estética Brawl Stars (tabla de colores oscuros con cabeceras cian/doradas y contenedor metálico).
 */
public class MisPartidasFrame extends JFrame {

    private User usuario;
    private JLabel lblInfo;
    private JTable tblPartidas;
    private DefaultTableModel model;
    private JButton btnCerrar;

    public MisPartidasFrame(User user) {
        this.usuario = user;
        initComponents();
        findTeamAndLoadData();
    }

    private void initComponents() {
        setTitle("Mis Partidas - Gestión de Torneos");
        setSize(850, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        // Fondo de Pantalla Principal (Brawl Stars o Gradiente de Fallback)
        JPanel bgPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Image img = obtenerBrawlBg();
                if (img != null) {
                    int imgW = img.getWidth(this);
                    int imgH = img.getHeight(this);
                    int winW = getWidth();
                    int winH = getHeight();

                    double scale = Math.max((double) winW / imgW, (double) winH / imgH);
                    int newW = (int) (imgW * scale);
                    int newH = (int) (imgH * scale);

                    int x = (winW - newW) / 2;
                    int y = (winH - newH) / 2;

                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    g2.drawImage(img, x, y, newW, newH, this);
                    g2.dispose();
                } else {
                    // Fallback Gradiente Brawl Stars
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setPaint(new GradientPaint(0, 0, new Color(14, 116, 144), 0, getHeight(), new Color(8, 47, 73)));
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    g2.dispose();
                }
            }
        };
        bgPanel.setLayout(null);
        setContentPane(bgPanel);

        // Botón de regresar chunky azul/cian
        JButton btnVolver = new BrawlChunkyCyanButton("<");
        btnVolver.setBounds(20, 15, 55, 42);
        btnVolver.addActionListener(e -> dispose());
        bgPanel.add(btnVolver);

        // Título "MIS PARTIDAS" estilizado Brawl
        CustomBrawlTitleLabel lblTitulo = new CustomBrawlTitleLabel("MIS PARTIDAS", 28);
        lblTitulo.setBounds(90, 15, 670, 42);
        bgPanel.add(lblTitulo);

        // Contenedor Central 'Estilo Brawl' (Contenedor de Recompensas)
        JPanel brawlCentralPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();
                int arc = 30;

                // Fondo Morado Oscuro de Brawl Stars
                g2.setColor(new Color(45, 10, 85));
                g2.fillRoundRect(5, 5, w - 10, h - 10, arc, arc);

                // Textura de patrón de diamantes/estrellas
                g2.setColor(new Color(65, 20, 115, 100));
                int size = 18;
                for (int x = 0; x < w; x += size * 2) {
                    for (int y = 0; y < h; y += size * 2) {
                        int[] xPoints = {x + size, x + size * 2, x + size, x};
                        int[] yPoints = {y, y + size, y + size * 2, y + size};
                        g2.fillPolygon(xPoints, yPoints, 4);
                    }
                }

                // Borde de metal dorado grueso
                g2.setStroke(new BasicStroke(6));
                g2.setColor(new Color(234, 179, 8)); // Dorado brillante
                g2.drawRoundRect(5, 5, w - 10, h - 10, arc, arc);

                // Brillo interno de metal
                g2.setStroke(new BasicStroke(1.5f));
                g2.setColor(new Color(254, 240, 138)); // Dorado claro
                g2.drawRoundRect(8, 8, w - 16, h - 16, arc - 3, arc - 3);

                // Remaches metálicos en las esquinas
                int offset = 18;
                drawRivet(g2, offset, offset);
                drawRivet(g2, w - offset - 8, offset);
                drawRivet(g2, offset, h - offset - 8);
                drawRivet(g2, w - offset - 8, h - offset - 8);

                g2.dispose();
            }

            private void drawRivet(Graphics2D g2, int x, int y) {
                g2.setColor(Color.DARK_GRAY);
                g2.fillOval(x, y, 10, 10);
                g2.setColor(Color.LIGHT_GRAY);
                g2.fillOval(x + 1, y + 1, 8, 8);
            }
        };
        brawlCentralPanel.setLayout(null);
        brawlCentralPanel.setOpaque(false);
        brawlCentralPanel.setBounds(40, 75, 770, 410);
        bgPanel.add(brawlCentralPanel);

        // Información de tu equipo
        lblInfo = new JLabel("Buscando información de tu equipo...", SwingConstants.CENTER);
        lblInfo.setFont(new Font("Arial Black", Font.BOLD, 14));
        lblInfo.setForeground(new Color(254, 240, 138)); // Amarillo brillante
        lblInfo.setBounds(30, 20, 710, 30);
        brawlCentralPanel.add(lblInfo);

        // Tabla
        String[] columnas = {"Torneo", "Rival", "Fecha", "Resultado"};
        model = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblPartidas = new JTable(model);
        tblPartidas.setFont(new Font("Arial Black", Font.BOLD, 12));
        tblPartidas.setRowHeight(32);
        tblPartidas.setBackground(new Color(20, 5, 40));
        tblPartidas.setForeground(Color.WHITE);
        tblPartidas.setGridColor(new Color(45, 10, 85));
        tblPartidas.setShowGrid(true);
        tblPartidas.setSelectionBackground(new Color(6, 182, 212));
        tblPartidas.setSelectionForeground(Color.BLACK);

        // Renderizadores de celda personalizados para la estética Brawl Stars
        tblPartidas.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (isSelected) {
                    c.setBackground(new Color(6, 182, 212));
                    c.setForeground(Color.BLACK);
                } else {
                    c.setBackground(row % 2 == 0 ? new Color(30, 10, 60) : new Color(45, 15, 85));
                    c.setForeground(Color.WHITE);
                }
                setFont(new Font("Arial Black", Font.BOLD, 12));
                setHorizontalAlignment(SwingConstants.CENTER);
                return c;
            }
        });

        tblPartidas.getTableHeader().setDefaultRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setBackground(new Color(14, 116, 144));
                c.setForeground(Color.WHITE);
                c.setFont(new Font("Arial Black", Font.BOLD, 13));
                setHorizontalAlignment(SwingConstants.CENTER);
                setBorder(BorderFactory.createMatteBorder(0, 0, 2, 1, new Color(234, 179, 8))); // Dorado
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(tblPartidas);
        scrollPane.setBounds(30, 65, 710, 250);
        scrollPane.getViewport().setBackground(new Color(20, 5, 40));
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(234, 179, 8), 2));
        brawlCentralPanel.add(scrollPane);

        // Botón Cerrar (Estilo Rojo Chunky Brawl)
        btnCerrar = new BrawlChunkyRedButton("Regresar");
        btnCerrar.setBounds(285, 335, 200, 46);
        btnCerrar.addActionListener(e -> dispose());
        brawlCentralPanel.add(btnCerrar);

        // Decoraciones flotantes en la ventana
        JLabel decGema = new JLabel("💎");
        decGema.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        decGema.setBounds(15, 495, 40, 40);
        bgPanel.add(decGema);

        JLabel decPower = new JLabel("⚡");
        decPower.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        decPower.setBounds(795, 495, 40, 40);
        bgPanel.add(decPower);

        setVisible(true);
    }

    private void findTeamAndLoadData() {
        JugadorDAO jugadorDAO = new JugadorDAO();
        List<Jugador> jugadores = jugadorDAO.obtenerTodos();

        Equipo equipoJugador = null;
        for (Jugador j : jugadores) {
            if (j.getUser() != null && j.getUser().getId() == usuario.getId()) {
                equipoJugador = j.getEquipo();
                break;
            }
        }

        if (equipoJugador == null) {
            lblInfo.setText("No estás registrado como jugador de ningún equipo.");
            return;
        }

        lblInfo.setText("⚔️ Mi Equipo: " + equipoJugador.getNombre() + " ⚔️");

        PartidaDAO partidaDAO = new PartidaDAO();
        List<Partida> todas = partidaDAO.obtenerTodos();

        model.setRowCount(0);
        for (Partida p : todas) {
            boolean isEq1 = p.getEquipo1() != null && p.getEquipo1().getId() == equipoJugador.getId();
            boolean isEq2 = p.getEquipo2() != null && p.getEquipo2().getId() == equipoJugador.getId();

            if (isEq1 || isEq2) {
                String rival = "";
                if (isEq1) {
                    rival = p.getEquipo2() != null ? p.getEquipo2().getNombre() : "N/A";
                } else {
                    rival = p.getEquipo1() != null ? p.getEquipo1().getNombre() : "N/A";
                }

                String torneoNombre = p.getTorneo() != null ? p.getTorneo().getNombre() : "N/A";
                String res = p.getResultado();
                if (res == null || res.trim().isEmpty()) {
                    res = "Pendiente";
                }

                model.addRow(new Object[]{torneoNombre, rival, p.getFecha(), res});
            }
        }
    }

    private Image obtenerBrawlBg() {
        String[] paths = {
            "brawl_stars_bg.png",
            "../brawl_stars_bg.png",
            "gestion-torneos-videojuegos/brawl_stars_bg.png",
            "src/main/resources/brawl_stars_bg.png",
            "target/classes/brawl_stars_bg.png"
        };
        for (String p : paths) {
            java.io.File file = new java.io.File(p);
            if (file.exists()) {
                return new ImageIcon(p).getImage();
            }
        }
        return null;
    }

    // =========================================================================
    // CLASES AUXILIARES DE DISEÑO
    // =========================================================================

    private static class CustomBrawlTitleLabel extends JLabel {
        private int fontSize;

        public CustomBrawlTitleLabel(String text, int fontSize) {
            super(text, SwingConstants.CENTER);
            this.fontSize = fontSize;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            Font font = new Font("Arial Black", Font.BOLD, fontSize);
            g2.setFont(font);

            String text = getText();
            FontMetrics fm = g2.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(text)) / 2;
            int y = fm.getAscent() + (getHeight() - fm.getHeight()) / 2;

            java.awt.font.TextLayout tl = new java.awt.font.TextLayout(text, font, g2.getFontRenderContext());
            g2.translate(x, y);

            // Borde grueso negro
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(6, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.draw(tl.getOutline(null));

            // Degradado de color amarillo a naranja brillante
            GradientPaint gp = new GradientPaint(0, -fm.getAscent(), new Color(254, 240, 138), 0, 0, new Color(249, 115, 22));
            g2.setPaint(gp);
            g2.fill(tl.getOutline(null));

            g2.dispose();
        }
    }

    private static class BrawlChunkyCyanButton extends JButton {
        private boolean hover = false;

        public BrawlChunkyCyanButton(String text) {
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
            int arc = 15;

            // Sombra
            g2.setColor(new Color(3, 30, 60));
            g2.fillRoundRect(0, 4, w, h - 4, arc, arc);

            // Gradiente cian/azul
            if (hover) {
                g2.setPaint(new GradientPaint(0, 0, new Color(34, 211, 238), 0, h, new Color(14, 116, 144)));
            } else {
                g2.setPaint(new GradientPaint(0, 0, new Color(6, 182, 212), 0, h, new Color(3, 105, 161)));
            }
            g2.fillRoundRect(0, 0, w, h - 4, arc, arc);

            // Borde blanco
            g2.setStroke(new BasicStroke(2.5f));
            g2.setColor(Color.WHITE);
            g2.drawRoundRect(0, 0, w - 1, h - 5, arc, arc);

            // Texto
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

    private static class BrawlChunkyRedButton extends JButton {
        private boolean hover = false;

        public BrawlChunkyRedButton(String text) {
            super(text);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setFont(new Font("Arial Black", Font.BOLD, 14));
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
            int arc = 15;

            // Sombra
            g2.setColor(new Color(100, 20, 20));
            g2.fillRoundRect(0, 4, w, h - 4, arc, arc);

            // Gradiente rojo
            if (hover) {
                g2.setPaint(new GradientPaint(0, 0, new Color(239, 68, 68), 0, h, new Color(185, 28, 28)));
            } else {
                g2.setPaint(new GradientPaint(0, 0, new Color(185, 28, 28), 0, h, new Color(153, 27, 27)));
            }
            g2.fillRoundRect(0, 0, w, h - 4, arc, arc);

            // Borde blanco
            g2.setStroke(new BasicStroke(3));
            g2.setColor(Color.WHITE);
            g2.drawRoundRect(0, 0, w - 1, h - 5, arc, arc);

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

