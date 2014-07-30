package at.tuwien.mucke.plugin.conceptdemo;

/**
 * Maps Java constants to naming conventions in configuration files
 */
public class ConfigConstants {

    //
    // Plug-in management
    // TODO: Should be put in super-class as this is common to all Plug-ins. 
    //

    /**
     * Manager class for this plugin
     */
    public final static String CLASS = "class";


    //
    // High level Plug-in features
    //

    /**
     * Determines whether the collection should be indexed
     */
    public final static String DO_INDEX = "do.index";

    /**
     * Determines whether the collection should be searched
     */
    public final static String DO_SEARCH = "do.search";

    /**
     * Determines whether the collection should be evaluated
     */
    public final static String DO_EVALUATE = "do.evaluate";


    //
    // Index, Content and Query file locations
    //

    /**
     * wikipedia document directory
     */
    public final static String WIKI_DOC_DIR = "dir.wiki.doc";

    /**
     * wikepedia metadata directory
     */
    public final static String WIKI_META_DIR = "dir.wiki.meta";

    /**
     * wikipedia document index directory
     */
    public final static String WIKI_DOC_INDEXDIR = "dir.wiki.doc.index";

    /**
     * wikipedia metadata index directory
     */
    public final static String WIKI_META_INDEXDIR = "dir.wiki.meta.index";

    /**
     * file location of the topics (query) file
     */
    public final static String TOPICS_FILE = "file.topics";


    //
    // 4) Index Field naming
    // 
    // Note: Changing any field name requires an index re-build
    //

    /**
     * WIKI_DOC_INDEXDIR index field that stores the path to the Wiki document
     */
    public static final String FIELD_WIKIDOC_PATH = "field.wikidoc.path";

    /**
     * WIKI_DOC_INDEXDIR index field that stores the filename of the Wiki document without extension
     */
    public static final String FIELD_WIKIDOC_FILENAME = "field.wikidoc.filename";

    /**
     * WIKI_DOC_INDEXDIR index field that stores all information of the wiki document in a flat, unstructured form
     */
    public static final String FIELD_WIKIDOC_CONTENTS = "field.wikidoc.contents";

    /**
     * WIKI_META_INDEXDIR index field that stores the path to the Wiki document
     */
    public static final String FIELD_WIKIMETA_PATH = "field.wikimeta.path";

    /**
     * WIKI_META_INDEXDIR index field that stores the filename of the Wiki document without extension
     */
    public static final String FIELD_WIKIMETA_FILENAME = "field.wikimeta.filename";

    /**
     * WIKI_META_INDEXDIR index field that stores the ID of the image
     */
    public static final String FIELD_WIKIMETA_IMAGEID = "field.wikimeta.imageid";

    /**
     * WIKI_META_INDEXDIR index field that stores the path to the image
     */
    public static final String FIELD_WIKIMETA_IMAGEPATH = "field.wikimeta.imageid";

    /**
     * WIKI_META_INDEXDIR index field that stores the English document ID
     */
    public static final String FIELD_WIKIMETA_DOCID_EN = "field.wikimeta.docid.en";

    /**
     * WIKI_META_INDEXDIR index field that stores the German document ID
     */
    public static final String FIELD_WIKIMETA_DOCID_DE = "field.wikimeta.docid.de";

    /**
     * WIKI_META_INDEXDIR index field that stores the French document ID
     */
    public static final String FIELD_WIKIMETA_DOCID_FR = "field.wikimeta.docid.fr";

    /**
     * WIKI_META_INDEXDIR index field that stores the English description
     */
    public static final String FIELD_WIKIMETA_DESC_EN = "field.wikimeta.desc.en";

    /**
     * WIKI_META_INDEXDIR index field that stores the English description
     */
    public static final String FIELD_WIKIMETA_DESC_DE = "field.wikimeta.desc_de";

    /**
     * WIKI_META_INDEXDIR index field that stores the French description
     */
    public static final String FIELD_WIKIMETA_DESC_FR = "field.wikimeta.desc_fr";

    /**
     * WIKI_META_INDEXDIR index field that stores the English comment
     */
    public static final String FIELD_WIKIMETA_COMMENT_EN = "field.wikimeta.comment.en";

    /**
     * WIKI_META_INDEXDIR index field that stores the German comment
     */
    public static final String FIELD_WIKIMETA_COMMENT_DE = "field.wikimeta.comment.de";

    /**
     * WIKI_META_INDEXDIR index field that stores the French comment
     */
    public static final String FIELD_WIKIMETA_COMMENT_FR = "field.wikimeta.comment.fr";

    /**
     * WIKI_META_INDEXDIR index field that stores the English image caption
     */
    public static final String FIELD_WIKIMETA_CAPTION_EN = "field.wikimeta.caption.en";

    /**
     * WIKI_META_INDEXDIR index field that stores the German image caption
     */
    public static final String FIELD_WIKIMETA_CAPTION_DE = "field.wikimeta.caption.de";

    /**
     * WIKI_META_INDEXDIR index field that stores the French image caption
     */
    public static final String FIELD_WIKIMETA_CAPTION_FR = "field.wikimeta.caption.fr";

    /**
     * WIKI_META_INDEXDIR index field that stores the general comment
     */
    public static final String FIELD_WIKIMETA_COMMENT = "field.wikimeta.comment";

    /**
     * WIKI_META_INDEXDIR index field that stores all information of the wiki document in a flat, unstructured form
     */
    public static final String FIELD_WIKIMETA_CONTENTS = "field.wikimeta.contents";


    //
    // 6) Search configuration
    //

    /**
     * Fields to use when searching for documents with a topic
     */
    public static final String SEARCH_DOC_FIELDS = "search.fields.wikidoc";

    /**
     * Fields to use when searching images for a topic and a document
     */
    public static final String SEARCH_IMG_FIELDS = "search.fields.wikimeta";

    /**
     * Sets an ID field as a search filter or 'null' if you do not want to filter
     */
    public static final String FILTER_META_FIELD = "search.filter.field.meta";

    /**
     * Length of result list for searched documents from WIKIDOC index
     */
    public static final String RESULTLIST_DOC_LENGTH = "resultlist.doc.length";

    /**
     * Length of result list for searched documents from WIKIMETA index
     */
    public static final String RESULTLIST_META_LENGTH = "resultlist.meta.length";

}