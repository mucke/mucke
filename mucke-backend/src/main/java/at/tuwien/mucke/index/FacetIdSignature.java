package at.tuwien.mucke.index;


/**
 * A FacetIdSignature defines how a facet it identified for a document and used by the DocumentIndexer.
 * It defines where the document id is found inside the file that is processed by the DocumentIndexer with
 * the use of a signature. A signature can be a XPath or a RegEx expression or it can use one or more
 * predefined variables (e.g. FILENAME that uses the filename as the identifier). The class also defines
 * To what facet (name) is belongs which allows using multiple indices for one facet type (e.g. multiple
 * text indices or multiple image indices). The facet type is groups the facets across indices in categories,
 * e.g. TEXT, TAG, IMAGE in the simplest case but it can extend to an an arbitrarily high amount of types
 * (e.g. GERMAN_TEXT, ENGLISH_TEXT, TAG, COLOR_IMAGE, BW_IMAGE.
 */
public class FacetIdSignature {

    private String name = "";
    private String parsingType = "";
    private String idSignature = "";
    private String type = "";

    /**
     * Constructor
     */
    public FacetIdSignature(String name, String parsingType, String idSignature, String type) {
        this.name = name;
        this.parsingType = parsingType;
        this.idSignature = idSignature;
        this.type = type;
    }

    /**
     * @return the name
     */
    public final String getName() {
        return name;
    }

    /**
     * @return the parsingType
     */
    public final String getParsingType() {
        return parsingType;
    }

    /**
     * @return the idSignature
     */
    public final String getIdSignature() {
        return idSignature;
    }

    /**
     * @return the type
     */
    public final String getType() {
        return type;
    }

}