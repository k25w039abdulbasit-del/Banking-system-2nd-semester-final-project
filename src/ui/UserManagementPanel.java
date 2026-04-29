package ui;

import dao.UserDAO;
import models.User;
import utils.UITheme;
import utils.Validator;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * UserManagementPanel - Admin-only panel for managing system users.
 * Allows adding and deleting Admin/User accounts.
 *
 * Course  : SW121 – Object Oriented Programming
 * Project : Library Management System
 */
public class UserManagementPanel extends JPanel {

    private final User    currentUser;
    private final UserDAO userDAO = new UserDAO();

    private JTable            table;
    private DefaultTableModel tableModel;

    private static final String[] COLUMNS = {"ID", "Username", "Full Name", "Email", "Role"};

    public UserManagementPanel(User user) {
        this.currentUser = user;
        setBackground(UITheme.BG_MAIN);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        buildUI();
        loadUsers();
    }

    private void buildUI() {
        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setOpaque(false);
        toolbar.setBorder(BorderFactory.createEmptyBorder(0, 0, 14, 0));

        JLabel title = UITheme.titleLabel("⚙️ User Management");
        JLabel sub   = UITheme.mutedLabel("Admin access only – manage system login accounts");

        JPanel titlePanel = new JPanel(new BorderLayout(0, 4));
        titlePanel.setOpaque(false);
        titlePanel.add(title, BorderLayout.NORTH);
        titlePanel.add(sub,   BorderLayout.SOUTH);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        JButton btnAdd     = UITheme.successButton("+ Add User");
        JButton btnDelete  = UITheme.dangerButton("🗑 Delete");
        JButton btnRefresh = UITheme.secondaryButton("↻ Refresh");
        actions.add(btnRefresh);
        actions.add(btnDelete);
        actions.add(btnAdd);

        toolbar.add(titlePanel, BorderLayout.WEST);
        toolbar.add(actions,    BorderLayout.EAST);

        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        UITheme.styleTable(table);
        table.getColumnModel().getColumn(0).setMaxWidth(50);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR));
        scroll.getViewport().setBackground(Color.WHITE);

        add(toolbar, BorderLayout.NORTH);
        add(scroll,  BorderLayout.CENTER);

        btnAdd    .addActionListener(e -> openAddUserDialog());
        btnDelete .addActionListener(e -> deleteSelected());
        btnRefresh.addActionListener(e -> loadUsers());
    }

    private void loadUsers() {
        tableModel.setRowCount(0);
        List<User> users = userDAO.getAllUsers();
        for (User u : users) {
            tableModel.addRow(new Object[]{
                u.getUserId(), u.getUsername(), u.getFullName(), u.getEmail(), u.getRole()
            });
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { Validator.showError(this, "Select a user to delete."); return; }
        int    id       = (int)    tableModel.getValueAt(row, 0);
        String username = (String) tableModel.getValueAt(row, 1);
        if (id == currentUser.getUserId()) {
            Validator.showError(this, "You cannot delete your own account.");
            return;
        }
        if (Validator.confirm(this, "Delete user: \"" + username + "\"?")) {
            if (userDAO.deleteUser(id)) {
                Validator.showSuccess(this, "User deleted successfully.");
                loadUsers();
            } else {
                Validator.showError(this, "Failed to delete user.");
            }
        }
    }

    private void openAddUserDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
            "Add New User", true);
        dialog.setSize(420, 420);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(20, 24, 8, 24));
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill   = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(6, 0, 6, 0);

        JTextField     txtUsername = UITheme.styledField(20);
        JPasswordField txtPassword = UITheme.styledPasswordField(20);
        JTextField     txtFullName = UITheme.styledField(20);
        JTextField     txtEmail    = UITheme.styledField(20);
        JComboBox<String> cmbRole  = new JComboBox<>(new String[]{"User", "Admin"});
        cmbRole.setFont(UITheme.FONT_BODY);

        String[]     labels = {"Username *", "Password *", "Full Name *", "Email", "Role *"};
        JComponent[] flds   = {txtUsername, txtPassword, txtFullName, txtEmail, cmbRole};

        for (int i = 0; i < labels.length; i++) {
            gc.gridy = i * 2;     form.add(makeLabel(labels[i]), gc);
            gc.gridy = i * 2 + 1; form.add(flds[i], gc);
        }

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 12));
        btns.setBackground(UITheme.BG_MAIN);
        btns.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.BORDER_COLOR));
        JButton btnSave   = UITheme.successButton("➕ Add User");
        JButton btnCancel = UITheme.secondaryButton("Cancel");
        btns.add(btnCancel);
        btns.add(btnSave);

        btnCancel.addActionListener(e -> dialog.dispose());
        btnSave.addActionListener(e -> {
            String uname = txtUsername.getText().trim();
            String pass  = new String(txtPassword.getPassword()).trim();
            String fname = txtFullName.getText().trim();
            String email = txtEmail.getText().trim();

            if (Validator.isEmpty(uname)) { Validator.showError(dialog, "Username is required."); return; }
            if (Validator.isEmpty(pass))  { Validator.showError(dialog, "Password is required."); return; }
            if (Validator.isEmpty(fname)) { Validator.showError(dialog, "Full name is required."); return; }
            if (!email.isEmpty() && !Validator.isValidEmail(email)) {
                Validator.showError(dialog, "Invalid email address."); return;
            }

            User newUser = new User();
            newUser.setUsername(uname);
            newUser.setPassword(pass);
            newUser.setFullName(fname);
            newUser.setEmail(email.isEmpty() ? null : email);
            newUser.setRole((String) cmbRole.getSelectedItem());

            if (userDAO.addUser(newUser)) {
                Validator.showSuccess(dialog, "User \"" + uname + "\" created successfully.");
                dialog.dispose();
                loadUsers();
            } else {
                Validator.showError(dialog, "Failed. Username may already exist.");
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
