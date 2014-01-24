package mucke.config;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import mucke.index.IndexFieldGenerator;

import org.apache.log4j.Logger;
import org.apache.log4j.chainsaw.Main;

import com.hp.hpl.jena.tdb.index.Index;

public class ConfigurationManager {

    static Logger logger = Logger.getLogger(ConfigurationManager.class);

    /** Manages the system configuration file and the handling of system runs */
    public ConfigurationManager() {

	// load primary.properties configuration file
	this.loadPrimaryPropoerties();
    }

    /**
     * Load properties based on primary.properties and the secondary.properties file (as set in primiary.properties).
     * 
     * @throws Exception if of the property files was not fully loaded
     */
    public void loadPrimaryPropoerties() {

	try {

	    // load primary properties file
	    logger.debug("Loading primary properties...");
	    
	    System.getProperties().load(Main.class.getClassLoader().getResourceAsStream("primary.properties"));
	    logger.info("Primary properties successfully loaded.");

	} catch (Exception e) {

	    logger.error("Failed to load 'primary.properties' file. Check if it is included in the classpath. Exception: " + e.getMessage());
	    e.printStackTrace();

	}
    }

    /** Load configuration for a single run.
     * @param propertiesFilename The file with the properties */
    public void loadRunPropoerties(String propertiesFilename) {

	try {

	    // load run configuration
	    this.loadRunPropoerties(propertiesFilename);
	    logger.info("Run configuration '" + propertiesFilename + "' successfully loaded.");

	} catch (Exception e) {

	    logger.error("Failed to load run configuration. Check if it is included in the classpath. Exception: " + e.getMessage());
	    e.printStackTrace();

	}
    }

    /**
     * Extracts configuration file names of runs.
     * 
     * @return List of runs
     */
    public List<String> getRuns() {

	List<String> runs = new ArrayList<String>();
	StringTokenizer tokenizer = new StringTokenizer(System.getProperties().getProperty("run.properties"), ",");
	while (tokenizer.hasMoreTokens()) {
	    String run = tokenizer.nextToken().trim();
	    if (run.length() > 0) {
		runs.add(run);
	    }
	}
	return runs;
    }

    /**
     * Extracts Run class (property "class") from the run configuration for the current manager.
     * 
     * @param propertiesFilename The configuration file of the run
     */
    public Run getRunClass(String propertiesFilename) {

	// first re-load properties
	this.loadPrimaryPropoerties();

	Run run = null;

	try {
	    // load run configuration
	    System.getProperties().load(Main.class.getClassLoader().getResourceAsStream(propertiesFilename));
	    // extract Run class
	    String runClass = System.getProperties().getProperty("class");

	    // instantiate Run class by name (standard constructor)
	    //Class<?> clazz = Class.forName(runClass);
	    //run = (Run) clazz.newInstance();
	    
	    // instantiate Run class by name (non-standard constructor) that associates each run class with its ConfigurationManager
	    Class<?> clazz = Class.forName(runClass);
	    Constructor constructor = clazz.getConstructor(ConfigurationManager.class);
	    run = (Run) constructor.newInstance(this);
	    
	} catch (Exception e) {
	    logger.error("Exception while reading and creating Run instance: " + e.getMessage());
	    e.printStackTrace();
	}
	return run;
    }
    
    
    /**
     * Recursively looks up multiple comma-separated configuration properties of the form "prop1 = prop2" where "prop2 = value1, value2".
     * When "prop1" is looked up, it returns a list of Strings containing "prop1" and "prop2". Since this is done recursively, it allows for
     * properties being reused and linked up in multiple functions.
     */
    public void getProperties(String property, List<String> values, boolean recursive) {
	
	// extract value and split all children
	String value = System.getProperty(property);
	StringTokenizer tokenizer = new StringTokenizer(value, ";");
	
	// iterate over each token
	while (tokenizer.hasMoreTokens()){
	    // extract token
	    String token = tokenizer.nextToken().trim();
	    
	    if (recursive){
		// check token
		if(this.isProperty(token)) {
		    getProperties(token, values, recursive);	
		} else {
		    values.add(token);
		}
	    } else {
		 values.add(token);
	    }
	}
    }
    
    
    /**
     * Checks if the given property is an active configuration property
     * 
     * @param property The configuration property to check
     * @return true, if the property is a configuration property, false otherwise
     */
    public boolean isProperty(String property) {
	String value = System.getProperty(property);
	if (value != null && value.trim().length() > 0) {
	    return true;
	}
	return false;
    }

    public static void main(String[] args) {

	ConfigurationManager configManager = new ConfigurationManager();

	// test property
	String property = "run.properties";
	logger.info("Property: " + property);
	
	// extract property values 
	List<String> values = new ArrayList<String>();
	configManager.getProperties(property, values, true);
	logger.info("Property values found: " + values.size());
	for (int i = 0; i < values.size(); i++) {
	    logger.info((i + 1) + ": " + values.get(i));
	}
    }

}