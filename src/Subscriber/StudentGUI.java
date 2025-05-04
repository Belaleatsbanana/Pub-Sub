package Subscriber;

import Service.ServiceIF;
import common.Message;
import common.PublisherInfo;
import common.StudentInfo;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.List;

public class StudentGUI extends javax.swing.JFrame {
    private javax.swing.JLabel headerLabel;
    private javax.swing.JPanel headerPanel;
    private javax.swing.JPanel homePanel;
    private javax.swing.JTextField searchTextField;
    private javax.swing.JPanel servicesPanel;
    private javax.swing.JTabbedPane tabbedPanel;
    private javax.swing.JButton logoutButton;

    private StudentIMP studentImp;
    private ServiceIF service;
    private StudentInfo studentInfo;
    private JButton refreshButton;

    ArrayList<PublisherInfo> publishers = new ArrayList<>();

    public StudentGUI(ServiceIF service, StudentInfo studentInfo) {
        this.service = service;
        this.studentInfo = studentInfo;

        try {
            // Create and initialize the StudentIMP with this GUI
            this.studentImp = new StudentIMP(this);
        } catch (RemoteException e) {
            JOptionPane.showMessageDialog(null,
                    "Failed to initialize student implementation: " + e.getMessage(),
                    "Initialization Error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        initComponents();
        customizeTabbedPane();
        customizeHomeUI();
        initializeServicesTab();

        // Add window listener to handle cleanup when closing
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // 1) Tell the server to deregister this student
                try {
                    service.deregisterStudent(studentInfo.getId());
                } catch (RemoteException ex) {
                    // log or show warning if deregistration fails
                    System.err.println("Warning: deregister failed: " + ex.getMessage());
                }

                // 2) Unexport our callback object so no more RMI calls land here
                if (studentImp != null) {
                    try {
                        UnicastRemoteObject.unexportObject(studentImp, true);
                    } catch (NoSuchObjectException ex) {
                        // ignore
                    }
                    studentImp.cleanup();
                }

                // 3) Now actually exit
                System.exit(0);
            }
        });
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

        headerLabel.setFont(new java.awt.Font("Segoe UI", Font.BOLD, 20));
        headerLabel.setText("Banana Lagoona");

        searchTextField.setFont(new java.awt.Font("Segoe UI", Font.PLAIN, 14));
        searchTextField.setText("search");

