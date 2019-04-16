package com.wildtigerrr.StoryOfCamelot.database;

import com.wildtigerrr.StoryOfCamelot.web.BotResponseHandler;
import org.postgresql.util.PSQLException;

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
            String dml = "CREATE TABLE \"PLAYER\" (\n" +
                    "\t\"id\" serial NOT NULL,\n" +
                    "\t\"external_id\" varchar(15) NOT NULL,\n" +
                    "\t\"nickname\" varchar(30) NOT NULL UNIQUE,\n" +
                    "\t\"level\" integer NOT NULL,\n" +
                    "\t\"experience\" integer NOT NULL,\n" +
                    "\t\"hitpoints\" integer NOT NULL,\n" +
                    "\t\"maximum_hitpoints\" integer NOT NULL,\n" +
                    "\t\"damage\" integer NOT NULL,\n" +
                    "\t\"status\" varchar(25) NOT NULL,\n" +
                    "\t\"current_location\" integer NOT NULL,\n" +
                    "\t\"speed\" integer NOT NULL,\n" +
                    "\tCONSTRAINT PLAYER_pk PRIMARY KEY (\"id\")\n" +
                    ") WITH (\n" +
                    "  OIDS=FALSE\n" +
                    ");\n" +
                    "\n" +
                    "\n" +
                    "\n" +
                    "CREATE TABLE \"WEAPON\" (\n" +
                    "\t\"id\" serial NOT NULL,\n" +
                    "\t\"damage\" DECIMAL NOT NULL,\n" +
                    "\t\"type\" varchar(20) NOT NULL,\n" +
                    "\t\"quality\" varchar(20) NOT NULL,\n" +
                    "\t\"durability\" integer,\n" +
                    "\t\"price\" DECIMAL NOT NULL,\n" +
                    "\t\"image_link\" integer NOT NULL,\n" +
                    "\tCONSTRAINT WEAPON_pk PRIMARY KEY (\"id\")\n" +
                    ") WITH (\n" +
                    "  OIDS=FALSE\n" +
                    ");\n" +
                    "\n" +
                    "\n" +
                    "\n" +
                    "CREATE TABLE \"ARMOR\" (\n" +
                    "\t\"id\" serial NOT NULL,\n" +
                    "\t\"defence\" DECIMAL NOT NULL,\n" +
                    "\t\"type\" varchar(20) NOT NULL,\n" +
                    "\t\"quality\" varchar(20) NOT NULL,\n" +
                    "\t\"durability\" integer,\n" +
                    "\t\"price\" DECIMAL NOT NULL,\n" +
                    "\t\"image_link\" integer NOT NULL,\n" +
                    "\tCONSTRAINT ARMOR_pk PRIMARY KEY (\"id\")\n" +
                    ") WITH (\n" +
                    "  OIDS=FALSE\n" +
                    ");\n" +
                    "\n" +
                    "\n" +
                    "\n" +
                    "CREATE TABLE \"TRANSPORT\" (\n" +
                    "\t\"id\" serial NOT NULL,\n" +
                    "\t\"type\" varchar(20) NOT NULL,\n" +
                    "\t\"speed\" DECIMAL NOT NULL,\n" +
                    "\t\"quality\" varchar(20) NOT NULL,\n" +
                    "\t\"price\" DECIMAL NOT NULL,\n" +
                    "\t\"image_link\" integer NOT NULL,\n" +
                    "\tCONSTRAINT TRANSPORT_pk PRIMARY KEY (\"id\")\n" +
                    ") WITH (\n" +
                    "  OIDS=FALSE\n" +
                    ");\n" +
                    "\n" +
                    "\n" +
                    "\n" +
                    "CREATE TABLE \"ITEM\" (\n" +
                    "\t\"id\" serial NOT NULL,\n" +
                    "\t\"type\" varchar(20),\n" +
                    "\t\"value\" DECIMAL,\n" +
                    "\t\"quality\" varchar(20) NOT NULL,\n" +
                    "\t\"price\" DECIMAL NOT NULL,\n" +
                    "\t\"image_link\" integer NOT NULL,\n" +
                    "\tCONSTRAINT ITEM_pk PRIMARY KEY (\"id\")\n" +
                    ") WITH (\n" +
                    "  OIDS=FALSE\n" +
                    ");\n" +
                    "\n" +
                    "\n" +
                    "\n" +
                    "CREATE TABLE \"BACKPACK\" (\n" +
                    "\t\"user_id\" integer NOT NULL,\n" +
                    "\t\"item_id\" integer NOT NULL,\n" +
                    "\t\"quantity\" integer NOT NULL,\n" +
                    "\t\"status\" varchar(25),\n" +
                    "\t\"isDeleted\" BOOLEAN NOT NULL DEFAULT 'false',\n" +
                    "\t\"deletedDate\" TIMESTAMP NOT NULL\n" +
                    ") WITH (\n" +
                    "  OIDS=FALSE\n" +
                    ");\n" +
                    "\n" +
                    "\n" +
                    "\n" +
                    "CREATE TABLE \"MOB\" (\n" +
                    "\t\"id\" serial NOT NULL,\n" +
                    "\t\"name\" varchar(40) NOT NULL,\n" +
                    "\t\"level\" integer NOT NULL,\n" +
                    "\t\"damage\" integer NOT NULL,\n" +
                    "\t\"maximum_hitpoints\" integer NOT NULL,\n" +
                    "\t\"defence\" integer NOT NULL,\n" +
                    "\t\"speed\" integer NOT NULL,\n" +
                    "\t\"image_link\" integer NOT NULL,\n" +
                    "\tCONSTRAINT MOB_pk PRIMARY KEY (\"id\")\n" +
                    ") WITH (\n" +
                    "  OIDS=FALSE\n" +
                    ");\n" +
                    "\n" +
                    "\n" +
                    "\n" +
                    "CREATE TABLE \"NPC\" (\n" +
                    "\t\"id\" serial NOT NULL,\n" +
                    "\t\"name\" varchar(40) NOT NULL,\n" +
                    "\t\"home_location\" integer NOT NULL,\n" +
                    "\t\"image_link\" integer NOT NULL,\n" +
                    "\tCONSTRAINT NPC_pk PRIMARY KEY (\"id\")\n" +
                    ") WITH (\n" +
                    "  OIDS=FALSE\n" +
                    ");\n" +
                    "\n" +
                    "\n" +
                    "\n" +
                    "CREATE TABLE \"LOCATION\" (\n" +
                    "\t\"id\" serial NOT NULL,\n" +
                    "\t\"image_link\" integer,\n" +
                    "\t\"parent_location\" integer,\n" +
                    "\t\"hasStores\" BOOLEAN NOT NULL DEFAULT 'false',\n" +
                    "\tCONSTRAINT LOCATION_pk PRIMARY KEY (\"id\")\n" +
                    ") WITH (\n" +
                    "  OIDS=FALSE\n" +
                    ");\n" +
                    "\n" +
                    "\n" +
                    "\n" +
                    "CREATE TABLE \"NEAR_LOCATIONS\" (\n" +
                    "\t\"location_one\" integer NOT NULL,\n" +
                    "\t\"location_two\" integer NOT NULL\n" +
                    ") WITH (\n" +
                    "  OIDS=FALSE\n" +
                    ");\n" +
                    "\n" +
                    "\n" +
                    "\n" +
                    "CREATE TABLE \"POSSIBLE_LOCATIONS\" (\n" +
                    "\t\"mob_id\" integer NOT NULL,\n" +
                    "\t\"location_id\" integer NOT NULL\n" +
                    ") WITH (\n" +
                    "  OIDS=FALSE\n" +
                    ");\n" +
                    "\n" +
                    "\n" +
                    "\n" +
                    "CREATE TABLE \"STORE\" (\n" +
                    "\t\"id\" serial NOT NULL,\n" +
                    "\t\"image_link\" integer NOT NULL,\n" +
                    "\tCONSTRAINT STORE_pk PRIMARY KEY (\"id\")\n" +
                    ") WITH (\n" +
                    "  OIDS=FALSE\n" +
                    ");\n" +
                    "\n" +
                    "\n" +
                    "\n" +
                    "CREATE TABLE \"STORE_LOCATION\" (\n" +
                    "\t\"store_id\" integer NOT NULL,\n" +
                    "\t\"location_id\" integer NOT NULL\n" +
                    ") WITH (\n" +
                    "  OIDS=FALSE\n" +
                    ");\n" +
                    "\n" +
                    "\n" +
                    "\n" +
                    "CREATE TABLE \"FILE_LINK\" (\n" +
                    "\t\"id\" serial NOT NULL,\n" +
                    "\t\"location\" varchar(100) NOT NULL,\n" +
                    "\tCONSTRAINT FILE_LINK_pk PRIMARY KEY (\"id\")\n" +
                    ") WITH (\n" +
                    "  OIDS=FALSE\n" +
                    ");\n" +
                    "\n" +
                    "\n" +
                    "\n" +
                    "ALTER TABLE \"PLAYER\" ADD CONSTRAINT \"PLAYER_fk0\" FOREIGN KEY (\"current_location\") REFERENCES \"LOCATION\"(\"id\");\n" +
                    "\n" +
                    "ALTER TABLE \"WEAPON\" ADD CONSTRAINT \"WEAPON_fk0\" FOREIGN KEY (\"image_link\") REFERENCES \"FILE_LINK\"(\"id\");\n" +
                    "\n" +
                    "ALTER TABLE \"ARMOR\" ADD CONSTRAINT \"ARMOR_fk0\" FOREIGN KEY (\"image_link\") REFERENCES \"FILE_LINK\"(\"id\");\n" +
                    "\n" +
                    "ALTER TABLE \"TRANSPORT\" ADD CONSTRAINT \"TRANSPORT_fk0\" FOREIGN KEY (\"image_link\") REFERENCES \"FILE_LINK\"(\"id\");\n" +
                    "\n" +
                    "ALTER TABLE \"ITEM\" ADD CONSTRAINT \"ITEM_fk0\" FOREIGN KEY (\"image_link\") REFERENCES \"FILE_LINK\"(\"id\");\n" +
                    "\n" +
                    "ALTER TABLE \"BACKPACK\" ADD CONSTRAINT \"BACKPACK_fk0\" FOREIGN KEY (\"user_id\") REFERENCES \"PLAYER\"(\"id\");\n" +
                    "\n" +
                    "ALTER TABLE \"MOB\" ADD CONSTRAINT \"MOB_fk0\" FOREIGN KEY (\"image_link\") REFERENCES \"FILE_LINK\"(\"id\");\n" +
                    "\n" +
                    "ALTER TABLE \"NPC\" ADD CONSTRAINT \"NPC_fk0\" FOREIGN KEY (\"home_location\") REFERENCES \"LOCATION\"(\"id\");\n" +
                    "ALTER TABLE \"NPC\" ADD CONSTRAINT \"NPC_fk1\" FOREIGN KEY (\"image_link\") REFERENCES \"FILE_LINK\"(\"id\");\n" +
                    "\n" +
                    "ALTER TABLE \"LOCATION\" ADD CONSTRAINT \"LOCATION_fk0\" FOREIGN KEY (\"image_link\") REFERENCES \"FILE_LINK\"(\"id\");\n" +
                    "ALTER TABLE \"LOCATION\" ADD CONSTRAINT \"LOCATION_fk1\" FOREIGN KEY (\"parent_location\") REFERENCES \"LOCATION\"(\"id\");\n" +
                    "\n" +
                    "ALTER TABLE \"NEAR_LOCATIONS\" ADD CONSTRAINT \"NEAR_LOCATIONS_fk0\" FOREIGN KEY (\"location_one\") REFERENCES \"LOCATION\"(\"id\");\n" +
                    "ALTER TABLE \"NEAR_LOCATIONS\" ADD CONSTRAINT \"NEAR_LOCATIONS_fk1\" FOREIGN KEY (\"location_two\") REFERENCES \"LOCATION\"(\"id\");\n" +
                    "\n" +
                    "ALTER TABLE \"POSSIBLE_LOCATIONS\" ADD CONSTRAINT \"POSSIBLE_LOCATIONS_fk0\" FOREIGN KEY (\"mob_id\") REFERENCES \"MOB\"(\"id\");\n" +
                    "ALTER TABLE \"POSSIBLE_LOCATIONS\" ADD CONSTRAINT \"POSSIBLE_LOCATIONS_fk1\" FOREIGN KEY (\"location_id\") REFERENCES \"LOCATION\"(\"id\");\n" +
                    "\n" +
                    "ALTER TABLE \"STORE\" ADD CONSTRAINT \"STORE_fk0\" FOREIGN KEY (\"image_link\") REFERENCES \"FILE_LINK\"(\"id\");\n" +
                    "\n" +
                    "ALTER TABLE \"STORE_LOCATION\" ADD CONSTRAINT \"STORE_LOCATION_fk0\" FOREIGN KEY (\"store_id\") REFERENCES \"STORE\"(\"id\");\n" +
                    "ALTER TABLE \"STORE_LOCATION\" ADD CONSTRAINT \"STORE_LOCATION_fk1\" FOREIGN KEY (\"location_id\") REFERENCES \"LOCATION\"(\"id\");\n" +
                    "\n";
            /*String dml = "CREATE TABLE IF NOT EXISTS BOT_USER (\n"
                    + " id integer PRIMARY KEY,\n"
                    + " name text NOT NULL\n"
                    + ");";
            statement.execute(dml);
            dml = "CREATE TABLE IF NOT EXISTS WEAPON ("
                    + " id integer PRIMARY KEY,\n"
                    + " type text NOT NULL,\n"
                    + " damage integer,\n"
                    + " price integer\n"
                    + ");";*/
            statement.execute(dml);
            connection.commit();
            BotResponseHandler.sendMessageToAdmin("Database created");
        } catch (SQLException e) {
            e.printStackTrace();
            BotResponseHandler.sendMessageToAdmin(e.getMessage());
        } catch (Exception e) {
            System.out.println("Main exception");
            e.printStackTrace();
            BotResponseHandler.sendMessageToAdmin(e.getMessage());
        }
    }

    public static void dropDatabase() {
        try (Connection connection = getConnection()) {
            if (connection == null) return;
            Statement statement = connection.createStatement();
            connection.setAutoCommit(false);
            String dml = "ALTER TABLE \"PLAYER\" DROP CONSTRAINT IF EXISTS \"PLAYER_fk0\";\n" +
                    "\n" +
                    "ALTER TABLE \"WEAPON\" DROP CONSTRAINT IF EXISTS \"WEAPON_fk0\";\n" +
                    "\n" +
                    "ALTER TABLE \"ARMOR\" DROP CONSTRAINT IF EXISTS \"ARMOR_fk0\";\n" +
                    "\n" +
                    "ALTER TABLE \"TRANSPORT\" DROP CONSTRAINT IF EXISTS \"TRANSPORT_fk0\";\n" +
                    "\n" +
                    "ALTER TABLE \"ITEM\" DROP CONSTRAINT IF EXISTS \"ITEM_fk0\";\n" +
                    "\n" +
                    "ALTER TABLE \"BACKPACK\" DROP CONSTRAINT IF EXISTS \"BACKPACK_fk0\";\n" +
                    "\n" +
                    "ALTER TABLE \"MOB\" DROP CONSTRAINT IF EXISTS \"MOB_fk0\";\n" +
                    "\n" +
                    "ALTER TABLE \"NPC\" DROP CONSTRAINT IF EXISTS \"NPC_fk0\";\n" +
                    "\n" +
                    "ALTER TABLE \"NPC\" DROP CONSTRAINT IF EXISTS \"NPC_fk1\";\n" +
                    "\n" +
                    "ALTER TABLE \"LOCATION\" DROP CONSTRAINT IF EXISTS \"LOCATION_fk0\";\n" +
                    "\n" +
                    "ALTER TABLE \"LOCATION\" DROP CONSTRAINT IF EXISTS \"LOCATION_fk1\";\n" +
                    "\n" +
                    "ALTER TABLE \"NEAR_LOCATIONS\" DROP CONSTRAINT IF EXISTS \"NEAR_LOCATIONS_fk0\";\n" +
                    "\n" +
                    "ALTER TABLE \"NEAR_LOCATIONS\" DROP CONSTRAINT IF EXISTS \"NEAR_LOCATIONS_fk1\";\n" +
                    "\n" +
                    "ALTER TABLE \"POSSIBLE_LOCATIONS\" DROP CONSTRAINT IF EXISTS \"POSSIBLE_LOCATIONS_fk0\";\n" +
                    "\n" +
                    "ALTER TABLE \"POSSIBLE_LOCATIONS\" DROP CONSTRAINT IF EXISTS \"POSSIBLE_LOCATIONS_fk1\";\n" +
                    "\n" +
                    "ALTER TABLE \"STORE\" DROP CONSTRAINT IF EXISTS \"STORE_fk0\";\n" +
                    "\n" +
                    "ALTER TABLE \"STORE_LOCATION\" DROP CONSTRAINT IF EXISTS \"STORE_LOCATION_fk0\";\n" +
                    "\n" +
                    "ALTER TABLE \"STORE_LOCATION\" DROP CONSTRAINT IF EXISTS \"STORE_LOCATION_fk1\";\n" +
                    "\n" +
                    "DROP TABLE IF EXISTS \"PLAYER\";\n" +
                    "\n" +
                    "DROP TABLE IF EXISTS \"WEAPON\";\n" +
                    "\n" +
                    "DROP TABLE IF EXISTS \"ARMOR\";\n" +
                    "\n" +
                    "DROP TABLE IF EXISTS \"TRANSPORT\";\n" +
                    "\n" +
                    "DROP TABLE IF EXISTS \"ITEM\";\n" +
                    "\n" +
                    "DROP TABLE IF EXISTS \"BACKPACK\";\n" +
                    "\n" +
                    "DROP TABLE IF EXISTS \"MOB\";\n" +
                    "\n" +
                    "DROP TABLE IF EXISTS \"NPC\";\n" +
                    "\n" +
                    "DROP TABLE IF EXISTS \"LOCATION\";\n" +
                    "\n" +
                    "DROP TABLE IF EXISTS \"NEAR_LOCATIONS\";\n" +
                    "\n" +
                    "DROP TABLE IF EXISTS \"POSSIBLE_LOCATIONS\";\n" +
                    "\n" +
                    "DROP TABLE IF EXISTS \"STORE\";\n" +
                    "\n" +
                    "DROP TABLE IF EXISTS \"STORE_LOCATION\";\n" +
                    "\n" +
                    "DROP TABLE IF EXISTS \"FILE_LINK\";\n";
            /*String dml = "DROP TABLE BOT_USER;";
            statement.execute(dml);
            dml = "DROP TABLE WEAPON;";*/
            statement.execute(dml);
            connection.commit();
            BotResponseHandler.sendMessageToAdmin("Database destroyed");
        } catch (SQLException e) {
            e.printStackTrace();
            BotResponseHandler.sendMessageToAdmin(e.getMessage());
        } catch (Exception e) {
            System.out.println("Main exception");
            e.printStackTrace();
            BotResponseHandler.sendMessageToAdmin(e.getMessage());
        }
    }

}
