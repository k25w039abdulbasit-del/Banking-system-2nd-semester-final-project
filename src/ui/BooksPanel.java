package ui;

import dao.BookDAO;
import models.Book;
import models.User;
import utils.UITheme;
import utils.Validator;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * BooksPanel - Full CRUD interface for managing library books.
 * Provides search/filter, add, edit, and delete functionality.
 *
 * Course  : SW121 – Object Oriented Programming
 * Project : Library Management System
 */
public class BooksPanel extends JPanel {

    private final User     currentUser;
    private final BookDAO  bookDAO = new BookDAO();

    // ── Table ─────────────────────────────────────────────────────────────────
    private JTable           table;
    private DefaultTableModel tableModel;

    // ── Search / Filter ───────────────────────────────────────────────────────
    private JTextField  txtSearch;
    private JComboBox<String> cmbCategory;

    // ── Column definitions ────────────────────────────────────────────────────
    private static final String[] COLUMNS = {
        "ID", "Title", "Author", "ISBN", "Category", "Publisher", "Year", "Total", "Available"
    };

    public BooksPanel(User user) {
        this.currentUser = user;
        setBackground(UITheme.BG_MAIN);
        setLayout(new BorderLayout(0, 0));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        buildUI();
        loadBooks();
    }

    // ── UI Layout ─────────────────────────────────────────────────────────────

    private void buildUI() {
        add(buildToolbar(), BorderLayout.NORTH);
        add(buildTablePanel(), BorderLayout.CENTER);
    }

    private JPanel buildToolbar() {
        JPanel bar = new JPanel(new BorderLayout(12, 0));
        bar.setOpaque(false);
        bar.setBorder(BorderFactory.createEmptyBorder(0, 0, 14, 0));

        // Search
        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        searchRow.setOpaque(false);

        txtSearch = UITheme.styledField(22);
        txtSearch.putClientProperty("placeholder", "Search by title, author, ISBN…");

        cmbCategory = new JComboBox<>(new String[]{
            "All Categories", "Technology", "Fiction", "Classic", "Science", "History", "Other"
        });
        cmbCategory.setFont(UITheme.FONT_BODY);
        cmbCategory.setPreferredSize(new Dimension(160, 36));

        JButton btnSearch = UITheme.primaryButton("🔍 Search");
        JButton btnReset  = UITheme.secondaryButton("Reset");

        searchRow.add(new JLabel("Search:"));
        searchRow.add(txtSearch);
        searchRow.add(new JLabel("Category:"));
        searchRow.add(cmbCategory);
        searchRow.add(btnSearch);
        searchRow.add(btnReset);

        // Action buttons (Admin/User both can add; only Admin can delete)
        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actionRow.setOpaque(false);

        JButton btnAdd    = UITheme.successButton("+ Add Book");
        JButton btnEdit   = UITheme.primaryButton("✏ Edit");
        JButton btnDelete = UITheme.dangerButton("🗑 Delete");
        JButton btnRefresh= UITheme.secondaryButton("↻ Refresh");

        if (!currentUser.isAdmin()) btnDelete.setEnabled(false);

        actionRow.add(btnRefresh);
        actionRow.add(btnEdit);
        actionRow.add(btnDelete);
        actionRow.add(btnAdd);

        bar.add(searchRow, BorderLayout.WEST);
        bar.add(actionRow, BorderLayout.EAST);

        // ── Events ──────────────────────────────────────────────────────────
        btnSearch.addActionListener(e -> handleSearch());
        btnReset .addActionListener(e -> { txtSearch.setText(""); cmbCategory.setSelectedIndex(0); loadBooks(); });
        btnAdd   .addActionListener(e -> openBookDialog(null));
        btnEdit  .addActionListener(e -> editSelected());
        btnDelete.addActionListener(e -> deleteSelected());
        btnRefresh.addActionListener(e -> loadBooks());

        txtSearch.addActionListener(e -> handleSearch());  // Enter to search

        return bar;
    }

