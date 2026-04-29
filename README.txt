====================================================
  LIBRARY MANAGEMENT SYSTEM
  SW121 – Object Oriented Programming | Batch K25SW
  Mehran University of Engineering & Technology
====================================================

TECHNOLOGIES
  - Java JDK 17+
  - Java Swing (UI)
  - MySQL 8+ (Database)
  - JDBC (mysql-connector-j)
  - NetBeans / IntelliJ IDEA

----------------------------------------------------
SETUP INSTRUCTIONS
----------------------------------------------------

STEP 1 – Create Database
  Open MySQL Workbench or MySQL CLI and run:
    source database/library_schema.sql;
  This creates the 'library_db' database with all tables
  and sample data (8 books, 5 members, 2 users).

STEP 2 – Add JDBC Driver
  Download: mysql-connector-j-8.x.jar
  From: https://dev.mysql.com/downloads/connector/j/
  Place JAR inside the /lib folder.

  In NetBeans:
    Right-click project > Properties > Libraries > Add JAR/Folder
    Select the mysql-connector-j JAR file.

STEP 3 – Configure Database Credentials
  Open: src/database/DBConnection.java
  Update:
    private static final String USER     = "root";
    private static final String PASSWORD = "your_password_here";

STEP 4 – Build & Run
  Set Main.java as the main class and run the project.
  The Login screen will appear.

----------------------------------------------------
DEFAULT LOGIN CREDENTIALS
----------------------------------------------------

  ADMIN ACCOUNT
    Username : admin
    Password : admin123
    Access   : Full access (including User Management)

  USER ACCOUNT
    Username : librarian
    Password : user123
    Access   : Books, Members, Issue/Return, Reports

----------------------------------------------------
PROJECT STRUCTURE
----------------------------------------------------

  LibraryManagementSystem/
  ├── database/
  │   └── library_schema.sql      MySQL schema + seed data
  ├── lib/
  │   └── (place mysql-connector-j.jar here)
  ├── src/
  │   ├── Main.java               Application entry point
  │   ├── database/
  │   │   └── DBConnection.java   JDBC Singleton
  │   ├── models/
  │   │   ├── User.java
  │   │   ├── Book.java
  │   │   ├── Member.java
  │   │   └── IssuedBook.java
  │   ├── dao/
  │   │   ├── UserDAO.java        Authentication + user CRUD
  │   │   ├── BookDAO.java        Book CRUD + search/filter
  │   │   ├── MemberDAO.java      Member CRUD + search
  │   │   └── IssueDAO.java       Issue/Return transactions
  │   ├── ui/
  │   │   ├── LoginFrame.java     Login screen
  │   │   ├── MainFrame.java      Main window + sidebar
  │   │   ├── DashboardPanel.java Stats overview
  │   │   ├── BooksPanel.java     Book management
  │   │   ├── MembersPanel.java   Member management
  │   │   ├── IssuePanel.java     Issue & return books
  │   │   ├── ReportsPanel.java   History & print report
  │   │   └── UserManagementPanel.java  Admin: manage users
  │   └── utils/
  │       ├── UITheme.java        Colors, fonts, styled components
  │       └── Validator.java      Input validation helpers
  ├── Project_Report.html         Open in browser, Print as PDF
  └── README.txt                  This file

----------------------------------------------------
REQUIREMENTS COVERAGE
----------------------------------------------------

  REQ 1 – Login/Authentication   : LoginFrame + UserDAO (Admin/User roles)
  REQ 2 – CRUD Operations        : BookDAO, MemberDAO, IssueDAO, UserDAO
  REQ 3 – Search & Filter        : SQL LIKE queries + category/status dropdowns
  REQ 4 – Data Validation        : Validator.java (UI) + DB constraints (SQL)
  REQ 5 – Reports Generation     : ReportsPanel + JTable.print() for printing
  REQ 6 – Attractive GUI         : UITheme.java with blue/dark-sidebar design
  REQ 7 – Database Connectivity  : DBConnection.java using JDBC + PreparedStatement
  REQ 8 – Modular Code Design    : MVC packages + Javadoc on all classes

----------------------------------------------------
FINE CALCULATION
----------------------------------------------------
  Overdue fine = Rs 5.00 per day (configurable in IssuePanel.java)

====================================================
