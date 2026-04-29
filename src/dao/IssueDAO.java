package dao;

import database.DBConnection;
import models.IssuedBook;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * IssueDAO - Handles all book issue/return database operations.
 *
 * Course  : SW121 – Object Oriented Programming
 * Project : Library Management System
 */
public class IssueDAO {

    private final Connection conn;

    public IssueDAO() {
        this.conn = DBConnection.getConnection();
    }

    // ── ISSUE A BOOK ──────────────────────────────────────────────────────────

    /**
     * Records a new book issue transaction.
     * @param issue IssuedBook object with bookId, memberId, issuedBy, dates
     * @return true if successful
     */
    public boolean issueBook(IssuedBook issue) {
        String sql = "INSERT INTO issued_books "
                   + "(book_id, member_id, issued_by, issue_date, due_date, status) "
                   + "VALUES (?, ?, ?, ?, ?, 'Issued')";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt   (1, issue.getBookId());
            ps.setInt   (2, issue.getMemberId());
            ps.setInt   (3, issue.getIssuedBy());
            ps.setString(4, issue.getIssueDate());
            ps.setString(5, issue.getDueDate());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[IssueDAO] issueBook error: " + e.getMessage());
            return false;
        }
    }

    // ── RETURN A BOOK ─────────────────────────────────────────────────────────

    /**
     * Marks a book as returned, sets return date and fine amount.
     * @param issueId   the issue transaction ID
     * @param returnDate actual return date
     * @param fine       calculated fine amount
     * @return true if successful
     */
    public boolean returnBook(int issueId, String returnDate, double fine) {
        String sql = "UPDATE issued_books SET return_date=?, fine_amount=?, status='Returned' "
                   + "WHERE issue_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, returnDate);
            ps.setDouble(2, fine);
            ps.setInt   (3, issueId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[IssueDAO] returnBook error: " + e.getMessage());
            return false;
        }
    }

    // ── GET ALL ISSUES ────────────────────────────────────────────────────────

    /**
     * Retrieves all issue records with book and member names joined.
     * @return list of IssuedBook records
     */
    public List<IssuedBook> getAllIssues() {
        List<IssuedBook> list = new ArrayList<>();
        String sql = "SELECT ib.*, b.title AS book_title, m.name AS member_name, "
                   + "u.full_name AS issued_by_name "
                   + "FROM issued_books ib "
                   + "JOIN books   b ON ib.book_id   = b.book_id "
                   + "JOIN members m ON ib.member_id = m.member_id "
                   + "JOIN users   u ON ib.issued_by  = u.user_id "
                   + "ORDER BY ib.issue_id DESC";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[IssueDAO] getAllIssues error: " + e.getMessage());
        }
        return list;
    }

    // ── GET ACTIVE ISSUES ─────────────────────────────────────────────────────

    /** Returns only currently issued (not yet returned) books. */
    public List<IssuedBook> getActiveIssues() {
        List<IssuedBook> list = new ArrayList<>();
        String sql = "SELECT ib.*, b.title AS book_title, m.name AS member_name, "
                   + "u.full_name AS issued_by_name "
                   + "FROM issued_books ib "
                   + "JOIN books   b ON ib.book_id   = b.book_id "
                   + "JOIN members m ON ib.member_id = m.member_id "
                   + "JOIN users   u ON ib.issued_by  = u.user_id "
                   + "WHERE ib.status = 'Issued' "
                   + "ORDER BY ib.due_date ASC";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[IssueDAO] getActiveIssues error: " + e.getMessage());
        }
        return list;
    }

    // ── SEARCH ISSUES ─────────────────────────────────────────────────────────

    public List<IssuedBook> searchIssues(String keyword) {
        List<IssuedBook> list = new ArrayList<>();
        String sql = "SELECT ib.*, b.title AS book_title, m.name AS member_name, "
                   + "u.full_name AS issued_by_name "
                   + "FROM issued_books ib "
                   + "JOIN books   b ON ib.book_id   = b.book_id "
                   + "JOIN members m ON ib.member_id = m.member_id "
                   + "JOIN users   u ON ib.issued_by  = u.user_id "
                   + "WHERE b.title LIKE ? OR m.name LIKE ? "
                   + "ORDER BY ib.issue_id DESC";
        String like = "%" + keyword + "%";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, like);
            ps.setString(2, like);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[IssueDAO] searchIssues error: " + e.getMessage());
        }
        return list;
    }

    // ── STATS ─────────────────────────────────────────────────────────────────

    public int getTotalIssuedToday() {
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(
                 "SELECT COUNT(*) FROM issued_books WHERE DATE(issue_date) = CURDATE()")) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[IssueDAO] getTotalIssuedToday: " + e.getMessage());
        }
        return 0;
    }

    public int getOverdueCount() {
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(
                 "SELECT COUNT(*) FROM issued_books WHERE status='Issued' AND due_date < CURDATE()")) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[IssueDAO] getOverdueCount: " + e.getMessage());
        }
        return 0;
    }

    // ── HELPER ───────────────────────────────────────────────────────────────

    private IssuedBook mapRow(ResultSet rs) throws SQLException {
        IssuedBook ib = new IssuedBook(
            rs.getInt   ("issue_id"),
            rs.getInt   ("book_id"),
            rs.getInt   ("member_id"),
            rs.getInt   ("issued_by"),
            rs.getString("issue_date"),
            rs.getString("due_date"),
            rs.getString("return_date"),
            rs.getDouble("fine_amount"),
            rs.getString("status")
        );
        ib.setBookTitle   (rs.getString("book_title"));
        ib.setMemberName  (rs.getString("member_name"));
        ib.setIssuedByName(rs.getString("issued_by_name"));
        return ib;
    }
}
