package models;

/**
 * User - Model class representing an application user.
 * Supports Admin and User roles for authentication.
 *
 * Course  : SW121 – Object Oriented Programming
 * Project : Library Management System
 */
public class User {

    private int    userId;
    private String username;
    private String password;
    private String fullName;
    private String email;
    private String role;       // "Admin" | "User"

    // ── Constructors ──────────────────────────────────────────────────────────

    public User() {}

    public User(int userId, String username, String fullName,
                String email, String role) {
        this.userId   = userId;
        this.username = username;
        this.fullName = fullName;
        this.email    = email;
        this.role     = role;
    }

    // ── Getters & Setters ─────────────────────────────────────────────────────

    public int    getUserId()              { return userId;   }
    public void   setUserId(int userId)    { this.userId = userId; }

    public String getUsername()            { return username; }
    public void   setUsername(String u)    { this.username = u; }

    public String getPassword()            { return password; }
    public void   setPassword(String p)    { this.password = p; }

    public String getFullName()            { return fullName; }
    public void   setFullName(String fn)   { this.fullName = fn; }

    public String getEmail()               { return email; }
    public void   setEmail(String e)       { this.email = e; }

    public String getRole()                { return role; }
    public void   setRole(String role)     { this.role = role; }

    public boolean isAdmin()               { return "Admin".equals(role); }

    @Override
    public String toString() {
        return fullName + " (" + role + ")";
    }
}
