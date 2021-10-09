import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBconnect {

    public static void connect() {
        Connection conn = null;
        try {
            String url = "jdbc:sqlite:C:\\Users\\ericv\\sql-jdbc.wiki\\sql-jdbc\\chinook.db";
            conn = DriverManager.getConnection(url);
            System.out.println("Connection to SQLite has been established.");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }catch (Exception except){
            System.out.println(" error ");
        }finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
        }
}
