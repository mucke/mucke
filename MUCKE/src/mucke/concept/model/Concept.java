package mucke.concept.model;

import java.net.URI;

/** Represents an abstract concept functioning as the basis for indexing and searching within MUCKE */
public class Concept {

    private URI id;

    /**
     * @return the id
     */
    public final URI getId() {
	return id;
    }

    /**
     * Construct a concept
     * 
     * @param URI id
     */
    public Concept(URI id) {
	this.id = id;
    }

}
