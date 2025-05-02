package Publisher;

import Service.ServiceIF;
import common.PublisherInfo;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.Date;

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

    public PublisherGUI(PublisherIMP publisherIMP,PublisherInfo publisherInfo,ServiceIF service) {
        initComponents();
        customizePublisherUI();
    }

    private void initComponents() {
        headerPanel     = new JPanel();
        headerLabel     = new JLabel("Banana Lagoona");
        searchTextField = new JTextField("search");


        headerLabel.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        headerLabel.setText("Banana Lagoona");

        searchTextField.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        searchTextField.setText("search");

        // Create logout as a “modern” nav‐style button:
        Color normalBg   = new Color(245, 245, 245);
        Color hoverBg    = new Color(220, 220, 220);
        JButton logoutButton = new JButton("Logout") {
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

        // common styling
        logoutButton.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        logoutButton.setFocusPainted(false);
        logoutButton.setContentAreaFilled(false);
        logoutButton.setOpaque(false);
        logoutButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        logoutButton.setBackground(normalBg);
        logoutButton.setForeground(Color.DARK_GRAY);
        logoutButton.setAlignmentY(Component.CENTER_ALIGNMENT);

        // hover effect
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

        // action
        logoutButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this, "Are you sure you want to logout?", "Confirm Logout", JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });

        // header layout: name |   [search   ] [Logout]
        headerPanel.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));  // Reduced padding
        GroupLayout layout = new GroupLayout(headerPanel);
        headerPanel.setLayout(layout);

        // Adjust font sizes for compact header
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));  // Reduced from 24
        searchTextField.setFont(new Font("Segoe UI", Font.PLAIN, 14));  // Reduced from 18

        // Compact logout button styling
        logoutButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));  // Reduced from 16
        logoutButton.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));  // Reduced padding

        // Horizontal layout
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

        // Vertical layout
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
        // Left nav
        JPanel nav = new JPanel();
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setBackground(Color.WHITE);
        nav.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        Icon avatar = createLetterIcon('P', 64, new Color(100,150,240), Color.WHITE);
        JLabel pic = new JLabel(avatar);
        pic.setAlignmentX(Component.CENTER_ALIGNMENT);
        nav.add(pic);
        nav.add(Box.createRigidArea(new Dimension(0,12)));

        JLabel name = new JLabel("Publisher Name");
        name.setFont(new Font("Segoe UI", Font.BOLD, 18));
        name.setAlignmentX(Component.CENTER_ALIGNMENT);
        nav.add(name);
        nav.add(Box.createRigidArea(new Dimension(0,24)));

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

            // Hover effects
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

            // Single action listener
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

        // Content area with real panels
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

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(245, 245, 245));

        // Header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(new Color(245, 245, 245));
        JLabel headerLabel = new JLabel("Dashboard");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerLabel.setForeground(new Color(60, 60, 60));
        headerPanel.add(headerLabel);

        // Stats Cards
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        statsPanel.setBorder(new EmptyBorder(15, 0, 25, 0));
        statsPanel.setBackground(new Color(245, 245, 245));

        String[] stats = {"Total Posts", "Subscribers", "Engagement"};
        String[] values = {"42", "1.2K", "86%"};
        Color[] colors = {new Color(100, 150, 240), new Color(240, 120, 100), new Color(100, 200, 150)};

        for(int i = 0; i < stats.length; i++) {
            JPanel card = new JPanel(new BorderLayout());
            card.setBackground(Color.WHITE);
            card.setBorder(BorderFactory.createCompoundBorder(
                    new RoundedBorder(15, new Color(220, 220, 220)),
                    new EmptyBorder(20, 20, 20, 20)
            ));

            JLabel statValue = new JLabel(values[i]);
            statValue.setFont(new Font("Segoe UI", Font.BOLD, 28));
            statValue.setForeground(colors[i]);

            JLabel statLabel = new JLabel(stats[i]);
            statLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            statLabel.setForeground(new Color(120, 120, 120));

            card.add(statValue, BorderLayout.CENTER);
            card.add(statLabel, BorderLayout.SOUTH);
            statsPanel.add(card);
        }

        // Post Composition Panel
        JPanel composePanel = new JPanel(new BorderLayout());
        composePanel.setBackground(Color.WHITE);
        composePanel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(15, new Color(220, 220, 220)),
                new EmptyBorder(20, 20, 20, 20)
        ));

        // Styled Text Area with Floating Label
        JPanel inputContainer = new JPanel(new BorderLayout());
        inputContainer.setBorder(new EmptyBorder(0, 0, 15, 0));

        JTextArea postInput = new JTextArea();
        postInput.setLineWrap(true);
        postInput.setWrapStyleWord(true);
        postInput.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        postInput.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 2, 0, new Color(200, 200, 200)),
                new EmptyBorder(15, 10, 10, 10)
        ));

        JLabel inputLabel = new JLabel("What's on your mind?");
        inputLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        inputLabel.setForeground(new Color(150, 150, 150));
        inputLabel.setBorder(new EmptyBorder(0, 10, 0, 0));

        inputContainer.add(inputLabel, BorderLayout.NORTH);
        inputContainer.add(new JScrollPane(postInput), BorderLayout.CENTER);

        // Character Counter
        JLabel charCounter = new JLabel("0/500");
        charCounter.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        charCounter.setForeground(new Color(150, 150, 150));
        charCounter.setHorizontalAlignment(SwingConstants.RIGHT);

        postInput.getDocument().addDocumentListener(new DocumentListener() {
            public void update() {
                charCounter.setText(postInput.getText().length() + "/500");
            }
            public void insertUpdate(DocumentEvent e) { update(); }
            public void removeUpdate(DocumentEvent e) { update(); }
            public void changedUpdate(DocumentEvent e) { update(); }
        });

        // Publish Button with Icon
        JButton publishButton = new JButton("Publish");
        publishButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        publishButton.setForeground(Color.WHITE);
        publishButton.setBackground(new Color(100, 150, 240));
        publishButton.setBorder(new RoundedBorder(8, new Color(80, 130, 220)));
        publishButton.setFocusPainted(false);
        publishButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Add icon using Unicode character
        publishButton.setText(" Publish"); // Use appropriate icon font

        // Button hover effects
        publishButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                publishButton.setBackground(new Color(80, 130, 220));
            }
            public void mouseExited(MouseEvent e) {
                publishButton.setBackground(new Color(100, 150, 240));
            }
        });

        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.add(charCounter, BorderLayout.CENTER);
        buttonPanel.add(publishButton, BorderLayout.EAST);
        buttonPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        composePanel.add(inputContainer, BorderLayout.CENTER);
        composePanel.add(buttonPanel, BorderLayout.SOUTH);

        // Recent Posts Preview
        JPanel recentPostsPanel = new JPanel();
        recentPostsPanel.setLayout(new BoxLayout(recentPostsPanel, BoxLayout.Y_AXIS));
        recentPostsPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        recentPostsPanel.setBackground(new Color(245, 245, 245));

        JLabel recentLabel = new JLabel("Recent Posts");
        recentLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        recentLabel.setForeground(new Color(60, 60, 60));
        recentLabel.setBorder(new EmptyBorder(0, 0, 15, 0));


        // Assemble main panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.add(headerPanel);
        contentPanel.add(statsPanel);
        contentPanel.add(composePanel);
        contentPanel.add(recentPostsPanel);

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createPostCard(String content) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(12, new Color(220, 220, 220)),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JTextArea postContent = new JTextArea(content);
        postContent.setLineWrap(true);
        postContent.setWrapStyleWord(true);
        postContent.setEditable(false);
        postContent.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        postContent.setBackground(Color.WHITE);

        JLabel postDate = new JLabel("2 hours ago");
        postDate.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        postDate.setForeground(new Color(150, 150, 150));

        card.add(postContent, BorderLayout.CENTER);
        card.add(postDate, BorderLayout.SOUTH);

        return card;
    }

    // Custom rounded border class
    class RoundedBorder implements Border {
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
            String timestamp = new Date().toString();
            postsModel.addElement(postText + " - " + timestamp);
            postInputArea.setText("");
        }
    }

    private JPanel createContentPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Add sample posts
        postsModel.addElement("Welcome to our platform! - " + new Date().toString());
        postsModel.addElement("First post! Excited to connect! - " + new Date().toString());

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

        // Hardcoded subscribers
        String[] subscribers = {
                "Alice Johnson", "Bob Smith", "Charlie Brown",
                "Diana Miller", "Eve Wilson", "Frank Davis"
        };
        for (String sub : subscribers) {
            subscribersModel.addElement(sub);
        }

        JList<String> subscribersList = new JList<>(subscribersModel);
        subscribersList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected,
                                                          boolean cellHasFocus) {
                JPanel panel = new JPanel(new BorderLayout(10, 10));
                panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

                JLabel icon = new JLabel(createLetterIcon(
                        value.toString().charAt(0),
                        32,
                        new Color(100, 150, 240),
                        Color.WHITE
                ));

                JLabel name = new JLabel(value.toString());
                name.setFont(new Font("Segoe UI", Font.PLAIN, 14));

                panel.add(icon, BorderLayout.WEST);
                panel.add(name, BorderLayout.CENTER);
                return panel;
            }
        });

        panel.add(new JScrollPane(subscribersList), BorderLayout.CENTER);
        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PublisherGUI gui = new PublisherGUI(null, null, null);
            gui.setVisible(true);
        });
    }
}
