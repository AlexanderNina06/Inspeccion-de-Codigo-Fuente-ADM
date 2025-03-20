package screensframework.DBConnect;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    
    private static Connection conn;
    private static String url = "jdbc:postgresql://localhost:5432/test_ventas";
    private static String user = "postgres";
    private static String pass = "Alexpostgres123*";
    /*
    private static String url = "jdbc:mysql://localhost/sysventas";
    private static String user = "root";
    private static String pass = "";*/
    
    public static Connection connect() throws SQLException{
        try {
            // Cargar el driver de PostgreSQL
            Class.forName("org.postgresql.Driver");
            // Para MySQL (si se descomenta la línea):
            // Class.forName("com.mysql.cj.jdbc.Driver");

        } catch (ClassNotFoundException cnfe) {
            System.err.println("Error: " + cnfe.getMessage());
        }
        conn = DriverManager.getConnection(url, user, pass);
        return conn;
	}
	
    public static Connection getConnection() throws SQLException, ClassNotFoundException{
        if(conn !=null && !conn.isClosed())
            return conn;
            connect();
            return conn;
    }
}
