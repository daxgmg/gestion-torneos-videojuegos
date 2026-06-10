import vistas.LoginFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Set native OS look and feel for a modern premium aesthetic
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("No se pudo establecer el Look and Feel del sistema: " + e.getMessage());
        }

        // Run application
        SwingUtilities.invokeLater(() -> {
            LoginFrame login = new LoginFrame();
            login.setVisible(true);
        });
    }
}
