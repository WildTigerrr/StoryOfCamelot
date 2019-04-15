package com.wildtigerrr.StoryOfCamelot.database;

import com.wildtigerrr.StoryOfCamelot.web.BotResponseHandler;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.*;

public class DatabaseInterraction {

    private static final String dBProperty = "JDBC_DATABASE_URL";

    private static Connection getConnection() {
        Connection connection;
        try {
            String jDBCUrl = System.getenv(dBProperty);
            if (jDBCUrl == null) {
                connection = getAlternativeConnection();
            } else {
                connection = DriverManager.getConnection(jDBCUrl);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
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
        try (Connection connection = getConnection()) {
            if (connection == null) return null;
            Statement statement = connection.createStatement();
        }
        return null;
    }

    public static void createDatabase() {
        try (Connection connection = getConnection()) {
            if (connection == null) return;
            Statement statement = connection.createStatement();
            connection.setAutoCommit(false);
            String dml = "CREATE TABLE IF NOT EXIST USER (\n"
                    + " id integer PRIMARY KEY,\n"
                    + " name text NOT NULL,\n"
                    + ")";
            statement.execute(dml);
            dml = "CREATE TABLE IF NOT EXIST WEAPON ("
                    + " id integer PRIMARY KEY,\n"
                    + " type text NOT NULL,\n"
                    + " damage integer,\n"
                    + " price integer,\n"
                    + ")";
            statement.execute(dml);
            connection.commit();
            BotResponseHandler.sendMessageToAdmin("Database created");
        } catch (SQLException e) {
            e.printStackTrace();
            BotResponseHandler.sendMessageToAdmin(e.getMessage());
        }
    }

}
