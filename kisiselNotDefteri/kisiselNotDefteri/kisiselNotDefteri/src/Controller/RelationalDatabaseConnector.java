package Controller;

import Model.DatabasePlatform;
import Model.MongoDBConnection;
import Model.MySQLConnection;

import java.sql.Connection;

public class RelationalDatabaseConnector implements DatabaseConnector{
    protected DatabasePlatform databasePlatform;
    public RelationalDatabaseConnector(DatabasePlatform databasePlatform){
        this.databasePlatform = databasePlatform;
    }
    @Override
    public void connect() {
        databasePlatform.configureConnection();
    }

    @Override
    public void executeQuery(String query) {
        System.out.println("Executing query: " + query);
    }
}
