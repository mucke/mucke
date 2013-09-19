package mucke.credibility;

import mucke.concept.model.Document;


/** 
 * Determines subjective and objective Credibility scores. 
 * 
 * @author Ralf Bierig
 */
public class CredibilityManager {

    
    /** Determines subjective credibility of the content-producing user */
    public double getUserCredibility(User consumer, User producer){
	return 1.0;	// dummy return value
    }
    
    /** Determines the objective credibility of the content based on content features */
    public double getContentCredibility(Document document){
	return 1.0;	// dummy return value
    }
    
}
