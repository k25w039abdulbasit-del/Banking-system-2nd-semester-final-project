import ui.LoginFrame;
import javax.swing.*;

/**
 * Main - Application entry point.
 * Sets Nimbus Look & Feel and launches the Login screen.
 *
 * Course  : SW121 – Object Oriented Programming
 * Project : Library Management System
 * Batch   : K25SW
 */
public class Main {

    public static void main(String[] args) {

        // ── Apply Nimbus Look & Feel for modern UI rendering ───────────────
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // Fall back to default L&F if Nimbus unavailable
            System.out.println("[Main] Nimbus L&F not available, using default.");
        }

        // ── Launch Login screen on Event Dispatch Thread ───────────────────
        SwingUtilities.invokeLater(() -> new LoginFrame());
    }
}
