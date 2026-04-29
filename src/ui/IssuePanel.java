package ui;

import dao.BookDAO;
import dao.IssueDAO;
import dao.MemberDAO;
import models.Book;
import models.IssuedBook;
import models.Member;
import models.User;
import utils.UITheme;
import utils.Validator;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * IssuePanel - Handles book issuing and returning operations.
 * Shows all active issues and allows processing returns with fine calculation.
 *
 * Course  : SW121 – Object Oriented Programming
 * Project : Library Management System
 */
public class IssuePanel extends JPanel {

    private final User      currentUser;
    private final BookDAO   bookDAO   = new BookDAO();
    private final MemberDAO memberDAO = new MemberDAO();
    private final IssueDAO  issueDAO  = new IssueDAO();

    private JTable            table;
    private DefaultTableModel tableModel;
    private JTextField        txtSearch;
    private JComboBox<String> cmbFilter;

    private static final String[] COLUMNS = {
        "ID", "Book Title", "Member", "Issued By", "Issue Date", "Due Date", "Return Date", "Fine (Rs)", "Status"
    };

    private static final double FINE_PER_DAY = 5.0;  // Rs 5 per overdue day

    public IssuePanel(User user) {
        this.currentUser = user;
        setBackground(UITheme.BG_MAIN);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        buildUI();
        loadIssues();
    }

    private void buildUI() {
        add(buildToolbar(),   BorderLayout.NORTH);
        add(buildTableArea(), BorderLayout.CENTER);
    }

    private JPanel buildToolbar() {
        JPanel bar = new JPanel(new BorderLayout(12, 0));
        bar.setOpaque(false);
        bar.setBorder(BorderFactory.createEmptyBorder(0, 0, 14, 0));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        left.setOpaque(false);

        txtSearch = UITheme.styledField(18);
        cmbFilter = new JComboBox<>(new String[]{"All", "Issued", "Returned", "Overdue"});
        cmbFilter.setFont(UITheme.FONT_BODY);
        cmbFilter.setPreferredSize(new Dimension(130, 36));

        JButton btnSearch  = UITheme.primaryButton("🔍 Search");
        JButton btnReset   = UITheme.secondaryButton("Reset");

        left.add(new JLabel("Search:"));
        left.add(txtSearch);
        left.add(new JLabel("Status:"));
        left.add(cmbFilter);
        left.add(btnSearch);
        left.add(btnReset);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);

        JButton btnIssue   = UITheme.successButton("📤 Issue Book");
        JButton btnReturn  = UITheme.primaryButton("📥 Return Book");
        JButton btnRefresh = UITheme.secondaryButton("↻ Refresh");

        right.add(btnRefresh);
        right.add(btnReturn);
        right.add(btnIssue);

        bar.add(left,  BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);

        btnSearch.addActionListener (e -> handleSearch());
        btnReset .addActionListener (e -> { txtSearch.setText(""); cmbFilter.setSelectedIndex(0); loadIssues(); });
        btnIssue .addActionListener (e -> openIssueDialog());
        btnReturn.addActionListener (e -> processReturn());
        btnRefresh.addActionListener(e -> loadIssues());
        txtSearch.addActionListener (e -> handleSearch());

