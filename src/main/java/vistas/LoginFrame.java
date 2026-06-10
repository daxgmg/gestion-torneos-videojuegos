package vistas;

import com.torneos.dominio.User;
import com.torneos.persistencia.UserDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame {

    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JButton btnLogin;

    public LoginFrame() {
        setTitle("Acceso al Sistema - Gestión de Torneos");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Principal panel with modern styling
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(245, 247, 250));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel lblTitle = new JLabel("Iniciar Sesión", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(33, 37, 41));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(lblTitle, gbc);

        // Subtitle or decorator line
        JSeparator sep = new JSeparator();
        gbc.gridy = 1;
        panel.add(sep, gbc);

        // Email Label
        JLabel lblEmail = new JLabel("Correo Electrónico:");
        lblEmail.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        panel.add(lblEmail, gbc);

        // Email Field
        txtEmail = new JTextField(20);
        txtEmail.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridx = 1;
        panel.add(txtEmail, gbc);

        // Password Label
        JLabel lblPassword = new JLabel("Contraseña:");
        lblPassword.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(lblPassword, gbc);

        // Password Field
        txtPassword = new JPasswordField(20);
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridx = 1;
        panel.add(txtPassword, gbc);

        // Login Button
        btnLogin = new JButton("Iniciar sesión");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setBackground(new Color(13, 110, 253));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 8, 8, 8);
        panel.add(btnLogin, gbc);

        add(panel);

        // Action listener
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });

        // Enter key to login
        txtPassword.addActionListener(e -> performLogin());
        txtEmail.addActionListener(e -> performLogin());
    }

    private void performLogin() {
        String email = txtEmail.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                    "Por favor, complete todos los campos.", 
                    "Campos Vacíos", 
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        btnLogin.setEnabled(false);
        btnLogin.setText("Autenticando...");

        // Authenticate in a worker thread to keep GUI responsive
        SwingWorker<User, Void> worker = new SwingWorker<>() {
            @Override
            protected User doInBackground() {
                UserDAO userDAO = new UserDAO();
                return userDAO.autenticar(email, password);
            }

            @Override
            protected void done() {
                try {
                    User user = get();
                    if (user != null && user.getRol() != null) {
                        String rol = user.getRol().getNombre().toUpperCase();
                        if ("ADMIN".equals(rol)) {
                            new MenuAdminFrame().setVisible(true);
                            dispose();
                        } else if ("JUGADOR".equals(rol)) {
                            new MenuJugadorFrame(user).setVisible(true);
                            dispose();
                        } else {
                            JOptionPane.showMessageDialog(LoginFrame.this,
                                    "Rol de usuario desconocido: " + rol,
                                    "Error de Rol",
                                    JOptionPane.ERROR_MESSAGE);
                            resetButton();
                        }
                    } else {
                        JOptionPane.showMessageDialog(LoginFrame.this,
                                "Credenciales incorrectas o usuario no encontrado.",
                                "Acceso Denegado",
                                JeffersonExceptionType());
                        resetButton();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(LoginFrame.this,
                            "Error de conexión a la base de datos:\n" + ex.getMessage(),
                            "Error de Conexión",
                            JOptionPane.ERROR_MESSAGE);
                    resetButton();
                }
            }
        };
        worker.execute();
    }

    private int JeffersonExceptionType() {
        return JOptionPane.ERROR_MESSAGE;
    }

    private void resetButton() {
        btnLogin.setEnabled(true);
        btnLogin.setText("Iniciar sesión");
        txtPassword.setText("");
        txtPassword.requestFocus();
    }
}
