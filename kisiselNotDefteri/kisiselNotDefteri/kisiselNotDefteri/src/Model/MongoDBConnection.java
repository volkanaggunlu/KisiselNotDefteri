package Model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MongoDBConnection extends InformationDatabase implements DatabasePlatform{
    private static MongoDBConnection instance;
    private Connection connection;

    private static final String URL = "jdbc:mysql://localhost:3306/notdefteridb";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "deneme123";

    private MongoDBConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("Bağlantı başarılı bir şekilde sağlandı.");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Veritabanı bağlantısı kurulamadı.", e);
        }
    }

    // Singleton tasarım deseni uygulandı.

    public static DatabasePlatform getInstance() {
        if (instance == null) {
            synchronized (MySQLConnection.class) {
                if (instance == null) {
                    instance = new MongoDBConnection();
                }
            }
        }
        return instance;
    }

    public Connection getConnection() {
        return this.connection;
    }

    @Override
    public void configureConnection() {
        getInstance().getConnection();
    }

    @Override
    public void databaseInformation(){
        System.out.println("MongoDBConnection bağlantısı kuruldu.");
    }

}
