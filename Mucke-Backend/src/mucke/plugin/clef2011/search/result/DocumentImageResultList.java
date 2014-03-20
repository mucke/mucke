package mucke.plugin.clef2011.search.result;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Manages a list of results for one topic with an option for sorting
 * and logging.
 * 
 * @author Ralf Bierig
 *
 */
public class DocumentImageResultList {
    
    private List<DocumentImageResult> results;
    private String topicId = "UNASSIGNED";
    
    
    /** Constructor */
    public DocumentImageResultList(){
	this.results = new ArrayList<DocumentImageResult>();
    }
    
    
    /** Adds a Result */
    public void add(DocumentImageResult result){
	results.add(result);
    }
    
    /** Sorts the result list in descending order*/
    public void sort(){
	Collections.sort(results, new ResultListComparator());
    }
    
    
    /** Sorts result objects in descending order based on imageScore */
    private class ResultListComparator implements Comparator {
	
	@Override
	public boolean equals(Object obj) { 
	    return (obj instanceof ResultListComparator); 
	}

	@Override
	public int compare(Object arg0, Object arg1) {
	    
	    DocumentImageResult r1 = (DocumentImageResult)arg0;
	    DocumentImageResult r2 = (DocumentImageResult)arg1;
	    
	    if (r1.getImageScore() < r2.getImageScore()){
		return 1;
	    } else if (r1.getImageScore() > r2.getImageScore()){
		return -1;
	    }
	    return 0;
	}
    }

}
