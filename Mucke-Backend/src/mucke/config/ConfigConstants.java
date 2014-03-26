package mucke.config;

/** Maps Java constants to naming conventions in configuration files */
public class ConfigConstants {

    /** Run configuration filenames */
    public final static String RUN_PROPERTIES = "run.properties";

    /** SQL driver constant */
    public final static String DRIVER = "driver";
    /** MySQL super user name */
    public final static String USER = "user";
    /** MySQL super user password */
    public final static String PASS = "pass";
    
    
    /** Plugin manager class */
    public final static String PLUGIN_CLASS = "class";
    
    
    //
    // Credibility package
    //
    
    /** Defines the location of the query collection*/
    public final static String CRED_COLLECTION_FILE = "credibilitycollection.file";
    
    /** Defines the location of the query collection*/
    public final static String CRED_COLLECTION_READER = "credibilitycollection.reader";
    
    
    
    //
    // Query package
    //
    
    /** Defines the location of the query collection*/
    public final static String QUERY_COLLECTION_FILE = "querycollection.file";
    
    /** Defines the location of the query collection*/
    public final static String QUERY_COLLECTION_READER = "querycollection.reader";
    
    /** Defines the query collection signature that identifies the element that distinguishes one query 
     * in a collection of queries. The signature consists of two parts, the first part defines what language 
     * is used to describe the signature (XPATH or REGEX) and the second part is the signature itself. */
    public final static String QUERY_COLLECTION_SIG = "querycollection.signature";
    
    /** Defines the signature of the query id. The first part defines the language that is used to describe 
     * the signature (XPATH or REGEX), second the signature itself.*/
    public final static String QUERY_ID = "query.id";
    
    /** Defines the names of the query facets that are part of every query. */
    public final static String QUERY_FACETS = "query.facets";
        
    
    //
    // Search package
    //
    
    /** Defines the location of the query collection*/
    public final static String SEARCH_FACETSEARCHERS = "search.facetsearchers";
    
    
    //
    // Index package
    //
    
    /** Defines the location of the content that is parsed for the document index */
    public final static String DOCINDEX_CONTENTFOLDER = "docindex.contentfolder";
    
    /** Document id signature: Possible Values are FILENAME, an xPath or a Regular Expression */
    public final static String DOCINDEX_DOCID = "docindex.docid";
    
    /** Determines the format of facet signatures: XPATH or REGEX */
    public final static String DOCINDEX_FACET_SIG_FORMAT = "docindex.facetsignatureformat";
    
    /** Defines which facets are used by the document indexer */
    public final static String DOCINDEX_FACETS = "docindex.facets";
    
        
    /** ImageFacetIndexer AutoColorCorrelogram feature name */
    public final static String IMAGEFACET_FEATURE_AUTOCOLORCORRELOGRAM = "AutoColorCorrelogram";
    /** ImageFacetIndexer CEDD feature name */
    public final static String IMAGEFACET_FEATURE_CEDD = "CEDD";
    /** ImageFacetIndexer CEDD feature name */
    public final static String IMAGEFACET_FEATURE_COLORHIST = "ColorHistogram";
    /** ImageFacetIndexer CEDD feature name */
    public final static String IMAGEFACET_FEATURE_COLOR = "ColorLayout";
    /** ImageFacetIndexer CEDD feature name */
    public final static String IMAGEFACET_FEATURE_EDGEHIST = "EdgeHistogram";
    /** ImageFacetIndexer CEDD feature name */
    public final static String IMAGEFACET_FEATURE_FCTH = "FCTH";
    /** ImageFacetIndexer CEDD feature name */
    public final static String IMAGEFACET_FEATURE_FULL = "Full";
    /** ImageFacetIndexer CEDD feature name */
    public final static String IMAGEFACET_FEATURE_GABOR = "Gabor";
    /** ImageFacetIndexer CEDD feature name */
    public final static String IMAGEFACET_FEATURE_JCD = "JCD";
    /** ImageFacetIndexer CEDD feature name */
    public final static String IMAGEFACET_FEATURE_JOINTHIST = "JointHistogram";
    /** ImageFacetIndexer CEDD feature name */
    public final static String IMAGEFACET_FEATURE_JPEG = "JpegCoefficientHistogram";
    /** ImageFacetIndexer xxx feature name */
    public final static String IMAGEFACET_FEATURE_OPPONENTHIST = "OpponentHistogram";
    /** ImageFacetIndexer Scalable feature name that creates a fast (byte[] based) version of the MPEG-7 ColorLayout. */
    public final static String IMAGEFACET_FEATURE_SCALABLECOLOR = "Scalable";
    /** ImageFacetIndexer Tamura feature name that creates three Tamura features*/
    public final static String IMAGEFACET_FEATURE_TAMURA = "Tamura";
    
}