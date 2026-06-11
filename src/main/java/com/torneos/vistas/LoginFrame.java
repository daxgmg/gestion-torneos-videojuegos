package com.torneos.vistas;

import com.torneos.dominio.User;
import com.torneos.persistencia.UserDAO;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JButton btnLogin;

    public LoginFrame() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Acceso al Sistema - Gestión de Torneos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(480, 400);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(Color.WHITE);
        setLayout(null);

        // Título
        JLabel lblTitulo = new JLabel("Iniciar Sesión", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 26));
        lblTitulo.setForeground(new Color(40, 40, 40));
        lblTitulo.setBounds(40, 30, 390, 40);
        add(lblTitulo);

        // Separador
        JSeparator sep = new JSeparator();
        sep.setBounds(40, 80, 390, 2);
        sep.setForeground(new Color(200, 200, 200));
        add(sep);

        // Email label
        JLabel lblEmail = new JLabel("Correo Electrónico:");
        lblEmail.setFont(new Font("Arial", Font.PLAIN, 14));
        lblEmail.setForeground(Color.BLACK);
        lblEmail.setBounds(40, 100, 200, 25);
        add(lblEmail);

        // Email field
        txtEmail = new JTextField();
        txtEmail.setFont(new Font("Arial", Font.PLAIN, 14));
        txtEmail.setForeground(Color.BLACK);
        txtEmail.setBackground(Color.WHITE);
        txtEmail.setCaretColor(Color.BLACK);
        txtEmail.setBounds(40, 130, 390, 38);
        txtEmail.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(150, 150, 150), 1),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        add(txtEmail);

        // Password label
        JLabel lblPassword = new JLabel("Contraseña:");
        lblPassword.setFont(new Font("Arial", Font.PLAIN, 14));
        lblPassword.setForeground(Color.BLACK);
        lblPassword.setBounds(40, 180, 200, 25);
        add(lblPassword);

        // Password field
        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Arial", Font.PLAIN, 14));
        txtPassword.setForeground(Color.BLACK);
        txtPassword.setBackground(Color.WHITE);
        txtPassword.setCaretColor(Color.BLACK);
        txtPassword.setBounds(40, 210, 390, 38);
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(150, 150, 150), 1),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        add(txtPassword);

        // Botón login
        btnLogin = new JButton("Iniciar Sesión");
        btnLogin.setBounds(40, 270, 390, 42);
        btnLogin.setFont(new Font("Arial", Font.BOLD, 16));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setBackground(new Color(70, 130, 180));
        btnLogin.setOpaque(true);
        btnLogin.setContentAreaFilled(true);
        btnLogin.setFocusPainted(false);
        btnLogin.setBorderPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        add(btnLogin);

        btnLogin.addActionListener(e -> login());
        txtPassword.addActionListener(e -> login());

        setVisible(true);
    }

    private void login() {
        String email = txtEmail.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Por favor ingresa email y contraseña",
                    "Campos vacíos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        UserDAO dao = new UserDAO();
        User user = dao.autenticar(email, password);

        if (user == null) {
            JOptionPane.showMessageDialog(this,
                    "Credenciales incorrectas",
                    "Error de autenticación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        dispose();
        if ("ADMIN".equals(user.getRol().getNombre())) {
            new MenuAdminFrame(user);
        } else {
            new MenuJugadorFrame(user);
        }
    }
}