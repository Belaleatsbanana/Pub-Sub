package Publisher;

import common.PublisherInfo;
import common.StudentInfo;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class PublisherRegistrationGUI extends JFrame {

    public interface RegistrationListener {
        void onRegistrationSuccess(PublisherInfo publisherInfo);
    }

    private RegistrationListener registrationListener;
    private ImageIcon selectedIcon;

    private JLabel profileIcon;
    private JButton selectPictureButton;
    private JTextField emailField;
    private JTextField nameField;
    private JTextArea descriptionArea;
    private JButton registerButton;

    public PublisherRegistrationGUI(RegistrationListener listener) {
        this.registrationListener = listener;

        initComponents();
        addFormComponents();
    }

    private void addFormComponents() {
        // Dark background for contrast
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        formPanel.setBackground(new Color(40, 40, 40));
        formPanel.setPreferredSize(new Dimension(500, 700));

        // Centered container
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.setBackground(new Color(40, 40, 40));

        // Profile Picture Section
        JPanel picturePanel = new JPanel();
        picturePanel.setLayout(new BoxLayout(picturePanel, BoxLayout.Y_AXIS));
        picturePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        picturePanel.setBackground(new Color(40, 40, 40));

        ImageIcon defaultIcon = loadDefaultIcon();
        profileIcon = new JLabel(createRoundIcon(defaultIcon, 120));
        profileIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        picturePanel.add(profileIcon);
        picturePanel.add(Box.createRigidArea(new Dimension(0, 20)));

        selectPictureButton = new JButton("Select Profile Picture");
        styleButton(selectPictureButton, new Color(80, 150, 200));
        selectPictureButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        selectPictureButton.addActionListener(this::selectPictureAction);
        picturePanel.add(selectPictureButton);
        centerPanel.add(picturePanel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 40)));

        // Form Fields
        JPanel fieldsPanel = new JPanel();
        fieldsPanel.setLayout(new BoxLayout(fieldsPanel, BoxLayout.Y_AXIS));
        fieldsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        fieldsPanel.setBackground(new Color(40, 40, 40));

        fieldsPanel.add(createLabel("Email Address:"));
        fieldsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        emailField = createTextField();
        fieldsPanel.add(emailField);
        fieldsPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        fieldsPanel.add(createLabel("Publisher Name:"));
        fieldsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        nameField = createTextField();
        fieldsPanel.add(nameField);
        fieldsPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        fieldsPanel.add(createLabel("Description:"));
        fieldsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        descriptionArea = new JTextArea(4, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descriptionArea.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(100, 100, 100), 1, true),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        descriptionArea.setBackground(new Color(60, 60, 60));
        descriptionArea.setForeground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        scrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);
        scrollPane.setMaximumSize(new Dimension(400, 120));
        scrollPane.setBorder(null);
        fieldsPanel.add(scrollPane);

        centerPanel.add(fieldsPanel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 40)));

        // Register Button
        registerButton = new JButton("Register Publisher");
        styleButton(registerButton, new Color(200, 80, 80));
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
                "Image files", ImageIO.getReaderFileSuffixes()));

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                BufferedImage originalImage = ImageIO.read(fileChooser.getSelectedFile());
                selectedIcon = createRoundIcon(new ImageIcon(originalImage), 120);
                profileIcon.setIcon(selectedIcon);
            } catch (IOException ex) {
                showError("Error loading image");
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
        label.setForeground(Color.WHITE);
        return label;
    }

    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setMaximumSize(new Dimension(400, 30));
        field.setAlignmentX(Component.CENTER_ALIGNMENT);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(100, 100, 100), 1, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        field.setBackground(new Color(60, 60, 60));
        field.setForeground(Color.WHITE);
        return field;
    }

    private void styleButton(JButton button, Color bgColor) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(bgColor);
        // Dynamic text color for contrast
        int brightness = (bgColor.getRed() + bgColor.getGreen() + bgColor.getBlue()) / 3;
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createRaisedBevelBorder(),
                BorderFactory.createEmptyBorder(10, 25, 10, 25)
        ));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void handleRegistration() {
        String email = emailField.getText().trim();
        String name = nameField.getText().trim();
        String description = descriptionArea.getText().trim();

        if (email.isEmpty() || name.isEmpty()) {
            showError("Please fill in all required fields");
            return;
        }

        if (selectedIcon == null) {
            selectedIcon = loadDefaultIcon();
        }

        PublisherInfo info = new PublisherInfo(
                email,
                name,
                description,
                selectedIcon
        );

        registrationListener.onRegistrationSuccess(info);
        dispose();
    }

    // Helper methods
    private ImageIcon loadDefaultIcon() {
        try (InputStream in = getClass().getResourceAsStream("/assets/default.jpg")) {
            return new ImageIcon(ImageIO.read(in));
        } catch (Exception e) {
            return new ImageIcon(new BufferedImage(120, 120, BufferedImage.TYPE_INT_ARGB));
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    // GUI components
    private JPanel formPanel;
    private JPanel yapPanel;
    private JLabel yap1;
    private JLabel yap2;

    private void initComponents() {
        yapPanel = new JPanel();
        yap1 = new JLabel();
        yap2 = new JLabel();
        formPanel = new JPanel();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Publisher Registration");
        setPreferredSize(new Dimension(1100, 700));
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setResizable(true);

        // Wider yap panel, less form space
        yapPanel.setBackground(new Color(240, 240, 240));
        yapPanel.setPreferredSize(new Dimension(600, 700));

        yap1.setFont(new Font("Segoe UI", Font.BOLD, 36));
        yap1.setText("Banana Lagoona");

        yap2.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        yap2.setText("Ready to publish content? Join now!");

        GroupLayout yapPanelLayout = new GroupLayout(yapPanel);
        yapPanel.setLayout(yapPanelLayout);
        yapPanelLayout.setHorizontalGroup(
                yapPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(yapPanelLayout.createSequentialGroup()
                                .addGap(30)
                                .addGroup(yapPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(yap1)
                                        .addComponent(yap2))
                                .addContainerGap(30, Short.MAX_VALUE))
        );
        yapPanelLayout.setVerticalGroup(
                yapPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(yapPanelLayout.createSequentialGroup()
                                .addGap(100)
                                .addComponent(yap1)
                                .addGap(20)
                                .addComponent(yap2)
                                .addContainerGap(100, Short.MAX_VALUE))
        );

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(yapPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(formPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(yapPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(formPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }
}
