package mucke.config;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import mucke.data.DBManager;
import mucke.index.IndexFieldGenerator;

import org.apache.log4j.Logger;
import org.apache.log4j.chainsaw.Main;

import com.hp.hpl.jena.tdb.index.Index;

public class ConfigurationManager {

    static Logger logger = Logger.getLogger(ConfigurationManager.class);
    
    private DBManager dbManager = null; 

    /** Manages the system configuration file and the handling of system runs */
    public ConfigurationManager() {

	// load primary.properties configuration file
	this.loadPrimaryPropoerties();
	
	// initilize system database
	dbManager = new DBManager(this);
	dbManager.createTables();
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
	StringTokenizer tokenizer = new StringTokenizer(System.getProperties().getProperty("run.properties"), ";");
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
     * Creates instance of a FacetIndexer class by name and cast it to a interface.
     * 
     * @param class
     * @param propertiesFilename The configuration file of the run
     */
    public Object getFacetIndexerClass(String indexName, String clazzName) {

	Object object = null;

	try {
	    
	    // instantiate Run class by name (standard constructor)
	    Class<?> clazz = Class.forName(clazzName);
	    Constructor constructor = clazz.getConstructor(String.class, ConfigurationManager.class);
	    object = constructor.newInstance(indexName, this);
	    
	} catch (Exception e) {
	    logger.error("Exception while reading and creating Object instance: " + e.getMessage());
	    e.printStackTrace();
	}
	return object;
    }    
    
    /** Extracts configuration properties that may contain multiple, comma-separated values of the form:
     * <li>
     * <ul>non-recursive, such as "prop1 = value_1; value_2; ...; value_n.".</ul>
     * <ul>recursive, such as "prop1 = value_1; prop_2" and "prop_2 = value_2" resulting in a result list
     * containing value_1 and value_2. Recursive properties can be build and linked hierarchically inside
     * the configuration file.</ul>
     *</li>
     * @param property The property to be extracted
     * @param recursive traversal, if true, non-recursive otherwise
     * @return List of values
     */
    public List<String> getProperties(String property, boolean recursive) {
	
	List<String> values = new ArrayList<String>();
	
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
		    // collect recursive values
		    List<String> moreValues = getProperties(token, recursive);
		    // and add to own values
		    values.addAll(moreValues);
		} else {
		    // just add to own values
		    values.add(token);
		}
	    } else {
		// just add to own values
		values.add(token);
	    }
	}
	
	// return the result values
	return values; 
    }
    

    /** Extracts simple property. Does not recognize comma-separated values. */
    public String getProperty(String property) {
	if (!isProperty(property)){
	    logger.error("No such property: " + property);
	    return "";
	}
	return System.getProperty(property);
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

    /**
     * @return the dbManager
     */
    public final DBManager getDbManager() {
        return dbManager;
    }
}