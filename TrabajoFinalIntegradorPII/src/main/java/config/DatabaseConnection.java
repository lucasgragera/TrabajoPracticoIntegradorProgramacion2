package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/vehiculos_db";
    private static final String USER = "root";
    private static final String PASSWORD = "password";
    
    // Constructor privado para evitar instanciación
    private DatabaseConnection() {}

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Error: no se encontro el driver JDBC");
            e.printStackTrace();
        }
        
        //Intenta establecer la conexion
        Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
        System.out.println("Conexion establecida con MySQL");
        return conn;
    }
}