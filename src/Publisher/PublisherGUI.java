package Publisher;

import Service.ServiceIF;
import common.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


public class PublisherGUI extends JFrame {
    private PublisherIMP publisherIMP;
    private PublisherInfo publisherInfo;
    private ServiceIF service;

    private JPanel headerPanel;
    private JLabel headerLabel;
    private JTextField searchTextField;
    private JPanel contentHolder;

    private DefaultListModel<String> postsModel = new DefaultListModel<>();
    private DefaultListModel<String> subscribersModel = new DefaultListModel<>();
    private JTextArea postInputArea;

    private JLabel totalPostsLabel;
    private JLabel subscribersLabel;
    private JLabel activeUsersLabel;

    private static final Color HOVER_COLOR = new Color(232, 240, 254);

    public PublisherGUI(PublisherIMP publisherIMP,PublisherInfo publisherInfo,ServiceIF service) {
        this.publisherIMP = publisherIMP;
        this.publisherInfo = publisherInfo;
        this.service = service;

        initComponents();
        customizePublisherUI();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // 1) Tell the server to deregister this student
                try {
                    service.deregisterPublisher(publisherInfo.getId());
                } catch (RemoteException ex) {
                    // log or show warning if deregistration fails
                    System.err.println("Warning: deregister failed: " + ex.getMessage());
                }

                // 2) Unexport our callback object so no more RMI calls land here
                if (publisherIMP != null) {
                    try {
                        UnicastRemoteObject.unexportObject(publisherIMP, true);
                    } catch (NoSuchObjectException _) {
                    }
                }

                System.exit(0);
            }
        });
    }



    private void initComponents() {
        headerPanel     = new JPanel();
        headerLabel     = new JLabel("Banana Lagoona");
        searchTextField = new JTextField("search");


        headerLabel.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        headerLabel.setText("Banana Lagoona");

        searchTextField.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        searchTextField.setText("search");

        Color normalBg   = new Color(245, 245, 245);
        Color hoverBg    = new Color(220, 220, 220);
        JButton logoutButton = new JButton("Logout") {

            // 4o8l 3aly gdn
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

        logoutButton.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        logoutButton.setFocusPainted(false);
        logoutButton.setContentAreaFilled(false);
        logoutButton.setOpaque(false);
        logoutButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
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
                    this, "Are you sure you want to logout?", "Confirm Logout", JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                // 1) Tell the server to deregister this publisher

                try {
                    service.deregisterPublisher(publisherInfo.getId());
                } catch (RemoteException ex) {
                    // log or show warning if deregistration fails
                    System.err.println("Warning: deregister failed: " + ex.getMessage());
                }

                System.exit(0);
            }
        });

        // header layout: name |   [search   ] [Logout]
        headerPanel.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        GroupLayout layout = new GroupLayout(headerPanel);
        headerPanel.setLayout(layout);

        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        searchTextField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        logoutButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        logoutButton.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));

        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addComponent(headerLabel)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                .addComponent(searchTextField, GroupLayout.PREFERRED_SIZE, 400, GroupLayout.PREFERRED_SIZE)
                        )
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(logoutButton)
                )
        );

        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
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

    private void customizePublisherUI() {
        JPanel nav = new JPanel();
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setBackground(Color.WHITE);
        nav.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        // Use publisher info for avatar(image icon) and name
        Icon avatar = createLetterIcon(publisherInfo.getName().charAt(0), 64, new Color(100, 150, 240), Color.WHITE);
        JLabel pic = new JLabel(avatar);
        pic.setAlignmentX(Component.CENTER_ALIGNMENT);
        nav.add(pic);
        nav.add(Box.createRigidArea(new Dimension(0, 12)));

        JLabel name = new JLabel(publisherInfo.getName());
        name.setFont(new Font("Segoe UI", Font.BOLD, 18));
        name.setAlignmentX(Component.CENTER_ALIGNMENT);
        nav.add(name);
        nav.add(Box.createRigidArea(new Dimension(0, 24)));


        String[] items = {"Dashboard","Content","Community"};
        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.Y_AXIS));
        btnPanel.setOpaque(false);

        Color normalBg   = new Color(245,245,245);
        Color hoverBg    = new Color(220,220,220);
        Color selectedBg = new Color(200,230,255);
        JButton[] navButtons = new JButton[items.length];
        final String[] current = {null};  // Fixed declaration

        for (int i = 0; i < items.length; i++) {
            String key = items[i];
            JButton b = new JButton(key) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(getBackground());
                    g2.fillRoundRect(0,0,getWidth(),getHeight(),20,20);
                    super.paintComponent(g2);
                    g2.dispose();
                }
            };
            b.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            b.setFocusPainted(false);
            b.setContentAreaFilled(false);
            b.setOpaque(false);
            b.setBorder(BorderFactory.createEmptyBorder(10,20,10,20));
            b.setAlignmentX(Component.CENTER_ALIGNMENT);
            b.setMaximumSize(new Dimension(180,40));
            b.setBackground(normalBg);
            b.setForeground(Color.DARK_GRAY);

            b.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (!key.equals(current[0])) b.setBackground(hoverBg);
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    if (!key.equals(current[0])) b.setBackground(normalBg);
                }
            });

            b.addActionListener(e -> {
                CardLayout cl = (CardLayout) contentHolder.getLayout();
                cl.show(contentHolder, key);
                current[0] = key;
                for (JButton btn : navButtons) {
                    if (btn != null) {
                        btn.setBackground(btn.getText().equals(key) ? selectedBg : normalBg);
                    }
                }
            });

            navButtons[i] = b;
            btnPanel.add(b);
            btnPanel.add(Box.createRigidArea(new Dimension(0,8)));
        }
        nav.add(btnPanel);

        contentHolder = new JPanel(new CardLayout());
        contentHolder.add(createDashboardPanel(), "Dashboard");
        contentHolder.add(createContentPanel(), "Content");
        contentHolder.add(createCommunityPanel(), "Community");

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, nav, contentHolder);
        split.setDividerLocation(220);
        split.setBorder(null);

        getContentPane().removeAll();
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(headerPanel, BorderLayout.NORTH);
        getContentPane().add(split,       BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private JPanel panel(String text) {
        JPanel p = new JPanel(new BorderLayout());
        p.add(new JLabel(text, SwingConstants.CENTER), BorderLayout.CENTER);
        return p;
    }

    private Icon createLetterIcon(char letter, int size, Color bg, Color fg) {
        BufferedImage img = new BufferedImage(size,size,BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(bg);
        g.fillOval(0,0,size,size);
        g.setColor(fg);
        g.setFont(new Font("Segoe UI", Font.BOLD, size/2));
        FontMetrics fm = g.getFontMetrics();
        String s = String.valueOf(letter).toUpperCase();
        int x = (size - fm.stringWidth(s))/2;
        int y = (size - fm.getHeight())/2 + fm.getAscent();
        g.drawString(s, x, y);
        g.dispose();
        return new ImageIcon(img);
    }



    // Custom class 34an round corners
    static class RoundedBorder implements Border {
        private int radius;
        private Color color;

        public RoundedBorder(int radius, Color color) {
            this.radius = radius;
            this.color = color;
        }

        public Insets getBorderInsets(Component c) {
            return new Insets(this.radius+1, this.radius+1, this.radius+1, this.radius+1);
        }

        public boolean isBorderOpaque() {
            return true;
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.drawRoundRect(x, y, width-1, height-1, radius, radius);
            g2.dispose();
        }
    }
    private void publishPost() {
        String postText = postInputArea.getText().trim();
        if (!postText.isEmpty()) {
            try {
                LocalDateTime timestamp = LocalDateTime.now();
                Message message = new Message(postText, timestamp);

                // Call service to publish
                service.publish(publisherInfo.getId(), message);

                // Update gui
                postsModel.addElement(formatPost(message));
                postInputArea.setText("");
                updateDashboardStats();
            } catch (RemoteException e) {
                JOptionPane.showMessageDialog(this, "Error publishing post", "Network Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    private String formatPost(Message message) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
        return message.getContent() + " - " + message.getTimestamp().format(formatter);
    }

    private void updateDashboardStats() {
        totalPostsLabel.setText(String.valueOf(postsModel.size()));
        subscribersLabel.setText(String.valueOf(subscribersModel.size()));
    }

    private JPanel createContentPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JList<String> postsList = new JList<>(postsModel);
        postsList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected,
                                                          boolean cellHasFocus) {
                JPanel panel = new JPanel(new BorderLayout());
                panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

                String[] parts = value.toString().split(" - ", 2);
                JTextArea content = new JTextArea(parts[0]);
                content.setLineWrap(true);
                content.setWrapStyleWord(true);
                content.setEditable(false);

                JLabel timestamp = new JLabel(parts.length > 1 ? parts[1] : "");
                timestamp.setFont(new Font("Segoe UI", Font.ITALIC, 12));
                timestamp.setForeground(Color.GRAY);

                panel.add(content, BorderLayout.CENTER);
                panel.add(timestamp, BorderLayout.SOUTH);
                return panel;
            }
        });

        panel.add(new JScrollPane(postsList), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createCommunityPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header with title and refresh button
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

        JLabel titleLabel = new JLabel("Subscribers");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));

        JButton refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        refreshButton.setPreferredSize(new Dimension(100, 32));
        refreshButton.addActionListener(e -> refreshSubscribers());

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(refreshButton, BorderLayout.EAST);

        // Subscriber list
        JList<String> subscribersList = new JList<>(subscribersModel);
        subscribersList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected,
                                                          boolean cellHasFocus) {
                JPanel itemPanel = new JPanel(new BorderLayout(10, 10));
                itemPanel.setBackground(isSelected ? HOVER_COLOR : Color.WHITE);
                itemPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

                JLabel icon = new JLabel(createLetterIcon(
                        value.toString().charAt(0),
                        32,
                        new Color(100, 150, 240),
                        Color.WHITE
                ));

                JLabel name = new JLabel(value.toString());
                name.setFont(new Font("Segoe UI", Font.PLAIN, 14));

                itemPanel.add(icon, BorderLayout.WEST);
                itemPanel.add(name, BorderLayout.CENTER);
                return itemPanel;
            }
        });

        JScrollPane scrollPane = new JScrollPane(subscribersList);
        scrollPane.setBorder(null);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }



    private void refreshSubscribers() {
        try {
            ArrayList<StudentInfo> subscribers = (ArrayList<StudentInfo>) service.getSubscribers(publisherInfo.getId());
            subscribersModel.clear();
            for (StudentInfo student : subscribers) {
                subscribersModel.addElement(student.getName());
            }
            updateDashboardStats();
        } catch (RemoteException e) {
            JOptionPane.showMessageDialog(this, "Error fetching subscribers", "Network Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(245, 245, 245));

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth();
                int h = getHeight();
                Color color1 = new Color(245, 245, 245);
                Color color2 = new Color(235, 235, 240);
                GradientPaint gp = new GradientPaint(0, 0, color1, w, h, color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
                g2d.dispose();
            }
        };
        headerPanel.setOpaque(false);

        JLabel headerLabel = new JLabel("Dashboard");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        headerLabel.setForeground(new Color(50, 50, 50));
        headerPanel.add(headerLabel);

        JLabel welcomeLabel = new JLabel("Welcome back, " + publisherInfo.getName() + "!");
        welcomeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        welcomeLabel.setForeground(new Color(100, 100, 100));
        welcomeLabel.setBorder(new EmptyBorder(5, 10, 0, 0));

        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("EEEE, MMMM d, yyyy");
        JLabel dateLabel = new JLabel(sdf.format(new Date()));
        dateLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        dateLabel.setForeground(new Color(120, 120, 120));
        dateLabel.setBorder(new EmptyBorder(0, 10, 0, 0));

        JPanel headerInfoPanel = new JPanel();
        headerInfoPanel.setLayout(new BoxLayout(headerInfoPanel, BoxLayout.Y_AXIS));
        headerInfoPanel.setOpaque(false);
        headerInfoPanel.add(welcomeLabel);
        headerInfoPanel.add(dateLabel);

        headerPanel.add(Box.createHorizontalStrut(20));
        headerPanel.add(headerInfoPanel);

        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        statsPanel.setBorder(new EmptyBorder(15, 0, 25, 0));
        statsPanel.setBackground(new Color(245, 245, 245));

        String[] stats = {"Total Posts", "Subscribers", "Active Users"};
        totalPostsLabel = new JLabel("0");
        subscribersLabel = new JLabel("0");
        activeUsersLabel = new JLabel("0");

        JLabel[] valueLabels = {totalPostsLabel, subscribersLabel, activeUsersLabel};
        Color[] colors = {
                new Color(79, 129, 189),  // Blue
                new Color(155, 187, 89),  // Green
                new Color(192, 80, 77)    // Red
        };

        for(int i = 0; i < stats.length; i++) {
            JPanel card = new JPanel(new BorderLayout()) {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setColor(getBackground());
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                    g2d.dispose();
                }
            };

            valueLabels[i].setFont(new Font("Segoe UI", Font.BOLD, 32));
            valueLabels[i].setForeground(colors[i]);

            card.setBackground(Color.WHITE);
            card.setBorder(BorderFactory.createCompoundBorder(
                    new ShadowBorder(3, new Color(0, 0, 0, 30)),
                    new EmptyBorder(20, 20, 20, 20)
            ));

            JPanel iconTextPanel = new JPanel(new BorderLayout(10, 0));
            iconTextPanel.setOpaque(false);

            JLabel iconLabel = new JLabel();
            iconLabel.setForeground(new Color(colors[i].getRed(), colors[i].getGreen(), colors[i].getBlue(), 200));
            iconLabel.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 24));

            if (i == 0) {
                iconLabel.setText("📝"); // Document icon for posts
            } else if (i == 1) {
                iconLabel.setText("👥"); // People icon for subscribers
            } else {
                iconLabel.setText("👁️"); // Eye icon for views
            }

            iconTextPanel.add(iconLabel, BorderLayout.WEST);
            iconTextPanel.add(new JLabel(stats[i]), BorderLayout.CENTER);

            card.add(valueLabels[i], BorderLayout.CENTER);
            card.add(iconTextPanel, BorderLayout.SOUTH);
            statsPanel.add(card);
        }

        JPanel composePanel = new JPanel(new BorderLayout());
        composePanel.setBackground(Color.WHITE);
        composePanel.setBorder(BorderFactory.createCompoundBorder(
                new ShadowBorder(3, new Color(0, 0, 0, 30)),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel composeTitle = new JLabel("Create New Post");
        composeTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        composeTitle.setForeground(new Color(70, 70, 70));
        composeTitle.setBorder(new EmptyBorder(0, 0, 15, 0));
        composePanel.add(composeTitle, BorderLayout.NORTH);

        JPanel inputContainer = new JPanel(new BorderLayout());
        inputContainer.setOpaque(false);
        inputContainer.setBorder(new EmptyBorder(0, 0, 15, 0));

        postInputArea = new JTextArea(5, 20);
        postInputArea.setLineWrap(true);
        postInputArea.setWrapStyleWord(true);
        postInputArea.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        JScrollPane scrollPane = new JScrollPane(postInputArea);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(10, new Color(200, 200, 200)),
                new EmptyBorder(10, 10, 10, 10)
        ));

        // Placeholder text
        postInputArea.setText("What's on your mind?");
        postInputArea.setForeground(new Color(150, 150, 150));

        // Focus listeners for placeholder removal
        postInputArea.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (postInputArea.getText().equals("What's on your mind?")) {
                    postInputArea.setText("");
                    postInputArea.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (postInputArea.getText().isEmpty()) {
                    postInputArea.setText("What's on your mind?");
                    postInputArea.setForeground(new Color(150, 150, 150));
                }
            }
        });

        inputContainer.add(scrollPane, BorderLayout.CENTER);

        JPanel counterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        counterPanel.setOpaque(false);

        final int MAX_CHARS = 500;
        JLabel charCounter = new JLabel("0/" + MAX_CHARS);
        charCounter.setFont(new Font("Segoe UI", Font.BOLD, 13));
        charCounter.setForeground(new Color(100, 100, 100));

        counterPanel.add(charCounter);

        // Update character counter
        postInputArea.getDocument().addDocumentListener(new DocumentListener() {
            public void update() {
                int length = postInputArea.getText().equals("What's on your mind?") ?
                        0 : postInputArea.getText().length();
                charCounter.setText(length + "/" + MAX_CHARS);

                // Change color based on length
                if (length > MAX_CHARS * 0.8) {
                    charCounter.setForeground(new Color(255, 150, 0)); // Orange
                } else if (length > MAX_CHARS * 0.95) {
                    charCounter.setForeground(new Color(200, 0, 0)); // Red
                } else {
                    charCounter.setForeground(new Color(100, 100, 100)); // Gray
                }
            }
            public void insertUpdate(DocumentEvent e) { update(); }
            public void removeUpdate(DocumentEvent e) { update(); }
            public void changedUpdate(DocumentEvent e) { update(); }
        });

        JButton publishButton = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // gradient paint
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(100, 165, 255),
                        0, getHeight(), new Color(79, 129, 189)
                );
                g2.setPaint(gp);

                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

                g2.setPaint(new Color(255, 255, 255, 50));
                g2.fillRoundRect(2, 2, getWidth()-4, getHeight()/2-2, 13, 13);

                super.paintComponent(g2);
                g2.dispose();
            }
        };

        publishButton.setLayout(new GridBagLayout());
        publishButton.setPreferredSize(new Dimension(140, 40));

        JPanel buttonContent = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        buttonContent.setOpaque(false);

        JLabel buttonIcon = new JLabel("\uD83D\uDCE8"); // Paper plane emoji
        buttonIcon.setForeground(Color.WHITE);

        JLabel buttonText = new JLabel("Publish");
        buttonText.setFont(new Font("Segoe UI", Font.BOLD, 15));
        buttonText.setForeground(Color.WHITE);

        buttonContent.add(buttonIcon);
        buttonContent.add(buttonText);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.CENTER;
        publishButton.add(buttonContent, gbc);

        publishButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        publishButton.setForeground(Color.WHITE);
        publishButton.setBorder(null);
        publishButton.setFocusPainted(false);
        publishButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        publishButton.setContentAreaFilled(false);

        publishButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                publishButton.setOpaque(false);
                publishButton.repaint();
            }
            public void mouseExited(MouseEvent e) {
                publishButton.setOpaque(false);
                publishButton.repaint();
            }
            public void mousePressed(MouseEvent e) {
                publishButton.repaint();
            }
            public void mouseReleased(MouseEvent e) {
                publishButton.repaint();
            }
        });

        publishButton.addActionListener(e -> publishPost());

        JPanel toolbarPanel = new JPanel(new BorderLayout(10, 0));
        toolbarPanel.setOpaque(false);
        toolbarPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        toolbarPanel.add(counterPanel, BorderLayout.CENTER);
        toolbarPanel.add(publishButton, BorderLayout.EAST);

        composePanel.add(inputContainer, BorderLayout.CENTER);
        composePanel.add(toolbarPanel, BorderLayout.SOUTH);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(245, 245, 245));
        contentPanel.add(headerPanel);
        contentPanel.add(statsPanel);
        contentPanel.add(composePanel);

        JScrollPane scrollPane2 = new JScrollPane(contentPanel);
        scrollPane2.setBorder(null);
        scrollPane2.getVerticalScrollBar().setUnitIncrement(16);

        panel.add(scrollPane2, BorderLayout.CENTER);
        return panel;
    }

    // Custom shadow border class
    static class ShadowBorder implements Border {
        private int size;
        private Color color;

        public ShadowBorder(int size, Color color) {
            this.size = size;
            this.color = color;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            for (int i = 0; i < size; i++) {
                g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(),
                        color.getAlpha() * (size - i) / (size * 2)));
                g2.drawRoundRect(x + i, y + i, width - i * 2 - 1, height - i * 2 - 1, 15, 15);
            }

            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(size, size, size, size);
        }

        @Override
        public boolean isBorderOpaque() {
            return false;
        }
    }
}
