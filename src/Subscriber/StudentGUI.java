package Subscriber;

import Service.ServiceIF;
import common.PublisherInfo;
import common.StudentInfo;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Map;

public class StudentGUI extends javax.swing.JFrame {
    private javax.swing.JLabel headerLabel;
    private javax.swing.JPanel headerPanel;
    private javax.swing.JPanel homePanel;
    private javax.swing.JTextField searchTextField;
    private javax.swing.JPanel servicesPanel;
    private javax.swing.JTabbedPane tabbedPanel;
    private javax.swing.JButton logoutButton;

    private ServiceIF service;
    private StudentInfo studentInfo;
    private JButton refreshButton;

    ArrayList<PublisherInfo> publishers = new ArrayList<>();

    public StudentGUI(ServiceIF service, StudentInfo studentInfo) {
        this.service = service;
        this.studentInfo = studentInfo;

        initComponents();
        customizeTabbedPane();
        customizeHomeUI();
        initializeServicesTab();
    }

    private void initComponents() {
        headerPanel = new javax.swing.JPanel();
        headerLabel = new javax.swing.JLabel();
        searchTextField = new javax.swing.JTextField();
        logoutButton = new javax.swing.JButton();
        tabbedPanel = new javax.swing.JTabbedPane();
        homePanel = new javax.swing.JPanel();
        servicesPanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(1360, 720));

        // Header Label
        headerLabel.setFont(new java.awt.Font("Segoe UI", Font.BOLD, 20));
        headerLabel.setText("Banana Lagoona");

        // Search Field
        searchTextField.setFont(new java.awt.Font("Segoe UI", Font.PLAIN, 14));
        searchTextField.setText("search");

        // Logout Button
        customizeLogoutButton();

        // Header Panel Layout
        headerPanel.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
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

        // Tabbed Panel
        tabbedPanel.addTab("Home", homePanel);

