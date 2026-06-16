package com.torneos.vistas;

import com.torneos.dominio.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Vista para la Bitácora de Logs del sistema.
 * Rediseñada completamente con estética Brawl Stars.
 */
public class BitacoraLogsFrame extends JFrame {

    private User usuarioActual;
    private JTable tblLogs;
    private DefaultTableModel model;
    private JButton btnRegresar;

    public BitacoraLogsFrame(User user) {
        this.usuarioActual = user;
        initComponents();
        loadLogs();
    }

    private void initComponents() {
        setTitle("Bitácora de Logs - Gestión de Torneos");
        setSize(800, 520);
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

        // Botón regresar cian
        JButton btnVolver = new BrawlChunkyCyanButton("<");
        btnVolver.setBounds(20, 15, 55, 42);
        btnVolver.addActionListener(e -> dispose());
        bgPanel.add(btnVolver);

        // Título estilizado Brawl Stars
        CustomBrawlTitleLabel lblTitulo = new CustomBrawlTitleLabel("BITÁCORA DE LOGS DEL SISTEMA", 24);
        lblTitulo.setBounds(90, 15, 620, 42);
        bgPanel.add(lblTitulo);

        // Contenedor Central Metálico Dorado
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
        brawlCentralPanel.setBounds(30, 75, 720, 380);
        bgPanel.add(brawlCentralPanel);

        // Tabla de logs
        String[] columnas = {"Fecha / Hora", "Categoría", "Usuario", "Evento / Actividad"};
        model = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblLogs = new JTable(model);
        tblLogs.setFont(new Font("Arial Black", Font.BOLD, 11));
        tblLogs.setRowHeight(30);
        tblLogs.setBackground(new Color(20, 5, 40));
        tblLogs.setForeground(Color.WHITE);
        tblLogs.setGridColor(new Color(45, 10, 85));
        tblLogs.setShowGrid(true);
        tblLogs.setSelectionBackground(new Color(6, 182, 212));
        tblLogs.setSelectionForeground(Color.BLACK);

        // Ajustar anchos de columnas
        tblLogs.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        tblLogs.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
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
                setFont(new Font("Arial Black", Font.BOLD, 11));
                if (column == 3) {
                    setHorizontalAlignment(SwingConstants.LEFT);
                } else {
                    setHorizontalAlignment(SwingConstants.CENTER);
                }
                return c;
            }
        });

        tblLogs.getTableHeader().setDefaultRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setBackground(new Color(14, 116, 144));
                c.setForeground(Color.WHITE);
                c.setFont(new Font("Arial Black", Font.BOLD, 12));
                setHorizontalAlignment(SwingConstants.CENTER);
                setBorder(BorderFactory.createMatteBorder(0, 0, 2, 1, new Color(234, 179, 8))); // Dorado
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(tblLogs);
        scrollPane.setBounds(30, 30, 660, 250);
        scrollPane.getViewport().setBackground(new Color(20, 5, 40));
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(234, 179, 8), 2));
        brawlCentralPanel.add(scrollPane);

        // Botón Regresar Rojo Chunky
        btnRegresar = new BrawlChunkyRedButton("Regresar");
        btnRegresar.setBounds(260, 305, 200, 46);
        btnRegresar.addActionListener(e -> dispose());
        brawlCentralPanel.add(btnRegresar);

        // Decoraciones flotantes en la ventana
        JLabel decGema = new JLabel("💎");
        decGema.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        decGema.setBounds(15, 465, 40, 40);
        bgPanel.add(decGema);

        JLabel decPower = new JLabel("⚡");
        decPower.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        decPower.setBounds(745, 465, 40, 40);
        bgPanel.add(decPower);

        setVisible(true);
    }

    private void loadLogs() {
        model.setRowCount(0);
        model.addRow(new Object[]{"2026-06-15 09:30:11", "SEGURIDAD", usuarioActual.getEmail(), "Inicio de sesión exitoso como ADMINISTRADOR"});
        model.addRow(new Object[]{"2026-06-15 09:32:04", "CONEXION", "System", "Conexión con base de datos SQL Server establecida"});
        model.addRow(new Object[]{"2026-06-15 09:35:45", "TORNEO", usuarioActual.getEmail(), "Carga de estadísticas del sistema completada"});
        model.addRow(new Object[]{"2026-06-15 09:40:22", "EQUIPO", usuarioActual.getEmail(), "Consulta de lista de equipos inscritos"});
        model.addRow(new Object[]{"2026-06-15 10:15:30", "SEGURIDAD", "admin@torneos.com", "Inicio de sesión de administrador validado"});
        model.addRow(new Object[]{"2026-06-15 10:20:12", "JUGADOR", "carlos@torneos.com", "Nuevo registro de perfil de jugador creado"});
        model.addRow(new Object[]{"2026-06-15 11:28:50", "SISTEMA", "System", "Verificación de consistencia del sistema completada"});
        model.addRow(new Object[]{"2026-06-15 20:30:05", "SEGURIDAD", usuarioActual.getEmail(), "Acceso al módulo de logs auditados"});
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
