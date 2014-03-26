package mucke.plugin.prototypetest;

import java.util.List;

import mucke.config.ConfigurationManager;
import mucke.credibility.CredibilityManager;
import mucke.credibility.User;
import mucke.documentmodel.Facet;
import mucke.documentmodel.ImageFacet;
import mucke.documentmodel.TextFacet;
import mucke.index.IndexManager;
import mucke.plugin.PluginManager;
import mucke.plugin.clef2011.ConfigConstants;
import mucke.query.Query;
import mucke.query.QueryManager;
import mucke.query.XMLQueryCollectionReader;

import org.apache.log4j.Logger;

/**
 * Runs IR evaluations of the ImageCLEF2011 collection based on the parameterization declared in the properties files. It offers methods for
 * 1) indexing, 2) searching and 3) evaluating results.
 * 
 * The index builds two indexes, one for Wikipedia content and one for metadata. Search is performed first on the documents of the Wiki
 * document collection as defined in the document index and then, for each document, a search is performed on the metadata index. The
 * evaluation generates performance measures based on a gold standard. 
 * 
 * Each of the three parts can run independently.
 * 
 * @author Ralf Bierig
 * 
 **/
public class PrototypetestPluginManager extends PluginManager {

    /** Logging facility */
    static Logger logger = Logger.getLogger(PrototypetestPluginManager.class);
    
    
    /** Constructor */
    public PrototypetestPluginManager(ConfigurationManager configManager) {
	super(configManager);
    }

    
    /** Runs the Test Plugin */
    public void run(String propertiesFile) {
	super.run(propertiesFile);
	
	logger.info("Start");
	
	// create indices
	//IndexManager indexManager = new IndexManager(this.configManager);
	//indexManager.index();

	// creating queries
	//QueryManager queryManager = new QueryManager(this.configManager);
	//List<Query> queries = queryManager.prepareCollection();
	
	// debug output
	/*int i = 1;
	for (Query q : queries){
	    logger.info("Query: " + i);
	    List<Facet> facets = q.getFacets();
	    int j = 1;
	    for (Facet f : facets){
		if (f instanceof TextFacet){
		    logger.info(j +") id: " + f.getId() + " name: " + f.getName() + " type: TEXTFACET content: " + f.getContent());
		} else if (f instanceof ImageFacet){
		    logger.info(j +") id: " + f.getId() + " name: " + f.getName() + " type: IMAGEFACET content: " + f.getContent());
		}
		j++;
	    }
	    i++;
	}*/

	// user credibility
	logger.debug("Creating CrediblityManager...");
	CredibilityManager credManager = new CredibilityManager(configManager);
	List<User> users = credManager.prepareCollection();
	for (User user : users){
	    credManager.addUser(user);	 
	    logger.debug("Userid: " + user.getId() + " added...");
	}
	// check for user
	String userId = "62559061@N06";
	User u = credManager.getUser(userId);
	if (u != null){
	    logger.debug("User '" + u.getId() + "' has credibitily: " + u.getCredibilityScore());
	} else {
	    logger.debug("No user with id " + userId);
	}
	
	
	logger.info("Done!");
	
    }

}