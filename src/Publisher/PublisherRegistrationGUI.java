package Publisher;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class PublisherRegistrationGUI extends javax.swing.JFrame {
    private JLabel profileIcon;
    private JButton selectPictureButton;
    private JTextField emailField;
    private JTextField nameField;
    private JTextArea descriptionArea;
    private JButton registerButton;

    public PublisherRegistrationGUI() {
        initComponents();
        addFormComponents();
    }

    private void addFormComponents() {
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        formPanel.setBackground(new Color(245, 245, 245));

        // Centered container
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.setBackground(new Color(245, 245, 245));

        // Profile Picture Section
        JPanel picturePanel = new JPanel();
        picturePanel.setLayout(new BoxLayout(picturePanel, BoxLayout.Y_AXIS));
        picturePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        picturePanel.setBackground(new Color(245, 245, 245));

        profileIcon = new JLabel(createRoundIcon(new ImageIcon("default_avatar.png"), 120)) {
            @Override
            public Dimension getMaximumSize() {
                return new Dimension(140, 140);
            }
        };
        profileIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        picturePanel.add(profileIcon);
        picturePanel.add(Box.createRigidArea(new Dimension(0, 20)));

        selectPictureButton = new JButton("Select Profile Picture");
        styleButton(selectPictureButton, new Color(70, 130, 180));
        selectPictureButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        selectPictureButton.addActionListener(this::selectPictureAction);
        picturePanel.add(selectPictureButton);
        centerPanel.add(picturePanel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 40)));

        // Form Fields
        JPanel fieldsPanel = new JPanel();
        fieldsPanel.setLayout(new BoxLayout(fieldsPanel, BoxLayout.Y_AXIS));
        fieldsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        fieldsPanel.setBackground(new Color(245, 245, 245));

        // Email Field
        fieldsPanel.add(createLabel("Email Address:"));
        fieldsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        emailField = createTextField();
        fieldsPanel.add(emailField);
        fieldsPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Name Field
        fieldsPanel.add(createLabel("Publisher Name:"));
        fieldsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        nameField = createTextField();
        fieldsPanel.add(nameField);
        fieldsPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Description Field
        fieldsPanel.add(createLabel("Description:"));
        fieldsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        descriptionArea = new JTextArea(4, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        scrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);
        scrollPane.setMaximumSize(new Dimension(300, 100));
        fieldsPanel.add(scrollPane);

        centerPanel.add(fieldsPanel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 40)));

        // Register Button
        registerButton = new JButton("Register Publisher");
        styleButton(registerButton, new Color(34, 139, 34));
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerButton.addActionListener(e -> handleRegistration());
        centerPanel.add(registerButton);

        formPanel.add(Box.createVerticalGlue());
        formPanel.add(centerPanel);
        formPanel.add(Box.createVerticalGlue());
    }

    private void selectPictureAction(ActionEvent evt) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Profile Picture");
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter(
                "Image files",
                ImageIO.getReaderFileSuffixes()
        ));

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                BufferedImage originalImage = ImageIO.read(fileChooser.getSelectedFile());
                ImageIcon roundedIcon = createRoundIcon(new ImageIcon(originalImage), 120);
                profileIcon.setIcon(roundedIcon);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                        "Error loading image",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private ImageIcon createRoundIcon(ImageIcon icon, int size) {
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Ellipse2D.Double shape = new Ellipse2D.Double(0, 0, size, size);
        g2.setClip(shape);

        Image scaled = icon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
        g2.drawImage(scaled, 0, 0, null);
        g2.dispose();

        return new ImageIcon(image);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return label;
    }

    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setMaximumSize(new Dimension(300, 30));
        field.setAlignmentX(Component.CENTER_ALIGNMENT);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setMargin(new Insets(5, 10, 5, 10));
        return field;
    }

    private void styleButton(JButton button, Color bgColor) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createRaisedBevelBorder(),
                BorderFactory.createEmptyBorder(10, 25, 10, 25)
        ));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void handleRegistration() {
        // Add your registration logic here
        String email = emailField.getText();
        String name = nameField.getText();
        String description = descriptionArea.getText();

        if (email.isEmpty() || name.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please fill in all required fields",
                    "Registration Error",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // TODO: Implement actual registration logic
        JOptionPane.showMessageDialog(this,
                "Registration successful!\nName: " + name + "\nEmail: " + email,
                "Registration Complete",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    // Generated GUI initialization code
    private void initComponents() {
        yapPanel = new javax.swing.JPanel();
        yap1 = new javax.swing.JLabel();
        yap2 = new javax.swing.JLabel();
        formPanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(1000, 700));

        yapPanel.setBackground(new java.awt.Color(240, 240, 240));
        yap1.setFont(new java.awt.Font("Segoe UI", 1, 36));
        yap1.setText("Banana Lagoona");

        yap2.setFont(new java.awt.Font("Segoe UI", 0, 24));
        yap2.setText("Ready to publish content? Join now!");

        javax.swing.GroupLayout yapPanelLayout = new javax.swing.GroupLayout(yapPanel);
        yapPanel.setLayout(yapPanelLayout);
        yapPanelLayout.setHorizontalGroup(
                yapPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(yapPanelLayout.createSequentialGroup()
                                .addGap(34, 34, 34)
                                .addGroup(yapPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(yap1)
                                        .addComponent(yap2))
                        ));
        yapPanelLayout.setVerticalGroup(
                yapPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(yapPanelLayout.createSequentialGroup()
                                .addGap(117, 117, 117)
                                .addComponent(yap1)
                                .addGap(18, 18, 18)
                                .addComponent(yap2)
                                .addContainerGap(117, Short.MAX_VALUE))
        );

        formPanel.setBackground(new java.awt.Color(245, 245, 245));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(yapPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(formPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 584, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(yapPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(formPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }

    public static void main(String args[]) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException |
                 IllegalAccessException | UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        }

        java.awt.EventQueue.invokeLater(() -> {
            new PublisherRegistrationGUI().setVisible(true);
        });
    }

    // Variables declaration
    private javax.swing.JPanel formPanel;
    private javax.swing.JLabel yap1;
    private javax.swing.JLabel yap2;
    private javax.swing.JPanel yapPanel;
}