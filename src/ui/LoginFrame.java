package ui;

import dao.UserDAO;
import models.User;
import utils.UITheme;
import utils.Validator;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;

/**
 * LoginFrame - The application entry point login screen.
 * Supports Admin and User roles. Provides credential validation.
 *
 * Course  : SW121 – Object Oriented Programming
 * Project : Library Management System
 */
public class LoginFrame extends JFrame {

    // ── Components ────────────────────────────────────────────────────────────
    private JTextField     txtUsername;
    private JPasswordField txtPassword;
    private JButton        btnLogin;
    private JLabel         lblError;

    private final UserDAO userDAO = new UserDAO();

    // ── Constructor ───────────────────────────────────────────────────────────

    public LoginFrame() {
        setTitle("Library Management System – Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setUndecorated(true);            // clean borderless window
        setSize(900, 560);
        setLocationRelativeTo(null);
        setResizable(false);

        buildUI();
        setVisible(true);
    }

    // ── UI Construction ───────────────────────────────────────────────────────

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BG_MAIN);
        root.setBorder(new LineBorder(UITheme.BORDER_COLOR, 1));

        root.add(buildLeftPanel(),  BorderLayout.WEST);
        root.add(buildRightPanel(), BorderLayout.CENTER);

        setContentPane(root);
    }

    /** Left blue branding panel */
    private JPanel buildLeftPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(
                    0, 0, UITheme.PRIMARY_DARK,
                    0, getHeight(), UITheme.PRIMARY);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setPreferredSize(new Dimension(360, 560));
        panel.setLayout(new GridBagLayout());

        JPanel inner = new JPanel();
        inner.setOpaque(false);
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));

        // Book icon
        JLabel icon = new JLabel("📚", SwingConstants.CENTER);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
        icon.setAlignmentX(CENTER_ALIGNMENT);

        JLabel title = new JLabel("Library MS", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Management System", SwingConstants.CENTER);
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        sub.setForeground(new Color(191, 219, 254));
        sub.setAlignmentX(CENTER_ALIGNMENT);

        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(200, 1));
        sep.setForeground(new Color(147, 197, 253));

        JLabel hint = new JLabel("<html><center>Admin: admin / admin123<br>"
                               + "User: librarian / user123</center></html>",
                                 SwingConstants.CENTER);
        hint.setFont(UITheme.FONT_SMALL);
        hint.setForeground(new Color(191, 219, 254));
        hint.setAlignmentX(CENTER_ALIGNMENT);

        inner.add(icon);
        inner.add(Box.createVerticalStrut(12));
        inner.add(title);
        inner.add(Box.createVerticalStrut(4));
        inner.add(sub);
        inner.add(Box.createVerticalStrut(24));
        inner.add(sep);
        inner.add(Box.createVerticalStrut(24));
        inner.add(hint);

        panel.add(inner);
        return panel;
    }

    /** Right white login form panel */
    private JPanel buildRightPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setMaximumSize(new Dimension(320, 400));

        JLabel welcome = new JLabel("Welcome Back");
        welcome.setFont(UITheme.FONT_TITLE);
        welcome.setForeground(UITheme.TEXT_DARK);
        welcome.setAlignmentX(LEFT_ALIGNMENT);

        JLabel subtitle = UITheme.mutedLabel("Sign in to your account");
        subtitle.setAlignmentX(LEFT_ALIGNMENT);

        form.add(welcome);
        form.add(Box.createVerticalStrut(4));
        form.add(subtitle);
        form.add(Box.createVerticalStrut(32));

        // Username field
        form.add(makeFieldLabel("Username"));
        form.add(Box.createVerticalStrut(6));
        txtUsername = UITheme.styledField(20);
        txtUsername.setAlignmentX(LEFT_ALIGNMENT);
        txtUsername.setMaximumSize(new Dimension(320, 36));
        form.add(txtUsername);
        form.add(Box.createVerticalStrut(16));

        // Password field
        form.add(makeFieldLabel("Password"));
        form.add(Box.createVerticalStrut(6));
        txtPassword = UITheme.styledPasswordField(20);
        txtPassword.setAlignmentX(LEFT_ALIGNMENT);
        txtPassword.setMaximumSize(new Dimension(320, 36));
        form.add(txtPassword);
        form.add(Box.createVerticalStrut(8));

        // Error label
        lblError = new JLabel(" ");
        lblError.setFont(UITheme.FONT_SMALL);
        lblError.setForeground(UITheme.DANGER);
        lblError.setAlignmentX(LEFT_ALIGNMENT);
        form.add(lblError);
        form.add(Box.createVerticalStrut(20));

        // Login button
        btnLogin = UITheme.primaryButton("Sign In →");
        btnLogin.setAlignmentX(LEFT_ALIGNMENT);
        btnLogin.setMaximumSize(new Dimension(320, 42));
        btnLogin.setPreferredSize(new Dimension(320, 42));
        form.add(btnLogin);

        // Close button
        form.add(Box.createVerticalStrut(12));
        JButton btnClose = UITheme.secondaryButton("Exit");
        btnClose.setAlignmentX(LEFT_ALIGNMENT);
        btnClose.setMaximumSize(new Dimension(320, 36));
        form.add(btnClose);

        panel.add(form);

        // ── Events ──────────────────────────────────────────────────────────
        btnLogin.addActionListener(e -> handleLogin());
        btnClose.addActionListener(e -> System.exit(0));

        // Allow Enter key to trigger login
        txtPassword.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) handleLogin();
            }
        });

        return panel;
    }

    private JLabel makeFieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(UITheme.FONT_LABEL);
        l.setForeground(UITheme.TEXT_DARK);
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }

    // ── Login Logic ───────────────────────────────────────────────────────────

    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        // UI Validation
        if (Validator.isEmpty(username)) {
            lblError.setText("Username is required.");
            Validator.markError(txtUsername);
            return;
        }
        if (Validator.isEmpty(password)) {
            lblError.setText("Password is required.");
            Validator.markError(txtPassword);
            return;
        }

        Validator.clearError(txtUsername);
        Validator.clearError(txtPassword);

        // Database authentication
        User user = userDAO.authenticate(username, password);
        if (user != null) {
            lblError.setText(" ");
            dispose();
            new MainFrame(user);   // open main dashboard
        } else {
            lblError.setText("Invalid username or password.");
            Validator.markError(txtUsername);
            Validator.markError(txtPassword);
            txtPassword.setText("");
        }
    }

    // ── Entry Point ───────────────────────────────────────────────────────────

    public static void main(String[] args) {
        // Set Nimbus Look & Feel for better UI rendering
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(LoginFrame::new);
    }
}
