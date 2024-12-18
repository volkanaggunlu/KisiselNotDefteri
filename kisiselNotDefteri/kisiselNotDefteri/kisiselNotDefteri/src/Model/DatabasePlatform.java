package Model;
// Bridge Tasarım deseni bileşeni
import java.sql.Connection;
import java.sql.SQLException;

public interface DatabasePlatform {
    void configureConnection();
    Connection getConnection();
}