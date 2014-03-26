package at.tuwien.mucke.plugin.prototypetest.query;

import at.tuwien.mucke.config.ConfigurationManager;
import at.tuwien.mucke.documentmodel.ImageFacet;
import at.tuwien.mucke.documentmodel.TextFacet;
import at.tuwien.mucke.query.Query;
import at.tuwien.mucke.query.QueryReader;
import org.apache.log4j.Logger;

/**
 * Implements a Query Reader that reads queries from a file with facets separated by CR (newlines)
 */
public class CSVQueryReader implements QueryReader {

    static Logger logger = Logger.getLogger(FileQueryCollectionReader.class);

    /**
     * Allow access to configuration parameters
     */
    private ConfigurationManager configManager;


    /**
     * Constructor
     */
    public CSVQueryReader(ConfigurationManager configManager) {
        this.configManager = configManager;
    }

    @Override
    public Query prepare(String queryString) {

        Query query = new Query();

        String lines[] = queryString.split("\n");

        // add part 1-3 as id, query terms, and narrative
        query.add(new TextFacet("1", "id", lines[0].trim()));
        query.add(new TextFacet("2", "query terms", lines[1].trim()));
        query.add(new TextFacet("3", "narrative", lines[2].trim()));
        // split part 4, which contains varying, multiple image file names
        String imageNames[] = lines[3].trim().split("\t");
        for (String imageName : imageNames) {
            query.add(new ImageFacet("3", "image", imageName));
        }

        return query;
    }

}