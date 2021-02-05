package sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnector {

    private Connection connection = null;
    private final String SampleURL = "jdbc:sqlserver://${dbServer};databaseName=${dbName};user=${user};password={${pass}}";

    private String databaseName = "trackflo_storoeb1_brillew";
    private String serverName = "titan.csse.rose-hulman.edu";

    public boolean connect() {
        //BUILD YOUR CONNECTION STRING HERE USING THE SAMPLE URL ABOVE
        String url = SampleURL;
        String fullURL = url.
                 replace("${dbServer}", this.serverName)
                .replace("${dbName}", this.databaseName)
                .replace("${user}", "brillew")
                .replace("${pass}", "Bongerdoo-1102");
        try{
            this.connection = DriverManager.getConnection(fullURL);
        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public Connection getConnection() {
        return this.connection;
    }

    public void closeConnection() {
        //TODO: Task 1
        try{
            this.connection.close();
            this.connection=null;
        }catch(SQLException e){
            e.printStackTrace();
        }finally{
            try {
                if (this.connection != null && !this.connection.isClosed()) {
                    this.connection.close();
                }
            }catch(SQLException e){
                System.out.println("Shouldn't happen");
            }
        }
    }
}
