package ui;

import dao.BookDAO;
import dao.IssueDAO;
import dao.MemberDAO;
import models.IssuedBook;
import models.User;
import utils.UITheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.print.*;
import java.text.MessageFormat;
import java.util.List;

/**
 * ReportsPanel - View issued books history and print reports.
 * Satisfies Requirement 5: Reports Generation with print functionality.
 *
 * Course  : SW121 – Object Oriented Programming
 * Project : Library Management System
 */
public class ReportsPanel extends JPanel {

    private final User     currentUser;
    private final IssueDAO issueDAO  = new IssueDAO();
    private final BookDAO  bookDAO   = new BookDAO();
    private final MemberDAO memberDAO= new MemberDAO();

    private JTable            table;
    private DefaultTableModel tableModel;

    private static final String[] COLUMNS = {
        "Issue ID", "Book Title", "Member", "Issue Date", "Due Date",
        "Return Date", "Fine (Rs)", "Status"
    };

    public ReportsPanel(User user) {
        this.currentUser = user;
        setBackground(UITheme.BG_MAIN);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        buildUI();
        loadReport();
    }

    private void buildUI() {
        // ── Header ─────────────────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 14, 0));

        JLabel title = UITheme.titleLabel("📊 Issue History Report");
        header.add(title, BorderLayout.WEST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);

        JComboBox<String> cmbFilter = new JComboBox<>(
            new String[]{"All Records", "Issued Only", "Returned Only", "Overdue Only"});
        cmbFilter.setFont(UITheme.FONT_BODY);
        cmbFilter.setPreferredSize(new Dimension(160, 36));

        JButton btnLoad  = UITheme.primaryButton("Load Report");
        JButton btnPrint = UITheme.successButton("🖨 Print");

        actions.add(new JLabel("Filter:"));
        actions.add(cmbFilter);
        actions.add(btnLoad);
        actions.add(btnPrint);
        header.add(actions, BorderLayout.EAST);

        // ── Stats Row ──────────────────────────────────────────────────────
        JPanel statsRow = new JPanel(new GridLayout(1, 4, 12, 0));
        statsRow.setOpaque(false);
        statsRow.setBorder(BorderFactory.createEmptyBorder(0, 0, 14, 0));

        statsRow.add(miniStat("Total Books",   String.valueOf(bookDAO.getTotalBooks()),   UITheme.PRIMARY));
        statsRow.add(miniStat("Total Members", String.valueOf(memberDAO.getTotalMembers()),UITheme.ACCENT));
        statsRow.add(miniStat("Issued Today",  String.valueOf(issueDAO.getTotalIssuedToday()), UITheme.WARNING));
        statsRow.add(miniStat("Overdue",       String.valueOf(issueDAO.getOverdueCount()), UITheme.DANGER));

        // ── Table ──────────────────────────────────────────────────────────
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        UITheme.styleTable(table);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR));
        scroll.getViewport().setBackground(Color.WHITE);

        // ── Assemble ───────────────────────────────────────────────────────
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(header,   BorderLayout.NORTH);
        top.add(statsRow, BorderLayout.CENTER);

        add(top,    BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);

        // ── Events ────────────────────────────────────────────────────────
        btnLoad.addActionListener(e -> {
            String selected = (String) cmbFilter.getSelectedItem();
            List<IssuedBook> data = issueDAO.getAllIssues();
            if ("Issued Only".equals(selected))
                data = data.stream().filter(i -> "Issued".equals(i.getStatus())).toList();
            else if ("Returned Only".equals(selected))
                data = data.stream().filter(i -> "Returned".equals(i.getStatus())).toList();
            else if ("Overdue Only".equals(selected))
                data = data.stream().filter(i -> "Overdue".equals(i.getStatus())).toList();
            populateTable(data);
        });

        btnPrint.addActionListener(e -> printTable());
    }

    private void loadReport() {
        populateTable(issueDAO.getAllIssues());
    }

    private void populateTable(List<IssuedBook> list) {
        tableModel.setRowCount(0);
        for (IssuedBook i : list) {
            tableModel.addRow(new Object[]{
                i.getIssueId(), i.getBookTitle(), i.getMemberName(),
                i.getIssueDate(), i.getDueDate(),
                i.getReturnDate() != null ? i.getReturnDate() : "—",
                String.format("%.2f", i.getFineAmount()),
                i.getStatus()
            });
        }
    }

    // ── Print Table ───────────────────────────────────────────────────────────

    private void printTable() {
        try {
            MessageFormat header = new MessageFormat("Library Management System – Issue Report");
            MessageFormat footer = new MessageFormat("Page {0}");
            table.print(JTable.PrintMode.FIT_WIDTH, header, footer);
        } catch (PrinterException e) {
            JOptionPane.showMessageDialog(this,
                "Printing failed: " + e.getMessage(),
                "Print Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── Mini Stat Card ────────────────────────────────────────────────────────

    private JPanel miniStat(String label, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout(0, 4));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER_COLOR),
            BorderFactory.createEmptyBorder(12, 14, 12, 14)
        ));
        JLabel lVal = new JLabel(value);
        lVal.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lVal.setForeground(color);
        JLabel lLbl = UITheme.mutedLabel(label);
        card.add(lVal, BorderLayout.CENTER);
        card.add(lLbl, BorderLayout.SOUTH);
        return card;
    }
}
