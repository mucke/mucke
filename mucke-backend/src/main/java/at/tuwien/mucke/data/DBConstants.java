package at.tuwien.mucke.data;

/**
 * Maps Java constants to naming conventions in configuration files
 */
public class DBConstants {

    /**
     * Prefix for all MUCKE tables
     */
    public static final String TABLE_PREFIX = "mucke_";

    //
    // document index table
    //

    /**
     * Table name for document index
     */
    public static final String DOCINDEX_TABLE_NAME = TABLE_PREFIX + "docindex";

    /**
     * document index docid field
     */
    public static final String DOCINDEX_DOCID = "docid";
    /**
     * document index facetid field
     */
    public static final String DOCINDEX_FACETID = "facetid";
    /**
     * document index facetindex field where the facet is stored
     */
    public static final String DOCINDEX_FACETNAME = "facetname";
    /**
     * document index facettype field
     */
    public static final String DOCINDEX_FACETTYPE = "facettype";

    //
    // credibilty index table
    //

    /**
     * Table name for document index
     */
    public static final String CREDINDEX_TABLE_NAME = TABLE_PREFIX + "credindex";

    /**
     * document index docid field
     */
    public static final String CREDINDEX_USERID = "userid";
    /**
     * document index facetid field
     */
    public static final String CREDINDEX_SCORE = "score";

    //
    // evaluation table
    //

    /**
     * Table name for main evaluation table
     */
    public static final String EVAL_TABLE_NAME = TABLE_PREFIX + "evaluation";


}