package at.tuwien.mucke.plugin.conceptdemo;

import at.tuwien.mucke.config.ConfigurationManager;
import at.tuwien.mucke.index.IndexManager;
import at.tuwien.mucke.plugin.PluginManager;
import org.apache.log4j.Logger;

/**
 * Runs IR evaluations of the ImageCLEF2011 collection based on the parameterization declared in the properties files. It offers methods for
 * 1) indexing, 2) searching and 3) evaluating results.
 * <p/>
 * The index builds two indexes, one for Wikipedia content and one for metadata. Search is performed first on the documents of the Wiki
 * document collection as defined in the document index and then, for each document, a search is performed on the metadata index. The
 * evaluation generates performance measures based on a gold standard.
 * <p/>
 * Each of the three parts can run independently.
 *
 * @author Ralf Bierig
 */
public class ConceptDemoPluginManager extends PluginManager {

    /**
     * Logging facility
     */
    static Logger logger = Logger.getLogger(ConceptDemoPluginManager.class);


    /**
     * Constructor
     */
    public ConceptDemoPluginManager(ConfigurationManager configManager) {
        super(configManager);
    }


    /**
     * Runs the Test Plugin
     */
    public void run(String propertiesFile) {
        super.run(propertiesFile);

        logger.info("Start");

        // create indices
        IndexManager indexManager = new IndexManager(this.configManager);
        indexManager.index();

        logger.info("Done!");

    }

}