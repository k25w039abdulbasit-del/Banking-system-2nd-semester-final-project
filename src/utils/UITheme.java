package utils;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;

/**
 * UITheme - Centralized color, font and styling constants.
 * Apply consistently across all Swing components for a professional UI.
 *
 * Course  : SW121 – Object Oriented Programming
 * Project : Library Management System
 */
public class UITheme {

    // ── Color Palette ─────────────────────────────────────────────────────────
    public static final Color PRIMARY        = new Color(26,  86, 219);   // Blue
    public static final Color PRIMARY_DARK   = new Color(17,  56, 160);
    public static final Color PRIMARY_LIGHT  = new Color(219, 234, 254);
    public static final Color ACCENT         = new Color(5,  150, 105);   // Green
    public static final Color DANGER         = new Color(220,  38,  38);  // Red
    public static final Color WARNING        = new Color(217, 119,   6);  // Amber
    public static final Color BG_MAIN        = new Color(248, 250, 252);  // Near-white
    public static final Color BG_SIDEBAR     = new Color(15,  23,  42);   // Dark navy
    public static final Color BG_CARD        = Color.WHITE;
    public static final Color TEXT_DARK      = new Color(15,  23,  42);
    public static final Color TEXT_MUTED     = new Color(100, 116, 139);
    public static final Color TEXT_WHITE     = Color.WHITE;
    public static final Color BORDER_COLOR   = new Color(226, 232, 240);
    public static final Color TABLE_ROW_ALT  = new Color(241, 245, 249);
    public static final Color TABLE_SELECT   = new Color(219, 234, 254);

    // ── Fonts ──────────────────────────────────────────────────────────────────
    public static final Font FONT_TITLE   = new Font("Segoe UI", Font.BOLD,  22);
    public static final Font FONT_HEADING = new Font("Segoe UI", Font.BOLD,  15);
    public static final Font FONT_BODY    = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL   = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_LABEL   = new Font("Segoe UI", Font.BOLD,  12);
    public static final Font FONT_BTN     = new Font("Segoe UI", Font.BOLD,  13);
    public static final Font FONT_SIDEBAR = new Font("Segoe UI", Font.BOLD,  13);

    // ── Buttons ───────────────────────────────────────────────────────────────

    /** Creates a styled primary (blue) button. */
    public static JButton primaryButton(String text) {
        return makeButton(text, PRIMARY, TEXT_WHITE);
    }

    /** Creates a styled success (green) button. */
    public static JButton successButton(String text) {
        return makeButton(text, ACCENT, TEXT_WHITE);
    }

    /** Creates a styled danger (red) button. */
    public static JButton dangerButton(String text) {
        return makeButton(text, DANGER, TEXT_WHITE);
    }

    /** Creates a styled secondary (muted) button. */
    public static JButton secondaryButton(String text) {
        return makeButton(text, new Color(241, 245, 249), TEXT_DARK);
    }

    private static JButton makeButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? bg.darker()
                          : getModel().isRollover() ? bg.brighter() : bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(FONT_BTN);
        btn.setForeground(fg);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(130, 36));
        return btn;
    }

    // ── Text Fields ───────────────────────────────────────────────────────────

    /** Creates a styled JTextField. */
    public static JTextField styledField(int columns) {
        JTextField field = new JTextField(columns);
        styleField(field);
        return field;
    }

    /** Creates a styled JPasswordField. */
    public static JPasswordField styledPasswordField(int columns) {
        JPasswordField field = new JPasswordField(columns);
        styleField(field);
        return field;
    }

    private static void styleField(JComponent c) {
        c.setFont(FONT_BODY);
        c.setBackground(Color.WHITE);
        c.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        c.setPreferredSize(new Dimension(c.getPreferredSize().width, 36));
    }

    // ── Table ─────────────────────────────────────────────────────────────────

    /** Applies the standard library theme to any JTable. */
    public static void styleTable(JTable table) {
        table.setFont(FONT_BODY);
        table.setRowHeight(32);
        table.setGridColor(BORDER_COLOR);
        table.setSelectionBackground(TABLE_SELECT);
        table.setSelectionForeground(TEXT_DARK);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));

        // Alternating row colors
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                if (!sel) {
                    setBackground(row % 2 == 0 ? BG_CARD : TABLE_ROW_ALT);
                    setForeground(TEXT_DARK);
                }
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return this;
            }
        });

        // Header styling
        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_LABEL);
        header.setBackground(PRIMARY);
        header.setForeground(TEXT_WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 38));
        ((DefaultTableCellRenderer) header.getDefaultRenderer())
            .setHorizontalAlignment(SwingConstants.LEFT);
    }

    // ── Card Panel ────────────────────────────────────────────────────────────

    /** Returns a white rounded card panel. */
    public static JPanel cardPanel() {
        JPanel p = new JPanel();
        p.setBackground(BG_CARD);
        p.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));
        return p;
    }

    // ── Label helpers ─────────────────────────────────────────────────────────

    public static JLabel titleLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_TITLE);
        l.setForeground(TEXT_DARK);
        return l;
    }

    public static JLabel headingLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_HEADING);
        l.setForeground(TEXT_DARK);
        return l;
    }

    public static JLabel mutedLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_SMALL);
        l.setForeground(TEXT_MUTED);
        return l;
    }
}
