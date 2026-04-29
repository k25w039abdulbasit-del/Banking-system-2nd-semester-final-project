package ui;

import dao.MemberDAO;
import models.Member;
import models.User;
import utils.UITheme;
import utils.Validator;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * MembersPanel - Full CRUD interface for managing library members.
 *
 * Course  : SW121 – Object Oriented Programming
 * Project : Library Management System
 */
public class MembersPanel extends JPanel {

    private final User      currentUser;
    private final MemberDAO memberDAO = new MemberDAO();

    private JTable           table;
    private DefaultTableModel tableModel;
    private JTextField       txtSearch;

    private static final String[] COLUMNS = {
        "ID", "Name", "Email", "Phone", "Address", "Joined", "Status"
    };

    public MembersPanel(User user) {
        this.currentUser = user;
        setBackground(UITheme.BG_MAIN);
        setLayout(new BorderLayout(0, 0));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        buildUI();
        loadMembers();
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

        txtSearch = UITheme.styledField(22);
        JButton btnSearch  = UITheme.primaryButton("🔍 Search");
        JButton btnReset   = UITheme.secondaryButton("Reset");
        left.add(new JLabel("Search:"));
        left.add(txtSearch);
        left.add(btnSearch);
        left.add(btnReset);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);
        JButton btnAdd     = UITheme.successButton("+ Add Member");
        JButton btnEdit    = UITheme.primaryButton("✏ Edit");
        JButton btnDelete  = UITheme.dangerButton("🗑 Delete");
        JButton btnRefresh = UITheme.secondaryButton("↻ Refresh");

        if (!currentUser.isAdmin()) btnDelete.setEnabled(false);

        right.add(btnRefresh);
        right.add(btnEdit);
        right.add(btnDelete);
        right.add(btnAdd);

        bar.add(left,  BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);

        btnSearch.addActionListener (e -> handleSearch());
        btnReset .addActionListener (e -> { txtSearch.setText(""); loadMembers(); });
        btnAdd   .addActionListener (e -> openMemberDialog(null));
        btnEdit  .addActionListener (e -> editSelected());
        btnDelete.addActionListener (e -> deleteSelected());
        btnRefresh.addActionListener(e -> loadMembers());
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

    private void loadMembers() {
        populateTable(memberDAO.getAllMembers());
    }

    private void handleSearch() {
        String kw = txtSearch.getText().trim();
        populateTable(kw.isEmpty() ? memberDAO.getAllMembers()
                                   : memberDAO.searchMembers(kw));
    }

    private void populateTable(List<Member> members) {
        tableModel.setRowCount(0);
        for (Member m : members) {
            tableModel.addRow(new Object[]{
                m.getMemberId(), m.getName(), m.getEmail(),
                m.getPhone(), m.getAddress(), m.getJoinedDate(), m.getStatus()
            });
        }
    }

    private void editSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { Validator.showError(this, "Please select a member to edit."); return; }
        int id = (int) tableModel.getValueAt(row, 0);
        openMemberDialog(memberDAO.getMemberById(id));
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { Validator.showError(this, "Please select a member to delete."); return; }
        String name = (String) tableModel.getValueAt(row, 1);
        int    id   = (int)    tableModel.getValueAt(row, 0);
        if (Validator.confirm(this, "Delete member: \"" + name + "\"?")) {
            if (memberDAO.deleteMember(id)) {
                Validator.showSuccess(this, "Member deleted.");
                loadMembers();
            } else {
                Validator.showError(this, "Cannot delete – member has active book issues.");
            }
        }
    }

    // ── Add/Edit Dialog ───────────────────────────────────────────────────────

    private void openMemberDialog(Member existing) {
        boolean isEdit = existing != null;
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
            isEdit ? "Edit Member" : "Add New Member", true);
        dialog.setSize(440, 440);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(20, 24, 8, 24));
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(5, 0, 5, 0);

        JTextField txtName    = UITheme.styledField(20);
        JTextField txtEmail   = UITheme.styledField(20);
        JTextField txtPhone   = UITheme.styledField(15);
        JTextField txtAddress = UITheme.styledField(20);
        JComboBox<String> cmbStatus = new JComboBox<>(new String[]{"Active","Inactive"});
        cmbStatus.setFont(UITheme.FONT_BODY);

        if (isEdit) {
            txtName   .setText(existing.getName());
            txtEmail  .setText(existing.getEmail());
            txtPhone  .setText(existing.getPhone());
            txtAddress.setText(existing.getAddress());
            cmbStatus.setSelectedItem(existing.getStatus());
        }

        String[] labels = {"Full Name *", "Email", "Phone", "Address", "Status"};
        JComponent[] fields = {txtName, txtEmail, txtPhone, txtAddress, cmbStatus};

        for (int i = 0; i < labels.length; i++) {
            gc.gridy = i * 2;     form.add(makeLabel(labels[i]), gc);
            gc.gridy = i * 2 + 1; form.add(fields[i], gc);
        }

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 12));
        btns.setBackground(UITheme.BG_MAIN);
        btns.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.BORDER_COLOR));
        JButton btnSave   = UITheme.successButton(isEdit ? "💾 Update" : "➕ Add");
        JButton btnCancel = UITheme.secondaryButton("Cancel");
        btns.add(btnCancel);
        btns.add(btnSave);

        btnCancel.addActionListener(e -> dialog.dispose());
        btnSave.addActionListener(e -> {
            String name  = txtName.getText().trim();
            String email = txtEmail.getText().trim();
            String phone = txtPhone.getText().trim();
            String err   = Validator.validateMember(name, email, phone);
            if (err != null) { Validator.showError(dialog, err); return; }

            Member m = isEdit ? existing : new Member();
            m.setName   (name);
            m.setEmail  (email.isEmpty() ? null : email);
            m.setPhone  (phone.isEmpty() ? null : phone);
            m.setAddress(txtAddress.getText().trim());
            m.setStatus ((String) cmbStatus.getSelectedItem());

            boolean ok = isEdit ? memberDAO.updateMember(m) : memberDAO.addMember(m);
            if (ok) {
                Validator.showSuccess(dialog, "Member " + (isEdit ? "updated" : "added") + " successfully.");
                dialog.dispose();
                loadMembers();
            } else {
                Validator.showError(dialog, "Failed. Email may already be in use.");
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
