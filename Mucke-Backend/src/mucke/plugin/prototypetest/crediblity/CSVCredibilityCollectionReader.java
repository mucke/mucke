package mucke.plugin.prototypetest.crediblity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import mucke.config.ConfigConstants;
import mucke.config.ConfigurationManager;
import mucke.credibility.CredibilityCollectionReader;
import mucke.credibility.User;
import mucke.data.DBConstants;
import mucke.query.Query;
import mucke.query.QueryCollectionReader;
import mucke.util.Util;

import org.apache.log4j.Logger;

/** Test implementation of a a PrepareCollectionStrategy */
public class CSVCredibilityCollectionReader implements CredibilityCollectionReader {

    static Logger logger = Logger.getLogger(CSVCredibilityCollectionReader.class);
    private ConfigurationManager configManager;
    private CSVCredibilityReader credibilityReader;
    
    /** Constructor */
    public CSVCredibilityCollectionReader(ConfigurationManager configManager){
	this.configManager = configManager;
	this.credibilityReader = new CSVCredibilityReader(configManager);
    }
    
    @Override
    public List<User> prepare() {

	// empty credibility index table before building a new one
	configManager.getDbManager().emptyTable(DBConstants.CREDINDEX_TABLE_NAME);
	
	// list of queries
	List<User> users = new ArrayList<User>();
	
	// read query directory
	File file = null;
	try {
	
	    file = new File(configManager.getProperty(ConfigConstants.CRED_COLLECTION_FILE));

	} catch (Exception e){
	    logger.error("Error accessing query collection. Nothing done!");
	    return null;
	}
	
	// do not try to index files that cannot be read
	List<String> usersData = Util.getContents(file, "\n");
	
	int i = 1;
	for (String userData: usersData){
	    // generate single query
	    logger.debug("Reading crediblity of user # " + (i+1) + " ...");
	    CSVCredibilityReader crediblityReader = new CSVCredibilityReader(configManager);
	    if (userData != null && userData.length() > 0){
		User user = crediblityReader.prepare(userData);
		if (user != null){
		    users.add(user);		    
		} else {
		    logger.warn("User was null. Not added.");
		}
	    } else {
		logger.warn("User String was null or empty. Nothing done.");
	    }
	    i++;
	}
	
	return users;
    }	
    
}