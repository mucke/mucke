package at.tuwien.mucke.plugin;

import at.tuwien.mucke.config.ConfigurationManager;
import at.tuwien.mucke.config.Run;
import org.apache.log4j.Logger;


/**
 * Runs a plugin and provides the most essential functionality for all plugins in the collection.
 *
 * @author Ralf Bierig
 */
public class PluginManager implements Run {

    static Logger logger = Logger.getLogger(PluginManager.class);
    protected ConfigurationManager configManager = null;


    /**
     * Constructor
     */
    public PluginManager(ConfigurationManager configManager) {
        this.configManager = configManager;
    }

    @Override
    public void run(String propertiesFile) {
        logger.info("Running: " + getClass().getName());
    }

}