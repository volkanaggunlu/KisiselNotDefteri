package Controller;
// Bridge Tasarım deseni bileşeni
public interface DatabaseConnector {
    void connect();
    void executeQuery(String query);
}
