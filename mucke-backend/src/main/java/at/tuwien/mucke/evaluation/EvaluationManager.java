package at.tuwien.mucke.evaluation;

import at.tuwien.mucke.config.ConfigurationManager;
import org.apache.log4j.Logger;

/**
 * Interfaces evaluation applications (e.g. TREC_EVAL) and persists results in the system database through the
 * <see>at.tuwien.mucke.data</see> package.
 */
public class EvaluationManager {

    /** Configuration parameter access */
    protected ConfigurationManager configManager = null;

    /**
     * Logging facility
     */
    static Logger logger = Logger.getLogger(EvaluationManager.class);

    public EvaluationManager(ConfigurationManager configManager){
        this.configManager = configManager;
    }



}