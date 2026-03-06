import javax.swing.*;
import java.io.File;

public class Badget {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Ensure data directory exists
            File dataDir = new File("data");
            if (!dataDir.exists()) {
                dataDir.mkdir();
            }
            
            // Create and display main window
            JFrame frame = new JFrame("Badget - Budget & Expense Tracker");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1200, 700);
            frame.setLocationRelativeTo(null);
            frame.setResizable(true);
            
            // Set icon and styling
                // TODO: Set icon or look and feel here if needed
            
            // Create main dashboard panel
            DashboardPanel dashboard = new DashboardPanel();
            frame.add(dashboard);
            
            frame.setVisible(true);
        });
    }
}
