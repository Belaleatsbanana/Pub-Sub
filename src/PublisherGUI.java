import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class PublisherGUI extends JFrame {

    private JPanel headerPanel;
    private JLabel headerLabel;
    private JTextField searchTextField;
    private JPanel contentHolder;

    public PublisherGUI() {
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
        final String[] current = {null};

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

            // hover
            b.addMouseListener(new MouseAdapter(){
                public void mouseEntered(MouseEvent e){
                    if (!key.equals(current[0])) b.setBackground(hoverBg);
                }
                public void mouseExited(MouseEvent e){
                    if (!key.equals(current[0])) b.setBackground(normalBg);
                }
            });

            // select
            b.addActionListener(e -> {
                CardLayout cl = (CardLayout) contentHolder.getLayout();
                cl.show(contentHolder, key);
                current[0] = key;
                for (JButton btn: navButtons) {
                    if (btn!=null) {
                        btn.setBackground(btn.getText().equals(key)? selectedBg : normalBg);
                    }
                }
            });

            navButtons[i] = b;
            btnPanel.add(b);
            btnPanel.add(Box.createRigidArea(new Dimension(0,8)));
        }
        nav.add(btnPanel);

        // Content area
        contentHolder = new JPanel(new CardLayout());
        contentHolder.add(panel("Dashboard View"), "Dashboard");
        contentHolder.add(panel("Content View"),   "Content");
        contentHolder.add(panel("Community View"), "Community");

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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PublisherGUI().setVisible(true));
    }
}