        javax.swing.GroupLayout servicesPanelLayout = new javax.swing.GroupLayout(servicesPanel);
        servicesPanel.setLayout(servicesPanelLayout);
        servicesPanelLayout.setHorizontalGroup(
                servicesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 1348, Short.MAX_VALUE)
        );
        servicesPanelLayout.setVerticalGroup(
                servicesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 593, Short.MAX_VALUE)
        );
        tabbedPanel.addTab("Services", servicesPanel);

        // Main Layout
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(headerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(tabbedPanel))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(headerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tabbedPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 628, Short.MAX_VALUE)
                                .addContainerGap())
        );

        pack();
    }

    private void customizeLogoutButton() {
        Color normalBg = new Color(245, 245, 245);
        Color hoverBg = new Color(220, 220, 220);

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

        logoutButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        logoutButton.setFocusPainted(false);
        logoutButton.setContentAreaFilled(false);
        logoutButton.setOpaque(false);
        logoutButton.setBorder(BorderFactory.createEmptyBorder(6,12,6,12));
        logoutButton.setBackground(normalBg);
        logoutButton.setForeground(Color.DARK_GRAY);
        logoutButton.setAlignmentY(Component.CENTER_ALIGNMENT);

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
                    this,
                    "Are you sure you want to logout?",
                    "Confirm Logout",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {

                System.exit(0);
            }
        });
    }

    private void customizeTabbedPane() {
        // 1) Neutralize default painting
        tabbedPanel.setBorder(BorderFactory.createEmptyBorder());
        tabbedPanel.setUI(new BasicTabbedPaneUI() {
            @Override protected void paintTabBackground(Graphics g, int tp, int ti,
                                                        int x, int y, int w, int h,
                                                        boolean isSelected) {}
            @Override protected void paintTabBorder(Graphics g, int tp, int ti,
                                                    int x, int y, int w, int h,
                                                    boolean isSelected) {}
            @Override protected void paintFocusIndicator(Graphics g, int tp,
                                                         Rectangle[] rects, int ti,
                                                         Rectangle iconRect,
                                                         Rectangle textRect,
                                                         boolean isSelected) {}
        });

        // 2) Wrap each title in a JLabel with 20pt font
        for (int i = 0; i < tabbedPanel.getTabCount(); i++) {
            JLabel lbl = new JLabel(tabbedPanel.getTitleAt(i), SwingConstants.CENTER);
            Font baseFont = new Font("Segoe UI", Font.PLAIN, 20);
            lbl.setFont(baseFont);
            lbl.setOpaque(false);
            tabbedPanel.setTabComponentAt(i, lbl);
        }

        // 3) ChangeListener that applies identical total heights
        tabbedPanel.addChangeListener(e -> {
            int sel = tabbedPanel.getSelectedIndex();
            for (int i = 0; i < tabbedPanel.getTabCount(); i++) {
                JLabel lbl = (JLabel) tabbedPanel.getTabComponentAt(i);
                Font current = lbl.getFont();
                if (i == sel) {
                    // SELECTED: bold + underline
                    lbl.setFont(current.deriveFont(current.getStyle() | Font.BOLD));
                    Border underline = BorderFactory.createMatteBorder(0, 0, 3, 0, Color.BLUE);
                    Border padAbove = BorderFactory.createEmptyBorder(8, 12, 0, 12);
                    lbl.setBorder(new CompoundBorder(underline, padAbove));
                } else {
                    // UNSELECTED: plain + empty bottom pad
                    lbl.setFont(current.deriveFont(current.getStyle() & ~Font.BOLD));
                    lbl.setBorder(BorderFactory.createEmptyBorder(8, 12, 3, 12));
                }
            }
        });

        // 4) Force initial styling
        tabbedPanel.setSelectedIndex(0);

        int sel = tabbedPanel.getSelectedIndex();
        for (int i = 0; i < tabbedPanel.getTabCount(); i++) {
            JLabel lbl = (JLabel) tabbedPanel.getTabComponentAt(i);
            Font current = lbl.getFont();
            if (i == sel) {
                // SELECTED: bold + underline
                lbl.setFont(current.deriveFont(current.getStyle() | Font.BOLD));
                Border underline = BorderFactory.createMatteBorder(0, 0, 3, 0, Color.BLUE);
                Border padAbove = BorderFactory.createEmptyBorder(8, 12, 0, 12);
                lbl.setBorder(new CompoundBorder(underline, padAbove));
            } else {
                // UNSELECTED: plain + empty bottom pad
                lbl.setFont(current.deriveFont(current.getStyle() & ~Font.BOLD));
                lbl.setBorder(BorderFactory.createEmptyBorder(8, 12, 3, 12));
            }
        }
    }
    // Helper: generate a solid-color background based on the name

    private void customizeHomeUI() {
        // 1) Hard-coded notifier names (14 entries)
        String[] notifiers = {
                "Alice", "Bob",   "Charlie", "Diana", "Eve",
                "Frank", "Grace", "Heidi",   "Ivan",  "Judy",
                "Alice", "Bob",   "Charlie", "Diana"
        };

// 2) Hard-coded messages + timestamps (14 rows each)
        String[][] msgs = {
                {"Hey, are we still on for tomorrow?", "Don’t forget the report!","Quick check-in","See you soon"},
                {"Lunch at 1?", "Here’s the draft."},
                {"Project update: all tests passed.", "Deploying now."},
                {"Reminder: meeting at 4pm."},
                {"Secret mission briefing."},
                {"Status: OK", "Ping me when free."},
                {"Party at my place!", "Bring snacks."},
                {"Docs sent.", "Let me know feedback."},
                {"Happy birthday!", "Cake at 3."},
                {"Hello!"},
                {"Hey, are we still on for tomorrow?", "Don’t forget the report!","Quick check-in","See you soon"},
                {"Lunch at 1?", "Here’s the draft."},
                {"Project update: all tests passed.", "Deploying now."},
                {"Reminder: meeting at 4pm."}
        };

        String[][] times = {
                {"Apr 30, 2025 10:15 AM", "Apr 29, 2025 04:22 PM","Apr 28, 2025 09:00 AM","Apr 27, 2025 11:45 AM"},
                {"Apr 30, 2025 12:00 PM", "Apr 30, 2025 09:45 AM"},
                {"Apr 29, 2025 05:30 PM", "Apr 29, 2025 06:00 PM"},
                {"Apr 28, 2025 03:45 PM"},
                {"Apr 27, 2025 11:11 AM"},
                {"Apr 26, 2025 08:00 AM", "Apr 26, 2025 12:30 PM"},
                {"Apr 25, 2025 07:20 PM", "Apr 25, 2025 08:45 PM"},
                {"Apr 24, 2025 02:15 PM"},
                {"Apr 23, 2025 09:00 AM", "Apr 23, 2025 09:05 AM"},
                {"Apr 22, 2025 10:00 AM"},
                {"Apr 30, 2025 10:15 AM", "Apr 29, 2025 04:22 PM","Apr 28, 2025 09:00 AM","Apr 27, 2025 11:45 AM"},
                {"Apr 30, 2025 12:00 PM", "Apr 30, 2025 09:45 AM"},
                {"Apr 29, 2025 05:30 PM", "Apr 29, 2025 06:00 PM"},
                {"Apr 28, 2025 03:45 PM"}
        };


        // 3) Build left JList with letter-icons
        DefaultListModel<String> notiModel = new DefaultListModel<>();
        for (String n : notifiers) notiModel.addElement(n);
        JList<String> notifierList = new JList<>(notiModel);
        notifierList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        notifierList.setFont(new Font("Segoe UI", Font.PLAIN, 18));

        notifierList.setCellRenderer((list, value, idx, sel, focus) -> {
            // create a letter-icon for the first char in the name
            Icon icon = createLetterIcon(value.charAt(0), 36, getColorForName(value), Color.WHITE);
            JLabel lbl = new JLabel(" " + value, icon, JLabel.LEFT);
            lbl.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
            lbl.setOpaque(sel);
            if (sel) lbl.setBackground(new Color(220, 235, 255));
            return lbl;
        });

        // 4) Right-hand content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);

        // 5) Wrap both in scroll panes
        JScrollPane leftScroll  = new JScrollPane(notifierList);
        JScrollPane rightScroll = new JScrollPane(contentPanel);
        leftScroll.setBorder(null);
        rightScroll.setBorder(null);

        // 6) JSplitPane setup
        JSplitPane homeSplit = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                leftScroll,
                rightScroll
        );
        homeSplit.setDividerLocation(200);
        homeSplit.setResizeWeight(0);
        homeSplit.setBorder(null);

        // 7) Update messages on selection
        notifierList.addListSelectionListener(ev -> {
            if (ev.getValueIsAdjusting()) return;
            int idx = notifierList.getSelectedIndex();
            contentPanel.removeAll();
            if (idx >= 0) {
                for (int j = 0; j < msgs[idx].length; j++) {
                    JPanel card = new JPanel(new BorderLayout());
                    card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
                    card.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                            BorderFactory.createEmptyBorder(8, 8, 8, 8)
                    ));
                    JTextArea ta = new JTextArea(msgs[idx][j]);
                    ta.setLineWrap(true); ta.setWrapStyleWord(true);
                    ta.setEditable(false);
                    ta.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                    ta.setBackground(Color.WHITE);
                    card.add(ta, BorderLayout.CENTER);

                    JLabel timeLbl = new JLabel(times[idx][j], SwingConstants.RIGHT);
                    timeLbl.setFont(new Font("Segoe UI", Font.ITALIC, 12));
                    card.add(timeLbl, BorderLayout.SOUTH);

                    contentPanel.add(card);
                    contentPanel.add(Box.createRigidArea(new Dimension(0, 8)));
                }
            }
            contentPanel.revalidate();
            contentPanel.repaint();
        });

        // 8) Place split pane into homePanel
        homePanel.removeAll();
        homePanel.setLayout(new BorderLayout());
        homePanel.add(homeSplit, BorderLayout.CENTER);

        // 9) Select the first notifier by default
        notifierList.setSelectedIndex(0);
    }

    private Color getColorForName(String name) {
        float hue = (name.toUpperCase().charAt(0) - 'A') / 26f;
        return Color.getHSBColor(hue, 0.5f, 0.85f);
    }

    // Helper: create a circular icon with a letter
    private Icon createLetterIcon(char letter, int size, Color bg, Color fg) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // fill circle
        g.setColor(bg);
        g.fillOval(0, 0, size, size);
        // draw letter
        g.setColor(fg);
        Font font = new Font("Segoe UI", Font.BOLD, size/2);
        g.setFont(font);
        FontMetrics fm = g.getFontMetrics();
        String s = String.valueOf(letter).toUpperCase();
        int x = (size - fm.stringWidth(s)) / 2;
        int y = (size - fm.getHeight()) / 2 + fm.getAscent();
        g.drawString(s, x, y);
        g.dispose();
        return new ImageIcon(img);
    }

    private void initializeServicesTab() {
        servicesPanel.setLayout(new BorderLayout());

        // Refresh button panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        refreshButton = new JButton("Refresh Publishers");
        refreshButton.addActionListener(e -> refreshPublishers());
        topPanel.add(refreshButton);

        // Grid panel for publisher cards
        JPanel gridPanel = createPublishersGrid();
        JScrollPane scrollPane = new JScrollPane(gridPanel);

        servicesPanel.add(topPanel, BorderLayout.NORTH);
        servicesPanel.add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createPublishersGrid() {
        JPanel gridPanel = new JPanel(new GridLayout(0, 3, 20, 20));
        gridPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Hardcoded publishers

        publishers.add(new PublisherInfo("1", "Tech News", "Latest technology updates",
                createPublisherIcon(Color.BLUE, "T")));
        publishers.add(new PublisherInfo("2", "Sports Daily", "Live scores and analysis",
                createPublisherIcon(Color.RED, "S")));
        publishers.add(new PublisherInfo("3", "Finance Watch", "Market trends & reports",
                createPublisherIcon(Color.GREEN, "F")));
        publishers.add(new PublisherInfo("4", "Cooking Corner", "Recipes & tips",
                createPublisherIcon(Color.ORANGE, "C")));
        publishers.add(new PublisherInfo("5", "Travel Guide", "Destination reviews",
                createPublisherIcon(Color.CYAN, "T")));
        publishers.add(new PublisherInfo("6", "Gaming Hub", "Game reviews & news",
                createPublisherIcon(Color.MAGENTA, "G")));

        for (PublisherInfo publisher : publishers) {
            gridPanel.add(createPublisherCard(publisher));
        }

        return gridPanel;
    }

    private JPanel createPublisherCard(PublisherInfo publisher) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        card.setPreferredSize(new Dimension(300, 200));

        // Publisher Icon
        JPanel iconPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel iconLabel = new JLabel(publisher.getIcon());
        iconPanel.add(iconLabel);
        card.add(iconPanel, BorderLayout.NORTH);

        // Publisher Info
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));

        JLabel nameLabel = new JLabel(publisher.getName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        nameLabel.setHorizontalAlignment(SwingConstants.CENTER);  // Center alignment
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);      // For BoxLayout centering

        JTextArea descArea = new JTextArea(publisher.getDescription());
        descArea.setEditable(false);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setBackground(card.getBackground());
        descArea.setAlignmentX(Component.CENTER_ALIGNMENT);

        infoPanel.add(Box.createVerticalGlue());
        infoPanel.add(nameLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        infoPanel.add(descArea);
        infoPanel.add(Box.createVerticalGlue());

        card.add(infoPanel, BorderLayout.CENTER);

        // Subscribe Button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));  // Centered button
        JButton subscribeButton = new JButton("Subscribe");
        subscribeButton.addActionListener(e -> handleSubscription(publisher));
        buttonPanel.add(subscribeButton);

        card.add(buttonPanel, BorderLayout.SOUTH);

        return card;
    }
    private void handleSubscription(PublisherInfo publisher) {
        try {
            service.subscribe(studentInfo.getId(), publisher.getId());
            JOptionPane.showMessageDialog(this,
                    "Subscribed to " + publisher.getName(),
                    "Subscription Success",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (RemoteException ex) {
            JOptionPane.showMessageDialog(this,
                    "Subscription failed: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private ImageIcon createPublisherIcon(Color bgColor, String text) {
        int size = 80;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(bgColor);
        g2d.fillOval(0, 0, size, size);

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 24));
        FontMetrics fm = g2d.getFontMetrics();
        int x = (size - fm.stringWidth(text)) / 2;
        int y = (size - fm.getHeight()) / 2 + fm.getAscent();
        g2d.drawString(text, x, y);

        g2d.dispose();
        return new ImageIcon(image);
    }

    private void refreshPublishers() {
        servicesPanel.removeAll();
        initializeServicesTab();
        servicesPanel.revalidate();
        servicesPanel.repaint();
    }

    public static void main(String[] args) {
        // Example usage
        SwingUtilities.invokeLater(() -> {
            ServiceIF service = null; // Replace with actual service instance
            StudentInfo studentInfo = new StudentInfo("John Doe", "123456");
            new StudentGUI(service, studentInfo).setVisible(true);
        });
    }
}
