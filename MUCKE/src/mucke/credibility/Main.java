package mucke.credibility;

import java.util.ArrayList;

import mucke.documentmodel.Document;
import mucke.documentmodel.Facet;

import org.apache.log4j.Logger;

/**
 * Tests the credibility package
 * 
 * @author Ralf Bierig and Alexandra-Mihaela Siriteanu
 */
public class Main {

    static Logger logger = Logger.getLogger(Main.class);

    /** Tests the credibility package */
    public static void main(String[] args) {
	CredibilityManager manager = new CredibilityManager();

	// two users for testing
	User searcher = new User("bob123");
	User producer = new User("sally999");

	// empty document for testing
	Document document = new Document(new ArrayList<Facet>());

	// test subjective credibility
	logger.info("User crediblity between searcher: " + searcher.getId() + " and producer: " + producer.getId() + " is: "
		+ manager.getUserCredibility(searcher, producer));

	// test subjective credibility
	logger.info("Content crediblity for Document" + document.toString() + " is: " + manager.getContentCredibility(document));
    }

}