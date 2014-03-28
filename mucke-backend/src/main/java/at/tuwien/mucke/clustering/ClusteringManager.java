package at.tuwien.mucke.clustering;


import at.tuwien.mucke.config.ConfigurationManager;

/**
 * Main service interface for custering that fuses results based on content, result lists, and various forms of credibility.
 *
 * @author Alexandra-Mihaela Siriteanu
 */
public class ClusteringManager {

    private final ConfigurationManager configManager;

    public ClusteringManager(ConfigurationManager configManager){
        this.configManager = configManager;
    }


}
