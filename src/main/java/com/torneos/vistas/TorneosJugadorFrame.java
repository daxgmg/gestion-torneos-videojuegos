package com.torneos.vistas;

import com.torneos.dominio.Torneo;
import com.torneos.persistencia.TorneoDAO;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Vista para el jugador que muestra una cuadrícula de torneos ACTIVOS
 * con estética de "Wanted Posters" (Carteles de Se Busca) de One Piece.
 */
public class TorneosJugadorFrame extends JFrame {

    private JPanel gridPanel;
    private JScrollPane scrollPane;
    private List<Torneo> torneosActivos;
    private com.torneos.dominio.User user;

    public TorneosJugadorFrame() {
        this(null);
    }

    public TorneosJugadorFrame(com.torneos.dominio.User user) {
        this.user = user;
        initComponents();
        loadData();
    }

    private void initComponents() {
        setTitle("Torneos Activos - Gestión de Torneos");
        setSize(700, 520);
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

        // Título Estilo One Piece
        CustomAnimeTitleLabel lblTitulo = new CustomAnimeTitleLabel("TORNEOS DISPONIBLES");
        lblTitulo.setBounds(90, 15, 520, 45);
        bgPanel.add(lblTitulo);

        // Cuadrícula de Torneos
        gridPanel = new JPanel(new GridLayout(0, 2, 20, 20));
        gridPanel.setOpaque(false);

        // ScrollPane transparente
        scrollPane = new JScrollPane(gridPanel);
        scrollPane.setBounds(20, 75, 640, 380);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        // Ajustar velocidad de scroll
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        bgPanel.add(scrollPane);

        setVisible(true);
    }

    private void loadData() {
        gridPanel.removeAll();
        TorneoDAO dao = new TorneoDAO();
        List<Torneo> todos = dao.obtenerTodos();
        torneosActivos = new java.util.ArrayList<>();
        for (Torneo t : todos) {
            if ("ACTIVO".equalsIgnoreCase(t.getEstado())) {
                torneosActivos.add(t);
                gridPanel.add(new WantedPosterPanel(t));
            }
        }
        gridPanel.revalidate();
        gridPanel.repaint();
    }

