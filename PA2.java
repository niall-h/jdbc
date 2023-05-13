import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.PreparedStatement;

public class PA2 {
  public static void main(String args[]) {
    Connection conn = null;

    try {
      Class.forName("org.sqlite.JDBC");
      conn = DriverManager.getConnection("jdbc:sqlite:pa2.db");
      System.out.println("Opened database successfully.");

      Statement stmt = conn.createStatement();
      
      stmt.executeUpdate("DROP TABLE IF EXISTS T;");
      stmt.executeUpdate("CREATE TABLE T(airline, origin, destination, stops);");
      stmt.executeUpdate("DROP TABLE IF EXISTS delta;");
      stmt.executeUpdate("CREATE TABLE delta(airline, origin, destination);");

      // T = G
      stmt.executeUpdate("INSERT INTO T SELECT *, 0 FROM Flight;");


      // delta = G
      stmt.executeUpdate("INSERT INTO delta(airline, origin, destination) SELECT * FROM Flight;");

      int count = 0;
      // while delta is not empty
      ResultSet delta = stmt.executeQuery("SELECT * FROM delta;");
      while (delta.next()) {
        if (count == 0) {
          count++;
          continue;
        }
        // T_old = T
        stmt.executeUpdate("DROP TABLE IF EXISTS T_old;");
        stmt.executeUpdate("CREATE TABLE T_old(airline, origin, destination, stops);");
        stmt.executeUpdate("INSERT INTO T_old SELECT * FROM T");

        // T = recursive
        String query = "INSERT INTO T SELECT y.airline, y.origin, x.destination, " +
          count + " FROM Flight x, delta y WHERE x.origin = y.destination AND " +
          "x.airline = y.airline AND y.origin <> x.Destination;";
        stmt.executeUpdate(query);

        // delta = T - T_old
        stmt.executeUpdate("DROP TABLE IF EXISTS delta;");
        stmt.executeUpdate("CREATE TABLE delta(airline, origin, destination);");
        stmt.executeUpdate("INSERT INTO delta SELECT airline, origin, destination FROM T EXCEPT SELECT airline, origin, destination FROM T_old;");
        
        delta = stmt.executeQuery("SELECT * FROM delta;");
        count++;
      }

      stmt.executeUpdate("DROP TABLE IF EXISTS Connected;");
      stmt.executeUpdate("CREATE TABLE Connected(airline, origin, destination, stops);");
      stmt.executeUpdate("INSERT INTO Connected SELECT airline, origin, destination, MIN(stops) as stops FROM T GROUP BY airline, origin, destination;");

      // displaying T
      ResultSet T = stmt.executeQuery("SELECT * FROM Connected;");
      System.out.println("Result:");
      while (T.next()) {
        System.out.print(T.getString("airline"));
        System.out.print("------");
        System.out.print(T.getString("origin"));
        System.out.print("------");
        System.out.print(T.getString("destination"));
        System.out.print("------");
        System.out.println(T.getInt("stops"));
      }

      T.close();
      delta.close();
      stmt.close();
    }
    catch (Exception e) {
      throw new RuntimeException("There was a runtime problem!", e);
    }
    finally {
      try {
        if (conn != null) conn.close();
      }
      catch (SQLException e) {
        throw new RuntimeException("Cannot close the connection!", e);
      }
    }
  }
}