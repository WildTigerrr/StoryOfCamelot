package com.wildtigerrr.StoryOfCamelot.database;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.*;

public class DatabaseInterraction {

    private static Connection connection;
    private static final String dBProperty = "JDBC_DATABASE_URL";

    private static Connection getConnection() {
        if (connection == null) {
            try {
                String jDBCUrl = System.getenv(dBProperty);
                if (jDBCUrl == null) {
                    connection = getAlternativeConnection();
                } else {
                    connection = DriverManager.getConnection(jDBCUrl);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return connection;
    }

    private static Connection getAlternativeConnection() throws URISyntaxException, SQLException{
        URI dbUri = new URI(System.getenv("DATABASE_URL"));

        String username = dbUri.getUserInfo().split(":")[0];
        String password = dbUri.getUserInfo().split(":")[1];
        String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath() + "?sslmode=require";

        return DriverManager.getConnection(dbUrl, username, password);
    }

    public static ResultSet executeStatement() throws SQLException {
        Statement statement = getConnection().createStatement();
        return null;
    }

}
