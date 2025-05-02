package Service;

import common.PublisherInfo;
import common.StudentInfo;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.rmi.registry.LocateRegistry;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

/**
 * GUI for the service admin using ServiceIMP directly.
 */
public class ServiceGUI extends JFrame implements ServiceListener{
    private final ServiceIMP service;
    private JPanel headerPanel;
    private JLabel headerLabel;
    private JTextField searchTextField;
    private JPanel contentHolder;
    private JSplitPane splitPane;

    private DefaultTableModel notificationsModel = new DefaultTableModel();
    private DefaultTableModel logsModel = new DefaultTableModel();
    private JLabel publishersStat, studentsStat, notificationsStat, logsStat;
    private JPanel topicsPanel = new JPanel();
    private JPanel subsPanel = new JPanel();

    public ServiceGUI(ServiceIMP service) {
        this.service = service;
        service.addServiceListener(this);
        initComponents();
        customizeServiceUI();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    service.removeServiceListener(ServiceGUI.this);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }



    private void initComponents() {
        headerPanel = new JPanel(new BorderLayout(10, 0));
        headerPanel.setBorder(new EmptyBorder(6, 16, 6, 16));

        // Header label
        headerLabel = new JLabel("Banana Lagoona");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        headerPanel.add(headerLabel, BorderLayout.WEST);

        // Search field with fixed width
        JPanel searchContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        searchTextField = new JTextField("search", 20); // 20 columns width
        searchTextField.setPreferredSize(new Dimension(500, 32));
        searchTextField.setMaximumSize(new Dimension(500, 32));
        searchTextField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchContainer.add(searchTextField);
        headerPanel.add(searchContainer, BorderLayout.CENTER);

        // Add padding and rounded border
        searchTextField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));

        searchContainer.add(searchTextField);
        headerPanel.add(searchContainer, BorderLayout.CENTER);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1360, 720);
        setLocationRelativeTo(null);
    }

    private void customizeServiceUI() {
        JPanel nav = new JPanel();
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setBackground(Color.WHITE);
        nav.setBorder(new EmptyBorder(16, 16, 16, 16));

        String[] items = {"Home", "Notifications", "Topics", "Subscribers", "Logs"};
        Color normalBg = new Color(245, 245, 245);
        Color hoverBg = new Color(220, 220, 220);
        Color selBg = new Color(200, 230, 255);

        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.Y_AXIS));
        btnPanel.setOpaque(false);

        JButton[] navBtns = new JButton[items.length];
        for (int i = 0; i < items.length; i++) {
            final String key = items[i];
            JButton b = new JButton(key) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    try {
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                        // Paint the rounded background
                        g2.setColor(getBackground());
                        g2.fillRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 20, 20);

                        // Reset to original graphics for text rendering
                        g2.setColor(getForeground());

                        // Calculate text position for proper centering
                        FontMetrics fm = g2.getFontMetrics();
                        int textWidth = fm.stringWidth(getText());
                        int textHeight = fm.getHeight();
                        int x = (getWidth() - textWidth) / 2;
                        int y = (getHeight() - textHeight) / 2 + fm.getAscent();

                        // Draw the text manually
                        g2.drawString(getText(), x, y);
                    } finally {
                        g2.dispose();
                    }
                }

                @Override
                protected void paintBorder(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    try {
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(new Color(200, 200, 200));  // Light gray border

                        // Draw the border with inset to prevent clipping
                        g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 20, 20);
                    } finally {
                        g2.dispose();
                    }
                }

                // This is crucial to make the component respond to the custom shape
                @Override
                public boolean contains(int x, int y) {
                    // Check if the point is within the rounded rectangle
                    if (x < 0 || y < 0 || x >= getWidth() || y >= getHeight()) {
                        return false;
                    }

                    // For corners, use distance formula to check if point is inside the rounded part
                    int radius = 20;
                    int diameter = 2 * radius;

                    // Check top-left corner
                    if (x < radius && y < radius) {
                        return Math.pow(x - radius, 2) + Math.pow(y - radius, 2) <= Math.pow(radius, 2);
                    }

                    // Check top-right corner
                    if (x >= getWidth() - radius && y < radius) {
                        return Math.pow(x - (getWidth() - radius), 2) + Math.pow(y - radius, 2) <= Math.pow(radius, 2);
                    }

                    // Check bottom-left corner
                    if (x < radius && y >= getHeight() - radius) {
                        return Math.pow(x - radius, 2) + Math.pow(y - (getHeight() - radius), 2) <= Math.pow(radius, 2);
                    }

                    // Check bottom-right corner
                    if (x >= getWidth() - radius && y >= getHeight() - radius) {
                        return Math.pow(x - (getWidth() - radius), 2) + Math.pow(y - (getHeight() - radius), 2) <= Math.pow(radius, 2);
                    }

                    // If not in corners, it's in the main rectangle area
                    return true;
                }
            };

            // Button styling
            b.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            b.setFocusPainted(false);
            b.setContentAreaFilled(false);  // Crucial for custom painting
            b.setBorderPainted(true);       // We're handling border in paintBorder
            b.setOpaque(false);            // Set to false for transparency
            b.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
            b.setAlignmentX(Component.CENTER_ALIGNMENT);
            b.setMaximumSize(new Dimension(180, 44));  // Increased height slightly to accommodate borders
            b.setBackground(i == 0 ? selBg : normalBg);
            b.setForeground(Color.DARK_GRAY);

            b.addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) {
                    if (!b.getBackground().equals(selBg)) b.setBackground(hoverBg);
                    b.repaint();  // Force repaint to show hover effect
                }
                @Override public void mouseExited(MouseEvent e) {
                    if (!b.getBackground().equals(selBg)) b.setBackground(normalBg);
                    b.repaint();  // Force repaint to remove hover effect
                }
            });

            b.addActionListener(e -> {
                CardLayout cl = (CardLayout) contentHolder.getLayout();
                cl.show(contentHolder, key);
                for (int j = 0; j < navBtns.length; j++) {
                    navBtns[j].setBackground(items[j].equals(key) ? selBg : normalBg);
                    navBtns[j].repaint();  // Force repaint after changing state
                }
            });

            navBtns[i] = b;
            btnPanel.add(b);
            btnPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        }
        nav.add(btnPanel);

        contentHolder = new JPanel(new CardLayout());
        contentHolder.add(homePanel(), "Home");
        contentHolder.add(notifPanel(), "Notifications");
        contentHolder.add(topicsPanel(), "Topics");
        contentHolder.add(subsPanel(), "Subscribers");
        contentHolder.add(logsPanel(), "Logs");

        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, nav, contentHolder);
        splitPane.setDividerLocation(220);
        splitPane.setBorder(null);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(headerPanel, BorderLayout.NORTH);
        getContentPane().add(splitPane, BorderLayout.CENTER);
    }

    // Interface implementation
    @Override
    public void onStudentUpdate() {
        updateSubsPanel();
        updateStats();
    }

    @Override
    public void onPublisherUpdate() {
        updateTopicsPanel();
        updateStats();
    }

    @Override
    public void onNotificationUpdate() {
        updateNotifications();
    }

    @Override
    public void onLogUpdate() {
        updateLogs();
    }

    private void updateStats() {
        SwingUtilities.invokeLater(() -> {
            publishersStat.setText(String.valueOf(service.getPublishers().size()));
            studentsStat.setText(String.valueOf(service.getStudents().size()));
            notificationsStat.setText(String.valueOf(service.getNotificationCount()));
            logsStat.setText(String.valueOf(service.getLogs().size()));
        });
    }

    private void updateNotifications() {
        SwingUtilities.invokeLater(() -> {
            notificationsModel.setRowCount(0);
            service.getRecentNotifications().forEach(ne -> notificationsModel.addRow(
                    new Object[]{ne.getPublisherId(), ne.getMessage().getContent(), ne.getMessage().getTimestamp()}
            ));
        });
    }

    private void updateLogs() {
        System.out.println("Updating logs..." + service.getLogs().size());
        SwingUtilities.invokeLater(() -> {
            logsModel.setRowCount(0);
            service.getLogs().forEach(log -> {
                String[] parts = log.split(" - ", 2);
                logsModel.addRow(parts);
            });
        });
    }

    private void updateTopicsPanel() {
        SwingUtilities.invokeLater(() -> {
            topicsPanel.removeAll();
            service.getPublishers().forEach((id, rec) -> {
                JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
                row.add(new JLabel(rec.getInfo().getIcon()));
                JPanel txt = new JPanel(new GridLayout(2, 1));
                txt.add(new JLabel(rec.getInfo().getName()));
                txt.add(new JLabel(rec.getInfo().getDescription()));
                row.add(txt);
                topicsPanel.add(row);
            });
            topicsPanel.revalidate();
            topicsPanel.repaint();
        });
    }

    private void updateSubsPanel() {
        SwingUtilities.invokeLater(() -> {
            subsPanel.removeAll();
            service.getStudents().forEach((id, rec) -> {
                JPanel row = new JPanel(new BorderLayout());
                row.add(new JLabel(rec.getInfo().getName() + " (" + id + ")"), BorderLayout.NORTH);
                row.add(new JLabel("Subs: " + rec.getSubs()), BorderLayout.SOUTH);
                subsPanel.add(row);
            });
            subsPanel.revalidate();
            subsPanel.repaint();
        });
    }
    private JPanel homePanel() {
        JPanel p = new JPanel(new GridLayout(2, 2, 20, 20));
        p.setBorder(new EmptyBorder(20, 20, 20, 20));
        p.add(statCard("Publishers", publishersStat = createStatLabel()));
        p.add(statCard("Students", studentsStat = createStatLabel()));
        p.add(statCard("Notifications", notificationsStat = createStatLabel()));
        p.add(statCard("Logs", logsStat = createStatLabel()));
        updateStats();
        return p;
    }
    private JLabel createStatLabel() {
        JLabel label = new JLabel("0", SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 36));
        return label;
    }

    private JPanel statCard(String title, JLabel value) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(240, 248, 255));
        card.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(value, BorderLayout.CENTER);
        return card;
    }
    private JPanel notifPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(new EmptyBorder(20, 20, 20, 20));
        notificationsModel = new DefaultTableModel(new String[]{"Publisher", "Message", "Time"}, 0);  // Initialize instance variable
        JTable table = new JTable(notificationsModel);  // Use the instance variable
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        return p;
    }

    // Change topics panel initialization
    private JScrollPane topicsPanel() {
        topicsPanel = new JPanel();  // Use instance variable
        topicsPanel.setLayout(new BoxLayout(topicsPanel, BoxLayout.Y_AXIS));
        topicsPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        updateTopicsPanel();  // Initial population
        return new JScrollPane(topicsPanel);
    }
    private JScrollPane subsPanel() {
        subsPanel = new JPanel();  // Use instance variable
        subsPanel.setLayout(new BoxLayout(subsPanel, BoxLayout.Y_AXIS));
        subsPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        updateSubsPanel();  // Initial population
        return new JScrollPane(subsPanel);
    }
    private JPanel logsPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(new EmptyBorder(20, 20, 20, 20));
        logsModel = new DefaultTableModel(new String[]{"Timestamp", "Event"}, 0);  // Initialize instance variable
        JTable table = new JTable(logsModel);  // Use the instance variable
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        return p;
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
            button.setOpaque(true);
            button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            button.setMaximumSize(new Dimension(180, 40));
            button.setBackground(normalBg);
            button.setForeground(Color.DARK_GRAY);
            button.addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { if (!text.equals("Home")) button.setBackground(hoverBg);}
                @Override public void mouseExited(MouseEvent e)  { if (!text.equals("Home")) button.setBackground(normalBg);}
            });
            return button;
        }
    
        public static void main(String[] args) throws Exception {
            ServiceIMP service = new ServiceIMP();
            // start registry
            try { LocateRegistry.createRegistry(1099);} catch(Exception ignored) {}
            java.rmi.Naming.rebind("PubSubService", service);
            SwingUtilities.invokeLater(() -> new ServiceGUI(service).setVisible(true));
        }
    }
