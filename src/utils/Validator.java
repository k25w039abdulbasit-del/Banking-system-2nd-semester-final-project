package utils;

import javax.swing.*;
import java.awt.*;

/**
 * Validator - Utility class for UI and data validation.
 * Validates input fields and shows user-friendly error messages.
 *
 * Course  : SW121 – Object Oriented Programming
 * Project : Library Management System
 */
public class Validator {

    // ── String Checks ─────────────────────────────────────────────────────────

    /** Returns true if the string is null or blank. */
    public static boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    /** Returns true if string length is within [min, max]. */
    public static boolean isLengthValid(String value, int min, int max) {
        if (isEmpty(value)) return false;
        int len = value.trim().length();
        return len >= min && len <= max;
    }

    // ── Format Checks ─────────────────────────────────────────────────────────

    /** Returns true if the email address format is valid. */
    public static boolean isValidEmail(String email) {
        if (isEmpty(email)) return false;
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    /** Returns true if the phone number format is valid (Pakistani format or 10+ digits). */
    public static boolean isValidPhone(String phone) {
        if (isEmpty(phone)) return false;
        return phone.matches("^[0-9\\-+() ]{7,20}$");
    }

    /** Returns true if the string is a valid positive integer. */
    public static boolean isPositiveInt(String value) {
        try {
            return Integer.parseInt(value.trim()) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /** Returns true if the string is a valid year (1000–2100). */
    public static boolean isValidYear(String value) {
        try {
            int year = Integer.parseInt(value.trim());
            return year >= 1000 && year <= 2100;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // ── UI Field Highlighting ─────────────────────────────────────────────────

    /** Highlights a field in red to indicate a validation error. */
    public static void markError(JComponent field) {
        field.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            new javax.swing.border.LineBorder(UITheme.DANGER, 2, true),
            javax.swing.BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
    }

    /** Resets a field to its normal border. */
    public static void clearError(JComponent field) {
        field.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            new javax.swing.border.LineBorder(UITheme.BORDER_COLOR, 1, true),
            javax.swing.BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
    }

    // ── Dialog Helpers ────────────────────────────────────────────────────────

    /** Shows a standard error dialog. */
    public static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message,
            "Validation Error", JOptionPane.ERROR_MESSAGE);
    }

    /** Shows a success dialog. */
    public static void showSuccess(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message,
            "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    /** Shows a confirmation dialog; returns true if user clicks Yes. */
    public static boolean confirm(Component parent, String message) {
        return JOptionPane.showConfirmDialog(parent, message,
            "Confirm Action",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION;
    }

    // ── Book Validation ───────────────────────────────────────────────────────

    /**
     * Validates all book form fields.
     * @return null if valid, error message string otherwise
     */
    public static String validateBook(String title, String author,
                                      String year,  String copies) {
        if (isEmpty(title))              return "Book title is required.";
        if (isEmpty(author))             return "Author name is required.";
        if (!isLengthValid(title, 1, 200))  return "Title must be 1–200 characters.";
        if (!isLengthValid(author, 2, 100)) return "Author must be 2–100 characters.";
        if (!isEmpty(year) && !isValidYear(year))
            return "Year must be a valid number between 1000 and 2100.";
        if (!isEmpty(copies) && !isPositiveInt(copies))
            return "Number of copies must be a positive integer.";
        return null;
    }

    // ── Member Validation ─────────────────────────────────────────────────────

    /**
     * Validates all member form fields.
     * @return null if valid, error message string otherwise
     */
    public static String validateMember(String name, String email, String phone) {
        if (isEmpty(name))               return "Member name is required.";
        if (!isLengthValid(name, 2, 100)) return "Name must be 2–100 characters.";
        if (!isEmpty(email) && !isValidEmail(email))
            return "Please enter a valid email address.";
        if (!isEmpty(phone) && !isValidPhone(phone))
            return "Please enter a valid phone number.";
        return null;
    }
}
