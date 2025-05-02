package Subscriber;

import common.StudentInfo;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.regex.Pattern;

public class StudentRegistrationGUI extends JFrame {

    public interface RegistrationListener {
        void onRegistrationSuccess(StudentInfo studentInfo);
    }

    private RegistrationListener registrationListener;

    private JTextField emailField;
    private JTextField nameField;
    private JButton registerButton;
    private JPanel formPanel;
    private JPanel sidePanel;
    private JLabel header1;
    private JLabel header2;

    public StudentRegistrationGUI(RegistrationListener listener) {
        this.registrationListener = listener;

        initComponents();
        addFormComponents();
    }

    private void initComponents() {
        // Frame setup
        setTitle("Student Registration");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setResizable(true);

        // Side panel (branding)
        sidePanel = new JPanel();
        sidePanel.setPreferredSize(new Dimension(600, 700));
        sidePanel.setBackground(new Color(240, 240, 240));
        header1 = new JLabel("Welcome to Banana Lagoona");
        header1.setFont(new Font("Segoe UI", Font.BOLD, 36));
        header2 = new JLabel("Join as a Subscriber");
        header2.setFont(new Font("Segoe UI", Font.PLAIN, 24));

        GroupLayout sideLayout = new GroupLayout(sidePanel);
        sidePanel.setLayout(sideLayout);
        sideLayout.setHorizontalGroup(
                sideLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(sideLayout.createSequentialGroup()
                                .addGap(30)
                                .addGroup(sideLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(header1)
                                        .addComponent(header2))
                                .addContainerGap(30, Short.MAX_VALUE))
        );
        sideLayout.setVerticalGroup(
                sideLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(sideLayout.createSequentialGroup()
                                .addGap(100)
                                .addComponent(header1)
                                .addGap(20)
                                .addComponent(header2)
                                .addContainerGap(100, Short.MAX_VALUE))
        );

        // Form panel
        formPanel = new JPanel();
        formPanel.setBackground(new Color(40, 40, 40));
        formPanel.setPreferredSize(new Dimension(500, 700));

        // Layout frame
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(sidePanel)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(formPanel)
                                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(sidePanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(formPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }

    private void addFormComponents() {
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        // Center panel inside form
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBackground(new Color(40, 40, 40));
        center.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Email field
        center.add(createLabel("Email Address:"));
        center.add(Box.createRigidArea(new Dimension(0,5)));
        emailField = createTextField();
        center.add(emailField);
        center.add(Box.createRigidArea(new Dimension(0,20)));

        // Name field
        center.add(createLabel("Student Name:"));
        center.add(Box.createRigidArea(new Dimension(0,5)));
        nameField = createTextField();
        center.add(nameField);
        center.add(Box.createRigidArea(new Dimension(0,40)));

        // Register button
        registerButton = new JButton("Register Subscriber");
        styleButton(registerButton, new Color(80, 180, 80));
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerButton.addActionListener(this::handleRegistration);
        center.add(registerButton);

        formPanel.add(Box.createVerticalGlue());
        formPanel.add(center);
        formPanel.add(Box.createVerticalGlue());
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lbl.setForeground(Color.WHITE);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        return lbl;
    }

    private JTextField createTextField() {
        JTextField fld = new JTextField();
        fld.setMaximumSize(new Dimension(400, 30));
        fld.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        fld.setForeground(Color.WHITE);
        fld.setBackground(new Color(60, 60, 60));
        fld.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(100,100,100),1,true),
                BorderFactory.createEmptyBorder(5,10,5,10)
        ));
        return fld;
    }

    private void styleButton(JButton btn, Color bg) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(bg);
        int brightness = (bg.getRed()+bg.getGreen()+bg.getBlue())/3;
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createRaisedBevelBorder(),
                BorderFactory.createEmptyBorder(10,25,10,25)
        ));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void handleRegistration(ActionEvent e) {
        String email = emailField.getText().trim();
        String name = nameField.getText().trim();

        if (!Pattern.matches("[\\w.+-]+@gmail\\.com", email) || name.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Enter a valid Gmail address and your full name.",
                    "Registration Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        StudentInfo info = new StudentInfo(email, name);

        if(registrationListener != null)
        {
            registrationListener.onRegistrationSuccess(info);
            this.dispose();
        }else {
            JOptionPane.showMessageDialog(this,
                    "Registration failed. Please try again.",
                    "Registration Error",
                    JOptionPane.ERROR_MESSAGE);
        }

    }
}
