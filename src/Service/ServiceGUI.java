package Service;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ServiceGUI extends JFrame {
    private JPanel headerPanel;
    private JLabel headerLabel;
    private JTextField searchTextField;
    private JButton logoutButton;
    private JPanel contentHolder;
    private JSplitPane splitPane;

    public ServiceGUI() {
        initComponents();
        customizeServiceUI();
    }

    private void initComponents() {
        // Header Panel Setup
        headerPanel = new JPanel();
        headerLabel = new JLabel("Banana Lagoona");
        searchTextField = new JTextField("search");
        logoutButton = new JButton("Logout");

        // Header Styling
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        searchTextField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        customizeLogoutButton();

        // Header Layout
        headerPanel.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        GroupLayout headerLayout = new GroupLayout(headerPanel);
        headerPanel.setLayout(headerLayout);

        // Horizontal layout
        headerLayout.setHorizontalGroup(headerLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(headerLayout.createSequentialGroup()
                        .addComponent(headerLabel)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(headerLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                .addComponent(searchTextField, GroupLayout.PREFERRED_SIZE, 400, GroupLayout.PREFERRED_SIZE)
                        )
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(logoutButton)
                )
        );

        // Vertical layout
        headerLayout.setVerticalGroup(headerLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(headerLayout.createSequentialGroup()
                        .addGroup(headerLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(headerLabel)
                                .addComponent(searchTextField, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE)
                                .addComponent(logoutButton, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE))
                )
        );

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(1360, 720));
        pack();
        setLocationRelativeTo(null);
    }

    private void customizeLogoutButton() {
        Color normalBg = new Color(245, 245, 245);
        Color hoverBg = new Color(220, 220, 220);

        // Create custom button FIRST
        logoutButton = new JButton("Logout") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                super.paintComponent(g2);
                g2.dispose();
            }
        };

        // Apply styling AFTER creation
        logoutButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        logoutButton.setFocusPainted(false);
        logoutButton.setContentAreaFilled(false);
        logoutButton.setOpaque(false);
        logoutButton.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        logoutButton.setBackground(normalBg);
        logoutButton.setForeground(Color.DARK_GRAY);
        logoutButton.setAlignmentY(Component.CENTER_ALIGNMENT);

        // Keep existing hover effects and action listener
        logoutButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                logoutButton.setBackground(hoverBg);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                logoutButton.setBackground(normalBg);
            }
        });

        logoutButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this, "Are you sure you want to logout?", "Confirm Logout", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) System.exit(0);
        });
    }
    private void customizeServiceUI() {
        // Navigation Panel
        JPanel nav = new JPanel();
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setBackground(Color.WHITE);
        nav.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        // Navigation Items
        String[] items = {"Home", "Notifications", "Topics", "Subscribers", "Logs"};
        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.Y_AXIS));
        btnPanel.setOpaque(false);

        Color normalBg = new Color(245, 245, 245);
        Color hoverBg = new Color(220, 220, 220);
        Color selectedBg = new Color(200, 230, 255);
        JButton[] navButtons = new JButton[items.length];
        final String[] current = {null};

        for (int i = 0; i < items.length; i++) {
            String key = items[i];
            JButton b = createNavButton(key, normalBg, hoverBg);

            b.addActionListener(e -> {
                CardLayout cl = (CardLayout) contentHolder.getLayout();
                cl.show(contentHolder, key);
                current[0] = key;
                for (JButton btn : navButtons) {
                    btn.setBackground(btn.getText().equals(key) ? selectedBg : normalBg);
                }
            });

            navButtons[i] = b;
            btnPanel.add(b);
            btnPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        }
        nav.add(btnPanel);

        // Content Panels
        contentHolder = new JPanel(new CardLayout());
        contentHolder.add(createContentPanel("Home Dashboard"), "Home");
        contentHolder.add(createContentPanel("Notifications Management"), "Notifications");
        contentHolder.add(createContentPanel("Topics Configuration"), "Topics");
        contentHolder.add(createContentPanel("Subscribers List"), "Subscribers");
        contentHolder.add(createContentPanel("System Logs"), "Logs");

        // Split Pane
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, nav, contentHolder);
        splitPane.setDividerLocation(220);
        splitPane.setBorder(null);

        // Main Layout
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(headerPanel, BorderLayout.NORTH);
        getContentPane().add(splitPane, BorderLayout.CENTER);
    }

    private JButton createNavButton(String text, Color normalBg, Color hoverBg) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                super.paintComponent(g2);
                g2.dispose();
            }
        };

        button.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(180, 40));
        button.setBackground(normalBg);
        button.setForeground(Color.DARK_GRAY);

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (!button.getText().equals("Home")) button.setBackground(hoverBg);
            }
            public void mouseExited(MouseEvent e) {
                if (!button.getText().equals("Home")) button.setBackground(normalBg);
            }
        });

        return button;
    }

    private JPanel createContentPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel(title, SwingConstants.CENTER), BorderLayout.CENTER);
        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ServiceGUI().setVisible(true));
    }
}