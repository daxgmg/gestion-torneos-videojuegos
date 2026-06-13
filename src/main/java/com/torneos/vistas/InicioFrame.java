package com.torneos.vistas;

import javax.swing.*;
import java.awt.*;

/**
 * Pantalla de Inicio (Zarpar) con estética de anime de One Piece.
 * Muestra el barco Thousand Sunny navegando hacia el horizonte con un botón central para iniciar.
 */
public class InicioFrame extends JFrame {

    public InicioFrame() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Gestión de Torneos - One Piece Edition");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(750, 560);
        setLocationRelativeTo(null);
        setResizable(false);

        // Fondo Dinámico (Thousand Sunny)
        JPanel bgPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Image img = obtenerImagenFondo("sunny_bg.png");
                if (img != null) {
                    g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        bgPanel.setLayout(null);
        setContentPane(bgPanel);

        // Título del juego en estilo anime grande y centrado
        CustomAnimeTitleLabel lblTitulo = new CustomAnimeTitleLabel("GESTIÓN DE TORNEOS");
        lblTitulo.setBounds(100, 80, 550, 80);
        bgPanel.add(lblTitulo);

        // Subtítulo pirata
        JLabel lblSub = new JLabel("¡FORMA TU TRIPULACIÓN Y CONQUISTA LOS MARES!", SwingConstants.CENTER);
        lblSub.setFont(new Font("Arial Black", Font.BOLD, 14));
        lblSub.setForeground(new Color(254, 240, 138)); // Amarillo brillante
        lblSub.setBounds(100, 160, 550, 30);
        // Shadow effect
        JLabel lblSubShadow = new JLabel("¡FORMA TU TRIPULACIÓN Y CONQUISTA LOS MARES!", SwingConstants.CENTER);
        lblSubShadow.setFont(new Font("Arial Black", Font.BOLD, 14));
        lblSubShadow.setForeground(Color.BLACK);
        lblSubShadow.setBounds(102, 162, 550, 30);
        bgPanel.add(lblSub);
        bgPanel.add(lblSubShadow);

        // Botón ¡Zarpar! en el centro
        JButton btnZarpar = new AnimeZarparButton("¡ZARPAR!");
        btnZarpar.setBounds(250, 300, 250, 55);
        btnZarpar.addActionListener(e -> {
            dispose();
            new LoginFrame();
        });
        bgPanel.add(btnZarpar);

        setVisible(true);
    }

    // =========================================================================
    // CLASES AUXILIARES DE DISEÑO ANIME (ONE PIECE)
    // =========================================================================

    private static class CustomAnimeTitleLabel extends JLabel {
        public CustomAnimeTitleLabel(String text) {
            super(text, SwingConstants.CENTER);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            Font font = new Font("Arial Black", Font.BOLD, 36);
            g2.setFont(font);

            String text = getText();
            FontMetrics fm = g2.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(text)) / 2;
            int y = fm.getAscent() + (getHeight() - fm.getHeight()) / 2;

            java.awt.font.TextLayout tl = new java.awt.font.TextLayout(text, font, g2.getFontRenderContext());
            g2.translate(x, y);

            // Borde negro grueso típico de anime/manga
            g2.setColor(new Color(40, 20, 0));
            g2.setStroke(new BasicStroke(10, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.draw(tl.getOutline(null));

            // Degradado oro y amarillo brillante (One Piece Logo Style)
            GradientPaint gp = new GradientPaint(0, -fm.getAscent(), new Color(253, 224, 71), 0, 0, new Color(234, 88, 12));
            g2.setPaint(gp);
            g2.fill(tl.getOutline(null));

            g2.dispose();
        }
    }

    private static class AnimeZarparButton extends JButton {
        private boolean hover = false;

        public AnimeZarparButton(String text) {
            super(text);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setFont(new Font("Arial Black", Font.BOLD, 22));
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

            // Sombra 3D
            g2.setColor(new Color(154, 52, 0));
            g2.fillRoundRect(0, 6, w, h - 6, 20, 20);

            // Color del botón (Naranja vibrante a Oro)
            if (hover) {
                g2.setPaint(new GradientPaint(0, 0, new Color(251, 146, 60), 0, h, new Color(250, 204, 21)));
            } else {
                g2.setPaint(new GradientPaint(0, 0, new Color(249, 115, 22), 0, h, new Color(234, 88, 12)));
            }
            g2.fillRoundRect(0, 0, w, h - 6, 20, 20);

            // Brillo superior
            g2.setColor(new Color(255, 255, 255, 100));
            g2.fillRoundRect(4, 4, w - 8, 5, 8, 8);

            // Borde rústico blanco
            g2.setStroke(new BasicStroke(4));
            g2.setColor(Color.WHITE);
            g2.drawRoundRect(0, 0, w - 1, h - 7, 20, 20);

            // Texto con sombra
            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();
            String text = getText();
            int tx = (w - fm.stringWidth(text)) / 2;
            int ty = (h - fm.getHeight()) / 2 + fm.getAscent() - 3;

            g2.setColor(Color.BLACK);
            g2.drawString(text, tx + 2, ty + 2);

            g2.setColor(Color.WHITE);
            g2.drawString(text, tx, ty);

            g2.dispose();
        }
    }

    private static Image obtenerImagenFondo(String nombreArchivo) {
        String[] paths = {
            nombreArchivo,
            "../" + nombreArchivo,
            "gestion-torneos-videojuegos/" + nombreArchivo,
            "src/main/resources/" + nombreArchivo,
            "target/classes/" + nombreArchivo
        };
        for (String p : paths) {
            java.io.File file = new java.io.File(p);
            if (file.exists()) {
                return new ImageIcon(p).getImage();
            }
        }
        
        // Intentar por ClassLoader
        try {
            java.net.URL url = InicioFrame.class.getResource("/" + nombreArchivo);
            if (url != null) {
                return new ImageIcon(url).getImage();
            }
        } catch (Exception e) {}
        
        try {
            java.net.URL url = ClassLoader.getSystemResource(nombreArchivo);
            if (url != null) {
                return new ImageIcon(url).getImage();
            }
        } catch (Exception e) {}

        return null;
    }
}
