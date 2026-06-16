package com.torneos.vistas;

import com.torneos.dominio.User;
import com.torneos.dominio.Torneo;
import com.torneos.dominio.Equipo;
import com.torneos.persistencia.TorneoDAO;
import com.torneos.persistencia.EquipoDAO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Panel de Administración rediseñado con estética de Brawl Stars.
 * Presenta un menú horizontal de operaciones, un dashboard con tarjetas chunky
 * y controles dinámicos conectados a la base de datos.
 */
public class MenuAdminFrame extends JFrame {

    private User usuarioActual;
    private TorneoDAO torneoDAO = new TorneoDAO();
    private EquipoDAO equipoDAO = new EquipoDAO();

    public MenuAdminFrame(User user) {
        this.usuarioActual = user;
        initComponents();
    }

    private void initComponents() {
        setTitle("Panel de Administración - Gestión de Torneos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(850, 600);
        setLocationRelativeTo(null);
        setResizable(false);

        // Fondo de Pantalla Principal (Brawl Stars o Fallback de Gradiente)
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

        // Encabezado y Barra de Estado
        CustomBrawlTitleLabel lblTitulo = new CustomBrawlTitleLabel("PANEL DE CONTROL DE ADMINISTRADOR", 22);
        lblTitulo.setBounds(25, 15, 520, 42);
        bgPanel.add(lblTitulo);

        JLabel lblSesion = new JLabel("Sesión: Administrador", SwingConstants.RIGHT);
        lblSesion.setFont(new Font("Arial Black", Font.BOLD, 13));
        lblSesion.setForeground(new Color(254, 240, 138)); // Amarillo brillante
        lblSesion.setBounds(560, 20, 250, 30);
        bgPanel.add(lblSesion);

        // Menú de Operaciones Horizontal (Superior)
        JPanel menuHorizontalPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 15));
        menuHorizontalPanel.setOpaque(false);
        menuHorizontalPanel.setBounds(25, 70, 800, 80);
        bgPanel.add(menuHorizontalPanel);

        // Botones superiores de operaciones (Tokens / Medallas)
        menuHorizontalPanel.add(crearBrawlBoton("👥", "Gestionar Usuarios", e -> new UserFrame(usuarioActual)));
        menuHorizontalPanel.add(crearBrawlBoton("📢", "Crear Torneo", e -> new TorneoFrame(usuarioActual)));
        menuHorizontalPanel.add(crearBrawlBoton("🛡️", "Control de Equipos", e -> new EquipoFrame(usuarioActual)));
        menuHorizontalPanel.add(crearBrawlBoton("📜", "Bitácora de Logs", e -> new BitacoraLogsFrame(usuarioActual)));

        // Contenedor Principal de Control (Estilo Brawl)
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
        brawlCentralPanel.setBounds(25, 160, 800, 340);
        bgPanel.add(brawlCentralPanel);

        // Título del Dashboard interno
        JLabel lblDashboard = new JLabel("RESUMEN DE ESTADÍSTICAS DEL SISTEMA", SwingConstants.CENTER);
        lblDashboard.setFont(new Font("Arial Black", Font.BOLD, 14));
        lblDashboard.setForeground(new Color(14, 165, 233)); // Azul cian
        lblDashboard.setBounds(50, 25, 700, 25);
        brawlCentralPanel.add(lblDashboard);

        // Obtener estadísticas dinámicas
        long torneosActivos = 0;
        long equiposInscritos = 0;
        try {
            List<Torneo> torneos = torneoDAO.obtenerTodos();
            torneosActivos = torneos.stream().filter(t -> "ACTIVO".equalsIgnoreCase(t.getEstado())).count();
            equiposInscritos = equipoDAO.obtenerTodos().size();
        } catch (Exception e) {
            System.err.println("[MenuAdminFrame] Error al cargar estadísticas: " + e.getMessage());
        }

        // Tarjetas chunky de estadísticas
        BrawlStatsCard cardTorneos = new BrawlStatsCard("TORNEOS ACTIVOS", String.valueOf(torneosActivos), new Color(6, 182, 212));
        cardTorneos.setBounds(100, 75, 260, 210);
        brawlCentralPanel.add(cardTorneos);

        BrawlStatsCard cardEquipos = new BrawlStatsCard("EQUIPOS INSCRITOS", String.valueOf(equiposInscritos), new Color(249, 115, 22));
        cardEquipos.setBounds(440, 75, 260, 210);
        brawlCentralPanel.add(cardEquipos);