        customizeLogoutButton();

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
        logoutButton.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
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
                if (studentImp != null) {
                    studentImp.cleanup();
                }
                try {
                    service.deregisterStudent(studentInfo.getId());
                } catch (RemoteException ex) {
                    // log or show warning if deregistration fails
                    System.err.println("Warning: deregister failed: " + ex.getMessage());
                }
                System.exit(0);
            }
        });
    }

    private void customizeTabbedPane() {
        tabbedPanel.setBorder(BorderFactory.createEmptyBorder());
        tabbedPanel.setUI(new BasicTabbedPaneUI() {
            @Override
            protected void paintTabBackground(Graphics g, int tp, int ti,
                                              int x, int y, int w, int h,
                                              boolean isSelected) {
            }

            @Override
            protected void paintTabBorder(Graphics g, int tp, int ti,
                                          int x, int y, int w, int h,
                                          boolean isSelected) {
            }

            @Override
            protected void paintFocusIndicator(Graphics g, int tp,
                                               Rectangle[] rects, int ti,
                                               Rectangle iconRect,
                                               Rectangle textRect,
                                               boolean isSelected) {
            }
        });

        for (int i = 0; i < tabbedPanel.getTabCount(); i++) {
            JLabel lbl = new JLabel(tabbedPanel.getTitleAt(i), SwingConstants.CENTER);
            Font baseFont = new Font("Segoe UI", Font.PLAIN, 20);
            lbl.setFont(baseFont);
            lbl.setOpaque(false);
            tabbedPanel.setTabComponentAt(i, lbl);
        }

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

    private void customizeHomeUI() {
        homePanel.setLayout(new BorderLayout());

        // Get subscribed publishers from StudentIMP
        List<PublisherInfo> subscribedPublishers = studentImp.getSubscribedPublishers();
        if (!subscribedPublishers.isEmpty()) {
            publishers.addAll(subscribedPublishers);
        }

        // 1) Build left JList with publisher list
        DefaultListModel<PublisherInfo> notiModel = new DefaultListModel<>();
        for (PublisherInfo publisher : publishers) {
            notiModel.addElement(publisher);
        }

        JList<PublisherInfo> notifierList = new JList<>(notiModel);
        notifierList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        notifierList.setFont(new Font("Segoe UI", Font.PLAIN, 18));

        notifierList.setCellRenderer((list, value, idx, sel, focus) -> {
            String name = value.getName();
            Icon icon = value.getIcon();

            if (icon == null) {
                icon = createLetterIcon(name.charAt(0), 36, getColorForName(name), Color.WHITE);
            } else if (icon.getIconWidth() != 36 || icon.getIconHeight() != 36) {
                icon = resizeIcon(icon, 36, 36);
            }

            JLabel lbl = new JLabel(" " + name, icon, JLabel.LEFT);
            lbl.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
            lbl.setOpaque(sel);
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 18));
            if (sel) lbl.setBackground(new Color(220, 235, 255));
            return lbl;
        });

        // 2) Right-hand content panel for messages
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);

        // 3) Split pane setup
        JSplitPane homeSplit = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                new JScrollPane(notifierList),
                new JScrollPane(contentPanel)
        );
        homeSplit.setDividerLocation(200);
        homeSplit.setResizeWeight(0);
        homeSplit.setBorder(null);

        // 4) Message display update on selection
        notifierList.addListSelectionListener(ev -> {
            if (!ev.getValueIsAdjusting()) {
                PublisherInfo selected = notifierList.getSelectedValue();
                if (selected != null) {
                    updateMessageDisplay(selected);
                }
            }
        });

        homePanel.add(homeSplit, BorderLayout.CENTER);
        if (!publishers.isEmpty()) {
            notifierList.setSelectedIndex(0);
        }
    }

    private Icon resizeIcon(Icon icon, int size, int height) {
        BufferedImage result = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = result.createGraphics();

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        BufferedImage sourceImg;
        if (icon instanceof ImageIcon) {
            Image img = ((ImageIcon) icon).getImage();
            if (img instanceof BufferedImage) {
                sourceImg = (BufferedImage) img;
            } else {
                sourceImg = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = sourceImg.createGraphics();
                g.drawImage(img, 0, 0, null);
                g.dispose();
            }
        } else {
            // For non-ImageIcon, create a new image and paint the icon into it
            sourceImg = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = sourceImg.createGraphics();
            icon.paintIcon(null, g, 0, 0);
            g.dispose();
        }

        g2d.setClip(new java.awt.geom.Ellipse2D.Float(0, 0, size, size));

        // scaling it to fit
        g2d.drawImage(sourceImg, 0, 0, size, size, null);
        g2d.dispose();

        return new ImageIcon(result);
    }


    private Color getColorForName(String name) {
        float hue = (name.toUpperCase().charAt(0) - 'A') / 26f;
        return Color.getHSBColor(hue, 0.5f, 0.85f);
    }

    private Icon createLetterIcon(char letter, int size, Color bg, Color fg) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(bg);
        g.fillOval(0, 0, size, size);
        g.setColor(fg);
        Font font = new Font("Segoe UI", Font.BOLD, size / 2);
        g.setFont(font);
        FontMetrics fm = g.getFontMetrics();
        String s = String.valueOf(letter).toUpperCase();
        int x = (size - fm.stringWidth(s)) / 2;
        int y = (size - fm.getHeight()) / 2 + fm.getAscent();
        g.drawString(s, x, y);
        g.dispose();
        return new ImageIcon(img);
    }

    private JPanel createPublisherCard(PublisherInfo publisher) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        card.setPreferredSize(new Dimension(250, 140));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        Icon icon = createLetterIcon(publisher.getName().charAt(0), 32,
                getColorForName(publisher.getName()), Color.WHITE);
        JLabel iconLabel = new JLabel(icon);
        topPanel.add(iconLabel);

        JLabel nameLabel = new JLabel(publisher.getName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        topPanel.add(nameLabel);
        card.add(topPanel, BorderLayout.NORTH);

        JTextArea descArea = new JTextArea(publisher.getDescription());
        descArea.setEditable(false);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        JScrollPane descScroll = new JScrollPane(descArea);
        descScroll.setBorder(null);
        card.add(descScroll, BorderLayout.CENTER);

        JButton subButton = new JButton();
        try {
            boolean isSubscribed = service.isSubscribed(studentInfo.getId(), publisher.getId());
            subButton.setText(isSubscribed ? "Unsubscribe" : "Subscribe");
        } catch (RemoteException ex) {
            subButton.setText("Subscribe");
        }

        subButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subButton.setPreferredSize(new Dimension(100, 25));
        subButton.addActionListener(e -> handleSubscription(publisher, subButton));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(subButton);
        card.add(buttonPanel, BorderLayout.SOUTH);

        return card;
    }

    public void handleNewNotification(PublisherInfo publisher, Message message) {
        boolean publisherExists = false;
        for (PublisherInfo pub : publishers) {
            if (pub.getId().equals(publisher.getId())) {
                publisherExists = true;
                break;
            }
        }

        if (!publisherExists) {
            publishers.add(publisher);
            SwingUtilities.invokeLater(this::refreshHomePanel);
        }

        SwingUtilities.invokeLater(() -> {
            JList<?> notifierList = null;
            for (Component comp : homePanel.getComponents()) {
                if (comp instanceof JSplitPane) {
                    Component left = ((JSplitPane) comp).getLeftComponent();
                    if (left instanceof JScrollPane && ((JScrollPane) left).getViewport().getView() instanceof JList) {
                        notifierList = (JList<?>) ((JScrollPane) left).getViewport().getView();
                        break;
                    }
                }
            }

            if (notifierList != null) {
                Object selectedValue = notifierList.getSelectedValue();
                if (selectedValue instanceof PublisherInfo &&
                        ((PublisherInfo) selectedValue).getId().equals(publisher.getId())) {
                    updateMessageDisplay((PublisherInfo) selectedValue);
                }

                DefaultListModel<PublisherInfo> model = (DefaultListModel<PublisherInfo>) notifierList.getModel();
                boolean publisherInModel = false;
                for (int i = 0; i < model.getSize(); i++) {
                    if (model.getElementAt(i).getId().equals(publisher.getId())) {
                        publisherInModel = true;
                        break;
                    }
                }

                if (!publisherInModel) {
                    model.addElement(publisher);
                }
            }

            // cute
            showNotificationToast(publisher, message);
        });
    }

    private void updateMessageDisplay(PublisherInfo publisher) {
        JPanel contentPanel = null;
        for (Component comp : homePanel.getComponents()) {
            if (comp instanceof JSplitPane) {
                Component right = ((JSplitPane) comp).getRightComponent();
                if (right instanceof JScrollPane && ((JScrollPane) right).getViewport().getView() instanceof JPanel) {
                    contentPanel = (JPanel) ((JScrollPane) right).getViewport().getView();
                    break;
                }
            }
        }

        if (contentPanel != null) {
            contentPanel.removeAll();

            java.util.List<Message> messages = getMessagesForPublisher(publisher.getId());

            if (messages.isEmpty()) {
                JLabel noMessagesLabel = new JLabel("No messages from this publisher yet");
                noMessagesLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
                noMessagesLabel.setHorizontalAlignment(SwingConstants.CENTER);
                noMessagesLabel.setForeground(Color.GRAY);
                contentPanel.add(noMessagesLabel);
            } else {
                for (Message msg : messages) {
                    JPanel card = new JPanel(new BorderLayout());
                    card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
                    card.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                            BorderFactory.createEmptyBorder(8, 8, 8, 8)
                    ));

                    JTextArea ta = new JTextArea(msg.getContent());
                    ta.setLineWrap(true);
                    ta.setWrapStyleWord(true);
                    ta.setEditable(false);
                    ta.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                    ta.setBackground(Color.WHITE);
                    card.add(ta, BorderLayout.CENTER);

                    JLabel timeLbl = new JLabel(msg.getTimestamp().toString(), SwingConstants.RIGHT);
                    timeLbl.setFont(new Font("Segoe UI", Font.ITALIC, 12));
                    card.add(timeLbl, BorderLayout.SOUTH);

                    contentPanel.add(card);
                    contentPanel.add(Box.createRigidArea(new Dimension(0, 8)));
                }
            }

            contentPanel.revalidate();
            contentPanel.repaint();
        }
    }

    private void showNotificationToast(PublisherInfo publisher, Message message) {
        JDialog toast = new JDialog(this);
        toast.setUndecorated(true);
        toast.setLayout(new BorderLayout());

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel nameLabel = new JLabel(publisher.getName(), SwingConstants.LEFT);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

        Icon icon = publisher.getIcon();
        if (icon == null) {
            icon = createLetterIcon(publisher.getName().charAt(0), 24, getColorForName(publisher.getName()), Color.WHITE);
        } else if (icon.getIconWidth() != 24 || icon.getIconHeight() != 24) {
            icon = resizeIcon(icon, 24, 24);
        }
        nameLabel.setIcon(icon);
        headerPanel.add(nameLabel, BorderLayout.WEST);

        JButton closeBtn = new JButton("×");
        closeBtn.setFocusPainted(false);
        closeBtn.setContentAreaFilled(false);
        closeBtn.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        closeBtn.setFont(new Font("Arial", Font.BOLD, 16));
        closeBtn.addActionListener(e -> toast.dispose());
        headerPanel.add(closeBtn, BorderLayout.EAST);

        panel.add(headerPanel, BorderLayout.NORTH);

        String content = message.getContent();
        if (content.length() > 100) {
            content = content.substring(0, 97) + "...";
        }
        JTextArea msgText = new JTextArea(content);
        msgText.setEditable(false);
        msgText.setLineWrap(true);
        msgText.setWrapStyleWord(true);
        msgText.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        msgText.setBackground(panel.getBackground());
        panel.add(msgText, BorderLayout.CENTER);

        JLabel timeLabel = new JLabel(message.getTimestamp().toString(), SwingConstants.RIGHT);
        timeLabel.setFont(new Font("Segoe UI", Font.ITALIC, 10));
        timeLabel.setForeground(Color.GRAY);
        panel.add(timeLabel, BorderLayout.SOUTH);

        toast.add(panel);
        toast.pack();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        toast.setLocation(
                screenSize.width - toast.getWidth() - 20,
                screenSize.height - toast.getHeight() - 40
        );

        // Show and auto-close after 5 seconds
        toast.setVisible(true);

        new Timer(5000, e -> {
            toast.dispose();
            ((Timer) e.getSource()).stop();
        }).start();

        try {
            toast.setOpacity(0.0f);
            Timer fadeTimer = new Timer(50, null);
            final float[] opacity = {0.0f};
            fadeTimer.addActionListener(e -> {
                opacity[0] += 0.1f;
                if (opacity[0] > 1.0f) {
                    opacity[0] = 1.0f;
                    fadeTimer.stop();
                }
                toast.setOpacity(opacity[0]);
            });
            fadeTimer.start();
        } catch (Exception ex) {
            toast.setOpacity(1.0f);
        }
    }


    private java.util.List<Message> getMessagesForPublisher(String publisherId) {
        for (PublisherInfo publisher : publishers) {
            if (publisher.getId().equals(publisherId)) {
                return studentImp.getMessagesForPublisher(publisher);
            }
        }
        return Collections.emptyList();
    }

    private void initializeServicesTab() {
        servicesPanel.setLayout(new BorderLayout());

        // Refresh button panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        refreshButton = new JButton("Refresh Services");
        refreshButton.addActionListener(e -> refreshPublishers());
        topPanel.add(refreshButton);

        // Create wrapper panel with grid layout
        JPanel gridWrapper = new JPanel(new BorderLayout());
        JPanel gridPanel = createPublishersGrid();
        gridWrapper.add(gridPanel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(gridWrapper);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        servicesPanel.add(topPanel, BorderLayout.NORTH);
        servicesPanel.add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createPublishersGrid() {
        JPanel gridPanel = new JPanel(new GridLayout(0, 4, 15, 20));
        gridPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        try {
            // Get available publishers from service
            List<PublisherInfo> availablePublishers = service.getAvailablePublishers();

            publishers.clear();
            publishers.addAll(availablePublishers);

            if (availablePublishers.isEmpty()) {
                JLabel noPublishersLabel = new JLabel("No publishers available");
                noPublishersLabel.setFont(new Font("Segoe UI", Font.ITALIC, 16));
                noPublishersLabel.setHorizontalAlignment(SwingConstants.CENTER);
                gridPanel.add(noPublishersLabel);
            } else {
                for (PublisherInfo publisher : availablePublishers) {
                    gridPanel.add(createPublisherCard(publisher));
                }
            }
        } catch (RemoteException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error fetching publishers: " + ex.getMessage(),
                    "Service Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        return gridPanel;
    }

    private void refreshPublishers() {
        try {
            // Get list of available publishers from service
            List<PublisherInfo> availablePublishers = service.getAvailablePublishers();
            publishers.clear();
            publishers.addAll(availablePublishers);

            // Rebuild the services panel
            servicesPanel.removeAll();
            initializeServicesTab();
            servicesPanel.revalidate();
            servicesPanel.repaint();
        } catch (RemoteException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error refreshing publishers: " + ex.getMessage(),
                    "Refresh Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleSubscription(PublisherInfo publisher, JButton button) {
        try {
            boolean wasSubscribed = service.isSubscribed(studentInfo.getId(), publisher.getId());

            if (wasSubscribed) {
                service.unsubscribe(studentInfo.getId(), publisher.getId());
                JOptionPane.showMessageDialog(this,
                        "Unsubscribed from " + publisher.getName(),
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                service.subscribe(studentInfo.getId(), publisher.getId());
                JOptionPane.showMessageDialog(this,
                        "Subscribed to " + publisher.getName(),
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            }

            // Update UI immediately locally
            button.setText(wasSubscribed ? "Subscribe" : "Unsubscribe");

            if (wasSubscribed) {
                publishers.remove(publisher);
            } else {
                if (!publishers.contains(publisher)) {
                    publishers.add(publisher);
                }
            }

            refreshHomePanel();

        } catch (RemoteException ex) {
            JOptionPane.showMessageDialog(this,
                    "Operation failed: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshHomePanel() {
        // Remove all components from home panel
        homePanel.removeAll();

        // Re-initialize the home panel UI
        customizeHomeUI();

        homePanel.revalidate();
        homePanel.repaint();

        if (!publishers.isEmpty()) {
            updateMessageDisplay(publishers.getFirst());
        }
    }

    public StudentIMP getStudentImp(){
        return studentImp;
    }
}