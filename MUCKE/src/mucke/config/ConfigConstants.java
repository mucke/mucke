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
    
    
    /** Defines the location of the content that is parsed for the document index */
    public final static String DOCINDEX_CONTENTFOLDER = "docindex.contentfolder";
    
    /** Document id signature: Possible Values are FILENAME, an xPath or a Regular Expression */
    public final static String DOCINDEX_DOCID = "docindex.docid";
    
    /** Determines the format of facet signatures: XPATH or REGEX */
    public final static String DOCINDEX_FACET_SIG_FORMAT = "docindex.facetsignatureformat";
    
    /** Defines which facets are used by the document indexer */
    public final static String DOCINDEX_FACETS = "docindex.facets";
    
}