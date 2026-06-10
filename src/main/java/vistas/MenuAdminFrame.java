package vistas;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MenuAdminFrame extends JFrame {

    public MenuAdminFrame() {
        setTitle("Panel de Administración - Gestión de Torneos");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 247, 250));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(33, 37, 41));
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel lblTitle = new JLabel("Panel de Administración");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(Color.WHITE);
        headerPanel.add(lblTitle, BorderLayout.WEST);

        JLabel lblWelcome = new JLabel("Bienvenido, Administrador");
        lblWelcome.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lblWelcome.setForeground(new Color(206, 212, 218));
        headerPanel.add(lblWelcome, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Buttons Panel (2x2 grid for CRUDs)
        JPanel gridPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        gridPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        gridPanel.setBackground(new Color(245, 247, 250));

        JButton btnTorneos = createAdminButton("Gestionar Torneos", new Color(13, 110, 253));
        JButton btnEquipos = createAdminButton("Gestionar Equipos", new Color(25, 135, 84));
        JButton btnJugadores = createAdminButton("Gestionar Jugadores", new Color(220, 53, 69));
        JButton btnUsuarios = createAdminButton("Gestionar Usuarios", new Color(255, 193, 7));

        // Styling the yellow button text for better readability
        btnUsuarios.setForeground(new Color(33, 37, 41));

        gridPanel.add(btnTorneos);
        gridPanel.add(btnEquipos);
        gridPanel.add(btnJugadores);
        gridPanel.add(btnUsuarios);

        mainPanel.add(gridPanel, BorderLayout.CENTER);

        // Footer / Logout
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBorder(new EmptyBorder(10, 20, 15, 20));
        footerPanel.setBackground(new Color(245, 247, 250));

        JButton btnLogout = new JButton("Cerrar sesión");
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLogout.setBackground(new Color(108, 117, 125));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        footerPanel.add(btnLogout);

        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Action Listeners
        btnTorneos.addActionListener(e -> {
            new TorneoFrame(this).setVisible(true);
            this.setVisible(false);
        });

        btnEquipos.addActionListener(e -> {
            new EquipoFrame(this).setVisible(true);
            this.setVisible(false);
        });

        btnJugadores.addActionListener(e -> {
            new JugadorFrame(this).setVisible(true);
            this.setVisible(false);
        });

        btnUsuarios.addActionListener(e -> {
            new UserFrame(this).setVisible(true);
            this.setVisible(false);
        });

        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Está seguro de que desea cerrar la sesión?",
                    "Confirmar Salida",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                new LoginFrame().setVisible(true);
                dispose();
            }
        });
    }

    private JButton createAdminButton(String text, Color bg) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(bg);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bg.darker(), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        return button;
    }
}
