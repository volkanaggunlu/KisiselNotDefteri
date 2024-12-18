package Model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLConnection extends InformationDatabase implements DatabasePlatform {

    // Singleton instance
    private static MySQLConnection instance;
    private Connection connection;

    private static final String URL = "jdbc:mysql://localhost:3306/notdefteridb";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "deneme123";

    // Private Constructor for Singleton
    private MySQLConnection() {
        connect();
    }

    private void connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("Veritabanı bağlantısı sağlandı.");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Veritabanı bağlantısı kurulamadı.", e);
        }
    }

    // Singleton tasarım deseni uygulandı.
    public static MySQLConnection getInstance() {
        if (instance == null) {
            synchronized (MySQLConnection.class) {
                if (instance == null) {
                    instance = new MySQLConnection();
                }
            }
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            // Eğer bağlantı kapalıysa yeniden bağlantı kur
            if (connection == null || connection.isClosed()) {
                connect();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Bağlantı kontrolü sırasında hata oluştu.", e);
        }
        return this.connection;
    }

    @Override
    public void configureConnection() {
        getConnection();
    }

    @Override
    public void databaseInformation(){
        System.out.println("MySQLConnection bağlantısı kuruldu.");
    }

}
