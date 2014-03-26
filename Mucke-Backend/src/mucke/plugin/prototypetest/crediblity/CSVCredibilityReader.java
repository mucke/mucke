package mucke.plugin.prototypetest.crediblity;

import mucke.config.ConfigurationManager;
import mucke.credibility.CredibilityReader;
import mucke.credibility.User;

import org.apache.log4j.Logger;

/** Implements a Query Reader that reads queries from a file with facets separated by CR (newlines) */
public class CSVCredibilityReader implements CredibilityReader {

    static Logger logger = Logger.getLogger(CSVCredibilityReader.class);

    /** Allow access to configuration parameters */
    private ConfigurationManager configManager;

    /** Constructor */
    public CSVCredibilityReader(ConfigurationManager configManager) {
	this.configManager = configManager;
    }

    @Override
    public User prepare(String credibiltyString) {

	String parts[] = credibiltyString.trim().split("\t");

	if (parts.length != 2) {
	    logger.error("Invalid format. Crediblity file must provide two colomns, one for userid and one for the score. Nothing done.");
	    return null;
	}

	// add part 1-3 as id, query terms, and narrative
	String userid = parts[0].trim();
	double score = Double.parseDouble(parts[1].trim());

	return new User(userid, score);
    }

}