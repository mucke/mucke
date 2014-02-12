package mucke.index;

import java.util.List;

import mucke.config.ConfigurationManager;

import org.apache.log4j.Logger;

/**
 * @author Ralf Bierig
 *
 */
public class StandardImageFacetIndexer implements FacetIndexer {

    /** Logging facility */
    static Logger logger = Logger.getLogger(StandardTagFacetIndexer.class);

    private ConfigurationManager configManager = null;
    
    /** Constructor */
    public StandardImageFacetIndexer(ConfigurationManager configManager) {
	this.configManager = configManager; 
    }
    
    @Override
    public void index(String contentDirectory, String indexDirectory, List<IndexFieldGenerator> fieldGenerators) {

	// TODO
	logger.debug("TODO: Implement image indexer.");

    }
    
}