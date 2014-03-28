package at.tuwien.mucke.plugin.prototypetest;

import at.tuwien.mucke.config.ConfigurationManager;
import at.tuwien.mucke.credibility.CredibilityManager;
import at.tuwien.mucke.credibility.User;
import at.tuwien.mucke.documentmodel.Facet;
import at.tuwien.mucke.documentmodel.ImageFacet;
import at.tuwien.mucke.documentmodel.TextFacet;
import at.tuwien.mucke.index.IndexManager;
import at.tuwien.mucke.plugin.PluginManager;
import at.tuwien.mucke.query.Query;
import at.tuwien.mucke.query.QueryManager;
import at.tuwien.mucke.search.Result;
import at.tuwien.mucke.search.SearchManager;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * Runs IR evaluations of the ImageCLEF2011 collection based on the parameterization declared in the properties files. It offers methods for
 * 1) indexing, 2) searching and 3) evaluating results.
 * <p/>
 * The index builds two indexes, one for Wikipedia content and one for metadata. Search is performed first on the documents of the Wiki
 * document collection as defined in the document index and then, for each document, a search is performed on the metadata index. The
 * evaluation generates performance measures based on a gold standard.
 * <p/>
 * Each of the three parts can run independently.
 *
 * @author Ralf Bierig
 */
public class PrototypetestPluginManager extends PluginManager {

    /**
     * Logging facility
     */
    static Logger logger = Logger.getLogger(PrototypetestPluginManager.class);


    /**
     * Constructor
     */
    public PrototypetestPluginManager(ConfigurationManager configManager) {
        super(configManager);
    }


    /**
     * Runs the Test Plugin
     */
    public void run(String propertiesFile) {
        super.run(propertiesFile);

        logger.info("Start");

        // create indices
        IndexManager indexManager = new IndexManager(this.configManager);
        indexManager.index();

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
        logger.info("Creating CrediblityManager...");
        CredibilityManager credManager = new CredibilityManager(configManager);
        List<User> users = credManager.prepareCollection();
        for (User user : users) {
            credManager.addUser(user);
            logger.info("Userid: " + user.getId() + " added...");
        }
        // check for user
        String userId = "62559061@N06";
        User u = credManager.getUser(userId);
        if (u != null) {
            logger.info("User '" + u.getId() + "' has credibitily: " + u.getCredibilityScore());
        } else {
            logger.info("No user with id " + userId);
        }


        /*SearchManager searchManager = new SearchManager(configManager);
        String queryString = "tags:digitalart  tags:fractal tags:apophysis tags:fractals";
        List<Result> results = searchManager.facetSearch(queryString, null);

        if (results.size() == 0){
            logger.info("No results for query '" + queryString);
        }

        for (Result result : results){
            logger.info("score: " + result.getScore() + "    id: " + result.getId() + "    title: " + result.getTitle() + "     userid: " + result.getUserId());
        }

        logger.info("Done!");*/

    }

}