package database;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * DBConnection - Singleton class for embedded H2 database connectivity via JDBC.
 * Provides a single shared connection instance throughout the application.
 *
 * Course  : SW121 – Object Oriented Programming
 * Project : Library Management System
 */
public class DBConnection {

    private static final String URL =
        "jdbc:h2:file:./database/library_db;MODE=MySQL;DATABASE_TO_UPPER=false;IGNORECASE=TRUE;AUTO_SERVER=TRUE";
    private static final String USER     = "sa";
    private static final String PASSWORD = "";

    // ── Singleton instance ────────────────────────────────────────────────────
    private static Connection connection = null;

    /** Private constructor – use getConnection() */
    private DBConnection() {}

    /**
     * Returns the singleton JDBC connection.
     * Creates and initializes the embedded database if needed.
     *
     * @return active {@link Connection} object
     */
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                ensureDatabaseFolder();
                Class.forName("org.h2.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                initializeDatabase();
                System.out.println("[DB] Embedded H2 connection established successfully.");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("[DB] H2 Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("[DB] Connection failed: " + e.getMessage());
        }
        return connection;
    }

    private static void ensureDatabaseFolder() throws SQLException {
        try {
            Path dbFolder = Paths.get("database");
            if (Files.notExists(dbFolder)) {
                Files.createDirectories(dbFolder);
            }
        } catch (IOException e) {
            throw new SQLException("Failed to create database folder", e);
        }
    }

    private static void initializeDatabase() throws SQLException {
        boolean needsSchema = !isDatabaseInitialized();
        if (needsSchema) {
            executeSchema();
            System.out.println("[DB] Database schema initialized.");
        }

        if (needsSchema || isUsersTableEmpty()) {
            if (!needsSchema) {
                executeSchema();
            }
            System.out.println("[DB] Seed data inserted.");
        }
    }

    private static boolean isDatabaseInitialized() throws SQLException {
        String checkSql = "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES "
                        + "WHERE UPPER(TABLE_NAME)='USERS' AND TABLE_SCHEMA='PUBLIC'";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(checkSql)) {
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    private static boolean isUsersTableEmpty() throws SQLException {
        String countSql = "SELECT COUNT(*) FROM users";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(countSql)) {
            return rs.next() && rs.getInt(1) == 0;
        }
    }

    private static void executeSchema() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            String schemaSql = new String(Files.readAllBytes(Paths.get("database", "library_schema.sql")));
            schemaSql = schemaSql.replaceAll("(?m)^\\s*--.*$", "");
            schemaSql = schemaSql.replaceAll("(?i)CREATE DATABASE IF NOT EXISTS[^;]*;", "");
            schemaSql = schemaSql.replaceAll("(?i)USE [^;]*;", "");
            schemaSql = schemaSql.replaceAll("ENUM\\([^)]*\\)", "VARCHAR(20)");
            String[] statements = schemaSql.split(";\\s*(?=(?:[^']*'[^']*')*[^']*$)");
            for (String statement : statements) {
                String sql = statement.trim();
                if (!sql.isEmpty()) {
                    stmt.execute(sql);
                }
            }
        } catch (IOException e) {
            throw new SQLException("Failed to read database schema file", e);
        }
    }

    /**
     * Closes the database connection gracefully.
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("[DB] Connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("[DB] Error closing connection: " + e.getMessage());
        }
    }
}
