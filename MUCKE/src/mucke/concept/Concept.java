package mucke.concept;

import java.net.URI;

/** 
 * Represents an abstract concept that forms the basis for indexing and searching within the system 
 */
public class Concept {

    /** URL field uniquely defining a concept */
    private URI id;

    /** 
     * Constructor
     * @param id The URI of the concept
     */
    public Concept(URI id) {
	this.id = id;
    }
    
    /**
     * @return the id of the concept
     */
    public final URI getId() {
	return id;
    }  

}