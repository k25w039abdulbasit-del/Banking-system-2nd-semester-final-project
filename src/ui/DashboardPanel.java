package ui;

import dao.BookDAO;
import dao.IssueDAO;
import dao.MemberDAO;
import models.User;
import utils.UITheme;

import javax.swing.*;
import java.awt.*;

/**
 * DashboardPanel - Home screen with summary statistics cards.
 * Shows Total Books, Members, Issues today and Overdue count.
 *
 * Course  : SW121 – Object Oriented Programming
 * Project : Library Management System
 */
public class DashboardPanel extends JPanel {

    private final User       currentUser;
    private final BookDAO    bookDAO    = new BookDAO();
    private final MemberDAO  memberDAO  = new MemberDAO();
    private final IssueDAO   issueDAO   = new IssueDAO();

    public DashboardPanel(User user) {
        this.currentUser = user;
        setBackground(UITheme.BG_MAIN);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));
        buildUI();
    }

    private void buildUI() {
        // ── Header ─────────────────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 24, 0));

        JLabel welcome = UITheme.titleLabel("Welcome, " + currentUser.getFullName() + " 👋");
        JLabel date    = UITheme.mutedLabel(
            "Today is " + new java.util.Date().toString().substring(0, 10));

        header.add(welcome, BorderLayout.WEST);
        header.add(date,    BorderLayout.EAST);

        // ── Stats Row ──────────────────────────────────────────────────────
        JPanel statsRow = new JPanel(new GridLayout(1, 4, 16, 0));
        statsRow.setOpaque(false);

        int totalBooks   = bookDAO.getTotalBooks();
        int totalMembers = memberDAO.getTotalMembers();
        int issuedToday  = issueDAO.getTotalIssuedToday();
        int overdue      = issueDAO.getOverdueCount();

        statsRow.add(statCard("📚 Total Books",    String.valueOf(totalBooks),   UITheme.PRIMARY));
        statsRow.add(statCard("👥 Members",         String.valueOf(totalMembers), UITheme.ACCENT));
        statsRow.add(statCard("🔄 Issued Today",    String.valueOf(issuedToday),  UITheme.WARNING));
        statsRow.add(statCard("⚠️ Overdue",         String.valueOf(overdue),      UITheme.DANGER));

        // ── Quick Info ────────────────────────────────────────────────────
        JPanel infoRow = new JPanel(new GridLayout(1, 2, 16, 0));
        infoRow.setOpaque(false);
        infoRow.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        infoRow.add(buildQuickGuide());
        infoRow.add(buildSystemInfo());

        add(header,   BorderLayout.NORTH);
        add(statsRow, BorderLayout.CENTER);
        add(infoRow,  BorderLayout.SOUTH);
    }

    private JPanel statCard(String label, String value, Color accent) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // Colored left accent bar
        JPanel accentBar = new JPanel();
        accentBar.setBackground(accent);
        accentBar.setPreferredSize(new Dimension(5, 0));

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblValue.setForeground(accent);

        JLabel lblLabel = UITheme.mutedLabel(label);

        JPanel text = new JPanel(new BorderLayout(0, 6));
        text.setOpaque(false);
        text.add(lblValue, BorderLayout.CENTER);
        text.add(lblLabel, BorderLayout.SOUTH);

        card.add(accentBar, BorderLayout.WEST);
        card.add(text,      BorderLayout.CENTER);
        return card;
    }

    private JPanel buildQuickGuide() {
        JPanel card = UITheme.cardPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel title = UITheme.headingLabel("📋 Quick Guide");
        title.setAlignmentX(LEFT_ALIGNMENT);
        card.add(title);
        card.add(Box.createVerticalStrut(12));

        String[] tips = {
            "• Use Books panel to add, edit, delete, and search books.",
            "• Use Members panel to manage library members.",
            "• Use Issue/Return panel to lend and return books.",
            "• Use Reports panel to view and print issue history.",
            "• Admin can manage user accounts under User Management."
        };
        for (String tip : tips) {
            JLabel l = UITheme.mutedLabel(tip);
            l.setAlignmentX(LEFT_ALIGNMENT);
            card.add(l);
            card.add(Box.createVerticalStrut(4));
        }
        return card;
    }

    private JPanel buildSystemInfo() {
        JPanel card = UITheme.cardPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel title = UITheme.headingLabel("ℹ️ System Info");
        title.setAlignmentX(LEFT_ALIGNMENT);
        card.add(title);
        card.add(Box.createVerticalStrut(12));

        String[][] info = {
            {"Application",  "Library Management System"},
            {"Version",      "1.0.0"},
            {"Course",       "SW121 – OOP"},
            {"Database",     "MySQL (library_db)"},
            {"Logged in as", currentUser.getFullName()},
            {"Role",         currentUser.getRole()}
        };
        for (String[] row : info) {
            JPanel line = new JPanel(new BorderLayout());
            line.setOpaque(false);
            line.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
            JLabel key = UITheme.mutedLabel(row[0] + ":");
            JLabel val = new JLabel(row[1]);
            val.setFont(UITheme.FONT_BODY);
            line.add(key, BorderLayout.WEST);
            line.add(val, BorderLayout.EAST);
            card.add(line);
        }
        return card;
    }
}
