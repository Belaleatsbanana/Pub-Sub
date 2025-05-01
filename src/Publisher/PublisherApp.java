package Publisher;

import Service.ServiceIF;
import common.PublisherInfo;
import java.rmi.registry.*;

import javax.swing.*;

public class PublisherApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // 1) Lookup the remote ServiceIF stub
//                ServiceIF service = (ServiceIF) LocateRegistry.getRegistry().lookup("Publisher");

                // 2) Show registration dialog
//                PublisherRegistrationGUI dlg = new PublisherRegistrationGUI();
//                dlg.setVisible(true);
//                if (!dlg.isConfirmed()) {
//                    System.exit(0);
//                }
//
//                // 3) Build the PublisherInfo DTO
//                PublisherInfo info = new PublisherInfo(
//                        dlg.getId(),
//                        dlg.getName(),
//                        dlg.getDesc(),
//                        dlg.getIcon()
//                );

                // 4) Create and register the PublisherImpl callback
                //PublisherIMP impl = new PublisherIMP(info, service);

                // 5) Show the main publisher GUI
//                PublisherMainGUI gui = new PublisherMainGUI(impl, info, service);
//                gui.setVisible(true);

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        "Failed to start Publisher: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
}
