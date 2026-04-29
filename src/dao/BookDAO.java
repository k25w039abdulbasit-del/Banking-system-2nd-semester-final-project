package dao;

import database.DBConnection;
import models.Book;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * BookDAO - Data Access Object for all Book-related database operations.
 * Handles Create, Read, Update, Delete and Search operations.
 *
 * Course  : SW121 – Object Oriented Programming
 * Project : Library Management System
 */
public class BookDAO {

    private final Connection conn;

    public BookDAO() {
        this.conn = DBConnection.getConnection();
    }

    // ── CREATE ────────────────────────────────────────────────────────────────

    /**
     * Inserts a new book record into the database.
     * @param book Book object to insert
     * @return true if successful, false otherwise
     */
    public boolean addBook(Book book) {
        String sql = "INSERT INTO books (title, author, isbn, category, "
                   + "publisher, year_published, total_copies, available_copies) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, book.getTitle());
            ps.setString(2, book.getAuthor());
            ps.setString(3, book.getIsbn());
            ps.setString(4, book.getCategory());
            ps.setString(5, book.getPublisher());
            ps.setInt   (6, book.getYearPublished());
            ps.setInt   (7, book.getTotalCopies());
            ps.setInt   (8, book.getAvailableCopies());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[BookDAO] addBook error: " + e.getMessage());
            return false;
        }
    }

    // ── READ (ALL) ────────────────────────────────────────────────────────────

    /**
     * Retrieves all books from the database.
     * @return list of all Book objects
     */
    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books ORDER BY title";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                books.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("[BookDAO] getAllBooks error: " + e.getMessage());
        }
        return books;
    }

    // ── READ (BY ID) ──────────────────────────────────────────────────────────

    /**
     * Retrieves a single book by its ID.
     * @param bookId primary key of the book
     * @return Book object or null if not found
     */
    public Book getBookById(int bookId) {
        String sql = "SELECT * FROM books WHERE book_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.err.println("[BookDAO] getBookById error: " + e.getMessage());
        }
        return null;
    }

    // ── SEARCH ────────────────────────────────────────────────────────────────

    /**
     * Searches books by title, author, ISBN, or category.
     * @param keyword search term
     * @return list of matching books
     */
    public List<Book> searchBooks(String keyword) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE title LIKE ? OR author LIKE ? "
                   + "OR isbn LIKE ? OR category LIKE ? ORDER BY title";
        String like = "%" + keyword + "%";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, like);
            ps.setString(2, like);
            ps.setString(3, like);
            ps.setString(4, like);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) books.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[BookDAO] searchBooks error: " + e.getMessage());
        }
        return books;
    }

    // ── FILTER BY CATEGORY ────────────────────────────────────────────────────

    /**
     * Filters books by a specific category.
     * @param category category name to filter on
     * @return list of matching books
     */
    public List<Book> getBooksByCategory(String category) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE category = ? ORDER BY title";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, category);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) books.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[BookDAO] getBooksByCategory error: " + e.getMessage());
        }
        return books;
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────

    /**
     * Updates an existing book record.
     * @param book Book with updated fields (bookId must be set)
     * @return true if successful
     */
    public boolean updateBook(Book book) {
        String sql = "UPDATE books SET title=?, author=?, isbn=?, category=?, "
                   + "publisher=?, year_published=?, total_copies=?, available_copies=? "
                   + "WHERE book_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, book.getTitle());
            ps.setString(2, book.getAuthor());
            ps.setString(3, book.getIsbn());
            ps.setString(4, book.getCategory());
            ps.setString(5, book.getPublisher());
            ps.setInt   (6, book.getYearPublished());
            ps.setInt   (7, book.getTotalCopies());
            ps.setInt   (8, book.getAvailableCopies());
            ps.setInt   (9, book.getBookId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[BookDAO] updateBook error: " + e.getMessage());
            return false;
        }
    }

    // ── DELETE ────────────────────────────────────────────────────────────────

    /**
     * Deletes a book by its ID.
     * @param bookId ID of the book to delete
     * @return true if successful
     */
    public boolean deleteBook(int bookId) {
        String sql = "DELETE FROM books WHERE book_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[BookDAO] deleteBook error: " + e.getMessage());
            return false;
        }
    }

    // ── AVAILABILITY UPDATE ───────────────────────────────────────────────────

    /**
     * Decrements available copies when a book is issued.
     */
    public boolean decrementAvailableCopies(int bookId) {
        String sql = "UPDATE books SET available_copies = available_copies - 1 "
                   + "WHERE book_id = ? AND available_copies > 0";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[BookDAO] decrementAvailable error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Increments available copies when a book is returned.
     */
    public boolean incrementAvailableCopies(int bookId) {
        String sql = "UPDATE books SET available_copies = available_copies + 1 "
                   + "WHERE book_id = ? AND available_copies < total_copies";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[BookDAO] incrementAvailable error: " + e.getMessage());
            return false;
        }
    }

    // ── STATS ─────────────────────────────────────────────────────────────────

    /** Returns total number of books in the database. */
    public int getTotalBooks() {
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM books")) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[BookDAO] getTotalBooks error: " + e.getMessage());
        }
        return 0;
    }

    // ── HELPER ───────────────────────────────────────────────────────────────

    /** Maps a ResultSet row to a Book object. */
    private Book mapRow(ResultSet rs) throws SQLException {
        return new Book(
            rs.getInt   ("book_id"),
            rs.getString("title"),
            rs.getString("author"),
            rs.getString("isbn"),
            rs.getString("category"),
            rs.getString("publisher"),
            rs.getInt   ("year_published"),
            rs.getInt   ("total_copies"),
            rs.getInt   ("available_copies")
        );
    }
}
