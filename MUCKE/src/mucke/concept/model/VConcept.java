package mucke.concept.model;

import java.net.URI;
import mucke.concept.Classifier;

/** 
 * Represents a visual concept 
 * 
 * @author Ralf Bierig and Alexandra-Mihaela Siriteanu 
 */
public class VConcept extends Concept {

    private Classifier classifier;
    
    public VConcept(URI id, Classifier classifier) {
	super(id);
	this.classifier = classifier;
    }
    
    public Classifier getClassifier(){
	return this.classifier;
    }

}
