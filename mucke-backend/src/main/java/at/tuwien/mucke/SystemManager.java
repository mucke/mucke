package at.tuwien.mucke;

import at.tuwien.mucke.clustering.ClusteringManager;
import at.tuwien.mucke.concept.Concept;
import at.tuwien.mucke.concept.DocumentFacetProcessor;
import at.tuwien.mucke.concept.StandardDocumentFacetProcessor;
import at.tuwien.mucke.config.ConfigurationManager;
import at.tuwien.mucke.config.Run;
import at.tuwien.mucke.documentmodel.TextFacet;
import at.tuwien.mucke.plugin.bilkentdemo.BilkentDemoPluginManager;
import at.tuwien.mucke.search.Result;
import at.tuwien.mucke.search.SearchManager;
import at.tuwien.mucke.util.Util;
import org.apache.log4j.Logger;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This is the Main class that regulates everything the system does at the top level. It reads the configuration file "system.properties"
 * and executes a list of runs (defined by the property 'run.properties'). Each run is configured individually and independently and may work
 * on a different collection. Each runs executes sequentially in its own, separate parametric environment.
 *
 * @author Ralf Bierig
 */
public class SystemManager {

    static Logger logger = Logger.getLogger(SystemManager.class);

    private ConfigurationManager configManager = null;

    /**
     * Constructor
     */
    public SystemManager() {

        logger.info("SystemManager initialization...");
        // initialize the ConfigurationManager upon instantiation
        configManager = new ConfigurationManager();
        logger.info("SystemManager initialized!");

    }

    /**
     * Executes the currently configured state of the system based on the configuration as defined in "system.properties" and its secondary
     * properties file. The secondary properties file may contain multiple runs that are executed in the order of their listing. Results of
     * each run are stored in the central system database.
     */
    public void executeBatchMode() {

        // to calculate indexing time as a performance measure
        Date start = new Date();

        // extract configuration for all runs
        logger.info("Starting batch mode...");
        logger.info("Loading runs...");
        List<String> runConfigFilenames = configManager.getRuns();
        // execute each run
        int i = 0;
        for (String runConfigFilename : runConfigFilenames) {
            i++;
            logger.info("Run # " + (i) + ":" + runConfigFilename);

            // instantiate and execute Run
            Run run = configManager.getRunClass(runConfigFilename);

            if (run != null) {
                logger.info("Starting the Runner '" + run.getClass().getSimpleName().toString() + "' with configuration '"
                        + runConfigFilename + "'");

                run.run(runConfigFilename);
            }
        }
        // time stamping
        Date end = new Date();
        logger.info("Completed " + i + " runs in " + (end.getTime() - start.getTime()) + " milliseconds.");
    }

    /** Used for MUCKE ICMR Demo */
    public List<Result> executeInteractiveMode(String queryString, boolean credibilityEnabled) {

        // extract configuration for all runs
        List<String> runConfigFilenames = configManager.getRuns();
        // execute each run
        int i = 0;
        for (String runConfigFilename : runConfigFilenames) {
            i++;
            logger.info("Run # " + (i) + ":" + runConfigFilename);

            // instantiate and execute Run
            Run run = configManager.getRunClass(runConfigFilename);

        }

        SearchManager searchManager = new SearchManager(configManager);
        List<Result> results = searchManager.facetSearch(queryString, null);

        // debug output
        logger.info("======================== result list =================================");
        for (Result result : results){
            logger.info("result id:" + result.getId() + " title:" + result.getTitle() + " score:" + result.getScore() + " userid:" + result.getUserId());
        }
        logger.info("======================== result list END =================================");


        // result re-ranking based on crediblity information
        if (credibilityEnabled == true){

            logger.info("Credibility enabled, going to merge...");
            ClusteringManager clusteringManager = new ClusteringManager(configManager);
            results = clusteringManager.merge(results);

        } else {
            logger.info("Credibility was disabled, nothing done!");
        }

        return results;

    }

    /** Used for ConceptDemo */
    public List<Concept> executeConceptExtraction(String text) {

        // Hello!
        logger.info("executeConceptExtraction method!");

        // execute configuration
        this.executeBatchMode();

        // translate text into concepts
        DocumentFacetProcessor processor = new StandardDocumentFacetProcessor(configManager);
        List<Concept> concepts = processor.process(new TextFacet("0", "interactive-text", text));

        logger.info("======================== concept list =================================");
        for (Concept c : concepts){
            logger.info("Concept: " + c.getId());
        }
        logger.info("======================== concept list END =================================");

        return concepts;
    }

    /** Used for BilkentDemo */
    public List<Result> executeConceptSearch(String text) {

        logger.info("executeConceptSearch method!");
        logger.info("text: " + text);

        // execute configuration
        this.executeBatchMode();

        // search concept index with given query text (query text represents concepts)
        BilkentDemoPluginManager bManager = new BilkentDemoPluginManager(configManager);

        //List<Result> results = new ArrayList<>();
        List<Result> results = bManager.search(text);

        return results;
    }


    /**
     * Executes the manager
     */
    public static void main(String[] args) {

        // Initialize manager
        SystemManager manager = new SystemManager();
        manager.executeBatchMode();

    }

    /** For testing */
    public void test(){
        URL url = Thread.currentThread().getContextClassLoader().getResource("system.properties");
        System.out.println(url.toString());

        // load file somewhere on the disk
        System.out.println("---------------------------- ANYWHERE ON DISK -----------------------------------");
        System.out.println(Util.getContents(new File("D:/Data/collections/UserCredibilityImages/imageLists/1"), true));
        System.out.println("-------------------------FINISHED ANYWHERE ON DISK -----------------------------------");

        // load file in home directory
        //System.out.println("---------------------------- HOME DIRECTORY -----------------------------------");
        //String userHome = System.getProperty("user.home");
        //System.out.println(Util.getContents(new File(userHome + "/data/collections/UserCredibilityImages/imageLists/1")));
    }

    /**
     * @return the configManager
     */
    public final ConfigurationManager getConfigManager() {
        return configManager;
    }
}