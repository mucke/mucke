package at.tuwien.mucke.plugin.bilkentdemo;

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
    // Classification Paths
    //

    public final static String TRAIN_FOLDER = "train.folder";
    public final static String TEST_FOLDER = "test.folder";
    public final static String MAPPING_FILE = "mappings.file";
    public final static String RESULT_FOLDER = "result.folder";

    public final static String CONCEPT_INDEX_FOLDER = "concept.indexfolder";

}