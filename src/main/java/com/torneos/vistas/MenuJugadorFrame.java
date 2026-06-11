package com.torneos.vistas;

import com.torneos.dominio.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MenuJugadorFrame extends JFrame {

    private User usuarioActual;

    public MenuJugadorFrame(User user) {
        this.usuarioActual = user;
        initComponents();
    }

    private void initComponents() {
        setTitle("Panel de Jugador - Gestión de Torneos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 450);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(20, 30, 20, 30));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        JLabel lblTitulo = new JLabel("Panel de Jugador");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitulo.setForeground(new Color(50, 50, 50));
        JLabel lblBienvenido = new JLabel("Bienvenido, " + usuarioActual.getNombre());
        lblBienvenido.setFont(new Font("Arial", Font.ITALIC, 14));
        lblBienvenido.setForeground(new Color(100, 100, 100));
        lblBienvenido.setHorizontalAlignment(SwingConstants.RIGHT);
        headerPanel.add(lblTitulo, BorderLayout.WEST);
        headerPanel.add(lblBienvenido, BorderLayout.EAST);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Botones
        JPanel botonesPanel = new JPanel(new GridLayout(3, 1, 10, 15));
        botonesPanel.setBackground(Color.WHITE);
        botonesPanel.setBorder(new EmptyBorder(30, 50, 30, 50));

        botonesPanel.add(crearBoton("🏆  Ver Torneos Disponibles",
                e -> JOptionPane.showMessageDialog(this, "Función próximamente")));
        botonesPanel.add(
                crearBoton("⚔️  Ver mis Partidas", e -> JOptionPane.showMessageDialog(this, "Función próximamente")));
        botonesPanel.add(
                crearBoton("📊  Ver Clasificación", e -> JOptionPane.showMessageDialog(this, "Función próximamente")));

        mainPanel.add(botonesPanel, BorderLayout.CENTER);

        // Botón cerrar sesión
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBackground(Color.WHITE);
        JButton btnCerrar = new JButton("Cerrar Sesión");
        btnCerrar.setFont(new Font("Arial", Font.BOLD, 13));
        btnCerrar.setForeground(Color.WHITE);
        btnCerrar.setBackground(new Color(200, 50, 50));
        btnCerrar.setOpaque(true);
        btnCerrar.setContentAreaFilled(true);
        btnCerrar.setFocusPainted(false);
        btnCerrar.setPreferredSize(new Dimension(150, 35));
        btnCerrar.addActionListener(e -> {
            dispose();
            new LoginFrame();
        });
        footerPanel.add(btnCerrar);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    private JButton crearBoton(String texto, java.awt.event.ActionListener listener) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Arial", Font.BOLD, 15));
        btn.setForeground(Color.BLACK);
        btn.setBackground(new Color(235, 245, 255));
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(new Color(70, 130, 180), 2));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(listener);
        return btn;
    }
}