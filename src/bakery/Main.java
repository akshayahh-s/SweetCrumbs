package bakery;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        try {
            DBConnection.testConnection();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Could not connect to the database.\n\n" + e.getMessage()
                            + "\n\nCheck that MySQL is running, that sql/schema.sql has been run,\n"
                            + "and that the username/password in DBConnection.java are correct.",
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        SwingUtilities.invokeLater(() -> new BakeryApp().setVisible(true));
    }
}
