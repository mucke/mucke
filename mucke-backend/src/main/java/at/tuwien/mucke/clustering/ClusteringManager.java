package at.tuwien.mucke.clustering;


import at.tuwien.mucke.config.ConfigConstants;
import at.tuwien.mucke.config.ConfigurationManager;
import at.tuwien.mucke.search.Result;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * Main service interface for custering that fuses results based on content, result lists, and various forms of credibility.
 *
 * @author Alexandra-Mihaela Siriteanu
 */
public class ClusteringManager {

    static Logger logger = Logger.getLogger(ClusteringManager.class);

    private final ConfigurationManager configManager;

    public ClusteringManager(ConfigurationManager configManager){
        this.configManager = configManager;
    }

    /** Merges a single list of results with a merger based on the configuration */
    public List<Result> merge(List<Result> results){

        List<Result> newResults = null;

        String mergerClassName = configManager.getProperty(ConfigConstants.CLUSTERING_FACETMERGER_CLASS);
        if (mergerClassName != null && mergerClassName.length() > 0 ){
            ResultMerger merger = (ResultMerger)configManager.getResultMergerClass(mergerClassName);
            newResults = merger.merge(results);
        } else {
            logger.error("No merger '" + mergerClassName + "' found. Nothing merged." );
        }

        return newResults;

    }

}
