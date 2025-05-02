package Subscriber;

import Service.ServiceIF;
import common.StudentInfo;

import javax.swing.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class StudentApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            StudentRegistrationGUI registration = new StudentRegistrationGUI(
                    new StudentRegistrationGUI.RegistrationListener() {
                        @Override
                        public void onRegistrationSuccess(StudentInfo studentInfo) {
                            handleRegistrationSuccess(studentInfo);
                        }
                    }
            );
            registration.setVisible(true);
        });
    }

    private static void handleRegistrationSuccess(StudentInfo studentInfo) {
        try {
            // RMI Connection
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            ServiceIF service = (ServiceIF) registry.lookup("Service");

            // Register student with service
            if (service == null) {
                throw new Exception("Service not found");
            }

            // Create student GUI
            StudentGUI studentGUI = new StudentGUI(service, studentInfo);

            // Register student with service
            StudentIMP studentIMP = new StudentIMP(studentGUI);
            service.registerStudent(studentIMP, studentInfo);
            System.out.println("Student registered successfully");

            // Show student GUI
            SwingUtilities.invokeLater(() -> studentGUI.setVisible(true));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Connection to service failed: " + e.getMessage(),
                    "Network Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}