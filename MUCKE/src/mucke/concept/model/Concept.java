package mucke.concept.model;

import java.net.URI;

/** 
 * Represents an abstract concept that forms the basis for indexing and searching within the system 
 */
public class Concept {

    /** URL field uniquely defining a concept */
    private URI id;

    /** 
     * Constructor
     * @param URI id
     */
    public Concept(URI id) {
	this.id = id;
    }
    
    /**
     * @return the id
     */
    public final URI getId() {
	return id;
    }  

}