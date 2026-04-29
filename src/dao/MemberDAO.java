package dao;

import database.DBConnection;
import models.Member;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * MemberDAO - Data Access Object for all Member-related database operations.
 *
 * Course  : SW121 – Object Oriented Programming
 * Project : Library Management System
 */
public class MemberDAO {

    private final Connection conn;

    public MemberDAO() {
        this.conn = DBConnection.getConnection();
    }

    // ── CREATE ────────────────────────────────────────────────────────────────

    public boolean addMember(Member member) {
        String sql = "INSERT INTO members (name, email, phone, address, status) "
                   + "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, member.getName());
            ps.setString(2, member.getEmail());
            ps.setString(3, member.getPhone());
            ps.setString(4, member.getAddress());
            ps.setString(5, member.getStatus());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[MemberDAO] addMember error: " + e.getMessage());
            return false;
        }
    }

    // ── READ ALL ──────────────────────────────────────────────────────────────

    public List<Member> getAllMembers() {
        List<Member> list = new ArrayList<>();
        String sql = "SELECT * FROM members ORDER BY name";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[MemberDAO] getAllMembers error: " + e.getMessage());
        }
        return list;
    }

    // ── READ BY ID ────────────────────────────────────────────────────────────

    public Member getMemberById(int memberId) {
        String sql = "SELECT * FROM members WHERE member_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, memberId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.err.println("[MemberDAO] getMemberById error: " + e.getMessage());
        }
        return null;
    }

    // ── SEARCH ────────────────────────────────────────────────────────────────

    public List<Member> searchMembers(String keyword) {
        List<Member> list = new ArrayList<>();
        String sql = "SELECT * FROM members WHERE name LIKE ? OR email LIKE ? "
                   + "OR phone LIKE ? ORDER BY name";
        String like = "%" + keyword + "%";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, like);
            ps.setString(2, like);
            ps.setString(3, like);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[MemberDAO] searchMembers error: " + e.getMessage());
        }
        return list;
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────

    public boolean updateMember(Member member) {
        String sql = "UPDATE members SET name=?, email=?, phone=?, address=?, "
                   + "status=? WHERE member_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, member.getName());
            ps.setString(2, member.getEmail());
            ps.setString(3, member.getPhone());
            ps.setString(4, member.getAddress());
            ps.setString(5, member.getStatus());
            ps.setInt   (6, member.getMemberId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[MemberDAO] updateMember error: " + e.getMessage());
            return false;
        }
    }

    // ── DELETE ────────────────────────────────────────────────────────────────

    public boolean deleteMember(int memberId) {
        String sql = "DELETE FROM members WHERE member_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, memberId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[MemberDAO] deleteMember error: " + e.getMessage());
            return false;
        }
    }

    // ── STATS ─────────────────────────────────────────────────────────────────

    public int getTotalMembers() {
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM members")) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[MemberDAO] getTotalMembers error: " + e.getMessage());
        }
        return 0;
    }

    // ── HELPER ───────────────────────────────────────────────────────────────

    private Member mapRow(ResultSet rs) throws SQLException {
        return new Member(
            rs.getInt   ("member_id"),
            rs.getString("name"),
            rs.getString("email"),
            rs.getString("phone"),
            rs.getString("address"),
            rs.getString("joined_date"),
            rs.getString("status")
        );
    }
}
