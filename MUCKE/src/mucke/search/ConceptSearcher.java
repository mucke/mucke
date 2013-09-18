package mucke.search;

import java.util.ArrayList;
import java.util.List;

import mucke.concept.model.Concept;

import org.apache.log4j.Logger;

/** Searches for documents based on a list of concepts */
public class ConceptSearcher {

    static Logger logger = Logger.getLogger(ConceptSearcher.class);
    
    /** Searches for documents based on a list of concepts */
    public List<Result> search(List<Concept> concepts){

	// logging
	logger.info("Searching for a list of the following concepts:");
	for (Concept concept : concepts){
	    logger.info("concept: " + concept.getId());
	}
	
	return new ArrayList<Result>();
    }
   
}