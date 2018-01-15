package database;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import java.util.Vector;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

//DbUtils provides an easy way to transform a resultset to a model a table can use
public class DbUtils {
    public static TableModel resultSetToTableModel(ResultSet rs) {
        try {
        	//get metadata
            ResultSetMetaData metaData = rs.getMetaData();
            //get number of columns
            int numberOfColumns = metaData.getColumnCount();
            //create a new vector
            Vector columnNames = new Vector();

            // Get the column names
            for (int column = 0; column < numberOfColumns; column++) {
                columnNames.addElement(metaData.getColumnLabel(column + 1));
            }

            // Get all rows
            Vector rows = new Vector();
            while (rs.next()) {
                Vector newRow = new Vector();

                for (int i = 1; i <= numberOfColumns; i++) {
                    newRow.addElement(rs.getObject(i));
                }

                rows.addElement(newRow);
            }

            //return a new model using the rows and column names
            return new DefaultTableModel(rows, columnNames);
        //catch the exception if one occurs
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }
}
