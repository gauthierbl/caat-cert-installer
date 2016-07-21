package com.kcura.certificates;

import java.sql.*;

/**
 * TODO: Please don't forget to document me!
 */
public class JdbcTest {

    public static void main(String[] args) {



        // Declare the JDBC objects.
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;

        String jdbcUrl = "jdbc:sqlserver://172.17.66.162:1433;databaseName=EDDS;user=CERTafiable;password=TNAnT5kr4uWyhaUBuayG5ynpXVKMxP5hVupuMMQGJ4xftWVCD5MtyYPgcwM7VyyvsChtvJ8vsjXpLsk2mGCJmHGX99AXY5KwGpxERTLsd6hSMWGRw2YHf2X5wjRAenDz";



        try {
            // Establish the connection.
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            con = DriverManager.getConnection(jdbcUrl);


            // Create and execute an SQL statement that returns some data.
            String SQL = "SELECT TOP 10 * FROM eddsdbo.ResourceServer ";
            stmt = con.createStatement();
            rs = stmt.executeQuery(SQL);

            // Iterate through the data in the result set and display it.
            while (rs.next()) {
                System.out.println(rs.getString("ArtifactID"));
                System.out.println(rs.getString("Name"));
                System.out.println(rs.getString("SslPublicKeyCert"));
                System.out.println(rs.getString("SslCertChain"));

                System.out.println("======");
                System.out.println();
            }
        }

        // Handle any errors that may have occurred.
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (rs != null) try { rs.close(); } catch(Exception e) {}
            if (stmt != null) try { stmt.close(); } catch(Exception e) {}
            if (con != null) try { con.close(); } catch(Exception e) {}
        }

    }
}


// 172.17.66.162