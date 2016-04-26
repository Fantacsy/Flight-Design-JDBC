/**
PID:  A53078965
Name: Chao Fan
File: PA2.java

 * This Java program exemplifies the basic usage of JDBC.
 * Requirements:
 *   (1) JDK 1.6+.
 *   (2) SQLite3.
 *   (3) SQLite3 JDBC jar (https://bitbucket.org/xerial/sqlite-
 jdbc/downloads/sqlite-jdbc-3.8.7.jar).
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
public class PA2 {
    public static void main(String[] args) {
        Connection conn = null;  // Database connection.
        try {
            // Load the JDBC class.
            Class.forName("org.sqlite.JDBC");
            // Get the connection to the database.
            // - "jdbc" : JDBC connection name prefix.
            // - "sqlite" : The concrete database implementation
            // (e.g., sqlserver, postgresql).
            // - "pa2.db" : The name of the database. In this project,
            // we use a local database named "pa2.db".  This can also
            // be a remote database name.
            conn = DriverManager.getConnection("jdbc:sqlite:pa2.db");
            System.out.println("Opened database successfully.");
            // Use case #1: Create and populate a table.
            // Get a Statement object.
            Statement stmt = conn.createStatement();
            
            stmt.executeUpdate("Drop table if exists Connected0;");
            stmt.executeUpdate("Drop table if exists ConnecOld;");
            stmt.executeUpdate("Drop table if exists Conndelta;");
            
            stmt.executeUpdate("create  table Connected0 (airline char(32), origin char(32), destination char(32), stop integer);");
            stmt.executeUpdate("create  table ConnecOld (airline char(32), origin char(32), destination char(32));");
            stmt.executeUpdate("create  table Conndelta (airline char(32), origin char(32), destination char(32));");
            
            
            stmt.executeUpdate("INSERT INTO Connected0(airline, origin, destination) select airline, origin, destination from Flight;" );
            stmt.executeUpdate("INSERT INTO Conndelta(airline, origin, destination) select airline, origin, destination from Flight;" );
            
            ResultSet rs = stmt.executeQuery("select count(*) from Conndelta;");
            int deltasize = rs.getInt(1);
            int count = 1;
            
            while (deltasize > 0)
            {
                
                stmt.executeUpdate("DELETE FROM ConnecOld;" );
                stmt.executeUpdate("INSERT INTO ConnecOld select airline, origin, destination from Connected0;" );
                
                
                stmt.executeUpdate("INSERT INTO Connected0 select Flight.airline, Flight.origin, Conndelta.destination," + count + " from Flight, Conndelta where Flight.destination = Conndelta.Origin and Flight.airline = Conndelta.airline and Conndelta.Destination <> Flight.origin;");
                
                stmt.executeUpdate("DELETE FROM Conndelta;");
                stmt.executeUpdate("INSERT INTO Conndelta select Connected0.airline, Connected0.origin, Connected0.destination from Connected0 Except select * from ConnecOld;" );
                
                rs = stmt.executeQuery("select count(*) from Conndelta;");
                deltasize = rs.getInt(1);
                count++;
            }
            
            stmt.executeUpdate("UPDATE Connected0 SET stop = 0 where stop is null ;");
            stmt.executeUpdate("Drop table if exists Connected;");
            stmt.executeUpdate("create  table Connected (airline char(32), origin char(32), destination char(32), stop integer);");
            stmt.executeUpdate("INSERT INTO Connected select * from Connected0 c1 where not exists (select * from Connected0 c2 where c1.airline = c2.airline and c1.origin = c2.origin and c1.destination = c2.destination and c1.stop > c2.stop) group by airline, origin, destination ;" );
            
            stmt.executeUpdate("drop table Conndelta;");
            stmt.executeUpdate("drop table ConnecOld;");
            stmt.executeUpdate("drop table Connected0;");
            
            ResultSet rset2 = stmt.executeQuery("select * from Connected;");
            
            while (rset2.next())
            {
                System.out.print(rset2.getString("airline"));
                System.out.print("---");
                System.out.print(rset2.getString("origin"));
                System.out.print("---");
                System.out.print(rset2.getString("destination"));
                System.out.print("---");
                System.out.println(rset2.getString("stop"));
            }
            stmt.close();
        }
        
        catch (Exception e)
        {
            throw new RuntimeException("There was a runtime problem!", e);
        }
        finally
        {
            try
            {
                if (conn != null) conn.close();
            }
            catch (SQLException e)
            {
                throw new RuntimeException("Cannot close the connection!", e);
            }
        }
    }
}