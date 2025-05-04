package Service;

import javax.swing.*;
import java.awt.*;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServiceApp {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            try {
                // Start server
                Registry r = LocateRegistry.createRegistry(1099);
                System.out.println("RMI registry started on port 1099");

                ServiceIMP serviceImpl = new ServiceIMP();

                // Bind the service
                r.rebind("Service", serviceImpl);
                System.out.println("Service bound as 'Service' in registry");

                ServiceGUI gui = new ServiceGUI(serviceImpl);
                gui.setVisible(true);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        "Failed to start service: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                System.exit(1);
            }
        });
    }

}