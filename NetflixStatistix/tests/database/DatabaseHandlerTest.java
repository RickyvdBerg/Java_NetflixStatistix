package database;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.swing.table.TableModel;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseHandlerTest {
    @DisplayName("Execute invalid query, Should not return a valid TableModel")
    @Test
    void execute() {
        DatabaseHandler db = new DatabaseHandler();
        TableModel m = db.execute("Hallo Leraar!");
        assertFalse(m instanceof TableModel);
    }
}