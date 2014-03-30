package at.tuwien.mucke.data;

import at.tuwien.mucke.config.ConfigConstants;
import at.tuwien.mucke.config.ConfigurationManager;
import at.tuwien.mucke.credibility.User;
import at.tuwien.mucke.documentmodel.Document;
import java.sql.Connection;
import org.apache.log4j.Logger;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * Handles all reads and writes to the system database (e.g. document index lookup table and evaluation tables.
 */
public class DBManager {

    static Logger logger = Logger.getLogger(DBManager.class);

    Connection connection = null;

    private ConfigurationManager configManager = null;

    /**
     * Initializes the DatabaseManager and links it with the configuration
     */
    public DBManager(ConfigurationManager configManager) {
        this.configManager = configManager;
    }

    /**
     * Creates the database if it does not yet exist. Uses connection settings from secondary.properties
     */
    public void init() {

        // create database
        try {
            logger.info("Creating tables...");
            this.createTables();
        } catch (Exception e) {
            logger.error("Error creating database!");
        }
    }

    /**
     * Provides access to the system database based on the connection details of the configuration.
     *
     * @return Connection Connection to the system database
     */
    public Connection getDBConnection() {

        // only create a new connection the first time
        if (this.connection != null) {

            return this.connection;

        } else {

            try {

                Class.forName("com.mysql.jdbc.Driver");

            } catch (ClassNotFoundException e) {

                logger.error("JDBC driver not found!");
                e.printStackTrace();
            }

            try {

                connection = DriverManager.getConnection(configManager.getProperty(ConfigConstants.DRIVER),
                        System.getProperty(ConfigConstants.USER), System.getProperty(ConfigConstants.PASS));

            } catch (SQLException e) {

                logger.error("Connection Failed! Is DB running? Has database and database user been created according to the configuration?");

            }

            if (connection == null) {
                logger.debug("Failed to connect to database!");
            }

            return connection;
        }

    }

    /**
     * Creates the document index table. This table provides a lookup facility for all facets that are part of a document (as defined in
     * the document model).
     */
    public void createTables() {

        logger.info("Now creating tables!!");

        // document index table
        String sqlDoc = "CREATE TABLE IF NOT EXISTS `" + DBConstants.DOCINDEX_TABLE_NAME +
                "` (" + "`" + DBConstants.DOCINDEX_DOCID + "` varchar(20), " +
                "`" + DBConstants.DOCINDEX_FACETID + "` varchar(20), " +
                "`" + DBConstants.DOCINDEX_FACETNAME + "` varchar(20), " +
                "`" + DBConstants.DOCINDEX_FACETTYPE + "` varchar(20), PRIMARY KEY (`" + DBConstants.DOCINDEX_DOCID + "`, `" +
                DBConstants.DOCINDEX_FACETID + "`, `" + DBConstants.DOCINDEX_FACETNAME + "`)" + ") " +
                "ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;";
        query(sqlDoc);

        // crediblity index table
        String sqlCred = "CREATE TABLE IF NOT EXISTS `" + DBConstants.CREDINDEX_TABLE_NAME +
                "` (" + "`" + DBConstants.CREDINDEX_USERID + "` varchar(20), " +
                "`" + DBConstants.CREDINDEX_SCORE + "` float, PRIMARY KEY (`" + DBConstants.CREDINDEX_USERID + "`)" + ") " +
                "ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;";
        query(sqlCred);
    }

    /**
     * Truncates the given table name
     *
     * @param tableName SQL table that must exist in currently active Database.
     */
    public void emptyTable(String tableName) {

        String sql = "TRUNCATE TABLE `" + tableName + "`;";
        query(sql);
    }

    /**
     * Inserts a document into the document index table
     *
     * @param docId     The document identifier
     * @param facetId   The facet identifier
     * @param facetType The facet type
     */
    public void insertDocument(String docId, String facetId, String facetIndex, String facetType) {

        String sql = "INSERT INTO `" + DBConstants.DOCINDEX_TABLE_NAME +
                "` (`" + DBConstants.DOCINDEX_DOCID + "`, `" + DBConstants.DOCINDEX_FACETID + "`, `" +
                DBConstants.DOCINDEX_FACETNAME + "`, `" + DBConstants.DOCINDEX_FACETTYPE + "`) " +
                "VALUES ('" + docId + "', '" + facetId + "', '" + facetIndex + "', '" + facetType + "')";
        logger.info("Indexing docid: " + docId + ", facetid: " + facetId);
        query(sql);
    }

    /**
     * Inserts a document into the document index table
     *
     * @param userId The user identifier
     * @param credibiliyScore The crediblity score
     */
    public void insertUserCredibilty(String userId, double credibiliyScore) {

        String sql = "INSERT INTO `" + DBConstants.CREDINDEX_TABLE_NAME +
                "` (`" + DBConstants.CREDINDEX_USERID + "`, `" + DBConstants.CREDINDEX_SCORE + "`) " +
                "VALUES ('" + userId + "', '" + credibiliyScore + "')";
        logger.info("Indexing userid: " + userId + " with crediblityScore: " + credibiliyScore);
        query(sql);
    }

    /**
     * Selects all documents for the given facetid
     *
     * @param facetId The facet identifier
     * @return List of documents that contain the given facet
     */
    public List<Document> selectDocument(String facetId) {

        // TODO --- IN PROGRESS
        String sql = "SELECT * FROM `" + DBConstants.DOCINDEX_TABLE_NAME;
        logger.info("Selecting document for facetId: " + facetId);
        query(sql);

        // TODO!!!!
        return null;

    }

    /**
     * Selects a user based on the given userid
     *
     * @param userId The user id
     * @return User with the given id
     */
    public User selectUser(String userId) {

        User user = null;

        // clean userId
        userId = userId.trim();

        String sql = "SELECT * FROM `" + DBConstants.CREDINDEX_TABLE_NAME + "` WHERE userid = '" + userId + "'";
        logger.info("Selecting user with this statement: " + sql);
        ResultSet results = queryResult(sql);

        // only care for the first, in fact only one should be in the set
        try {

            // check if result set is empty
            if (!results.next()) {
                logger.warn("No user with userId '" + userId + "'. Returning null.");
                return null;
            }

            // set cursor to first result
            results.first();
            // extract information
            String useridResult = results.getString(DBConstants.CREDINDEX_USERID);
            float scoreResult = results.getFloat(DBConstants.CREDINDEX_SCORE);
            // create User object
            user = new User(useridResult, scoreResult);

        } catch (SQLException e) {
            logger.error("Error while selecting user from credibilty index: " + e.getMessage());
        }

        return user;

    }


    /**
     * Executes the given SQL query for a list of results
     */
    private ResultSet queryResult(String query) {

        Connection connection = null;
        Statement statement = null;

        try {

            // get connected
            connection = this.getDBConnection();

            // create evaluation table
            statement = connection.createStatement();
            ResultSet results = statement.executeQuery(query);
            return results;

        } catch (SQLException e) {

            logger.error("SQLError: " + e.getMessage());

        }

        // something went wrong
        return null;
    }

    /**
     * Executes the given SQL query
     */
    private boolean query(String query) {

        //logger.debug("Trying to execute query: " + query);

        Connection connection = null;
        Statement statement = null;

        try {

            // get connected
            connection = this.getDBConnection();

            // create evaluation table
            statement = connection.createStatement();
            statement.execute(query);
            return true;

        } catch (SQLException e) {

            logger.error("SQLError: " + e.getMessage());

        }

        return false;
    }

}