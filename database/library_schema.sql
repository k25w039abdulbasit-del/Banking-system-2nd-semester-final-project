-- ============================================
-- Library Management System - MySQL Schema
-- Course: SW121 OOP Project
-- ============================================

CREATE DATABASE IF NOT EXISTS library_db;
USE library_db;

-- ============================================
-- USERS TABLE (Admin & User roles)
-- ============================================
CREATE TABLE IF NOT EXISTS users (
    user_id     INT AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(50)  NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    full_name   VARCHAR(100) NOT NULL,
    email       VARCHAR(100),
    role        ENUM('Admin', 'User') NOT NULL DEFAULT 'User',
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
-- BOOKS TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS books (
    book_id       INT AUTO_INCREMENT PRIMARY KEY,
    title         VARCHAR(200) NOT NULL,
    author        VARCHAR(100) NOT NULL,
    isbn          VARCHAR(20)  UNIQUE,
    category      VARCHAR(50),
    publisher     VARCHAR(100),
    year_published INT,
    total_copies  INT NOT NULL DEFAULT 1,
    available_copies INT NOT NULL DEFAULT 1,
    added_on      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
-- MEMBERS TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS members (
    member_id   INT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    email       VARCHAR(100) UNIQUE,
    phone       VARCHAR(20),
    address     TEXT,
    joined_date DATE DEFAULT (CURRENT_DATE),
    status      ENUM('Active', 'Inactive') DEFAULT 'Active'
);

-- ============================================
-- ISSUE / BORROW TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS issued_books (
    issue_id      INT AUTO_INCREMENT PRIMARY KEY,
    book_id       INT NOT NULL,
    member_id     INT NOT NULL,
    issued_by     INT NOT NULL,
    issue_date    DATE NOT NULL DEFAULT (CURRENT_DATE),
    due_date      DATE NOT NULL,
    return_date   DATE,
    fine_amount   DECIMAL(8,2) DEFAULT 0.00,
    status        ENUM('Issued', 'Returned', 'Overdue') DEFAULT 'Issued',
    FOREIGN KEY (book_id)   REFERENCES books(book_id),
    FOREIGN KEY (member_id) REFERENCES members(member_id),
    FOREIGN KEY (issued_by) REFERENCES users(user_id)
);

-- ============================================
-- SEED DATA
-- ============================================

-- Default Admin user  (password: admin123)
INSERT INTO users (username, password, full_name, email, role)
VALUES ('admin', 'admin123', 'System Administrator', 'admin@library.com', 'Admin');

-- Default normal user (password: user123)
INSERT INTO users (username, password, full_name, email, role)
VALUES ('librarian', 'user123', 'John Librarian', 'john@library.com', 'User');

-- Sample Books
INSERT INTO books (title, author, isbn, category, publisher, year_published, total_copies, available_copies) VALUES
('Clean Code',               'Robert C. Martin',  '9780132350884', 'Technology',  'Prentice Hall', 2008, 3, 3),
('The Great Gatsby',         'F. Scott Fitzgerald','9780743273565', 'Fiction',     'Scribner',      1925, 2, 2),
('Introduction to Algorithms','Thomas H. Cormen', '9780262033848', 'Technology',  'MIT Press',     2009, 2, 2),
('1984',                     'George Orwell',      '9780451524935', 'Fiction',     'Signet Classic',1949, 4, 4),
('Java: The Complete Reference','Herbert Schildt', '9781260440249', 'Technology',  'McGraw-Hill',   2019, 3, 3),
('To Kill a Mockingbird',    'Harper Lee',         '9780061935466', 'Fiction',     'Harper Perennial',1960,2,2),
('Design Patterns',          'Gang of Four',       '9780201633610', 'Technology',  'Addison-Wesley',1994, 2, 2),
('Pride and Prejudice',      'Jane Austen',        '9780141439518', 'Classic',     'Penguin Books', 1813, 3, 3);

-- Sample Members
INSERT INTO members (name, email, phone, address, status) VALUES
('Ali Hassan',     'ali@email.com',    '0300-1234567', 'Khairpur Mirs',  'Active'),
('Sara Ahmed',     'sara@email.com',   '0301-2345678', 'Sukkur',         'Active'),
('Bilal Khan',     'bilal@email.com',  '0302-3456789', 'Larkana',        'Active'),
('Fatima Noor',    'fatima@email.com', '0303-4567890', 'Nawabshah',      'Active'),
('Usman Shaikh',   'usman@email.com',  '0304-5678901', 'Hyderabad',      'Inactive');
