package mucke.credibility;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import mucke.config.ConfigConstants;
import mucke.config.ConfigurationManager;
import mucke.data.DBConstants;
import mucke.data.DBManager;
import mucke.documentmodel.Document;
import mucke.query.Query;
import mucke.query.QueryCollectionReader;
import mucke.query.QueryReader;

/**
 * Determines subjective and objective Credibility scores.
 * 
 * @author Ralf Bierig
 */
public class CredibilityManager {

    static Logger logger = Logger.getLogger(DBManager.class);
    
    private ConfigurationManager configManager;
    
    /** Constructor */
    public CredibilityManager(ConfigurationManager configManager){
	this.configManager = configManager;
    }
    
    /** Prepare a single query (as provided by the user interface) into a Query object with the given reader
     * @param query
     * @param reader
     * @return Query
     */
    public Query prepare(String query, QueryReader reader) {
	return reader.prepare(query);
    }

    /** Prepare a credibility collection (as provided by an IR collection) into a list of User objects.
     * The credibility collection file is defined by configuration via <see>ConfigConstants.CRED_COLLECTION_FILE</see>.
     * The credibility collection reader is defined by configuration via <see>ConfigConstants.CRED_COLLECTION_READER</see>.
     * @return List<User>
     */
    public List<User> prepareCollection() {
	
	// extract query reader from configuration
	String credibilityReaderValue = configManager.getProperty(ConfigConstants.CRED_COLLECTION_READER);
	CredibilityCollectionReader credibilityReader = (CredibilityCollectionReader)configManager.getQueryReaderClass(credibilityReaderValue);
	
	return credibilityReader.prepare();
    }
    
    
    
    /** Adds a user to the credibility index */
    public void addUser(User user) {
	
	// index user
	configManager.getDbManager().insertUserCredibilty(user.getId(), user.getCredibilityScore());
	
    }

    public User getUser(String id) {
	// TODO: Extract user from DB index by id
	return null;
    }

    /** Determines subjective credibility of the given user */
    public double getUserCredibility(User user) {
	return 1.0;
    }

    /** Determines subjective credibility of the content-producing user */
    // public double getUserCredibility(User consumer, User producer){
    // return 1.0; // dummy return value
    // }

    /** Determines the objective credibility of the content based on content features */
    public double getContentCredibility(Document document) {
	return 1.0; // dummy return value
    }

}