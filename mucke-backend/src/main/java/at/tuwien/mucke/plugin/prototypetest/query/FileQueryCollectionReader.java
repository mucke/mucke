package at.tuwien.mucke.plugin.prototypetest.query;

import at.tuwien.mucke.config.ConfigConstants;
import at.tuwien.mucke.config.ConfigurationManager;
import at.tuwien.mucke.query.Query;
import at.tuwien.mucke.query.QueryCollectionReader;
import at.tuwien.mucke.util.Util;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Test implementation of a a PrepareCollectionStrategy
 */
public class FileQueryCollectionReader implements QueryCollectionReader {

    static Logger logger = Logger.getLogger(FileQueryCollectionReader.class);
    private ConfigurationManager configManager;
    private CSVQueryReader queryReader;

    /**
     * Constructor
     */
    public FileQueryCollectionReader(ConfigurationManager configManager) {
        this.configManager = configManager;
        this.queryReader = new CSVQueryReader(configManager);
    }

    @Override
    public List<Query> prepare() {

        // list of queries
        List<Query> queries = new ArrayList<Query>();

        // read query directory
        File file = null;
        try {

            file = new File(configManager.getProperty(ConfigConstants.QUERY_COLLECTION_FILE));

        } catch (Exception e) {
            logger.error("Error accessing query collection. Nothing done!");
            return null;
        }

        // do not try to index files that cannot be read
        if (file.isDirectory()) {

            File[] files = file.listFiles();

            // an IO error could occur
            if (files != null) {
                for (int i = 0; i < files.length; i++) {

                    // generate single query
                    logger.debug("Reading topic # " + (i + 1) + " ...");
                    CSVQueryReader queryReader = new CSVQueryReader(configManager);
                    Query query = queryReader.prepare(Util.getContents(files[i]));
                    queries.add(query);
                }
            }

        } else {
            // no directory, bad path
            logger.error("The query file path is not a directory. This QueryCollectionReader can only work " +
                    "on directories of query files. Nothing done.");
        }
        return queries;
    }

}