    private void unirseTorneo(Torneo seleccionado) {
        if (user == null) {
            JOptionPane.showMessageDialog(this, "Error: No se ha detectado el usuario actual.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 1. Buscar el equipo del jugador logueado
        com.torneos.persistencia.JugadorDAO jugadorDAO = new com.torneos.persistencia.JugadorDAO();
        List<com.torneos.dominio.Jugador> jugadores = jugadorDAO.obtenerTodos();
        com.torneos.dominio.Equipo equipoJugador = null;

        for (com.torneos.dominio.Jugador j : jugadores) {
            if (j.getUser() != null && j.getUser().getId() == user.getId()) {
                equipoJugador = j.getEquipo();
                break;
            }
        }

        if (equipoJugador == null) {
            JOptionPane.showMessageDialog(this, "No estás registrado como jugador de ningún equipo.\nDebes estar en un equipo para unirte a un torneo.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. Verificar si el equipo ya está inscrito en este torneo
        com.torneos.persistencia.InscripcionDAO inscripcionDAO = new com.torneos.persistencia.InscripcionDAO();
        List<com.torneos.dominio.Inscripcion> inscripciones = inscripcionDAO.buscarPorTorneo(seleccionado.getId());
        boolean yaInscrito = false;
        for (com.torneos.dominio.Inscripcion ins : inscripciones) {
            if (ins.getEquipo() != null && ins.getEquipo().getId() == equipoJugador.getId()) {
                yaInscrito = true;
                break;
            }
        }

        if (yaInscrito) {
            JOptionPane.showMessageDialog(this, "Tu equipo '" + equipoJugador.getNombre() + "' ya está registrado en este torneo.", "Información", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // 3. Confirmar inscripción
        int confirm = JOptionPane.showConfirmDialog(this, 
            "¿Quieres unir a tu equipo '" + equipoJugador.getNombre() + "' al torneo '" + seleccionado.getNombre() + "'?", 
            "Confirmar Inscripción", 
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            com.torneos.dominio.Inscripcion nuevaInscripcion = new com.torneos.dominio.Inscripcion(0, seleccionado, equipoJugador);
            boolean exito = inscripcionDAO.insertar(nuevaInscripcion);
            if (exito) {
                JOptionPane.showMessageDialog(this, "¡Inscripción exitosa! Tu equipo ahora participa en este torneo.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Error al realizar la inscripción. Por favor intenta de nuevo.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // =========================================================================
    // COMPONENTES PERSONALIZADOS
    // =========================================================================

    private class WantedPosterPanel extends JPanel {
        private Torneo torneo;

        public WantedPosterPanel(Torneo t) {
            this.torneo = t;
            setLayout(null);
            setPreferredSize(new Dimension(295, 395));
            setOpaque(false);

            // Botón Ver Partidas (Azul Marino eSports)
            JButton btnVer = new JButton("Ver Partidas");
            btnVer.setFont(new Font("Arial Black", Font.BOLD, 11));
            btnVer.setForeground(Color.WHITE);
            btnVer.setBackground(new Color(15, 23, 42)); // Azul marino
            btnVer.setFocusPainted(false);
            btnVer.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnVer.setBounds(20, 332, 115, 38);
            btnVer.addActionListener(e -> {
                new PartidasTorneoFrame(torneo);
            });
            add(btnVer);

            // Botón Te quieres unir (Naranja One Piece)
            JButton btnJoin = new JButton("¿Te quieres unir?");
            btnJoin.setFont(new Font("Arial Black", Font.BOLD, 10));
            btnJoin.setForeground(Color.WHITE);
            btnJoin.setBackground(new Color(234, 88, 12)); // Naranja vibrante
            btnJoin.setFocusPainted(false);
            btnJoin.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnJoin.setBounds(145, 332, 135, 38);
            btnJoin.addActionListener(e -> {
                unirseTorneo(torneo);
            });
            add(btnJoin);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            // Fondo pergamino antiguo
            g2.setColor(new Color(244, 227, 193));
            g2.fillRoundRect(5, 5, w - 10, h - 10, 15, 15);

            // Borde marrón madera
            g2.setStroke(new BasicStroke(4));
            g2.setColor(new Color(101, 67, 33));
            g2.drawRoundRect(5, 5, w - 10, h - 10, 15, 15);

            // Línea punteada de cuerda
            g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{6}, 0));
            g2.setColor(new Color(180, 140, 90));
            g2.drawRoundRect(10, 10, w - 20, h - 20, 12, 12);

            // Texto WANTED
            g2.setColor(new Color(60, 30, 10));
            g2.setFont(new Font("Georgia", Font.BOLD, 28));
            FontMetrics fm = g2.getFontMetrics();
            String wanted = "WANTED";
            g2.drawString(wanted, (w - fm.stringWidth(wanted)) / 2, 42);

            // Texto DEAD OR ALIVE
            g2.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 11));
            fm = g2.getFontMetrics();
            String deadAlive = "DEAD OR ALIVE";
            g2.drawString(deadAlive, (w - fm.stringWidth(deadAlive)) / 2, 58);

            // Marco del retrato en el centro
            int imgX = 55;
            int imgY = 70;
            int imgW = 180;
            int imgH = 120;
            g2.setColor(new Color(101, 67, 33));
            g2.fillRect(imgX, imgY, imgW, imgH);
            g2.setColor(new Color(212, 163, 89)); // Fondo claro del retrato
            g2.fillRect(imgX + 4, imgY + 4, imgW - 8, imgH - 8);

            // Dibujar Copa / Trofeo Pirata en el centro del retrato
            Font emojiFont = new Font("Segoe UI Emoji", Font.PLAIN, 48);
            g2.setFont(emojiFont);
            fm = g2.getFontMetrics(emojiFont);
            String trophy = "🏆";
            g2.drawString(trophy, imgX + (imgW - fm.stringWidth(trophy)) / 2, imgY + 80);

            // Nombre del torneo
            g2.setColor(new Color(60, 30, 10));
            g2.setFont(new Font("Arial Black", Font.BOLD, 13));
            fm = g2.getFontMetrics();
            String name = torneo.getNombre().toUpperCase();
            if (fm.stringWidth(name) > w - 40) {
                name = name.substring(0, Math.min(name.length(), 18)) + "...";
            }
            g2.drawString(name, (w - fm.stringWidth(name)) / 2, 218);

            // Fechas del torneo
            g2.setFont(new Font("Arial", Font.BOLD, 11));
            fm = g2.getFontMetrics();
            String dates = torneo.getFechaInicio() + " AL " + torneo.getFechaFin();
            g2.drawString(dates, (w - fm.stringWidth(dates)) / 2, 240);

            // Recompensa en Berries
            g2.setColor(new Color(153, 27, 27)); // Rojo carmesí
            g2.setFont(new Font("Georgia", Font.BOLD, 18));
            fm = g2.getFontMetrics();
            long bountyVal = 100000000L + (torneo.getId() * 50000000L);
            String bountyStr = String.format("฿ %,d-", bountyVal).replace(',', '.');
            g2.drawString(bountyStr, (w - fm.stringWidth(bountyStr)) / 2, 272);

            // Texto de Recompensa
            g2.setColor(new Color(60, 30, 10));
            g2.setFont(new Font("Times New Roman", Font.BOLD, 10));
            fm = g2.getFontMetrics();
            String bountyText = "RECOMPENSA";
            g2.drawString(bountyText, (w - fm.stringWidth(bountyText)) / 2, 290);

            g2.dispose();
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
