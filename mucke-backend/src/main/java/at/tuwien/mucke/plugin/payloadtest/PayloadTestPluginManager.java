package at.tuwien.mucke.plugin.payloadtest;

import at.tuwien.mucke.config.ConfigurationManager;
import at.tuwien.mucke.plugin.PluginManager;
import at.tuwien.mucke.plugin.payloadtest.index.PayloadConceptIndexer;
import at.tuwien.mucke.plugin.payloadtest.search.PayloadSearcher;
import at.tuwien.mucke.search.Result;
import org.apache.log4j.Logger;

import java.util.List;

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
public class PayloadTestPluginManager extends PluginManager {

    /**
     * Logging facility
     */
    static Logger logger = Logger.getLogger(PayloadTestPluginManager.class);


    /**
     * Constructor
     */
    public PayloadTestPluginManager(ConfigurationManager configManager) {
        super(configManager);
    }


    /**
     * Runs the Plugin
     */
    public void run(String propertiesFile) {

        super.run(propertiesFile);

        //logger.info("1) Indexing concepts");
        PayloadConceptIndexer indexer = new PayloadConceptIndexer("conceptindexer", configManager);
        indexer.index();

        logger.info("");
        logger.info("");
        logger.info("2) Search for concepts");
        PayloadSearcher searcher = new PayloadSearcher("conceptsearcher", configManager);
        List<Result> cResult = searcher.search("blue");
        logger.info("================= RESULTS ===========================");
        int i = 1;
        for (Result r : cResult){
            logger.info("   " + i + ") id:" + r.getId() + " score:" + r.getScore());
            i++;
        }
        logger.info("================= END OF RESULTS ===================");


        //logger.info("3) Concept similarity");


        // search it
        // TODO

        logger.info("Done!");

    }

}