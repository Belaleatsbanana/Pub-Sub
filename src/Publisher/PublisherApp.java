package Publisher;

import Service.ServiceIF;
import common.PublisherInfo;
import javax.swing.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class PublisherApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PublisherRegistrationGUI registration = new PublisherRegistrationGUI(
                    new PublisherRegistrationGUI.RegistrationListener() {
                        @Override
                        public void onRegistrationSuccess(PublisherInfo publisherInfo) {
                            handleRegistrationSuccess(publisherInfo);
                        }
                    }
            );
            registration.setVisible(true);
        });
    }

    private static void handleRegistrationSuccess(PublisherInfo publisherInfo) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            ServiceIF service = (ServiceIF) registry.lookup("Service");

            if (service == null) {
                throw new Exception("Service not found");
            }

            // Create publisher implementation
            PublisherIMP publisherIMP = new PublisherIMP();

            // Register publisher with service
            service.registerPublisher(publisherIMP, publisherInfo);
            System.out.println("Publisher registered successfully");

            // Create and show main GUI
            SwingUtilities.invokeLater(() -> {
                PublisherGUI publisherGUI = new PublisherGUI(publisherIMP, publisherInfo, service);
                publisherGUI.setVisible(true);
            });

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Connection to service failed: " + e.getMessage(),
                    "Network Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}