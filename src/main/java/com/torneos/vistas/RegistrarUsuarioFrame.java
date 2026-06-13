package com.torneos.vistas;

import com.torneos.dominio.Rol;
import com.torneos.dominio.User;
import com.torneos.persistencia.RolDAO;
import com.torneos.persistencia.UserDAO;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Vista rediseñada con el tema Brawl Stars para que los nuevos jugadores puedan registrar su propia cuenta.
 */
public class RegistrarUsuarioFrame extends JFrame {

    private JFrame parent;
    private JTextField txtNombre;
    private JTextField txtUsername;
    private JPasswordField txtPassword;

    public RegistrarUsuarioFrame(JFrame parent) {
        this.parent = parent;
        if (parent != null) {
            parent.setVisible(false);
        }
        initComponents();
    }
    private void initComponents() {
        setTitle("Registrar Cuenta - Gestión de Torneos");
        setSize(750, 560);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(Color.WHITE);
        setLayout(null);
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

        // Encabezado del Registro
        // Icono Escudo Izquierdo
        JLabel lblShieldLeft = new JLabel("🛡️");
        lblShieldLeft.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        lblShieldLeft.setBounds(120, 15, 50, 50);
        lblShieldLeft.setForeground(new Color(251, 191, 36));
        bgPanel.add(lblShieldLeft);

        // Título "Crear Cuenta"
        CustomAnimeTitleLabel lblTitulo = new CustomAnimeTitleLabel("CREAR CUENTA");
        lblTitulo.setBounds(170, 15, 410, 50);
        bgPanel.add(lblTitulo);

        // Icono Escudo Derecho
        JLabel lblShieldRight = new JLabel("🛡️");
        lblShieldRight.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        lblShieldRight.setBounds(590, 15, 50, 50);
        lblShieldRight.setForeground(new Color(251, 191, 36));
        bgPanel.add(lblShieldRight);

        // Elementos decorativos
        JLabel dec1 = new JLabel("🏴‍☠️");
        dec1.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        dec1.setBounds(60, 140, 50, 50);
        bgPanel.add(dec1);

        JLabel dec2 = new JLabel("⚓");
        dec2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        dec2.setBounds(50, 360, 50, 50);
        bgPanel.add(dec2);

        JLabel dec3 = new JLabel("🧭");
        dec3.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 30));
        dec3.setBounds(640, 160, 50, 50);
        bgPanel.add(dec3);

        JLabel dec4 = new JLabel("💰");
        dec4.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        dec4.setBounds(640, 380, 50, 50);
        bgPanel.add(dec4);

        // Panel Central 'Pergamino'
        JPanel registerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();
                int arc = 20;

                // Papel pergamino antiguo
                g2.setColor(new Color(244, 227, 193));
                g2.fillRoundRect(5, 5, w - 10, h - 10, arc, arc);

                // Borde de madera
                g2.setStroke(new BasicStroke(6));
                g2.setColor(new Color(101, 67, 33)); // Marrón oscuro
                g2.drawRoundRect(5, 5, w - 10, h - 10, arc, arc);

                // Borde interno oro
                g2.setStroke(new BasicStroke(2));
                g2.setColor(new Color(217, 119, 6)); // Oro viejo
                g2.drawRoundRect(10, 10, w - 20, h - 20, arc - 4, arc - 4);

                // Cuerda decorativa (Línea punteada)
                g2.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0));
                g2.setColor(new Color(180, 140, 90));
                g2.drawRoundRect(14, 14, w - 28, h - 28, arc - 6, arc - 6);

                // Remaches de oro
                int offset = 18;
                drawGoldRivet(g2, offset, offset);
                drawGoldRivet(g2, w - offset - 10, offset);
                drawGoldRivet(g2, offset, h - offset - 10);
                drawGoldRivet(g2, w - offset - 10, h - offset - 10);

                g2.dispose();
            }

            private void drawGoldRivet(Graphics2D g2, int x, int y) {
                g2.setColor(new Color(50, 30, 10, 150));
                g2.fillOval(x + 2, y + 2, 12, 12);
                GradientPaint gp = new GradientPaint(x, y, new Color(251, 191, 36), x + 8, y + 8, new Color(180, 83, 9));
                g2.setPaint(gp);
                g2.fillOval(x, y, 12, 12);
                g2.setStroke(new BasicStroke(1));
                g2.setColor(new Color(120, 53, 4));
                g2.drawOval(x, y, 12, 12);
            }
        };
        registerPanel.setLayout(null);
        registerPanel.setOpaque(false);
        registerPanel.setBounds(150, 80, 450, 420);
        bgPanel.add(registerPanel);

        // Subtítulo "REGISTRARSE"
        CustomAnimeSubLabel lblSub = new CustomAnimeSubLabel("REGISTRARSE");
        lblSub.setBounds(30, 20, 390, 40);
        registerPanel.add(lblSub);

        // Nombre Completo
        JLabel lblNombre = new JLabel("NOMBRE COMPLETO:");
        lblNombre.setFont(new Font("Arial Black", Font.BOLD, 12));
        lblNombre.setForeground(new Color(60, 35, 20));
        lblNombre.setBounds(40, 70, 370, 20);
        registerPanel.add(lblNombre);

        txtNombre = new ParchmentTextField("👊", new Color(139, 92, 26));
        txtNombre.setBounds(40, 90, 370, 38);
        registerPanel.add(txtNombre);

        // Usuario
        JLabel lblUsername = new JLabel("USUARIO:");
        lblUsername.setFont(new Font("Arial Black", Font.BOLD, 12));
        lblUsername.setForeground(new Color(60, 35, 20));
        lblUsername.setBounds(40, 135, 370, 20);
        registerPanel.add(lblUsername);

        txtUsername = new ParchmentTextField("⭐", new Color(139, 92, 26));
        txtUsername.setBounds(40, 155, 370, 38);
        registerPanel.add(txtUsername);

        // Subtítulo de dominio del usuario
        JLabel lblDomainHint = new JLabel("Se creará como usuario@torneos.com");
        lblDomainHint.setFont(new Font("Arial Black", Font.BOLD, 11));
        lblDomainHint.setForeground(new Color(139, 92, 26));
        lblDomainHint.setBounds(45, 195, 360, 15);
        registerPanel.add(lblDomainHint);

        // Contraseña
        JLabel lblPassword = new JLabel("CONTRASEÑA:");
        lblPassword.setFont(new Font("Arial Black", Font.BOLD, 12));
        lblPassword.setForeground(new Color(60, 35, 20));
        lblPassword.setBounds(40, 215, 370, 20);
        registerPanel.add(lblPassword);

        txtPassword = new ParchmentPasswordField("🔑", new Color(139, 92, 26));
        txtPassword.setBounds(40, 235, 370, 38);
        registerPanel.add(txtPassword);

        // Botón Registrar (Naranja)
        JButton btnRegistrar = new AnimeOrangeButton("REGISTRAR");
        btnRegistrar.setBounds(40, 295, 180, 44);
        btnRegistrar.addActionListener(e -> registrar());
        registerPanel.add(btnRegistrar);

        // Botón Cancelar (Rojo)
        JButton btnCancelar = new PirateCancelButton("CANCELAR");
        btnCancelar.setBounds(230, 295, 180, 44);
        btnCancelar.addActionListener(e -> cerrar());
        registerPanel.add(btnCancelar);

        // Manejar evento de cierre de ventana
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                cerrar();
            }
        });

        setVisible(true);
    }

    private void registrar() {
        String nombre = txtNombre.getText().trim();
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (nombre.isEmpty() || username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor completa todos los campos.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (username.contains(" ") || username.contains("@")) {
            JOptionPane.showMessageDialog(this, "El usuario no debe contener espacios ni el símbolo '@'.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String email = username.toLowerCase() + "@torneos.com";

        UserDAO userDAO = new UserDAO();
        List<User> todos = userDAO.obtenerTodos();
        for (User u : todos) {
            if (u.getEmail().equalsIgnoreCase(email)) {
                JOptionPane.showMessageDialog(this, "¡Este pirata ya está registrado!", "Atención", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        RolDAO rolDAO = new RolDAO();
        Rol rolJugador = null;
        for (Rol r : rolDAO.obtenerTodos()) {
            if ("JUGADOR".equalsIgnoreCase(r.getNombre())) {
                rolJugador = r;
                break;
            }
        }

        if (rolJugador == null) {
            JOptionPane.showMessageDialog(this, "Error de configuración: No se encontró el rol de JUGADOR.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User nuevoUsuario = new User();
        nuevoUsuario.setNombre(nombre);
        nuevoUsuario.setEmail(email);
        nuevoUsuario.setPassword(password);
        nuevoUsuario.setRol(rolJugador);

        boolean exito = userDAO.insertar(nuevoUsuario);
        if (exito) {
            JOptionPane.showMessageDialog(this, "¡Cuenta de pirata registrada correctamente!\nTu correo es: " + email, "Éxito", JOptionPane.INFORMATION_MESSAGE);
            cerrar();
        } else {
            JOptionPane.showMessageDialog(this, "Error al registrar la cuenta.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cerrar() {
        dispose();
        if (parent != null) {
            parent.setVisible(true);
        }
    }

    // =========================================================================
    // CLASES AUXILIARES DE DISEÑO ESTILIZADO (ONE PIECE)
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

            Font font = new Font("Arial Black", Font.BOLD, 30);
            g2.setFont(font);

            String text = getText();
            FontMetrics fm = g2.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(text)) / 2;
            int y = fm.getAscent() + (getHeight() - fm.getHeight()) / 2;

            java.awt.font.TextLayout tl = new java.awt.font.TextLayout(text, font, g2.getFontRenderContext());
            g2.translate(x, y);

            // Borde grueso negro
            g2.setColor(new Color(40, 20, 0));
            g2.setStroke(new BasicStroke(8, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.draw(tl.getOutline(null));

            // Degradado oro y amarillo brillante
            GradientPaint gp = new GradientPaint(0, -fm.getAscent(), new Color(253, 224, 71), 0, 0, new Color(234, 88, 12));
            g2.setPaint(gp);
            g2.fill(tl.getOutline(null));

            g2.dispose();
        }
    }

    private static class CustomAnimeSubLabel extends JLabel {
        public CustomAnimeSubLabel(String text) {
            super(text, SwingConstants.CENTER);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            Font font = new Font("Arial Black", Font.BOLD, 22);
            g2.setFont(font);

            String text = getText();
            FontMetrics fm = g2.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(text)) / 2;
            int y = fm.getAscent() + (getHeight() - fm.getHeight()) / 2;

            java.awt.font.TextLayout tl = new java.awt.font.TextLayout(text, font, g2.getFontRenderContext());
            g2.translate(x, y);

            // Borde grueso
            g2.setColor(new Color(40, 20, 0));
            g2.setStroke(new BasicStroke(6, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.draw(tl.getOutline(null));

            // Degradado rojo carmesí
            GradientPaint gp = new GradientPaint(0, -fm.getAscent(), new Color(239, 68, 68), 0, 0, new Color(153, 27, 27));
            g2.setPaint(gp);
            g2.fill(tl.getOutline(null));

            g2.dispose();
        }
    }

    private static class ParchmentTextField extends JTextField {
        private String icon;
        private Color borderColor;

        public ParchmentTextField(String icon, Color borderColor) {
            this.icon = icon;
            this.borderColor = borderColor;
            setOpaque(false);
            setFont(new Font("Arial Black", Font.BOLD, 14));
            setForeground(new Color(60, 35, 20)); // Marrón oscuro
            setCaretColor(new Color(60, 35, 20));
            setBorder(BorderFactory.createEmptyBorder(5, 45, 5, 10));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Fondo pergamino claro
            g2.setColor(new Color(252, 246, 229));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

            // Borde marrón
            g2.setStroke(new BasicStroke(2));
            g2.setColor(borderColor);
            g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 10, 10);

            // Icono
            g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
            g2.drawString(icon, 15, getHeight() / 2 + 7);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class ParchmentPasswordField extends JPasswordField {
        private String icon;
        private Color borderColor;

        public ParchmentPasswordField(String icon, Color borderColor) {
            this.icon = icon;
            this.borderColor = borderColor;
            setOpaque(false);
            setFont(new Font("Arial Black", Font.BOLD, 14));
            setForeground(new Color(60, 35, 20));
            setCaretColor(new Color(60, 35, 20));
            setBorder(BorderFactory.createEmptyBorder(5, 45, 5, 10));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Fondo pergamino claro
            g2.setColor(new Color(252, 246, 229));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

            // Borde marrón
            g2.setStroke(new BasicStroke(2));
            g2.setColor(borderColor);
            g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 10, 10);

            // Icono
            g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
            g2.drawString(icon, 15, getHeight() / 2 + 7);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class AnimeOrangeButton extends JButton {
        private boolean hover = false;

        public AnimeOrangeButton(String text) {
            super(text);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setFont(new Font("Arial Black", Font.BOLD, 16));
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
            g2.setColor(new Color(154, 52, 0));
            g2.fillRoundRect(0, 4, w, h - 4, 15, 15);

            // Cuerpo naranja/oro
            if (hover) {
                g2.setPaint(new GradientPaint(0, 0, new Color(251, 146, 60), 0, h, new Color(250, 204, 21)));
            } else {
                g2.setPaint(new GradientPaint(0, 0, new Color(249, 115, 22), 0, h, new Color(234, 88, 12)));
            }
            g2.fillRoundRect(0, 0, w, h - 4, 15, 15);

            // Línea de brillo
            g2.setColor(new Color(255, 255, 255, 100));
            g2.fillRoundRect(3, 3, w - 6, 4, 6, 6);

            // Borde blanco
            g2.setStroke(new BasicStroke(3));
            g2.setColor(Color.WHITE);
            g2.drawRoundRect(0, 0, w - 1, h - 5, 15, 15);

            // Texto con sombra
            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();
            String text = getText();
            int tx = (w - fm.stringWidth(text)) / 2;
            int ty = (h - fm.getHeight()) / 2 + fm.getAscent() - 2;

            g2.setColor(Color.BLACK);
            g2.drawString(text, tx + 2, ty + 2);

            g2.setColor(Color.WHITE);
            g2.drawString(text, tx, ty);

            g2.dispose();
        }
    }

    private static class PirateCancelButton extends JButton {
        private boolean hover = false;

        public PirateCancelButton(String text) {
            super(text);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setFont(new Font("Arial Black", Font.BOLD, 15));
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
            g2.setColor(new Color(100, 20, 20));
            g2.fillRoundRect(0, 4, w, h - 4, 15, 15);

            // Cuerpo rojo carmesí
            if (hover) {
                g2.setPaint(new GradientPaint(0, 0, new Color(239, 68, 68), 0, h, new Color(185, 28, 28)));
            } else {
                g2.setPaint(new GradientPaint(0, 0, new Color(185, 28, 28), 0, h, new Color(153, 27, 27)));
            }
            g2.fillRoundRect(0, 0, w, h - 4, 15, 15);

            // Línea de brillo
            g2.setColor(new Color(255, 255, 255, 100));
            g2.fillRoundRect(3, 3, w - 6, 4, 6, 6);

            // Borde blanco
            g2.setStroke(new BasicStroke(3));
            g2.setColor(Color.WHITE);
            g2.drawRoundRect(0, 0, w - 1, h - 5, 15, 15);

            // Texto con sombra
            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();
            String text = getText();
            int tx = (w - fm.stringWidth(text)) / 2;
            int ty = (h - fm.getHeight()) / 2 + fm.getAscent() - 2;

            g2.setColor(Color.BLACK);
            g2.drawString(text, tx + 2, ty + 2);

            g2.setColor(Color.WHITE);
            g2.drawString(text, tx, ty);

            g2.dispose();
        }
    }
}
