package com.torneos.vistas;

import com.torneos.dominio.Rol;
import com.torneos.dominio.User;
import com.torneos.persistencia.RolDAO;
import com.torneos.persistencia.UserDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;

/**
 * Pantalla de Gestión de Usuarios rediseñada con la estética oficial, épica y náutica
 * de One Piece. Simula un pergamino antiguo/mapa náutico con bordes de madera
 * de barco pirata, tabla estilo ledger de tripulación y ventanas de WANTED.
 */
public class UserFrame extends JFrame {

    private User usuarioActual;
    private UserDAO userDAO = new UserDAO();
    private RolDAO rolDAO = new RolDAO();
    private JTable tabla;
    private DefaultTableModel modeloTabla;

    public UserFrame(User user) {
        this.usuarioActual = user;
        initComponents();
        cargarDatos();
    }

    private void initComponents() {
        setTitle("Control de Tripulantes y Usuarios");
        setSize(850, 580);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Contenedor principal con fondo de mapa náutico sepia y bordes de madera
        OnePieceBackgroundPanel mainPanel = new OnePieceBackgroundPanel();

        // Encabezado al estilo Marina/Pirata (Azul marino profundo con detalles dorados neón)
        OnePieceHeaderPanel headerPanel = new OnePieceHeaderPanel();

        JButton btnVolver = crearBotonMadera("VOLVER", e -> dispose());
        btnVolver.setPreferredSize(new Dimension(110, 38));
        headerPanel.add(btnVolver);

        JLabel lblTitulo = new JLabel("CONTROL DE TRIPULANTES Y USUARIOS ☠️");
        lblTitulo.setFont(new Font("Impact", Font.ITALIC, 20));
        lblTitulo.setForeground(new Color(255, 215, 0)); // Amarillo neón / brillante
        headerPanel.add(lblTitulo);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Tabla de usuarios diseñada como Registro de Tripulación
        String[] columnas = { "ID", "Nombre Completo", "Alias / Gamertag", "Rol" };
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tabla = new JTable(modeloTabla);
        tabla.setFont(new Font("Arial Black", Font.PLAIN, 12));
        tabla.setRowHeight(32);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setGridColor(new Color(210, 190, 160));
        tabla.setShowGrid(true);

        // Personalización de la cabecera
        tabla.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setOpaque(true);
                lbl.setBackground(new Color(11, 29, 58)); // Azul marino profundo
                lbl.setForeground(new Color(212, 175, 55)); // Dorado brillante
                lbl.setFont(new Font("Arial Black", Font.BOLD, 12));
                lbl.setHorizontalAlignment(JLabel.CENTER);
                lbl.setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, new Color(212, 175, 55)));
                return lbl;
            }
        });

        // Personalización de las celdas (Efecto Pergamino)
        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setOpaque(true);
                if (isSelected) {
                    lbl.setBackground(new Color(139, 90, 43)); // Madera seleccionada
                    lbl.setForeground(new Color(254, 252, 232)); // Texto crema
                    lbl.setFont(new Font("Arial Black", Font.BOLD, 12));
                } else {
                    if (row % 2 == 0) {
                        lbl.setBackground(new Color(250, 240, 215)); // Pergamino claro
                    } else {
                        lbl.setBackground(new Color(242, 226, 189)); // Pergamino oscuro
                    }
                    lbl.setForeground(new Color(50, 35, 20)); // Sepia
                    lbl.setFont(new Font("Arial Black", Font.PLAIN, 11));
                }
                lbl.setHorizontalAlignment(JLabel.CENTER);
                lbl.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(210, 190, 160)));
                return lbl;
            }
        });

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(90, 60, 30), 3),
                BorderFactory.createEmptyBorder(2, 2, 2, 2)
        ));

        mainPanel.add(scroll, BorderLayout.CENTER);

        // Panel inferior para botones estilo tablón de madera chunky
        JPanel botonesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        botonesPanel.setOpaque(false);

        JButton btnNuevo = crearBotonMadera("Nuevo Usuario", e -> mostrarFormulario(null));
        JButton btnEditar = crearBotonOroMadera("Editar Rol", e -> editarSeleccionado());
        JButton btnEliminar = crearBotonRojoMadera("Dar de Baja", e -> eliminarSeleccionado());

        btnNuevo.setPreferredSize(new Dimension(170, 42));
        btnEditar.setPreferredSize(new Dimension(170, 42));
        btnEliminar.setPreferredSize(new Dimension(170, 42));

        botonesPanel.add(btnNuevo);
        botonesPanel.add(btnEditar);
        botonesPanel.add(btnEliminar);

        mainPanel.add(botonesPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    private void cargarDatos() {
        modeloTabla.setRowCount(0);
        for (User u : userDAO.obtenerTodos()) {
            modeloTabla.addRow(new Object[] {
                    u.getId(), u.getNombre(), u.getEmail(),
                    u.getRol() != null ? u.getRol().getNombre() : ""
            });
        }
    }

    private void mostrarFormulario(User user) {
        String tituloAccion = (user == null) ? "Nuevo Usuario" : "Editar Usuario";
        JDialog dialog = new JDialog(this, "Crear / Editar Usuario", true);
        dialog.setSize(440, 430);
        dialog.setLocationRelativeTo(this);

        // Panel decorado como póster de Wanted de One Piece
        WantedPosterPanel wantedPanel = new WantedPosterPanel(tituloAccion);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);

        JTextField txtNombre = crearTextField(user != null ? user.getNombre() : "");
        JTextField txtEmail = crearTextField(user != null ? user.getEmail() : "");
        
        JPasswordField txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Arial Black", Font.PLAIN, 12));
        txtPassword.setBackground(new Color(254, 252, 232));
        txtPassword.setForeground(new Color(60, 40, 20));
        txtPassword.setCaretColor(new Color(90, 60, 30));
        txtPassword.setOpaque(true);
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(90, 60, 30), 2),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        txtPassword.setPreferredSize(new Dimension(200, 34));

        List<Rol> roles = rolDAO.obtenerTodos();
        JComboBox<String> cmbRol = crearComboBoxMadera(new String[0]);
        for (Rol r : roles)
            cmbRol.addItem(r.getNombre());
        if (user != null && user.getRol() != null)
            cmbRol.setSelectedItem(user.getRol().getNombre());

        agregarFila(wantedPanel, gbc, 0, "Nombre Completo:", txtNombre);
        agregarFila(wantedPanel, gbc, 1, "Alias / Gamertag:", txtEmail);
        agregarFila(wantedPanel, gbc, 2, "Contraseña:", txtPassword);
        agregarFila(wantedPanel, gbc, 3, "Rol de Tripulación:", cmbRol);

        JButton btnGuardar = crearBotonGuardar("💾 GUARDAR", e -> {
            String nombre = txtNombre.getText().trim();
            String email = txtEmail.getText().trim();
            String password = new String(txtPassword.getPassword()).trim();
            String rolSeleccionado = (String) cmbRol.getSelectedItem();

            if (nombre.isEmpty() || email.isEmpty() || (user == null && password.isEmpty())) {
                JOptionPane.showMessageDialog(dialog, "Todos los campos obligatorios deben completarse.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Rol rolObj = roles.stream()
                    .filter(r -> r.getNombre().equals(rolSeleccionado))
                    .findFirst().orElse(null);

            User u = user != null ? user : new User();
            u.setNombre(nombre);
            u.setEmail(email);
            if (!password.isEmpty())
                u.setPassword(password);
            u.setRol(rolObj);

            boolean ok = (user == null) ? userDAO.insertar(u) : userDAO.actualizar(u);
            if (ok) {
                JOptionPane.showMessageDialog(dialog, "¡Tripulante guardado en los archivos de la Marina!");
                dialog.dispose();
                cargarDatos();
            } else {
                JOptionPane.showMessageDialog(dialog, "Error al registrar tripulante.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 8, 8, 8);
        wantedPanel.add(btnGuardar, gbc);

        dialog.add(wantedPanel);
        dialog.setVisible(true);
    }

    private void editarSeleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona un tripulante de la lista.");
            return;
        }
        int id = (int) modeloTabla.getValueAt(fila, 0);
        User u = userDAO.buscarPorId(id);
        if (u != null)
            mostrarFormulario(u);
    }

    private void eliminarSeleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona al tripulante a dar de baja.");
            return;
        }
        int id = (int) modeloTabla.getValueAt(fila, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "¿Deseas dar de baja (eliminar) a este tripulante del barco?", "Confirmar",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (userDAO.eliminar(id))
                cargarDatos();
            else
                JOptionPane.showMessageDialog(this, "Error al dar de baja al tripulante.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // =========================================================================
    // Componentes y Estilizado Personalizado
    // =========================================================================

    private JTextField crearTextField(String valor) {
        JTextField txt = new JTextField(valor);
        txt.setFont(new Font("Arial Black", Font.PLAIN, 12));
        txt.setBackground(new Color(254, 252, 232)); // Pergamino claro
        txt.setForeground(new Color(60, 40, 20));
        txt.setCaretColor(new Color(90, 60, 30));
        txt.setOpaque(true);
        txt.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(90, 60, 30), 2),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        txt.setPreferredSize(new Dimension(200, 34));
        return txt;
    }

    private JComboBox<String> crearComboBoxMadera(String[] items) {
        JComboBox<String> cmb = new JComboBox<>(items);
        cmb.setFont(new Font("Arial Black", Font.PLAIN, 12));
        cmb.setBackground(new Color(254, 252, 232));
        cmb.setForeground(new Color(60, 40, 20));
        cmb.setBorder(BorderFactory.createLineBorder(new Color(90, 60, 30), 2));
        cmb.setPreferredSize(new Dimension(200, 34));
        return cmb;
    }

    private void agregarFila(JPanel p, GridBagConstraints g, int fila, String label, JComponent comp) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Arial Black", Font.BOLD, 12));
        lbl.setForeground(new Color(60, 40, 20)); // Sepia
        g.gridx = 0;
        g.gridy = fila;
        g.gridwidth = 1;
        g.anchor = GridBagConstraints.WEST;
        p.add(lbl, g);
        g.gridx = 1;
        p.add(comp, g);
    }

    private JButton crearBotonMadera(String texto, java.awt.event.ActionListener l) {
        JButton btn = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                // Madera con gradiente
                Color c1 = new Color(139, 90, 43); 
                Color c2 = new Color(90, 50, 20);  
                if (getModel().isRollover()) {
                    c1 = new Color(160, 110, 60);
                    c2 = new Color(110, 65, 30);
                }
                g2.setPaint(new GradientPaint(0, 0, c1, 0, h, c2));
                g2.fillRoundRect(0, 0, w, h, 10, 10);

                // Vetas de madera
                g2.setColor(new Color(60, 35, 10, 80));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawLine(4, h/3, w-4, h/3);
                g2.drawLine(8, 2*h/3, w-8, 2*h/3);

                // Borde negro grueso
                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(2.5f));
                g2.drawRoundRect(1, 1, w - 2, h - 2, 10, 10);

                // Texto con outline negro y sombra
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                String label = getText();
                int tx = (w - fm.stringWidth(label)) / 2;
                int ty = (h + fm.getAscent()) / 2 - 2;

                // Sombra
                g2.setColor(Color.BLACK);
                g2.drawString(label, tx + 2, ty + 2);

                // Outline
                g2.drawString(label, tx - 1, ty - 1);
                g2.drawString(label, tx + 1, ty - 1);
                g2.drawString(label, tx - 1, ty + 1);
                g2.drawString(label, tx + 1, ty + 1);

                // Frente
                g2.setColor(getForeground());
                g2.drawString(label, tx, ty);

                g2.dispose();
            }
        };
        btn.setFont(new Font("Arial Black", Font.BOLD, 13));
        btn.setForeground(new Color(254, 252, 232));
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(l);
        return btn;
    }

    private JButton crearBotonOroMadera(String texto, java.awt.event.ActionListener l) {
        JButton btn = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                // Madera dorada
                Color c1 = new Color(212, 175, 55); 
                Color c2 = new Color(160, 120, 30);  
                if (getModel().isRollover()) {
                    c1 = new Color(230, 195, 75);
                    c2 = new Color(180, 140, 45);
                }
                g2.setPaint(new GradientPaint(0, 0, c1, 0, h, c2));
                g2.fillRoundRect(0, 0, w, h, 10, 10);

                // Vetas
                g2.setColor(new Color(110, 80, 15, 80));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawLine(4, h/3, w-4, h/3);
                g2.drawLine(8, 2*h/3, w-8, 2*h/3);

                // Borde negro grueso
                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(2.5f));
                g2.drawRoundRect(1, 1, w - 2, h - 2, 10, 10);

                // Texto con outline negro y sombra
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                String label = getText();
                int tx = (w - fm.stringWidth(label)) / 2;
                int ty = (h + fm.getAscent()) / 2 - 2;

                // Sombra
                g2.setColor(Color.BLACK);
                g2.drawString(label, tx + 2, ty + 2);

                // Outline
                g2.drawString(label, tx - 1, ty - 1);
                g2.drawString(label, tx + 1, ty - 1);
                g2.drawString(label, tx - 1, ty + 1);
                g2.drawString(label, tx + 1, ty + 1);

                // Frente
                g2.setColor(getForeground());
                g2.drawString(label, tx, ty);

                g2.dispose();
            }
        };
        btn.setFont(new Font("Arial Black", Font.BOLD, 13));
        btn.setForeground(new Color(254, 252, 232));
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(l);
        return btn;
    }

    private JButton crearBotonRojoMadera(String texto, java.awt.event.ActionListener l) {
        JButton btn = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                // Madera roja carmesí
                Color c1 = new Color(180, 50, 40);
                Color c2 = new Color(110, 20, 15);
                if (getModel().isRollover()) {
                    c1 = new Color(200, 70, 60);
                    c2 = new Color(130, 30, 25);
                }
                g2.setPaint(new GradientPaint(0, 0, c1, 0, h, c2));
                g2.fillRoundRect(0, 0, w, h, 10, 10);

                // Vetas
                g2.setColor(new Color(60, 10, 10, 80));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawLine(4, h/3, w-4, h/3);
                g2.drawLine(8, 2*h/3, w-8, 2*h/3);

                // Borde negro grueso
                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(2.5f));
                g2.drawRoundRect(1, 1, w - 2, h - 2, 10, 10);

                // Texto con outline negro y sombra
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                String label = getText();
                int tx = (w - fm.stringWidth(label)) / 2;
                int ty = (h + fm.getAscent()) / 2 - 2;

                // Sombra
                g2.setColor(Color.BLACK);
                g2.drawString(label, tx + 2, ty + 2);

                // Outline
                g2.drawString(label, tx - 1, ty - 1);
                g2.drawString(label, tx + 1, ty - 1);
                g2.drawString(label, tx - 1, ty + 1);
                g2.drawString(label, tx + 1, ty + 1);

                // Frente
                g2.setColor(getForeground());
                g2.drawString(label, tx, ty);

                g2.dispose();
            }
        };
        btn.setFont(new Font("Arial Black", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(l);
        return btn;
    }

    private JButton crearBotonGuardar(String texto, java.awt.event.ActionListener l) {
        JButton btn = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                // Naranja brillante a dorado metálico
                Color c1 = new Color(255, 140, 0); 
                Color c2 = new Color(255, 215, 0); 
                if (getModel().isRollover()) {
                    c1 = new Color(255, 165, 0); 
                    c2 = new Color(255, 230, 50); 
                }
                g2.setPaint(new GradientPaint(0, 0, c1, 0, h, c2));
                g2.fillRoundRect(0, 0, w, h, 12, 12);

                // Borde negro grueso
                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(3.0f));
                g2.drawRoundRect(1, 1, w - 2, h - 2, 12, 12);

                // Texto con outline negro y sombra
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                String label = getText();
                int tx = (w - fm.stringWidth(label)) / 2;
                int ty = (h + fm.getAscent()) / 2 - 2;

                // Sombra
                g2.setColor(Color.BLACK);
                g2.drawString(label, tx + 2, ty + 2);

                // Outline
                g2.drawString(label, tx - 1, ty - 1);
                g2.drawString(label, tx + 1, ty - 1);
                g2.drawString(label, tx - 1, ty + 1);
                g2.drawString(label, tx + 1, ty + 1);

                // Frente
                g2.setColor(getForeground());
                g2.drawString(label, tx, ty);

                g2.dispose();
            }
        };
        btn.setFont(new Font("Arial Black", Font.BOLD, 14));
        btn.setForeground(Color.BLACK);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(l);
        return btn;
    }

    // =========================================================================
    // Clases Internas de Panel Personalizado
    // =========================================================================

    /**
     * Panel con fondo de mapa náutico (pergamino sepia) y bordes de madera de barco pirata.
     */
    private class OnePieceBackgroundPanel extends JPanel {
        public OnePieceBackgroundPanel() {
            setLayout(new BorderLayout(15, 15));
            setBorder(BorderFactory.createEmptyBorder(32, 32, 32, 32));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            // 1. Fondo de Pergamino Envejecido
            g2.setColor(new Color(240, 222, 180));
            g2.fillRect(0, 0, w, h);

            // Manchas sepia aleatorias en el pergamino
            g2.setColor(new Color(220, 200, 160, 60));
            for (int i = 0; i < 12; i++) {
                int rx = (i * 83) % w;
                int ry = (i * 53) % h;
                int rw = 65 + (i * 23) % 110;
                int rh = 55 + (i * 29) % 85;
                g2.fillOval(rx, ry, rw, rh);
            }

            // 2. Líneas de cuadrícula náuticas (sepia tenues)
            g2.setColor(new Color(180, 150, 110, 80));
            g2.setStroke(new BasicStroke(1.0f));
            for (int x = 40; x < w - 40; x += 70) {
                g2.drawLine(x, 25, x, h - 25);
            }
            for (int y = 40; y < h - 40; y += 70) {
                g2.drawLine(25, y, w - 25, y);
            }

            // 3. Brújula/Rosa de los vientos de fondo
            int cx = w - 160;
            int cy = 130;
            g2.setColor(new Color(160, 130, 90, 100));
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawOval(cx - 50, cy - 50, 100, 100);
            g2.drawOval(cx - 42, cy - 42, 84, 84);
            g2.drawLine(cx, cy - 60, cx, cy + 60);
            g2.drawLine(cx - 60, cy, cx + 60, cy);
            g2.drawLine(cx - 42, cy - 42, cx + 42, cy + 42);
            g2.drawLine(cx - 42, cy + 42, cx + 42, cy - 42);

            g2.setFont(new Font("Georgia", Font.ITALIC, 11));
            g2.drawString("N", cx - 4, cy - 64);
            g2.drawString("S", cx - 4, cy + 74);
            g2.drawString("E", cx + 66, cy + 4);
            g2.drawString("W", cx - 78, cy + 4);

            // 4. Bordes de madera oscura de barco pirata (22px)
            int woodTh = 22;
            g2.setColor(new Color(50, 30, 10)); // Madera oscura
            g2.fillRect(0, 0, w, woodTh);
            g2.fillRect(0, h - woodTh, w, woodTh);
            g2.fillRect(0, 0, woodTh, h);
            g2.fillRect(w - woodTh, 0, woodTh, h);

            // Líneas divisorias de tablones de madera
            g2.setColor(new Color(25, 15, 5));
            g2.setStroke(new BasicStroke(2.0f));
            g2.drawRect(0, 0, w, h);
            g2.drawRect(woodTh, woodTh, w - 2 * woodTh, h - 2 * woodTh);

            // Tablones simulados
            g2.drawLine(w/4, 0, w/4, woodTh);
            g2.drawLine(2*w/4, 0, 2*w/4, woodTh);
            g2.drawLine(3*w/4, 0, 3*w/4, woodTh);
            g2.drawLine(w/4, h - woodTh, w/4, h);
            g2.drawLine(2*w/4, h - woodTh, 2*w/4, h);
            g2.drawLine(3*w/4, h - woodTh, 3*w/4, h);
            g2.drawLine(0, h/3, woodTh, h/3);
            g2.drawLine(0, 2*h/3, woodTh, 2*h/3);
            g2.drawLine(w - woodTh, h/3, w, h/3);
            g2.drawLine(w - woodTh, 2*h/3, w, 2*h/3);

            // Remaches
            g2.setColor(new Color(197, 160, 89));
            int offset = 10;
            drawRivet(g2, offset, offset);
            drawRivet(g2, w - offset - 4, offset);
            drawRivet(g2, offset, h - offset - 4);
            drawRivet(g2, w - offset - 4, h - offset - 4);
            drawRivet(g2, w/2, offset);
            drawRivet(g2, w/2, h - offset - 4);
            drawRivet(g2, offset, h/2);
            drawRivet(g2, w - offset - 4, h/2);

            // 5. Cuerda
            g2.setColor(new Color(139, 90, 43, 160));
            g2.setStroke(new BasicStroke(1.5f));
            int ropeOffset = woodTh + 4;
            g2.drawRect(ropeOffset, ropeOffset, w - 2 * ropeOffset, h - 2 * ropeOffset);

            g2.dispose();
        }

        private void drawRivet(Graphics2D g2, int x, int y) {
            g2.fillOval(x, y, 6, 6);
            g2.setColor(new Color(110, 80, 30));
            g2.drawOval(x, y, 6, 6);
            g2.setColor(new Color(197, 160, 89));
        }
    }

    /**
     * Panel superior azul marino con reborde en dorado neón.
     */
    private class OnePieceHeaderPanel extends JPanel {
        public OnePieceHeaderPanel() {
            setLayout(new FlowLayout(FlowLayout.LEFT, 15, 10));
            setOpaque(true);
            setBackground(new Color(11, 29, 58)); // Azul marino profundo
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 4, 0, new Color(212, 175, 55)), // Oro
                    BorderFactory.createEmptyBorder(5, 5, 5, 5)
            ));
        }
    }

    /**
     * Panel para el JDialog que dibuja un pergamino de WANTED.
     */
    private class WantedPosterPanel extends JPanel {
        private String actionTitle;

        public WantedPosterPanel(String actionTitle) {
            this.actionTitle = actionTitle;
            setLayout(new GridBagLayout());
            setBorder(BorderFactory.createEmptyBorder(95, 25, 20, 25)); 
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            // Fondo
            g2.setColor(new Color(237, 218, 178));
            g2.fillRect(0, 0, w, h);

            // Manchas sepia
            g2.setColor(new Color(210, 185, 140, 75));
            g2.fillOval(20, 20, 90, 60);
            g2.fillOval(w - 110, h - 100, 80, 80);

            // Borde doble
            g2.setColor(new Color(90, 60, 30));
            g2.setStroke(new BasicStroke(3.0f));
            g2.drawRect(10, 10, w - 20, h - 20);
            g2.setStroke(new BasicStroke(1.0f));
            g2.drawRect(14, 14, w - 28, h - 28);

            // Jolly Roger
            g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
            g2.drawString("☠️", w/2 - 12, 42);

            // WANTED
            g2.setFont(new Font("Impact", Font.PLAIN, 28));
            g2.setColor(new Color(130, 20, 15)); 
            String header = "WANTED";
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(header, (w - fm.stringWidth(header))/2, 70);

            // Subtítulo
            g2.setFont(new Font("Arial Black", Font.BOLD, 12));
            g2.setColor(new Color(50, 40, 30));
            FontMetrics fm2 = g2.getFontMetrics();
            g2.drawString(actionTitle.toUpperCase(), (w - fm2.stringWidth(actionTitle.toUpperCase()))/2, 90);

            // Monedas de oro
            g2.setColor(new Color(218, 165, 32));
            g2.fillOval(25, h - 35, 12, 12);
            g2.fillOval(w - 35, h - 35, 12, 12);

            g2.setColor(new Color(150, 105, 15));
            g2.setStroke(new BasicStroke(1.0f));
            g2.drawOval(25, h - 35, 12, 12);
            g2.drawOval(w - 35, h - 35, 12, 12);

            // Cuerda
            g2.setColor(new Color(101, 67, 33));
            g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[]{5.0f, 5.0f}, 0.0f));
            g2.drawRect(18, 18, w - 36, h - 36);

            g2.dispose();
        }
    }
}