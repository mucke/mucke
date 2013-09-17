/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mucke.credibility;

import mucke.concept.model.Document;
import mucke.plugin.PluginManager;

import org.apache.log4j.Logger;


/**
 *
 * @author Alexandra
 */
public class Main {
    
    static Logger logger = Logger.getLogger(Main.class);
    
    public static void main(String[] args) {
       CredibilityManager manager = new CredibilityManager();
       
       // two users for testing
       User searcher = new User("bob123");
       User producer = new User("sally999");
       
       // document for testing
       Document document = new Document();
       
       // test subjective credibility
       logger.info("User crediblity between searcher: " + searcher.getId() + 
	       " and producer: " + producer.getId() + " is: " + 
	       manager.getUserCredibility(searcher, producer));
       
    // test subjective credibility
       logger.info("Content crediblity for Document" + document.toString() + " is: " + 
	       manager.getContentCredibility(document));
       
       
       
    }
}
