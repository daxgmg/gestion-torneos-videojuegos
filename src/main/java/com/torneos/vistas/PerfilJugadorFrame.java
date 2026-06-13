package com.torneos.vistas;

import com.torneos.dominio.Equipo;
import com.torneos.dominio.Jugador;
import com.torneos.dominio.User;
import com.torneos.persistencia.EquipoDAO;
import com.torneos.persistencia.JugadorDAO;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Vista para que el jugador logueado gestione su perfil de jugador y se una a un equipo.
 * Rediseñada con estética Brawl Stars (botones chunky, contenedor metálico, inputs con iconos).
 */
public class PerfilJugadorFrame extends JFrame {

    private User usuarioActual;
    private JugadorDAO jugadorDAO = new JugadorDAO();
    private EquipoDAO equipoDAO = new EquipoDAO();

    private JTextField txtNombre;
    private JTextField txtAlias;
    private JComboBox<ComboItem> cmbEquipo;
    private JButton btnGuardar;
    private Jugador jugadorExistente;

    public PerfilJugadorFrame(User user) {
        this.usuarioActual = user;
        initComponents();
        loadEquipos();
        loadPerfil();
    }

    private void initComponents() {
        setTitle("Mi Perfil - Gestión de Torneos");
        setSize(600, 500);
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

        // Título "MI PERFIL" estilizado Brawl
        CustomBrawlTitleLabel lblTitulo = new CustomBrawlTitleLabel("MI PERFIL", 26);
        lblTitulo.setBounds(90, 15, 420, 42);
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
        brawlCentralPanel.setBounds(40, 75, 520, 360);
        bgPanel.add(brawlCentralPanel);

        // Nombre
        JLabel lblNombre = new JLabel("Nombre Completo:");
        lblNombre.setFont(new Font("Arial Black", Font.BOLD, 12));
        lblNombre.setForeground(new Color(254, 240, 138)); // Amarillo brillante
        lblNombre.setBounds(60, 25, 400, 20);
        brawlCentralPanel.add(lblNombre);

        txtNombre = new BrawlTextField("👊");
        txtNombre.setBounds(60, 48, 400, 38);
        brawlCentralPanel.add(txtNombre);

        // Alias
        JLabel lblAlias = new JLabel("Alias / Gamertag:");
        lblAlias.setFont(new Font("Arial Black", Font.BOLD, 12));
        lblAlias.setForeground(new Color(254, 240, 138));
        lblAlias.setBounds(60, 100, 400, 20);
        brawlCentralPanel.add(lblAlias);

        txtAlias = new BrawlTextField("👤");
        txtAlias.setBounds(60, 123, 400, 38);
        brawlCentralPanel.add(txtAlias);

        // Equipo
        JLabel lblEquipo = new JLabel("Selecciona tu Equipo:");
        lblEquipo.setFont(new Font("Arial Black", Font.BOLD, 12));
        lblEquipo.setForeground(new Color(254, 240, 138));
        lblEquipo.setBounds(60, 175, 400, 20);
        brawlCentralPanel.add(lblEquipo);

        cmbEquipo = new JComboBox<>();
        cmbEquipo.setFont(new Font("Arial Black", Font.BOLD, 14));
        cmbEquipo.setBounds(60, 198, 400, 38);
        cmbEquipo.setBackground(new Color(20, 5, 40));
        cmbEquipo.setForeground(Color.WHITE);
        cmbEquipo.setBorder(BorderFactory.createLineBorder(new Color(14, 116, 144), 2));
        cmbEquipo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                lbl.setBackground(isSelected ? new Color(14, 116, 144) : new Color(20, 5, 40));
                lbl.setForeground(Color.WHITE);
                lbl.setFont(new Font("Arial Black", Font.BOLD, 13));
                lbl.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
                return lbl;
            }
        });
        brawlCentralPanel.add(cmbEquipo);

        // Guardar (Botón Amarillo Chunky)
        btnGuardar = new BrawlChunkyYellowButton("💾 Guardar Perfil");
        btnGuardar.setBounds(60, 275, 400, 46);
        btnGuardar.addActionListener(e -> guardarPerfil());
        brawlCentralPanel.add(btnGuardar);

        // Decoraciones extras
        JLabel decGema = new JLabel("💎");
        decGema.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        decGema.setBounds(15, 435, 40, 40);
        bgPanel.add(decGema);

        JLabel decPower = new JLabel("⚡");
        decPower.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        decPower.setBounds(545, 435, 40, 40);
        bgPanel.add(decPower);

        setVisible(true);
    }

    private void loadEquipos() {
        cmbEquipo.removeAllItems();
        List<Equipo> equipos = equipoDAO.obtenerTodos();
        for (Equipo eq : equipos) {
            cmbEquipo.addItem(new ComboItem(eq.getId(), eq.getNombre()));
        }
    }

    private void loadPerfil() {
        List<Jugador> jugadores = jugadorDAO.obtenerTodos();
        for (Jugador j : jugadores) {
            if (j.getUser() != null && j.getUser().getId() == usuarioActual.getId()) {
                jugadorExistente = j;
                break;
            }
        }

        if (jugadorExistente != null) {
            txtNombre.setText(jugadorExistente.getNombre());
            txtAlias.setText(jugadorExistente.getAlias());

            if (jugadorExistente.getEquipo() != null) {
                for (int i = 0; i < cmbEquipo.getItemCount(); i++) {
                    if (cmbEquipo.getItemAt(i).id == jugadorExistente.getEquipo().getId()) {
                        cmbEquipo.setSelectedIndex(i);
                        break;
                    }
                }
            }
        }
    }

    private void guardarPerfil() {
        String nombre = txtNombre.getText().trim();
        String alias = txtAlias.getText().trim();
        ComboItem equipoItem = (ComboItem) cmbEquipo.getSelectedItem();

        if (nombre.isEmpty() || alias.isEmpty() || equipoItem == null) {
            JOptionPane.showMessageDialog(this, "Por favor completa todos los campos y selecciona un equipo.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Equipo eq = new Equipo();
        eq.setId(equipoItem.id);
        eq.setNombre(equipoItem.name);

        boolean ok;
        if (jugadorExistente == null) {
            Jugador j = new Jugador();
            j.setNombre(nombre);
            j.setAlias(alias);
            j.setEquipo(eq);
            j.setUser(usuarioActual);
            ok = jugadorDAO.insertar(j);
            if (ok) {
                jugadorExistente = j;
            }
        } else {
            jugadorExistente.setNombre(nombre);
            jugadorExistente.setAlias(alias);
            jugadorExistente.setEquipo(eq);
            ok = jugadorDAO.actualizar(jugadorExistente);
        }

        if (ok) {
            JOptionPane.showMessageDialog(this, "Perfil y equipo guardados correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Error al guardar el perfil.", "Error", JOptionPane.ERROR_MESSAGE);
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

    private static class BrawlChunkyYellowButton extends JButton {
        private boolean hover = false;

        public BrawlChunkyYellowButton(String text) {
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
            g2.setColor(new Color(180, 83, 9));
            g2.fillRoundRect(0, 4, w, h - 4, arc, arc);

            // Gradiente amarillo
            if (hover) {
                g2.setPaint(new GradientPaint(0, 0, new Color(254, 240, 138), 0, h, new Color(234, 88, 12)));
            } else {
                g2.setPaint(new GradientPaint(0, 0, new Color(253, 224, 71), 0, h, new Color(217, 119, 6)));
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

    private static class BrawlTextField extends JTextField {
        private String emoji;

        public BrawlTextField(String emoji) {
            this.emoji = emoji;
            setOpaque(false);
            setFont(new Font("Arial Black", Font.BOLD, 14));
            setForeground(Color.WHITE);
            setCaretColor(Color.WHITE);
            setBorder(BorderFactory.createEmptyBorder(5, 45, 5, 10));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int arc = 15;

            // Fondo barra
            g2.setColor(new Color(20, 5, 40));
            g2.fillRoundRect(0, 0, w, h, arc, arc);

            // Borde
            g2.setStroke(new BasicStroke(2.5f));
            g2.setColor(new Color(14, 116, 144));
            g2.drawRoundRect(1, 1, w - 2, h - 2, arc, arc);

            // Emoji
            g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
            g2.drawString(emoji, 12, h / 2 + 7);

            g2.dispose();
            super.paintComponent(g);
        }
    }

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

