import javax.xml.crypto.Data;
import java.sql.*;

public class DatabaseHandler {

    // Dit zijn de instellingen voor de verbinding. Vervang de databaseName indien deze voor jou anders is.
    public static final String connectionUrl = "jdbc:sqlserver://localhost\\SQLEXPRESS:1433;databaseName=Statistix;integratedSecurity=true;";


    /**
     *
     * @param procedure Geef hier het overzicht mee waar je aan werkt.
     * @param param Word gebruikt indien er een user gegeven nodig is voor de query
     * @return
     */
    public String[] execute(StoredProcedures procedure, String param) {
//        // Connection beheert informatie over de connectie met de database.
//        Connection con = null;
//
//        // Statement zorgt dat we een SQL query kunnen uitvoeren.
//        Statement stmt = null;
//
//        // ResultSet is de tabel die we van de database terugkrijgen.
//        // We kunnen door de rows heen stappen en iedere kolom lezen.
//        ResultSet rs = null;
//        String SQL;
//
//        switch(procedure)
//        {
//            case OVERVIEW1:
//                SQL = "SELECT TOP 10 Episode";
//                break;
//            case OVERVIEW2:
//                break;
//            case OVERVIEW3:
//                break;
//            case OVERVIEW4:
//                break;
//            case OVERVIEW5:
//                break;
//            case OVERVIEW6:
//                break;
//        }
//
//        try {
//            // 'Importeer' de driver die je gedownload hebt.
//            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
//            // Maak de verbinding met de database.
//            con = DriverManager.getConnection(connectionUrl);
//
//            // Stel een SQL query samen.
//            stmt = con.createStatement();
//            // Voer de query uit op de database.
//            rs = stmt.executeQuery(SQL);
//
//            // Als de resultset waarden bevat dan lopen we hier door deze waarden en printen ze.
//            while (rs.next()) {
//
//            }
//
//        }
//
//        // Handle any errors that may have occurred.
//        catch (Exception e) {
//            e.printStackTrace();
//        }
//        finally {
//            if (rs != null) try { rs.close(); } catch(Exception e) {}
//            if (stmt != null) try { stmt.close(); } catch(Exception e) {}
//            if (con != null) try { con.close(); } catch(Exception e) {}
//        }
//    }
        return null;
    }
}
