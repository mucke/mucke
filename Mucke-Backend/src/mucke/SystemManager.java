package mucke;

import java.util.List;

import mucke.config.ConfigurationManager;
import mucke.config.Run;

//import org.apache.log4j.Logger;

/**
 * This is the Main class that regulates everything the system does at the top level. It reads the configuration file "primary.properties"
 * and executes a list of runs (defined by the property 'run.properties'). Each run is configured individually and independently and may work
 * on a different collection. Each runs executes sequentially in its own, separate parametric environment.
 * 
 * @author Ralf Bierig
 */
public class SystemManager {

    //static Logger logger = Logger.getLogger(SystemManager.class);

    private ConfigurationManager configManager = null;

    /** Constructor */
    public SystemManager() {

	System.out.println("SYSTEMMANAGER IS ALIVE AND KICKING!!!");
	
	// initialize the ConfigurationManager upon instantiation
	configManager = new ConfigurationManager();

	// initialize the DBManager upon instantiation
	// TODO
	// EvalDBManager.init();

    }

    /**
     * Executes the currently configured state of the system based on the configuration as defined in "primary.properties" and its secondary
     * properties file. The secondary properties file may contain multiple runs that are executed in the order of their listing. Results of
     * each run are stored in the central system database.
     */
    public void executeBatchMode() {

	// extract configuration for all runs
	//logger.debug("Starting batch mode...");
	//logger.debug("Loading runs...");
	List<String> runConfigFilenames = configManager.getRuns();
	// execute each run
	int i = 0;
	for (String runConfigFilename : runConfigFilenames) {
	    i++;
	    //logger.debug("Run # " + (i) + ":" + runConfigFilename);

	    // instantiate and execute Run
	    Run run = configManager.getRunClass(runConfigFilename);

	    if (run != null) {
		//logger.info("Starting the Runner '" + run.getClass().getSimpleName().toString() + "' with configuration '"
		//	+ runConfigFilename + "'");

		run.run(runConfigFilename);
	    }
	}
	//logger.info("Completed " + i + " runs!");
    }
    
    
    public void executeInteractiveMode(){
	//logger.debug("Starting interactive mode...");
	String interactiveConfigFilename = "prototypetest-interactive.properties";
	
	try {

	    // load interactive configuration
	    configManager.loadRunPropoerties(interactiveConfigFilename);
	    //logger.info("Run configuration '" + interactiveConfigFilename + "' successfully loaded.");

	} catch (Exception e) {

	    //logger.error("Failed to load run configuration. Check if it is included in the classpath. Exception: " + e.getMessage());
	    e.printStackTrace();

	}
	
    }
    
    /** Executes the manager */
    public static void main(String[] args) {

	// Initialize manager
	SystemManager manager = new SystemManager();
	manager.executeBatchMode();

    }

    /**
     * @return the configManager
     */
    public final ConfigurationManager getConfigManager() {
        return configManager;
    }
}