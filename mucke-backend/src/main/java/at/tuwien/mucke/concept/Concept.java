package at.tuwien.mucke.concept;

import java.io.File;
import java.net.URI;

/**
 * Represents an abstract concept that forms the basis for indexing and searching within the system
 */
public class Concept {


    public final static String CONCEPT_TYPE_TEXT = "TEXT";
    public final static String CONCEPT_TYPE_VISUAL = "VISUAL";

    /**
     * URL field uniquely defining a concept
     */
    private String id;

    /**
     * Defines the type of concept
     */
    private String type;

    /**
    * Model state provided by a visual classifier that represents the state of the visual concept
    */
    private File modelClassifier;

    /**
     * Constructor of Concept that does not store a classifier model within
     *
     * @param id The URI of the concept
     */
    public Concept(String id, String type) {
        this(id, type, null);
    }

    /**
     * Constructor of Concept that does store a classifier model within
     *
     * @param id The URI of the concept
     */
    public Concept(String id, String type, File modelClassifier) {
        this.id = id;
        this.type = type;
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
    public File getModelClassifier() {
        return this.modelClassifier;
    }

}