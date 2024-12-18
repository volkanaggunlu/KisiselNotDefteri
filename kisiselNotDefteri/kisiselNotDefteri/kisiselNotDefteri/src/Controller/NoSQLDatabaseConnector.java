package Controller;

import Model.DatabasePlatform;

public class NoSQLDatabaseConnector implements DatabaseConnector{

    protected DatabasePlatform databasePlatform;

    public NoSQLDatabaseConnector(DatabasePlatform databasePlatform){
        this.databasePlatform = databasePlatform;
    }

    @Override
    public void connect() {
        System.out.println("Connected to NoSQL Database");
        databasePlatform.configureConnection();
    }

    @Override
    public void executeQuery(String query) {
        System.out.println("Executing query: " + query);
    }
}