    private JScrollPane buildTablePanel() {
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        UITheme.styleTable(table);
        table.getColumnModel().getColumn(0).setMaxWidth(50);   // ID
        table.getColumnModel().getColumn(7).setMaxWidth(60);   // Total
        table.getColumnModel().getColumn(8).setMaxWidth(80);   // Available

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1));
        scroll.getViewport().setBackground(Color.WHITE);
        return scroll;
    }

    // ── Data Loading ──────────────────────────────────────────────────────────

    private void loadBooks() {
        populateTable(bookDAO.getAllBooks());
    }

    private void handleSearch() {
        String keyword  = txtSearch.getText().trim();
        String category = (String) cmbCategory.getSelectedItem();

        List<Book> results;
        if (!keyword.isEmpty()) {
            results = bookDAO.searchBooks(keyword);
        } else if (!"All Categories".equals(category)) {
            results = bookDAO.getBooksByCategory(category);
        } else {
            results = bookDAO.getAllBooks();
        }
        populateTable(results);
    }

    private void populateTable(List<Book> books) {
        tableModel.setRowCount(0);
        for (Book b : books) {
            tableModel.addRow(new Object[]{
                b.getBookId(), b.getTitle(), b.getAuthor(), b.getIsbn(),
                b.getCategory(), b.getPublisher(), b.getYearPublished(),
                b.getTotalCopies(), b.getAvailableCopies()
            });
        }
    }

    // ── CRUD Actions ──────────────────────────────────────────────────────────

    private void editSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { Validator.showError(this, "Please select a book to edit."); return; }
        int bookId = (int) tableModel.getValueAt(row, 0);
        openBookDialog(bookDAO.getBookById(bookId));
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { Validator.showError(this, "Please select a book to delete."); return; }
        String title  = (String) tableModel.getValueAt(row, 1);
        int    bookId = (int)    tableModel.getValueAt(row, 0);
        if (Validator.confirm(this, "Delete book: \"" + title + "\"?")) {
            if (bookDAO.deleteBook(bookId)) {
                Validator.showSuccess(this, "Book deleted successfully.");
                loadBooks();
            } else {
                Validator.showError(this, "Failed to delete. Book may have active issues.");
            }
        }
    }

    // ── Add/Edit Dialog ───────────────────────────────────────────────────────

    private void openBookDialog(Book existing) {
        boolean isEdit = (existing != null);
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
            isEdit ? "Edit Book" : "Add New Book", true);
        dialog.setSize(480, 520);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(20, 24, 8, 24));

        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(6, 0, 6, 0);

        // Form fields
        JTextField txtTitle     = UITheme.styledField(20);
        JTextField txtAuthor    = UITheme.styledField(20);
        JTextField txtIsbn      = UITheme.styledField(20);
        JTextField txtPublisher = UITheme.styledField(20);
        JTextField txtYear      = UITheme.styledField(6);
        JTextField txtCopies    = UITheme.styledField(4);
        JComboBox<String> cmbCat = new JComboBox<>(new String[]{
            "Technology", "Fiction", "Classic", "Science", "History", "Other"
        });
        cmbCat.setFont(UITheme.FONT_BODY);

        if (isEdit) {
            txtTitle    .setText(existing.getTitle());
            txtAuthor   .setText(existing.getAuthor());
            txtIsbn     .setText(existing.getIsbn());
            txtPublisher.setText(existing.getPublisher());
            txtYear     .setText(String.valueOf(existing.getYearPublished()));
            txtCopies   .setText(String.valueOf(existing.getTotalCopies()));
            cmbCat.setSelectedItem(existing.getCategory());
        }

        // Add rows to form
        String[][] rows = {
            {"Title *",     null},
            {"Author *",    null},
            {"ISBN",        null},
            {"Category",    null},
            {"Publisher",   null},
            {"Year",        null},
            {"Total Copies",null}
        };
        JComponent[] fields = {txtTitle, txtAuthor, txtIsbn, cmbCat,
                                txtPublisher, txtYear, txtCopies};

        for (int i = 0; i < rows.length; i++) {
            gc.gridy = i * 2;     gc.gridx = 0;
            form.add(makeLabel(rows[i][0]), gc);
            gc.gridy = i * 2 + 1; gc.gridx = 0;
            form.add(fields[i], gc);
        }

        // Buttons
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 12));
        btns.setBackground(UITheme.BG_MAIN);
        btns.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.BORDER_COLOR));
        JButton btnSave   = UITheme.successButton(isEdit ? "💾 Update" : "➕ Add Book");
        JButton btnCancel = UITheme.secondaryButton("Cancel");
        btns.add(btnCancel);
        btns.add(btnSave);

        btnCancel.addActionListener(e -> dialog.dispose());
        btnSave.addActionListener(e -> {
            String title     = txtTitle.getText().trim();
            String author    = txtAuthor.getText().trim();
            String yearStr   = txtYear.getText().trim();
            String copiesStr = txtCopies.getText().trim();

            String err = Validator.validateBook(title, author, yearStr, copiesStr);
            if (err != null) { Validator.showError(dialog, err); return; }

            Book book = isEdit ? existing : new Book();
            book.setTitle    (title);
            book.setAuthor   (author);
            book.setIsbn     (txtIsbn.getText().trim());
            book.setCategory ((String) cmbCat.getSelectedItem());
            book.setPublisher(txtPublisher.getText().trim());
            book.setYearPublished(yearStr.isEmpty()   ? 0 : Integer.parseInt(yearStr));
            int copies = copiesStr.isEmpty() ? 1 : Integer.parseInt(copiesStr);
            book.setTotalCopies    (copies);
            book.setAvailableCopies(isEdit ? existing.getAvailableCopies() : copies);

            boolean ok = isEdit ? bookDAO.updateBook(book) : bookDAO.addBook(book);
            if (ok) {
                Validator.showSuccess(dialog, "Book " + (isEdit ? "updated" : "added") + " successfully!");
                dialog.dispose();
                loadBooks();
            } else {
                Validator.showError(dialog, "Operation failed. ISBN may already exist.");
            }
        });

        dialog.add(new JScrollPane(form), BorderLayout.CENTER);
        dialog.add(btns, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private JLabel makeLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(UITheme.FONT_LABEL);
        l.setForeground(UITheme.TEXT_DARK);
        return l;
    }
}
