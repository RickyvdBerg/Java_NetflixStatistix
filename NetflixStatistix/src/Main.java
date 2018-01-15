


import java.awt.EventQueue;
import java.sql.*;

import javax.swing.SwingUtilities;

import userInterface.App;
/**
 * default implementation running a form
 */
public class Main {
    public static void main(String[] args) throws SQLException {
    	App p = new App();
    	SwingUtilities.invokeLater(p);
    }
}