        // Botón Cerrar Sesión (Estilo Rojo Chunky Brawl)
        JButton btnCerrar = new BrawlChunkyRedButton("Cerrar Sesión");
        btnCerrar.setBounds(645, 515, 180, 44);
        btnCerrar.addActionListener(e -> {
            dispose();
            new LoginFrame();
        });
        bgPanel.add(btnCerrar);

        // Elementos decorativos (Gemas y Power-ups)
        JLabel decGema = new JLabel("💎");
        decGema.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        decGema.setBounds(25, 510, 50, 50);
        bgPanel.add(decGema);

        JLabel decPower = new JLabel("⚡");
        decPower.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        decPower.setBounds(85, 510, 50, 50);
        bgPanel.add(decPower);

        setVisible(true);
    }

    private JButton crearBrawlBoton(String icon, String texto, ActionListener listener) {
        JButton btn = new BrawlTokenButton(icon, texto);
        btn.addActionListener(listener);
        return btn;
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
            super(text, SwingConstants.LEFT);
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
            int x = 0;
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

    private static class BrawlTokenButton extends JButton {
        private boolean hover = false;
        private String iconText;

        public BrawlTokenButton(String iconText, String text) {
            super(text);
            this.iconText = iconText;
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setFont(new Font("Arial Black", Font.BOLD, 11));
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

            // Dibujar icono y texto
            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();
            String fullText = iconText + " " + getText();
            int tx = (w - fm.stringWidth(fullText)) / 2;
            int ty = (h - fm.getHeight()) / 2 + fm.getAscent() - 2;

            g2.setColor(Color.BLACK);
            g2.drawString(fullText, tx + 1, ty + 1);

            g2.setColor(Color.WHITE);
            g2.drawString(fullText, tx, ty);

            g2.dispose();
        }

        @Override
        public Dimension getPreferredSize() {
            FontMetrics fm = getFontMetrics(getFont());
            int width = fm.stringWidth(iconText + " " + getText()) + 35;
            return new Dimension(Math.max(width, 170), 44);
        }
    }

    private static class BrawlChunkyRedButton extends JButton {
        private boolean hover = false;

        public BrawlChunkyRedButton(String text) {
            super(text);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setFont(new Font("Arial Black", Font.BOLD, 13));
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

    private static class BrawlStatsCard extends JPanel {
        private String titulo;
        private String valor;
        private Color cardColor;

        public BrawlStatsCard(String titulo, String valor, Color cardColor) {
            this.titulo = titulo;
            this.valor = valor;
            this.cardColor = cardColor;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int arc = 20;

            // Sombra
            g2.setColor(new Color(20, 5, 40, 150));
            g2.fillRoundRect(0, 6, w, h - 6, arc, arc);

            // Cuerpo de la tarjeta
            g2.setColor(cardColor);
            g2.fillRoundRect(0, 0, w, h - 6, arc, arc);

            // Borde grueso oscuro
            g2.setStroke(new BasicStroke(4));
            g2.setColor(new Color(20, 5, 40));
            g2.drawRoundRect(0, 0, w - 1, h - 7, arc, arc);

            // Brillo interno
            g2.setStroke(new BasicStroke(1.5f));
            g2.setColor(new Color(255, 255, 255, 100));
            g2.drawRoundRect(3, 3, w - 7, h - 13, arc - 2, arc - 2);

            // Título de la tarjeta
            g2.setFont(new Font("Arial Black", Font.BOLD, 13));
            FontMetrics fm = g2.getFontMetrics();
            int tx = (w - fm.stringWidth(titulo)) / 2;
            int ty = 40;

            // Sombra texto título
            g2.setColor(Color.BLACK);
            g2.drawString(titulo, tx + 1, ty + 1);
            g2.drawString(titulo, tx + 2, ty + 2);
            // Texto amarillo
            g2.setColor(new Color(254, 240, 138));
            g2.drawString(titulo, tx, ty);

            // Valor numérico gigante
            Font giantFont = new Font("Arial Black", Font.BOLD, 54);
            g2.setFont(giantFont);
            FontMetrics fmVal = g2.getFontMetrics();
            int vx = (w - fmVal.stringWidth(valor)) / 2;
            int vy = h / 2 + fmVal.getAscent() / 2 + 10;

            // Sombra valor
            g2.setColor(Color.BLACK);
            g2.drawString(valor, vx + 2, vy + 2);
            g2.drawString(valor, vx + 4, vy + 4);
            // Texto blanco
            g2.setColor(Color.WHITE);
            g2.drawString(valor, vx, vy);

            g2.dispose();
        }
    }
}