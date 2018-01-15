package database;

import javax.swing.DefaultComboBoxModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.xml.crypto.Data;


import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class DatabaseHandler {

    //the url used to establish a connection with the database 
    public static final String connectionUrl = "jdbc:sqlserver://localhost\\SQLEXPRESS:1433;databaseName=Statistix;integratedSecurity=true;";

    //execute method returns a tablemodel
    //requires one parameter: query, which is just the query string
    public TableModel execute(String query) {
        // Create a connection which is used to store info about our connection
        Connection con = null;

        // Statement is used to execute a SQL query
        Statement stmt = null;

        // Resultset is the set of info we get back from running our query
        ResultSet rs = null;
        String SQL = query;
        //our local tableModel which will later be returned 
        TableModel m = null;
        
        	
        try {
            // try to import the external driver
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            // try to establish a connection using the connectionUrl variable
            con = DriverManager.getConnection(connectionUrl);

            //prepare a new statement
            stmt = con.createStatement();
            //Provide the statement with a query and execute this
            //this will return a resultSet which we will save to rs
            rs = stmt.executeQuery(SQL);
            //send the resultSet we just received from our statement to the resultSetToTableModel method
            //this will try and convert it to a TableModel and return it
            //we save this model to our m variable
            m = DbUtils.resultSetToTableModel(rs);
        }
        // Handle any errors that may have occurred.
        catch (Exception e) {
            e.printStackTrace();
        }
        //when we are done with our actions we should close the connection
        finally {
            if (rs != null) try {
            	rs.close();
            	} catch(Exception e) {}
            if (stmt != null) try { stmt.close(); } catch(Exception e) {}
            if (con != null) try { con.close(); } catch(Exception e) {}
        }
        //return the TableModel
        return m;
    }
    
    //similiar method to the one above
    //however this one is used when you want to fill the contents of ComboBox instead of a table
    //returns a HashMap because we want the id coupled with the String so we can allways receive the Id of the String
    //==================================================================
    //!!!! Queries used in this method should only return 2 columns !!!!
    //the first one columns should be made up of some sort of numeric Id
    //the second one should be a String
    //==================================================================
    public HashMap<Integer, String> executeToMap(String query) {
    	// Create a connection which is used to store info about our connection
        Connection con = null;
        
        //create a local map will be returned later
        HashMap<Integer, String> map = new HashMap<>();
        
        // Statement is used to execute a SQL query
        Statement stmt = null;
        
        // Resultset is the set of info we get back from running our query
        ResultSet rs = null;
        String SQL = query;     
        

        try {
            // try to import the external driver
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            // try to establish a connection using the connectionUrl variable
            con = DriverManager.getConnection(connectionUrl);

            //prepare a new statement
            stmt = con.createStatement();
            //Provide the statement with a query and execute this
            //this will return a resultSet which we will save to rs
            rs = stmt.executeQuery(SQL);
            
            //As long as there are rows in our ResultSet we want to step through them
            //adding each row their id and string to our map;
            while (rs.next()) {
            	map.put(rs.getInt(1), rs.getString(2));
            }

        }

        // Handle any errors that may have occurred.
        catch (Exception e) {
            e.printStackTrace();
        }
        //when we are done with our actions we should close the connection
        finally {
            if (rs != null) try {
            	rs.close();
            	} catch(Exception e) {}
            if (stmt != null) try { stmt.close(); } catch(Exception e) {}
            if (con != null) try { con.close(); } catch(Exception e) {}
        }
        //return the map
        return map;
    }
}
