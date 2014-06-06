package at.tuwien.mucke;

import at.tuwien.mucke.clustering.ClusteringManager;
import at.tuwien.mucke.config.ConfigurationManager;
import at.tuwien.mucke.config.Run;
import at.tuwien.mucke.search.Result;
import at.tuwien.mucke.search.SearchManager;
import at.tuwien.mucke.util.Util;
import org.apache.log4j.Logger;

import java.io.File;
import java.net.URL;
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

        System.out.println("SYSTEMMANAGER IS ALIVE");

        // initialize the ConfigurationManager upon instantiation
        configManager = new ConfigurationManager();

        // initialize the DBManager upon instantiation
        // TODO
        // EvalDBManager.init();

    }

    /**
     * Executes the currently configured state of the system based on the configuration as defined in "system.properties" and its secondary
     * properties file. The secondary properties file may contain multiple runs that are executed in the order of their listing. Results of
     * each run are stored in the central system database.
     */
    public void executeBatchMode() {

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
        logger.info("Completed " + i + " runs!");
    }


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
        System.out.println(Util.getContents(new File("D:/Data/collections/UserCredibilityImages/imageLists/1")));
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