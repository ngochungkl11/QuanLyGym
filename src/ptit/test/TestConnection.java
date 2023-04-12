package ptit.test;



import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TestConnection {
    public static void main(String[] args) {
   
  
        Connection conn = null;
        try {
      
            String url = "jdbc:sqlserver://localhost:1433;databaseName=GYMMANAGER;";
            String username = "sa";
            String password = "1234";
          
            conn = DriverManager.getConnection(url, username, password);
            System.out.println("KẾT NỐI THÀNH CÔNG!!!!");
         
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

