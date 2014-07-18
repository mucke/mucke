package at.tuwien.mucke.concept;

/**
 * Represents an abstract concept that forms the basis for indexing and searching within the system
 */
public class Concept {

    /**
     * URL field uniquely defining a concept
     */
    private String id;

    /**
    * Model state provided by a visual classifier that represents the state of the visual concept
    */
    private ClassifierModel modelClassifier;

    /**
     * Constructor of Concept that does not store a classifier model within
     *
     * @param id The URI of the concept
     */
    public Concept(String id) {
        this(id, null);
    }

    /**
     * Constructor of Concept that does store a classifier model within
     *
     * @param id The URI of the concept
     */
    public Concept(String id, ClassifierModel modelClassifier) {
        this.id = id;
        this.modelClassifier = modelClassifier;
    }

    /**
     * @return the id of the concept
     */
    public final String getId() {
        return id;
    }

    /**
     * @return the modelClassifier
     */
    public ClassifierModel getModelClassifier() {
        return this.modelClassifier;
    }

}