package mucke.credibility;

import mucke.concept.model.Document;


/** Determines subjective and objective Crediblity scores. */
public class CredibilityManager {

    
    /** Determines subjective credibility of the producing user based on the 
     * relationship between the user consuming content and the user producing 
     * content */
    public static double getUserCredibility(User consumer, User producer){
	return 1.0;
    }
    
    /** Determines the objective credibility of the content */
    public static double getContentCredibility(Document document){
	return 1.0;
    }
    
}
