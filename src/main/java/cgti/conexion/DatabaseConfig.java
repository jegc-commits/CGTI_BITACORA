package cgti.conexion;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConfig {
    public static Connection conectar(){
        Connection conexion = null;
        String url = "jdbc:sqlserver://localhost:1433;databaseName=cgti;encrypt=true;trustServerCertificate=true";
        String username = "admin";
        String password = "admin123";
        try{
            System.out.println("== Intentando cargar el driver de SQL Server... ==");
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            System.out.println("== Intentando conectar a la base de datos.... ==");
            conexion = DriverManager.getConnection(url, username, password);
            System.out.println("!Conexion exitosa a la base de datos cgti");
        }catch(Throwable e){
            System.out.println("\n!!!SE DETECTO UN ERROR!!!!!");
            System.out.println("Mensaje" + e.getMessage());
            System.out.println("tipo de excepcion: " + e.getClass().getName());
            e.printStackTrace();
            if (conexion == null){
                System.out.println("== El metodo termino y la conexion va a retornar NULL ==");
            }
            return conexion;
        }
        return conexion;
    }
}