        return bar;
    }

    private JScrollPane buildTableArea() {
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        UITheme.styleTable(table);
        table.getColumnModel().getColumn(0).setMaxWidth(50);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR));
        scroll.getViewport().setBackground(Color.WHITE);
        return scroll;
    }

    private void loadIssues() {
        populateTable(issueDAO.getAllIssues());
    }

    private void handleSearch() {
        String kw     = txtSearch.getText().trim();
        List<IssuedBook> all = kw.isEmpty()
            ? issueDAO.getAllIssues()
            : issueDAO.searchIssues(kw);

        String status = (String) cmbFilter.getSelectedItem();
        if (!"All".equals(status)) {
            all = all.stream().filter(i -> status.equals(i.getStatus())).toList();
        }
        populateTable(all);
    }

    private void populateTable(List<IssuedBook> issues) {
        tableModel.setRowCount(0);
        for (IssuedBook i : issues) {
            tableModel.addRow(new Object[]{
                i.getIssueId(), i.getBookTitle(), i.getMemberName(),
                i.getIssuedByName(), i.getIssueDate(), i.getDueDate(),
                i.getReturnDate() != null ? i.getReturnDate() : "—",
                String.format("%.2f", i.getFineAmount()), i.getStatus()
            });
        }
    }

    // ── Issue Book Dialog ─────────────────────────────────────────────────────

    private void openIssueDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
            "Issue Book to Member", true);
        dialog.setSize(440, 380);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(20, 24, 8, 24));
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(6, 0, 6, 0);

        // Book selector
        List<Book> books = bookDAO.getAllBooks().stream()
            .filter(Book::isAvailable).toList();
        JComboBox<Book> cmbBook = new JComboBox<>(books.toArray(new Book[0]));
        cmbBook.setFont(UITheme.FONT_BODY);

        // Member selector
        List<Member> members = memberDAO.getAllMembers().stream()
            .filter(m -> "Active".equals(m.getStatus())).toList();
        JComboBox<Member> cmbMember = new JComboBox<>(members.toArray(new Member[0]));
        cmbMember.setFont(UITheme.FONT_BODY);

        // Due date (default 14 days from today)
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 14);
        JTextField txtDueDate = UITheme.styledField(12);
        txtDueDate.setText(sdf.format(cal.getTime()));

        String[] labels = {"Select Book *", "Select Member *", "Due Date *"};
        JComponent[] fields = {cmbBook, cmbMember, txtDueDate};

        for (int i = 0; i < labels.length; i++) {
            gc.gridy = i * 2;     form.add(makeLabel(labels[i]), gc);
            gc.gridy = i * 2 + 1; form.add(fields[i], gc);
        }

        // Info label
        gc.gridy = 6;
        JLabel info = UITheme.mutedLabel("Fine: Rs " + FINE_PER_DAY + " per overdue day");
        form.add(info, gc);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 12));
        btns.setBackground(UITheme.BG_MAIN);
        btns.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.BORDER_COLOR));
        JButton btnConfirm = UITheme.successButton("📤 Issue");
        JButton btnCancel  = UITheme.secondaryButton("Cancel");
        btns.add(btnCancel);
        btns.add(btnConfirm);

        btnCancel.addActionListener(e -> dialog.dispose());
        btnConfirm.addActionListener(e -> {
            if (cmbBook.getSelectedItem() == null) {
                Validator.showError(dialog, "No available books to issue."); return;
            }
            if (cmbMember.getSelectedItem() == null) {
                Validator.showError(dialog, "No active members found."); return;
            }
            String dueDate = txtDueDate.getText().trim();
            if (Validator.isEmpty(dueDate)) {
                Validator.showError(dialog, "Due date is required."); return;
            }

            Book   book   = (Book)   cmbBook.getSelectedItem();
            Member member = (Member) cmbMember.getSelectedItem();

            IssuedBook issue = new IssuedBook();
            issue.setBookId   (book.getBookId());
            issue.setMemberId (member.getMemberId());
            issue.setIssuedBy (currentUser.getUserId());
            issue.setIssueDate(sdf.format(new Date()));
            issue.setDueDate  (dueDate);

            if (issueDAO.issueBook(issue) && bookDAO.decrementAvailableCopies(book.getBookId())) {
                Validator.showSuccess(dialog,
                    "Book \"" + book.getTitle() + "\" issued to " + member.getName());
                dialog.dispose();
                loadIssues();
            } else {
                Validator.showError(dialog, "Failed to issue book. Please try again.");
            }
        });

        dialog.add(new JScrollPane(form), BorderLayout.CENTER);
        dialog.add(btns, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // ── Return Book ───────────────────────────────────────────────────────────

    private void processReturn() {
        int row = table.getSelectedRow();
        if (row < 0) {
            Validator.showError(this, "Please select an issued record to return."); return;
        }
        String status = (String) tableModel.getValueAt(row, 8);
        if ("Returned".equals(status)) {
            Validator.showError(this, "This book has already been returned."); return;
        }

        int    issueId  = (int)    tableModel.getValueAt(row, 0);
        String bookName = (String) tableModel.getValueAt(row, 1);
        String dueStr   = (String) tableModel.getValueAt(row, 5);

        // Calculate fine
        double fine = 0.0;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date due     = sdf.parse(dueStr);
            Date today   = new Date();
            long diffMs  = today.getTime() - due.getTime();
            long diffDays = diffMs / (1000 * 60 * 60 * 24);
            if (diffDays > 0) fine = diffDays * FINE_PER_DAY;
        } catch (Exception ignored) {}

        String msg = "Return book: \"" + bookName + "\"?";
        if (fine > 0) msg += "\nOverdue fine: Rs " + String.format("%.2f", fine);

        if (Validator.confirm(this, msg)) {
            // Get bookId from issues list
            List<IssuedBook> all = issueDAO.getAllIssues();
            int bookId = all.stream().filter(i -> i.getIssueId() == issueId)
                            .map(IssuedBook::getBookId).findFirst().orElse(-1);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            boolean ok = issueDAO.returnBook(issueId, sdf.format(new Date()), fine);
            if (ok && bookId != -1) {
                bookDAO.incrementAvailableCopies(bookId);
                Validator.showSuccess(this, "Book returned successfully."
                    + (fine > 0 ? " Fine collected: Rs " + fine : ""));
                loadIssues();
            } else {
                Validator.showError(this, "Return failed. Please try again.");
            }
        }
    }

    private JLabel makeLabel(String t) {
        JLabel l = new JLabel(t);
        l.setFont(UITheme.FONT_LABEL);
        l.setForeground(UITheme.TEXT_DARK);
        return l;
    }
}
