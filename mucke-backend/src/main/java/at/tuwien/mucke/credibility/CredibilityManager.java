package at.tuwien.mucke.credibility;

import at.tuwien.mucke.config.ConfigConstants;
import at.tuwien.mucke.config.ConfigurationManager;
import at.tuwien.mucke.data.DBManager;
import at.tuwien.mucke.documentmodel.Document;
import at.tuwien.mucke.query.Query;
import at.tuwien.mucke.query.QueryReader;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * Determines subjective and objective Credibility scores.
 *
 * @author Ralf Bierig
 */
public class CredibilityManager {

    static Logger logger = Logger.getLogger(DBManager.class);

    private ConfigurationManager configManager;

    /**
     * Constructor
     */
    public CredibilityManager(ConfigurationManager configManager) {
        this.configManager = configManager;
    }

    /**
     * Prepare a single query (as provided by the user interface) into a Query object with the given reader
     *
     * @param query
     * @param reader
     * @return Query
     */
    public Query prepare(String query, QueryReader reader) {
        return reader.prepare(query);
    }

    /**
     * Prepare a credibility collection (as provided by an IR collection) into a list of User objects.
     * The credibility collection file is defined by configuration via <see>ConfigConstants.CRED_COLLECTION_FILE</see>.
     * The credibility collection reader is defined by configuration via <see>ConfigConstants.CRED_COLLECTION_READER</see>.
     *
     * @return List<User>
     */
    public List<User> prepareCollection() {

        // extract query reader from configuration
        String credibilityReaderValue = configManager.getProperty(ConfigConstants.CRED_COLLECTION_READER);
        CredibilityCollectionReader credibilityReader = (CredibilityCollectionReader) configManager.getQueryReaderClass(credibilityReaderValue);

        return credibilityReader.prepare();
    }

    /**
     * Adds a user to the credibility index
     *
     * @param User user
     */
    public void addUser(User user) {

        // index user
        configManager.getDbManager().insertUserCredibilty(user.getId(), user.getCredibilityScore());

    }

    /**
     * Extracts user based on userId
     *
     * @param userId User id
     * @return User The user with the given id
     */
    public User getUser(String userId) {

        // select user
        return configManager.getDbManager().selectUser(userId);

    }

    /** Determines subjective credibility of the content-producing user */
    // public double getUserCredibility(User consumer, User producer){
    // return 1.0; // dummy return value
    // }

    /**
     * Determines the objective credibility of the content based on content features
     */
    public double getContentCredibility(Document document) {
        return 1.0; // dummy return value
    }

}