package ui;

import dao.BookDAO;
import dao.IssueDAO;
import dao.MemberDAO;
import models.User;
import utils.UITheme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * MainFrame - Primary application window with sidebar navigation.
 * Switches between Dashboard, Books, Members, Issue/Return, and Reports panels.
 * Admin users see additional User Management panel.
 *
 * Course  : SW121 – Object Oriented Programming
 * Project : Library Management System
 */
public class MainFrame extends JFrame {

    private final User    currentUser;
    private JPanel        contentArea;
    private JLabel        lblPageTitle;

    // Sidebar button references for active-state styling
    private JButton       activeBtn = null;

    public MainFrame(User user) {
        this.currentUser = user;
        setTitle("Library Management System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 720);
        setMinimumSize(new Dimension(1000, 600));
        setLocationRelativeTo(null);

        buildUI();
        // Default to dashboard
        showPanel(new DashboardPanel(currentUser), "Dashboard", null);
        setVisible(true);
    }

    // ── UI Construction ───────────────────────────────────────────────────────

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.add(buildTopBar(),   BorderLayout.NORTH);
        root.add(buildSidebar(),  BorderLayout.WEST);
        root.add(buildContent(),  BorderLayout.CENTER);
        setContentPane(root);
    }

    /** Top navigation bar */
    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(Color.WHITE);
        bar.setPreferredSize(new Dimension(0, 56));
        bar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER_COLOR));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 14));
        left.setOpaque(false);
        lblPageTitle = UITheme.headingLabel("Dashboard");
        left.add(new JLabel("📚 "));
        left.add(lblPageTitle);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 12));
        right.setOpaque(false);
        JLabel role = new JLabel(currentUser.getRole().toUpperCase());
        role.setFont(UITheme.FONT_SMALL);
        role.setForeground(UITheme.PRIMARY);
        role.setOpaque(true);
        role.setBackground(UITheme.PRIMARY_LIGHT);
        role.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));

        JLabel userName = UITheme.headingLabel(currentUser.getFullName());
        userName.setFont(UITheme.FONT_BODY);

        JButton logout = UITheme.dangerButton("Logout");
        logout.setPreferredSize(new Dimension(90, 30));
        logout.addActionListener(e -> {
            dispose();
            new LoginFrame();
        });

        right.add(role);
        right.add(userName);
        right.add(logout);

        bar.add(left,  BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    /** Left sidebar with navigation buttons */
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(UITheme.BG_SIDEBAR);
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // App brand in sidebar
        JLabel brand = new JLabel("  Library MS", SwingConstants.LEFT);
        brand.setFont(new Font("Segoe UI", Font.BOLD, 16));
        brand.setForeground(Color.WHITE);
        brand.setAlignmentX(LEFT_ALIGNMENT);
        brand.setBorder(BorderFactory.createEmptyBorder(0, 16, 20, 0));
        sidebar.add(brand);

        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(220, 1));
        sep.setForeground(new Color(51, 65, 85));
        sidebar.add(sep);
        sidebar.add(Box.createVerticalStrut(12));

        // Navigation items
        String[][] navItems = {
            {"🏠", "Dashboard"},
            {"📖", "Books"},
            {"👤", "Members"},
            {"🔄", "Issue / Return"},
            {"📊", "Reports"}
        };

        for (String[] item : navItems) {
            JButton btn = createNavButton(item[0] + "  " + item[1]);
            btn.addActionListener(e -> {
                setActiveButton(btn);
                lblPageTitle.setText(item[1]);
                switch (item[1]) {
                    case "Dashboard"    -> showPanel(new DashboardPanel(currentUser), item[1], btn);
                    case "Books"        -> showPanel(new BooksPanel(currentUser),     item[1], btn);
                    case "Members"      -> showPanel(new MembersPanel(currentUser),   item[1], btn);
                    case "Issue / Return"->showPanel(new IssuePanel(currentUser),     item[1], btn);
                    case "Reports"      -> showPanel(new ReportsPanel(currentUser),   item[1], btn);
                }
            });
            sidebar.add(btn);
        }

        // Admin-only section
        if (currentUser.isAdmin()) {
            sidebar.add(Box.createVerticalStrut(16));
            JLabel adminLabel = new JLabel("  ADMIN");
            adminLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
            adminLabel.setForeground(new Color(100, 116, 139));
            adminLabel.setAlignmentX(LEFT_ALIGNMENT);
            adminLabel.setBorder(BorderFactory.createEmptyBorder(0, 16, 8, 0));
            sidebar.add(adminLabel);

            JButton userMgmt = createNavButton("⚙️  User Management");
            userMgmt.addActionListener(e -> {
                setActiveButton(userMgmt);
                showPanel(new UserManagementPanel(currentUser), "User Management", userMgmt);
            });
            sidebar.add(userMgmt);
        }

        sidebar.add(Box.createVerticalGlue());
        return sidebar;
    }

    /** Scrollable main content area */
    private JPanel buildContent() {
        contentArea = new JPanel(new BorderLayout());
        contentArea.setBackground(UITheme.BG_MAIN);
        return contentArea;
    }

    // ── Panel Switching ───────────────────────────────────────────────────────

    private void showPanel(JPanel panel, String title, JButton btn) {
        contentArea.removeAll();
        contentArea.add(panel, BorderLayout.CENTER);
        contentArea.revalidate();
        contentArea.repaint();
        lblPageTitle.setText(title);
        if (btn != null) setActiveButton(btn);
    }

    // ── Sidebar Nav Button ────────────────────────────────────────────────────

    private JButton createNavButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                if (this == activeBtn) {
                    g2.setColor(UITheme.PRIMARY);
                    g2.fillRoundRect(8, 2, getWidth() - 16, getHeight() - 4, 8, 8);
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(30, 41, 59));
                    g2.fillRect(0, 0, getWidth(), getHeight());
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(UITheme.FONT_SIDEBAR);
        btn.setForeground(new Color(203, 213, 225));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(220, 42));
        btn.setPreferredSize(new Dimension(220, 42));
        btn.setAlignmentX(LEFT_ALIGNMENT);
        btn.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 0));

        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.repaint(); }
            @Override public void mouseExited (MouseEvent e) { btn.repaint(); }
        });

        return btn;
    }

    private void setActiveButton(JButton btn) {
        activeBtn = btn;
        repaint();
    }
}
