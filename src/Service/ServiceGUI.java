package Service;

import common.PublisherInfo;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.util.List;

public class ServiceGUI extends JFrame implements ServiceListener {
    private final ServiceIMP service;
    private JPanel headerPanel;
    private JLabel headerLabel;
    private JPanel contentHolder;
    private JSplitPane splitPane;

    private DefaultTableModel notificationsModel = new DefaultTableModel();
    private DefaultTableModel logsModel = new DefaultTableModel();
    private JLabel publishersStat, studentsStat, notificationsStat, logsStat;
    private JPanel topicsPanel;
    private JPanel subsPanel;

    public ServiceGUI(ServiceIMP service) {
        this.service = service;
        service.addServiceListener(this);
        initComponents();
        customizeServiceUI();
        setVisible(true);
    }

    private void initComponents() {
        headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBorder(new EmptyBorder(6, 16, 6, 16));
        headerLabel = new JLabel("Banana Lagoona");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        headerPanel.add(headerLabel);
        getContentPane().add(headerPanel, BorderLayout.NORTH);

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
        JButton[] navBtns = new JButton[items.length];
        Color normalBg = new Color(245, 245, 245);
        Color hoverBg = new Color(220, 220, 220);
        Color selBg = new Color(200, 230, 255);

        for (int i = 0; i < items.length; i++) {
            String key = items[i];
            JButton btn = new JButton(key);
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            btn.setFocusPainted(false);
            btn.setContentAreaFilled(false);
            btn.setOpaque(true);
            btn.setBackground(i == 0 ? selBg : normalBg);
            btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setMaximumSize(new Dimension(180, 44));

            btn.addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) {
                    if (!btn.getBackground().equals(selBg)) btn.setBackground(hoverBg);
                }
                @Override public void mouseExited(MouseEvent e) {
                    if (!btn.getBackground().equals(selBg)) btn.setBackground(normalBg);
                }
            });

            int idx = i;
            btn.addActionListener(e -> {
                CardLayout cl = (CardLayout) contentHolder.getLayout();
                cl.show(contentHolder, key);
                for (int j = 0; j < navBtns.length; j++) {
                    navBtns[j].setBackground(items[j].equals(key) ? selBg : normalBg);
                }
            });

            navBtns[i] = btn;
            nav.add(btn);
            nav.add(Box.createRigidArea(new Dimension(0, 8)));
        }

        contentHolder = new JPanel(new CardLayout());
        contentHolder.add(homePanel(), "Home");
        contentHolder.add(notifPanel(), "Notifications");
        contentHolder.add(topicsPanel(), "Topics");
        contentHolder.add(subsPanel(), "Subscribers");
        contentHolder.add(logsPanel(), "Logs");

        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, nav, contentHolder);
        splitPane.setDividerLocation(220);
        splitPane.setBorder(null);
        getContentPane().add(splitPane, BorderLayout.CENTER);
    }

    // Home stats
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

    private JPanel notifPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(new EmptyBorder(20, 20, 20, 20));
        notificationsModel = new DefaultTableModel(new String[]{"Publisher", "Message", "Time"}, 0);
        JTable table = new JTable(notificationsModel);
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        return p;
    }

    private JScrollPane topicsPanel() {
        topicsPanel = new JPanel();
        topicsPanel.setLayout(new GridBagLayout());
        topicsPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        updateTopicsPanel();
        JScrollPane scroll = new JScrollPane(topicsPanel);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(new Color(245, 245, 250));
        return scroll;
    }

    // Subscribers
    private JScrollPane subsPanel() {
        subsPanel = new JPanel();
        subsPanel.setLayout(new BoxLayout(subsPanel, BoxLayout.Y_AXIS));
        subsPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        updateSubsPanel();
        return new JScrollPane(subsPanel);
    }

    // Logs
    private JPanel logsPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(new EmptyBorder(20, 20, 20, 20));
        logsModel = new DefaultTableModel(new String[]{"Timestamp", "Event"}, 0);
        JTable table = new JTable(logsModel);
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        return p;
    }

    private JLabel createStatLabel() {
        JLabel lbl = new JLabel("0", SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 36));
        return lbl;
    }

    private JPanel statCard(String title, JLabel value) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(240, 248, 255));
        card.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel t = new JLabel(title, SwingConstants.CENTER);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        card.add(t, BorderLayout.NORTH);
        card.add(value, BorderLayout.CENTER);
        return card;
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

            List<PublisherInfo> infoList = service.getPublishers().values().stream()
                    .map(r -> r.getInfo())
                    .toList();

            if (infoList.isEmpty()) {
                JLabel lbl = new JLabel("No topics available", SwingConstants.CENTER);
                lbl.setFont(new Font("Segoe UI", Font.ITALIC, 16));
                lbl.setForeground(new Color(120, 120, 140));

                GridBagConstraints gbc = new GridBagConstraints();
                gbc.gridx = 0;
                gbc.gridy = 0;
                gbc.weightx = 1.0;
                gbc.weighty = 1.0;
                gbc.fill = GridBagConstraints.NONE;
                gbc.anchor = GridBagConstraints.CENTER;

                topicsPanel.add(lbl, gbc);
            } else {
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.insets = new Insets(10, 10, 10, 10);
                gbc.anchor = GridBagConstraints.NORTHWEST;
                gbc.fill = GridBagConstraints.NONE;

                int row = 0;
                int col = 0;
                int maxCols = Math.max(1, (int)Math.floor((topicsPanel.getWidth() - 60) / 320));
                if (maxCols < 1) maxCols = 3;

                for (PublisherInfo info : infoList) {
                    JPanel card = createCard(info);

                    gbc.gridx = col;
                    gbc.gridy = row;

                    topicsPanel.add(card, gbc);

                    col++;
                    if (col >= maxCols) {
                        col = 0;
                        row++;
                    }
                }

                // Add a component to take extra horizontal space
                gbc.gridx = maxCols;
                gbc.weightx = 1.0;
                gbc.fill = GridBagConstraints.HORIZONTAL;
                topicsPanel.add(Box.createHorizontalGlue(), gbc);

                // Add a component to take extra vertical space
                gbc.gridx = 0;
                gbc.gridy = row + 1;
                gbc.weighty = 1.0;
                gbc.fill = GridBagConstraints.VERTICAL;
                topicsPanel.add(Box.createVerticalGlue(), gbc);
            }

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

    private JPanel createCard(PublisherInfo info) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(new Color(0, 0, 0, 15));
                g2.fillRoundRect(4, 4, getWidth() - 8, getHeight() - 8, 16, 16);

                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 8, getHeight() - 8, 16, 16);

                g2.setColor(new Color(230, 230, 235));
                g2.setStroke(new BasicStroke(1.0f));
                g2.drawRoundRect(0, 0, getWidth() - 8, getHeight() - 8, 16, 16);

                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setLayout(new BorderLayout(10, 10));
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(16, 16, 16, 16));

        int cardWidth = 300;
        int cardHeight = 220;
        card.setPreferredSize(new Dimension(cardWidth, cardHeight));
        card.setMaximumSize(new Dimension(cardWidth, cardHeight));

        JPanel iconPanel = new JPanel(new GridBagLayout());
        iconPanel.setOpaque(false);

        JLabel iconLbl = new JLabel();
        iconLbl.setHorizontalAlignment(SwingConstants.CENTER);
        iconLbl.setPreferredSize(new Dimension(80, 80));

        try {
            Icon icon = info.getIcon();
            if (icon == null) throw new Exception("No icon provided");

            int maxSize = 80;
            int width = icon.getIconWidth();
            int height = icon.getIconHeight();

            double scale = Math.min((double)maxSize / width, (double)maxSize / height);
            width = (int)(width * scale);
            height = (int)(height * scale);

            icon = resizeIcon(icon, width, height);
            iconLbl.setIcon(icon);
        } catch (Exception e) {

            char initial = !info.getName().isEmpty() ? info.getName().charAt(0) : '?';
            Icon letterIcon = createLetterIcon(initial, 70, getColorForName(info.getName()), Color.WHITE);
            iconLbl.setIcon(letterIcon);
        }

        iconPanel.add(iconLbl);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel nameLbl = new JLabel(info.getName());
        nameLbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        nameLbl.setForeground(new Color(50, 50, 70));
        nameLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(230, 230, 235));
        separator.setAlignmentX(Component.LEFT_ALIGNMENT);
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

        JTextArea desc = new JTextArea(info.getDescription());
        desc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        desc.setLineWrap(true);
        desc.setWrapStyleWord(true);
        desc.setEditable(false);
        desc.setOpaque(false);
        desc.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
        desc.setForeground(new Color(70, 70, 90));

        JScrollPane descScroll = new JScrollPane(desc);
        descScroll.setBorder(null);
        descScroll.setOpaque(false);
        descScroll.getViewport().setOpaque(false);
        descScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        descScroll.setPreferredSize(new Dimension(cardWidth - 40, 80));

        textPanel.add(nameLbl);
        textPanel.add(Box.createVerticalStrut(8));
        textPanel.add(separator);
        textPanel.add(Box.createVerticalStrut(8));
        textPanel.add(descScroll);

        card.add(iconPanel, BorderLayout.NORTH);
        card.add(textPanel, BorderLayout.CENTER);

        return card;
    }

    private Icon createLetterIcon(char letter, int size, Color bg, Color fg) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        g.setColor(new Color(0, 0, 0, 20));
        g.fillOval(2, 2, size-2, size-2);

        g.setColor(bg);
        g.fillOval(0, 0, size-2, size-2);

        Paint oldPaint = g.getPaint();
        GradientPaint gradient = new GradientPaint(
                0, 0, new Color(255, 255, 255, 60),
                0, size, new Color(255, 255, 255, 0)
        );
        g.setPaint(gradient);
        g.fillOval(0, 0, size-2, size-2);
        g.setPaint(oldPaint);

        // Letter
        g.setColor(fg);
        g.setFont(new Font("Segoe UI", Font.BOLD, size/2));
        FontMetrics fm = g.getFontMetrics();
        String s = String.valueOf(letter);
        int x = ((size-2) - fm.stringWidth(s)) / 2;
        int y = ((size-2) - fm.getHeight()) / 2 + fm.getAscent();
        g.drawString(s, x, y);

        g.dispose();
        return new ImageIcon(img);
    }

    private Icon resizeIcon(Icon icon, int width, int height) {
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = bi.createGraphics();

        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        double scaleX = (double) width / icon.getIconWidth();
        double scaleY = (double) height / icon.getIconHeight();
        AffineTransform at = AffineTransform.getScaleInstance(scaleX, scaleY);
        g.drawImage(((ImageIcon) icon).getImage(), at, null);

        g.dispose();
        return new ImageIcon(bi);
    }
    private Color getColorForName(String name) {
        if (name.isEmpty()) return new Color(120, 144, 156); // Default color

        int hash = 0;
        for (char c : name.toCharArray()) {
            hash = 31 * hash + c;
        }
        hash = Math.abs(hash);

        Color[] palette = {
                new Color(66, 165, 245),  // Blue
                new Color(156, 39, 176),  // Purple
                new Color(3, 169, 244),   // Light Blue
                new Color(233, 30, 99),   // Pink
                new Color(0, 188, 212),   // Cyan
                new Color(139, 195, 74),  // Light Green
                new Color(255, 193, 7),   // Amber
                new Color(103, 58, 183),  // Deep Purple
                new Color(0, 150, 136),   // Teal
                new Color(255, 152, 0),   // Orange
                new Color(76, 175, 80),   // Green
                new Color(244, 67, 54)    // Red
        };

        return palette[hash % palette.length];
    }

    private void setupResizeListener() {
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                // Refresh topics layout when window size changes
                if (topicsPanel != null && topicsPanel.isVisible()) {
                    updateTopicsPanel();
                }
            }
        });
    }


    // ServiceListener methods
    @Override public void onStudentUpdate() { updateSubsPanel(); updateStats(); }
    @Override public void onPublisherUpdate() { updateTopicsPanel(); updateStats(); }
    @Override public void onNotificationUpdate() { updateNotifications(); }
    @Override public void onLogUpdate() { updateLogs(); }
    class WrapLayout extends FlowLayout {
        private Dimension preferredLayoutSize;

        public WrapLayout() {
            super();
        }

        public WrapLayout(int align) {
            super(align);
        }

        public WrapLayout(int align, int hgap, int vgap) {
            super(align, hgap, vgap);
        }

        @Override
        public Dimension preferredLayoutSize(Container target) {
            return layoutSize(target, true);
        }

        @Override
        public Dimension minimumLayoutSize(Container target) {
            return layoutSize(target, false);
        }

        private Dimension layoutSize(Container target, boolean preferred) {
            synchronized (target.getTreeLock()) {
                int targetWidth = target.getWidth();

                if (targetWidth == 0)
                    targetWidth = Integer.MAX_VALUE;

                int hgap = getHgap();
                int vgap = getVgap();
                Insets insets = target.getInsets();
                int horizontalInsetsAndGap = insets.left + insets.right + (hgap * 2);
                int maxWidth = targetWidth - horizontalInsetsAndGap;

                Dimension dim = new Dimension(0, 0);
                int rowWidth = 0;
                int rowHeight = 0;

                int nmembers = target.getComponentCount();

                for (int i = 0; i < nmembers; i++) {
                    Component m = target.getComponent(i);

                    if (m.isVisible()) {
                        Dimension d = preferred ? m.getPreferredSize() : m.getMinimumSize();

                        if (rowWidth + d.width > maxWidth) {
                            addRow(dim, rowWidth, rowHeight);
                            rowWidth = 0;
                            rowHeight = 0;
                        }

                        if (rowWidth != 0) {
                            rowWidth += hgap;
                        }

                        rowWidth += d.width;
                        rowHeight = Math.max(rowHeight, d.height);
                    }
                }

                addRow(dim, rowWidth, rowHeight);

                dim.width += horizontalInsetsAndGap;
                dim.height += insets.top + insets.bottom + vgap * 2;

                return dim;
            }
        }

        private void addRow(Dimension dim, int rowWidth, int rowHeight) {
            dim.width = Math.max(dim.width, rowWidth);

            if (dim.height > 0) {
                dim.height += getVgap();
            }

            dim.height += rowHeight;
        }
    }
    public static void main(String[] args) throws Exception {
        ServiceIMP service = new ServiceIMP();
        try { LocateRegistry.createRegistry(1099); } catch(Exception ignored) {}
        java.rmi.Naming.rebind("PubSubService", service);
        SwingUtilities.invokeLater(() -> new ServiceGUI(service));
    }
}