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
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            ServiceIF service = (ServiceIF) registry.lookup("Service");

            if (service == null) {
                throw new Exception("Service not found");
            }

            StudentGUI studentGUI = new StudentGUI(service, studentInfo);

            StudentIMP studentIMP = studentGUI.getStudentImp();

            service.registerStudent(studentIMP, studentInfo);
            System.out.println("Student registered successfully");

            studentGUI.setVisible(true);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Connection to service failed: " + e.getMessage(),
                    "Network Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}