package mucke.concept;

import java.net.URI;

import mucke.documentmodel.ClassifierModel;

/** 
 * Represents a visual concept 
 * 
 * @author Alexandra-Mihaela Siriteanu 
 */
public class VConcept extends Concept {

    /** Model state provided by a visual classifier that represents the state of the visual concept */
    private ClassifierModel modelClassifier;
    
    public VConcept(URI id, ClassifierModel modelClassifier) {
	super(id);
	this.modelClassifier = modelClassifier;
    }
    
    /**
     * @return the modelClassifier
     */
    public ClassifierModel getModelClassifier(){
	return this.modelClassifier;
    }

}