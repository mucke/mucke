package at.tuwien.mucke.plugin.clef2011;

import at.tuwien.mucke.config.ConfigurationManager;
import at.tuwien.mucke.plugin.PluginManager;
import at.tuwien.mucke.plugin.clef2011.evaluation.Result;
import at.tuwien.mucke.plugin.clef2011.index.CLEF2011WikiDocIndexer;
import at.tuwien.mucke.plugin.clef2011.index.CLEF2011WikiMetaIndexer;
import at.tuwien.mucke.plugin.clef2011.query.CLEF2011Query;
import at.tuwien.mucke.plugin.clef2011.query.TopicReader;
import at.tuwien.mucke.plugin.clef2011.search.CLEF2011WikiDocSearcher;
import at.tuwien.mucke.plugin.clef2011.search.CLEF2011WikiMetaSearcher;
import at.tuwien.mucke.plugin.clef2011.search.result.DocumentResult;
import at.tuwien.mucke.plugin.clef2011.search.result.ImageResult;
import com.memetix.mst.language.Language;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Date;
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
public class CLEF2011PluginManager extends PluginManager {

    /**
     * Logging facility
     */
    static Logger logger = Logger.getLogger(CLEF2011PluginManager.class);


    /**
     * Constructor
     */
    public CLEF2011PluginManager(ConfigurationManager configManager) {
        super(configManager);
    }

    /**
     * Indexes the CLEF2011 collection.
     */
    public void index() {

        // measuring index time
        Date start = new Date();

        // create indexers
        CLEF2011WikiDocIndexer clef2011WikiDocIndexer = new CLEF2011WikiDocIndexer(configManager);
        CLEF2011WikiMetaIndexer clef2011WikiMetaIndexer = new CLEF2011WikiMetaIndexer(configManager);

        // Creates or updated Wikipedia Document Index
        System.out.println("1) Building/Updating Wiki document index");
        clef2011WikiDocIndexer.index(System.getProperty(ConfigConstants.WIKI_DOC_DIR), System.getProperty(ConfigConstants.WIKI_DOC_INDEXDIR));

        // Creates or updates Wikipedia Meta Information Index
        System.out.println("2) Building/Updating Wiki meta index");
        clef2011WikiMetaIndexer.index(System.getProperty(ConfigConstants.WIKI_META_DIR), Language.ENGLISH);

        // time stamping
        Date end = new Date();
        System.out.println("CLEF2011Manager: Total index time: " + (end.getTime() - start.getTime()) + " ms");

    }

    /**
     * Searches the CLEF2011 collection based on the provided standard topics.
     */
    public void search() {

        // measuring search time
        Date start = new Date();

        // create searchers
        CLEF2011WikiDocSearcher clef2011WikiDocSearcher = new CLEF2011WikiDocSearcher(configManager);
        CLEF2011WikiMetaSearcher clef2011WikiMetaSearcher = new CLEF2011WikiMetaSearcher(configManager);

        System.out.println("3) Extracting english topics from topic XML");

        // read topics
        TopicReader tReader = new TopicReader();
        tReader.readTopics();

        // display topic information
        List<CLEF2011Query> topics = tReader.getTopics();

        // collect all Lucene Result Objects
        List<Result> results = new ArrayList<Result>();

        for (int i = 0; i < topics.size(); i++) {

            // rank reset for every topic
            int rank = 1;

            // store topic ID
            String topicId = topics.get(i).getTopicID();

            // search run with English query text
            String query = topics.get(i).getEn();

            System.out.println("	3.1) Searching for documents based on topic " + topicId + ": " + query);

            List<DocumentResult> docResults = clef2011WikiDocSearcher.searchDocuments(query, System.getProperty(ConfigConstants.WIKI_DOC_INDEXDIR));

            for (DocumentResult docResult : docResults) {

                // keep document ID for logging
                String docId = "" + docResult.getId();

                // keep document score for logging
                float docScore = docResult.getScore();

                // Find associated imageIDs
                System.out.println("	2) Searching for imageID with the documentID " + docId);
                List<ImageResult> imageResults = clef2011WikiMetaSearcher.searchMetadata(query, docId);

                System.out.println();
                System.out.println("	Image Results");
                System.out.println("	-------------");

                if (imageResults.size() == 0) {
                    System.out.println("	NO Images found");
                } else {

                    for (ImageResult imageResult : imageResults) {

                        // keep the image ID for logging
                        String imageId = "" + imageResult.getId();

                        // keep the image score for logging
                        float imageScore = imageResult.getScore();

                        // debug
                        System.out.println("	imageId: " + imageId + " imageScore: " + imageScore);

                        // keeping it as a result
                        results.add(new Result(topicId, "" + rank++, docId, "" + docScore, imageId, null, "" + imageScore, null));
                    }

                }

                // new line
                System.out.println();

            }

        }

        // store all results collected
        logger.info("So, going to store all results collected: " + results.size());

        // TODO
        //Evaluator evaluator = new Evaluator();
        //evaluator.scoreResults(results);

        // time stamping
        Date end = new Date();
        System.out.println("CLEF2011Manager: Total search time: " + (end.getTime() - start.getTime()) + " ms");

    }


    /**
     * Evaluates the results of the CLEF2011 collection based on the search run as configured.
     */
    public void evaluate() {

        // measuring search time
        Date start = new Date();

        // evaluate results
        // TODO
        //Evaluator evaluator = new Evaluator();
        //evaluator.evaluate();

        // measuring search time
        Date end = new Date();
        System.out.println("CLEF2011Manager: Total evaluation time: " + (end.getTime() - start.getTime()) + " ms");

    }


    /**
     * Performs a run based on the configuration as defined in the system properties file "primary.properties" and its plugin-specific
     * properties file. The CLEF2011Plugin run is divided into three parts.
     * <p/>
     * First, the indexing part which is performed only if <code>do.index</code> is true.
     * Second, the search part which is executed if <code>do.search</code> is true.
     * Third, the evaluation part which runs when <code>do.evaluate</code> is true.
     */
    @Override
    public void run(String configuration) {

        if (System.getProperty(ConfigConstants.DO_INDEX).equals("true")) {
            logger.info("Indexing started...");
            this.index();
        }

        if (System.getProperty(ConfigConstants.DO_SEARCH).equals("true")) {
            logger.info("Searching started...");
            // CLEF2011Manager.searchSameAs();
            this.search();
        }

        if (System.getProperty(ConfigConstants.DO_EVALUATE).equals("true")) {
            logger.info("Evaluation started...");
            this.evaluate();
        }

    }

}